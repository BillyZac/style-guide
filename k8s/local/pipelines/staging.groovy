@Library('buildit')
def LOADED = true
podTemplate(label: 'style-guide',
        containers: [
                containerTemplate(name: 'nodejs-builder', image: 'builditdigital/node-builder', ttyEnabled: true, command: 'cat', privileged: true),
                containerTemplate(name: 'docker', image: 'docker:1.11', ttyEnabled: true, command: 'cat'),
                containerTemplate(name: 'kubectl', image: 'builditdigital/kube-utils', ttyEnabled: true, command: 'cat')],
        volumes: [
                hostPathVolume(mountPath: '/var/projects', hostPath: '/Users/romansafronov/dev/projects'),
                hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock')]) {
  node('style-guide') {

    currentBuild.result = "SUCCESS"
    sendNotifications = false //FIXME !DEV_MODE

    try {
      stage('Set Up') {

        gitInst = new git()
        npmInst = new npm()
        slackInst = new slack()

        appName = "style-guide"
        slackChannel = "style-guide"
        gitUrl = "https://github.com/buildit/style-guide.git"
        appUrl = "http://style-guide.kube.local"
        dockerRegistry = "builditdigital"
        image = "$dockerRegistry/$appName"
        deployment = "style-guide-staging"
      }
      container('nodejs-builder') {
        stage('Checkout') {
          //checkout scm
          git(url: '/var/projects/style-guide', branch: 'k8s')          

          // global for exception handling
          shortCommitHash = gitInst.getShortCommit()
          commitMessage = gitInst.getCommitMessage()
          version = npmInst.getVersion()
        }

        stage("Install") {
          sh "npm install"
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
          tag = "${version}-${shortCommitHash}-${env.BUILD_NUMBER}"
          // Docker pipeline plugin does not work with kubernetes (see https://issues.jenkins-ci.org/browse/JENKINS-39664)
          sh "docker build -t $image:$tag ."
        }
      }

      container('kubectl') {
        stage('Deploy To K8S') {
          var deploymentName = "$deployment-$appName".substring(0, 24)
          sh "helm ls -q | grep $deploymentName || helm install ./k8s/style-guide -f ./k8s/local/vars/staging.yaml -n $deploymentName"
          sh "helm upgrade $deployment ./style-guide -f k8s/local/vars/staging.yaml --set image.repository=$image --set image.tag=$tag"
          sh "kubectl rollout status deployment/$deploymentName"
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
          if (sendNotifications) slackInst.notify("Deployed to Staging", "Commit <${gitUrl}/commits/${shortCommitHash}|${shortCommitHash}> has been deployed to <${appUrl}|${appUrl}>\n\n${commitMessage}", "good", "http://i3.kym-cdn.com/entries/icons/square/000/002/230/42.png", slackChannel)
        }
      }
    }
    catch (err) {
      currentBuild.result = "FAILURE"
      if (sendNotifications) slackInst.notify("Error while deploying to Staging", "Commit <${gitUrl}/commits/${shortCommitHash}|${shortCommitHash}> failed to deploy to <${appUrl}|${appUrl}>", "danger", "http://i2.kym-cdn.com/entries/icons/original/000/002/325/Evil.jpg", slackChannel)
      throw err
    }
  }
}
