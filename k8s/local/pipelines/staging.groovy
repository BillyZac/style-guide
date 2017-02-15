@Library('buildit')
def LOADED = true
podTemplate(label: 'style-guide',
        containers: [
                containerTemplate(name: 'nodejs-builder', image: 'builditdigital/node-builder', ttyEnabled: true, command: 'cat', privileged: true),
                containerTemplate(name: 'docker', image: 'docker:1.11', ttyEnabled: true, command: 'cat'),
                containerTemplate(name: 'kubectl', image: 'builditdigital/kube-utils', ttyEnabled: true, command: 'cat')],
        volumes: [
                hostPathVolume(mountPath: '/var/projects', hostPath: '/Users/romansafronov/dev/projects'),
                hostPathVolume(mountPath: '/var/cache', hostPath: '/tmp'),
                hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')]) {
  node('style-guide') {

    currentBuild.result = "SUCCESS"
    sendNotifications = false //FIXME !DEV_MODE

    try {
      stage('Set Up') {

        gitInst = new git()
        npmInst = new npm()
        slackInst = new slack()

        buildNumber = env.BUILD_NUMBER
        appName = "style-guide"
        cloud = "local"
        env = "staging"
        slackChannel = "style-guide"
        gitUrl = "https://github.com/buildit/style-guide.git"
        appUrl = "http://style-guide.kube.local"
        dockerRegistry = "builditdigital"
        image = "$dockerRegistry/$appName"
        deployment = "style-guide-staging"
      }
      container('nodejs-builder') {
        stage('Checkout') {
          checkout scm
          //git(url: '/var/projects/style-guide', branch: 'k8s')

          // global for exception handling
          shortCommitHash = gitInst.getShortCommit()
          commitMessage = gitInst.getCommitMessage()
          version = npmInst.getVersion()
        }

        stage("Install") {
          // poor man's caching for node modules
          sh 'mkdir -p /var/cache/synapse-build/_cache'
          sh 'cp -r /var/cache/synapse-build/* .'
          sh "npm install"
          sh "rm -rf /var/cache/synapse-build/* && cp -r node_modules /var/cache/synapse-build/"
        }

        stage("Test") {
          sh "npm run test"
        }

        stage("Analysis") {
          sh "npm run lint"
        }

        stage("Build") {
          sh "npm run build"
        }

      }
      container('docker') {
        stage('Docker Image Build') {
          tag = "${version}-${shortCommitHash}-${buildNumber}"
          // Docker pipeline plugin does not work with kubernetes (see https://issues.jenkins-ci.org/browse/JENKINS-39664)
          sh "docker build -t $image:$tag ."
        }
      }

      container('kubectl') {
        stage('Deploy To K8S') {
          def deployment = "$appName-$env"
          def deploymentObj = "$deployment-$appName".substring(0, 24)
          def varsFile = "./k8s/${cloud}/vars/${env}.yaml"
          sh "helm ls -q | grep $deployment || helm install ./k8s/style-guide -f $varsFile -n $deployment"
          sh "helm upgrade $deployment ./style-guide -f $varsFile --set image.repository=$image --set image.tag=$tag"
          sh "kubectl rollout status deployment/$deploymentObj"
        }
      }

      container('nodejs-builder') {
        stage('TODO: Run Functional Acceptance Tests') {
          // todo: add test run once we have them
        }
      }

      container('docker') {
        stage('Promote Build to latest') {
          sh "docker tag $image:$tag $image:latest"
          if (sendNotifications) slackInst.notify("Deployed to ${env}", "Commit <${gitUrl}/commits/${shortCommitHash}|${shortCommitHash}> has been deployed to <${appUrl}|${appUrl}>\n\n${commitMessage}", "good", "http://i3.kym-cdn.com/entries/icons/square/000/002/230/42.png", slackChannel)
        }
      }
    }
    catch (err) {
      currentBuild.result = "FAILURE"
      if (sendNotifications) slackInst.notify("Error while deploying to ${env}", "Commit <${gitUrl}/commits/${shortCommitHash}|${shortCommitHash}> failed to deploy to ${env}", "danger", "http://i2.kym-cdn.com/entries/icons/original/000/002/325/Evil.jpg", slackChannel)
      throw err
    }
  }
}
