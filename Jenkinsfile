@Library('jenkins-library' ) _

// Job properties
def jobParams = [
  booleanParam(defaultValue: false, description: 'push to the dev profile', name: 'prDeployment'),
]

def pipeline = new org.android.AppPipeline(steps: this,
    sonar: true,
    sonarProjectName: 'sora-card-android',
    sonarProjectKey: 'jp.co.soramitsu:sora-card-android',
    testCmd: ':oauth:test',
    jobParams: jobParams,
    appPushNoti: true,
    dockerImage: 'build-tools/android-build-box-jdk11:latest'
)
pipeline.runPipeline('sora')