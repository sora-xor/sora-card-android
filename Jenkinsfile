@Library('jenkins-library@feature/DOPS-2367/sora-card-android' ) _

// Job properties
def jobParams = [
  booleanParam(defaultValue: false, description: 'push to the dev profile', name: 'prDeployment'),
]

def pipeline = new org.android.AppPipeline(steps: this,
    sonar: false,
    // TODO: Create Sonar Project
    // sonarProjectName: 'sora-card-android',
    // sonarProjectKey: 'jp.co.soramitsu:sora-card-android',
    // testCmd: 'ktlint clean runModuleTests jacocoTestReport',
    jobParams: jobParams,
    // appPushNoti: true,
    dockerImage: 'build-tools/android-build-box-jdk11:latest')
pipeline.runPipeline('sora-card-android')