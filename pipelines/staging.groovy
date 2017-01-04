@Library('buildit')
def LOADED = true

node {
  withEnv(["PATH+NODE=${tool name: 'latest', type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'}/bin"]) {

    currentBuild.result = "SUCCESS"
    sendNotifications = false//!env.DEV_MODE

    try {
      stage("Set Up") {
        // clean the workspace before checking out
        if(fileExists('.git')) {
          echo 'Perform workspace cleanup'
          sh "git clean -ffdx"
        }

        ecrInst = new ecr()
        gitInst = new git()
        npmInst = new npm()
        slackInst = new slack()
        convoxInst = new convox()
        templateInst = new template()

        domainName = "${env.MONGO_HOSTNAME}".substring(8)
        registryBase = "006393696278.dkr.ecr.${env.AWS_REGION}.amazonaws.com"
        registry = "https://${registryBase}"
        appName = "style-guide"

        // global for exception handling
        slackChannel = "tbd"
        gitUrl = "https://github.com/BillyZac/style-guide"
        appUrl = "http://style-guide.staging.${domainName}"
      }

      stage("Checkout") {

        checkout scm

        // global for exception handling
        shortCommitHash = gitInst.getShortCommit()
        commitMessage = gitInst.getCommitMessage()
        version = npmInst.getVersion()
      }

      stage("Install") {
        sh "node --version"
        sh "npm install"
      }

      stage("Test") {
        sh "npm run test"
      }

      stage("Analysis") {
        sh "npm run lint"
      }

      stage("Build") {
        sh "NODE_ENV='staging' DOMAIN='${domainName}' npm run build"
      }

      stage("Docker Image Build") {
        tag = "${version}-${shortCommitHash}-${env.BUILD_NUMBER}"
        image = docker.build("${appName}:${tag}", '.')
        ecrInst.authenticate(env.AWS_REGION)
      }

      stage("Docker Push") {
        docker.withRegistry(registry) {
          image.push("${tag}")
        }
      }

      stage("Deploy To Staging") {
        tmpFile = UUID.randomUUID().toString() + ".tmp"
        ymlData = templateInst.transform(readFile("docker-compose.yml.template"), [tag: tag, registry_base: registryBase])
        writeFile(file: tmpFile, text: ymlData)

        sh "convox login ${env.CONVOX_RACKNAME} --password ${env.CONVOX_PASSWORD}"
        sh "convox env set NODE_ENV=staging EOLAS_DOMAIN=${domainName} --app ${appName}-staging"
        sh "convox deploy --app ${appName}-staging --description '${tag}' --file ${tmpFile} --wait"
      }

      stage("Promote Build to latest") {
        docker.withRegistry(registry) {
          image.push("latest")
        }

        if(sendNotifications) slackInst.notify("Deployed to Staging", "Commit <${gitUrl}/commits/${shortCommitHash}|${shortCommitHash}> has been deployed to <${appUrl}|${appUrl}>\n\n${commitMessage}", "good", "http://images.8tracks.com/cover/i/001/225/360/18893.original-9419.jpg?rect=50,0,300,300&q=98&fm=jpg&fit=max&w=100&h=100", slackChannel)
      }
    }
    catch (err) {
      currentBuild.result = "FAILURE"

      if(sendNotifications) slackInst.notify("Error while deploying to Staging", "Commit <${gitUrl}/commits/${shortCommitHash}|${shortCommitHash}> failed to deploy to <${appUrl}|${appUrl}>", "danger", "https://yt3.ggpht.com/-X2hgGcBURV8/AAAAAAAAAAI/AAAAAAAAAAA/QnCcurrZr50/s100-c-k-no-mo-rj-c0xffffff/photo.jpg", slackChannel)
      throw err
    }
  }
}
