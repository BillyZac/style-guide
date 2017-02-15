@Library('buildit')
def LOADED = true
podTemplate(label: 'style-guide',
        containers: [
                containerTemplate(name: 'nodejs-builder', image: 'builditdigital/node-builder', ttyEnabled: true, command: 'cat'),
                containerTemplate(name: 'kubectl', image: 'builditdigital/kube-utils', ttyEnabled: true, command: 'cat')],
        volumes: []) {
    node('style-guide') {

        sendNotifications = false //FIXME !DEV_MODE

        try {
            stage('Set Up') {

                slackInst = new slack()

                appName = "style-guide"
                cloud = "ec2"
                env = "prod"
                slackChannel = "style-guide"
                gitUrl = "https://github.com/buildit/style-guide.git"
                region = 'us-east-1'
                dockerRegistry = "006393696278.dkr.ecr.${region}.amazonaws.com"
                image = "$dockerRegistry/$appName"
                deployment = "style-guide-staging"
            }

            container('nodejs-builder') {
                stage('Checkout') {
                    checkout scm
                    //git(url: 'https://github.com/electroma/style-guide.git', branch: 'k8s')
                }
            }
            container('kubectl') {
                stage('Deploy To K8S') {
                    def deployment = "$appName-$env"
                    def deploymentObj = "$deployment-$appName".substring(0, 24)
                    def varsFile = "./k8s/${cloud}/vars/${env}.yaml"
                    sh "helm ls -q | grep $deployment || helm install ./k8s/style-guide -f $varsFile -n $deployment --namespace=public"
                    sh "helm upgrade $deployment ./k8s/style-guide -f $varsFile --set image.repository=$image --namespace=public"
                    sh "kubectl rollout status deployment/$deploymentObj -n public"
                }
            }
            if (sendNotifications) slackInst.notify("Deployed ${appName} latest to ${env}", "Latest image ${image} has been deployed to ${env}", "good", "http://images.8tracks.com/cover/i/001/225/360/18893.original-9419.jpg?rect=50,0,300,300&q=98&fm=jpg&fit=max&w=100&h=100", slackChannel)
        }
        catch (err) {
            currentBuild.result = "FAILURE"
            if (sendNotifications) slackInst.notify("Error while promoting ${appName} to ${env}", "Failed to promote image ${image} to ${env}", "danger", "http://i2.kym-cdn.com/entries/icons/original/000/002/325/Evil.jpg", slackChannel)
            throw err
        }
    }
}