@Library('jenkins-library@feature/DOPS-2957') _

def extraBuildSecrets = [
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
]

new org.android.ShareFeature().call(
  detekt: false,
  test: true,
  dockerImage: "build-tools/android-build-box:jdk17",
  nexusCredentials: "bot-soramitsu-rw",
  buildCmd: 'clean :oauth:build',
  testCmd: 'clean :oauth:test',
  extraBuildSecrets: extraBuildSecrets,
  dojo: true,
  dojoProductType: "sora-card"
)
