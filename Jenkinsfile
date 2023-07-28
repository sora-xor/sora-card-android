def dockerImage = 'build-tools/android-build-box-jdk11:latest'
def jenkinsAgent = 'android'
def deploymentBranches = ['master']

node(jenkinsAgent) {
    properties(
        [
            disableConcurrentBuilds(),
            buildDiscarder(steps.logRotator(numToKeepStr: '20'))
        ]
    )
    timestamps {
        try {
            stage('Git pull') {
                checkout scm
            }
            withCredentials([
                [$class: 'UsernamePasswordMultiBinding', credentialsId: 'bot-soramitsu-rw', usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD'],
                [$class: 'StringBinding', credentialsId: 'PAY_WINGS_REPOSITORY_URL', variable: 'PAY_WINGS_REPOSITORY_URL'],
                [$class: 'StringBinding', credentialsId: 'PAY_WINGS_USERNAME', variable: 'PAY_WINGS_USERNAME'],
                [$class: 'StringBinding', credentialsId: 'PAY_WINGS_PASSWORD', variable: 'PAY_WINGS_PASSWORD'],
                [$class: 'StringBinding', credentialsId: 'RELEASE_REPOSITORY_URL', variable: 'RELEASE_REPOSITORY_URL'],
                [$class: 'StringBinding', credentialsId: 'SORA_CARD_API_KEY_PROD', variable: 'SORA_CARD_API_KEY_PROD'],
                [$class: 'StringBinding', credentialsId: 'SORA_CARD_API_KEY_TEST', variable: 'SORA_CARD_API_KEY_TEST'],
                [$class: 'StringBinding', credentialsId: 'SORA_CARD_DOMAIN_PROD', variable: 'SORA_CARD_DOMAIN_PROD'],
                [$class: 'StringBinding', credentialsId: 'SORA_CARD_DOMAIN_TEST', variable: 'SORA_CARD_DOMAIN_TEST'],
                [$class: 'StringBinding', credentialsId: 'SORA_CARD_KYC_ENDPOINT_URL_PROD', variable: 'SORA_CARD_KYC_ENDPOINT_URL_PROD'],
                [$class: 'StringBinding', credentialsId: 'SORA_CARD_KYC_ENDPOINT_URL_TEST', variable: 'SORA_CARD_KYC_ENDPOINT_URL_TEST'],
                [$class: 'StringBinding', credentialsId: 'SORA_CARD_KYC_USERNAME_PROD', variable: 'SORA_CARD_KYC_USERNAME_PROD'],
                [$class: 'StringBinding', credentialsId: 'SORA_CARD_KYC_USERNAME_TEST', variable: 'SORA_CARD_KYC_USERNAME_TEST'],
                [$class: 'StringBinding', credentialsId: 'SORA_CARD_KYC_PASSWORD_PROD', variable: 'SORA_CARD_KYC_PASSWORD_PROD'],
                [$class: 'StringBinding', credentialsId: 'SORA_CARD_KYC_PASSWORD_TEST', variable: 'SORA_CARD_KYC_PASSWORD_TEST'],
                [$class: 'StringBinding', credentialsId: 'SORA_BACKEND_DEBUG', variable: 'SORA_BACKEND_DEBUG'],
                [$class: 'StringBinding', credentialsId: 'SORA_BACKEND_RELEASE', variable: 'SORA_BACKEND_RELEASE']
                ])
            {
                docker.withRegistry('https://docker.soramitsu.co.jp', 'bot-build-tools-ro') {
                    docker.image("${dockerImage}").inside {
                        stage('Test oauth module') {
                            sh '''
                                chmod +x ./gradlew
                                ./gradlew :oauth:test
                            '''
                        }
                        stage('Build oauth module') {
                            sh '''
                                chmod +x ./gradlew
                                ./gradlew :oauth:build
                            '''
                        }
                        if (env.BRANCH_NAME in deploymentBranches) {
                            stage('Publish') {
                                sh '''
                                    chmod +x ./gradlew  
                                    ./gradlew publish
                                '''
                            }
                        }
                    }
                }
            }
        } catch (e) {
            print e
            currentBuild.result = 'FAILURE'
        } finally {
            cleanWs()
        }
    }
}