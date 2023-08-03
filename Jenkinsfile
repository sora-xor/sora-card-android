@Library('jenkins-library@feature/DOPS-2648') _

def extraBuildEnv = [
  "testkey1": "123",
  "testkey2": "123"
]
new org.soramitsu.mainLibrary().call(
  agentLabel: "android",
  skipSonar: true,
  skipDojo: true,
  agentImage: "android-build-box-jdk11:latest",
  nexusCredentials: "bot-soramitsu-rw",
  buildCommand: './gradlew :oauth:build',
  testCommand: './gradlew :oauth:test',
  publishCommand: './gradlew publish',
  // pushTags: ['PR-69': 'pr-69'],
  publishLibrary: true,
  skipDockerImage: true,
  dojoProductType: "sora-card-android",
  extraBuildEnv: extraBuildEnv
)