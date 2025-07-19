pipeline {
    agent any

    triggers { githubPush() }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                dir('michiki') {
                    sh 'chmod +x gradlew || true'
                    sh './gradlew clean build -x test'
                }
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'michiki/build/libs/*.jar', fingerprint: true
            discordSend(
                description: "알람테스트",
                footer: "테스트 빌드가 성공했습니다.",
                link: env.BUILD_URL,
                result: currentBuild.currentResult,
                title: "테스트 젠킨스 job",
                webhookURL: "https://discord.com/api/webhooks/1396075120250060822/EOu3kTw5ewpPchVWlz3TkEkgadgi7_tUDfvKHk__0H5c-FZB_3fLHTzdYD4atxM9ZUdN"
            )
        }
        failure {
            discordSend(
                description: "알람테스트",
                footer: "테스트 빌드가 실패했습니다.",
                link: env.BUILD_URL,
                result: currentBuild.currentResult,
                title: "테스트 젠킨스 job",
                webhookURL: "https://discord.com/api/webhooks/1396075120250060822/EOu3kTw5ewpPchVWlz3TkEkgadgi7_tUDfvKHk__0H5c-FZB_3fLHTzdYD4atxM9ZUdN"
            )
        }
    }
}