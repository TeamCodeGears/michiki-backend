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
            script {
                // 마지막 커밋 작성자 추출
                def author = sh(
                    script: "git --no-pager log -1 --pretty=format:'%an'",
                    returnStdout: true
                ).trim()

                // 아카이브
                archiveArtifacts artifacts: 'michiki/build/libs/*.jar', fingerprint: true

                // Discord 알림 (작성자 포함)
                discordSend(
                    title: "테스트 젠킨스 job",
                    description: "빌드가 성공했습니다. (작성자: ${author})",
                    footer: "Build #${env.BUILD_NUMBER}",
                    link: env.BUILD_URL,
                    result: currentBuild.currentResult,
                    webhookURL: "https://discord.com/api/webhooks/1396075120250060822/EOu3kTw5ewpPchVWlz3TkEkgadgi7_tUDfvKHk__0H5c-FZB_3fLHTzdYD4atxM9ZUdN"
                )
            }
        }
        failure {
            script {
                // 실패 시에도 작성자를 함께 표시
                def author = sh(
                    script: "git --no-pager log -1 --pretty=format:'%an'",
                    returnStdout: true
                ).trim()

                discordSend(
                    title: "테스트 젠킨스 job",
                    description: "빌드가 실패했습니다. (작성자: ${author})",
                    footer: "Build #${env.BUILD_NUMBER}",
                    link: env.BUILD_URL,
                    result: currentBuild.currentResult,
                    webhookURL: "https://discord.com/api/webhooks/1396075120250060822/EOu3kTw5ewpPchVWlz3TkEkgadgi7_tUDfvKHk__0H5c-FZB_3fLHTzdYD4atxM9ZUdN"
                )
            }
        }
    }
}