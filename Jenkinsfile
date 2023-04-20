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
                                chmod +x gradlew
                                ./gradlew :oauth:test
                            '''
                        }
                        stage('Build oauth module') {
                            sh '''
                                chmod +x gradlew
                                ./gradlew :oauth:build
                            '''
                        }
                        if (env.BRANCH_NAME in deploymentBranches) {
                            stage('Publish') {
                                sh '''
                                    chmod +x gradlew
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
