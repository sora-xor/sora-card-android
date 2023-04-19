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
                [$class: 'UsernamePasswordMultiBinding', credentialsId: 'bot-soramitsu-rw', usernameVariable: 'NEXUS_USERNAME', passwordVariable: 'NEXUS_PASSWORD']
                ])
            {
                docker.withRegistry('https://docker.soramitsu.co.jp', 'bot-build-tools-ro') {
                    docker.image("${dockerImage}").inside {
                        stage('Test oauth module') {
                            sh '''
                                ./gradlew :oauth:test
                            '''
                        }
                        stage('Build oauth module') {
                            sh '''
                                ./gradlew :oauth:build
                            '''
                        }
                        if (env.BRANCH_NAME in deploymentBranches) {
                            stage('Publish') {
                                sh '''
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