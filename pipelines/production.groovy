// Production release pipeline
@Library('buildit')
def LOADED = true

node {

  currentBuild.result = "SUCCESS"
  sendNotifications = false//!env.DEV_MODE

  try {

    stage("Set Up") {
      checkout scm

      uiInst = new ui()
      ecrInst = new ecr()
      slackInst = new slack()
      templateInst = new template()
      convoxInst = new convox()

      domainName = "${env.MONGO_HOSTNAME}".substring(8)
      appName = "style-guide"
      registryBase = "006393696278.dkr.ecr.${env.AWS_REGION}.amazonaws.com"

      // global for exception handling
      slackChannel = "tbd"
      gitUrl = "https://github.com/buildit/style-guide"
      appUrl = "http://style-guide.${domainName}"
    }

    stage("Write docker-compose") {
      // global for exception handling
      tag = uiInst.selectTag(ecrInst.imageTags(appName, env.AWS_REGION))
      tmpFile = UUID.randomUUID().toString() + ".tmp"
      ymlData = templateInst.transform(readFile("docker-compose.yml.template"), [tag: tag, registry_base: registryBase])

      writeFile(file: tmpFile, text: ymlData)
    }

    stage("Deploy to production") {
      sh "convox login ${env.CONVOX_RACKNAME} --password ${env.CONVOX_PASSWORD}"
      sh "convox env set NODE_ENV=production EOLAS_DOMAIN=${domainName} --app ${appName}"
      sh "convox deploy --app ${appName} --description '${tag}' --file ${tmpFile} --wait"
      sh "rm ${tmpFile}"

      // wait until the app is deployed
      convoxInst.waitUntilDeployed("${appName}")
      convoxInst.ensureSecurityGroupSet("${appName}", env.CONVOX_SECURITYGROUP)
      if(sendNotifications) slackInst.notify("Deployed to Production", "Tag <${gitUrl}/commits/tag/${tag}|${tag}> has been deployed to <${appUrl}|${appUrl}>", "good", "http://images.8tracks.com/cover/i/001/225/360/18893.original-9419.jpg?rect=50,0,300,300&q=98&fm=jpg&fit=max&w=100&h=100", slackChannel)
    }
  }
  catch (err) {
    currentBuild.result = "FAILURE"
    if(sendNotifications) slackInst.notify("Error while deploying to Production", "Tag <${gitUrl}/commits/tag/${tag}|${tag}> failed to deploy to <${appUrl}|${appUrl}>", "danger", "https://yt3.ggpht.com/-X2hgGcBURV8/AAAAAAAAAAI/AAAAAAAAAAA/QnCcurrZr50/s100-c-k-no-mo-rj-c0xffffff/photo.jpg", slackChannel)
    throw err
  }
}
