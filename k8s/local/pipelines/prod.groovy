@Library('buildit')
def LOADED = true
podTemplate(label: 'style-guide',
        containers: [
                containerTemplate(name: 'nodejs-builder', image: 'builditdigital/node-builder', ttyEnabled: true, command: 'cat'),
                containerTemplate(name: 'kubectl', image: 'builditdigital/kube-utils', ttyEnabled: true, command: 'cat')],
        volumes: [hostPathVolume(mountPath: '/var/projects', hostPath: '/Users/romansafronov/dev/projects')]) {
    node('style-guide') {

        sendNotifications = false //FIXME !DEV_MODE

        try {
            stage('Set Up') {

                slackInst = new slack()

                appName = "style-guide"
                cloud = "local"
                env = "prod"
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
                }
            }
            container('kubectl') {
                stage('Deploy To K8S') {
                    def deployment = "$appName-$env"
                    def deploymentObj = "$deployment-$appName".substring(0, 24)
                    def varsFile = "./k8s/${cloud}/vars/${env}.yaml"
                    sh "helm ls -q | grep $deployment || helm install ./k8s/style-guide -f $varsFile -n $deployment"
                    sh "helm upgrade $deployment ./k8s/style-guide -f $varsFile --set image.repository=$image"
                    sh "kubectl rollout status deployment/$deploymentObj"
                }
            }
            if (sendNotifications) slackInst.notify("Deployed to ${env}", "Commit <${gitUrl}/commits/${shortCommitHash}|${shortCommitHash}> has been deployed to ${env}\n\n${commitMessage}", "good", "http://i3.kym-cdn.com/entries/icons/square/000/002/230/42.png", slackChannel)
        }
        catch (err) {
            currentBuild.result = "FAILURE"
            if (sendNotifications) slackInst.notify("Error while deploying to ${env}", "Commit <${gitUrl}/commits/${shortCommitHash}|${shortCommitHash}> failed to deploy to ${env}", "danger", "http://i2.kym-cdn.com/entries/icons/original/000/002/325/Evil.jpg", slackChannel)
            throw err
        }
    }
}