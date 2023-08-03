@Library('jenkins-library@feature/DOPS-2648') _

def extraBuildEnv = [
  "PAY_WINGS_REPOSITORY_URL": "credentials('PAY_WINGS_REPOSITORY_URL')",
  "PAY_WINGS_USERNAME": "credentials('PAY_WINGS_USERNAME')",
  "PAY_WINGS_PASSWORD": "credentials('PAY_WINGS_PASSWORD')",
  "RELEASE_REPOSITORY_URL": "credentials('RELEASE_REPOSITORY_URL')",
  "SORA_CARD_API_KEY_PROD": "credentials('SORA_CARD_API_KEY_PROD')",
  "SORA_CARD_API_KEY_TEST": "credentials('SORA_CARD_API_KEY_TEST')",
  "SORA_CARD_DOMAIN_PROD": "credentials('SORA_CARD_DOMAIN_PROD')",
  "SORA_CARD_DOMAIN_TEST": "credentials('SORA_CARD_DOMAIN_TEST')",
  "SORA_CARD_KYC_ENDPOINT_URL_PROD": "credentials('SORA_CARD_KYC_ENDPOINT_URL_PROD')",
  "SORA_CARD_KYC_ENDPOINT_URL_TEST": "credentials('SORA_CARD_KYC_ENDPOINT_URL_TEST')",
  "SORA_CARD_KYC_USERNAME_PROD": "credentials('SORA_CARD_KYC_USERNAME_PROD')",
  "SORA_CARD_KYC_USERNAME_TEST": "credentials('SORA_CARD_KYC_USERNAME_TEST')",
  "SORA_CARD_KYC_PASSWORD_PROD": "credentials('SORA_CARD_KYC_PASSWORD_PROD')",
  "SORA_CARD_KYC_PASSWORD_TEST": "credentials('SORA_CARD_KYC_PASSWORD_TEST')",
  "SORA_BACKEND_DEBUG": "credentials('SORA_BACKEND_DEBUG')",
  "SORA_BACKEND_RELEASE": "credentials('SORA_BACKEND_RELEASE')"
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