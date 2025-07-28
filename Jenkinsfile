pipeline {
    agent any

    // GitHub Push 이벤트로 자동 빌드
    triggers {
        githubPush()
    }

    stages {
        stage('Checkout') {
            steps {
                // 깃허브에서 main 브랜치 코드를 체크아웃
                git(
                    url: 'https://github.com/TeamCodeGears/michiki-backend.git',
                    branch: 'main',
                    credentialsId: 'GITHUB_CREDENTIALS_ID'  // ← 여기를 실제 credentialsId로 변경
                )
            }
        }

        stage('Build') {
            steps {
                // gradlew 가 있는 디렉터리로 이동해서 빌드
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
                // 마지막 커밋 작성자
                def author = sh(
                    script: "git --no-pager log -1 --pretty=format:'%an'",
                    returnStdout: true
                ).trim()

                // 빌드 아티팩트 보관
                archiveArtifacts artifacts: 'michiki/build/libs/*.jar', fingerprint: true

                // Discord 알림 (성공)
                discordSend(
                    title: "Michiki 백엔드 빌드 성공",
                    description: "🎉 빌드가 성공했습니다! (작성자: ${author})",
                    footer: "Build #${env.BUILD_NUMBER}",
                    link: env.BUILD_URL,
                    result: currentBuild.currentResult,
                    webhookURL: "https://discord.com/api/webhooks/1396075120250060822/EOu3kTw5ewpPchVWlz3TkEkgadgi7_tUDfvKHk__0H5c-FZB_3fLHTzdYD4atxM9ZUdN"
                )
            }
        }
        failure {
            script {
                def author = sh(
                    script: "git --no-pager log -1 --pretty=format:'%an'",
                    returnStdout: true
                ).trim()

                // Discord 알림 (실패)
                discordSend(
                    title: "Michiki 백엔드 빌드 실패",
                    description: "❌ 빌드가 실패했습니다. (작성자: ${author})",
                    footer: "Build #${env.BUILD_NUMBER}",
                    link: env.BUILD_URL,
                    result: currentBuild.currentResult,
                    webhookURL: "https://discord.com/api/webhooks/1396075120250060822/EOu3kTw5ewpPchVWlz3TkEkgadgi7_tUDfvKHk__0H5c-FZB_3fLHTzdYD4atxM9ZUdN"
                )
            }
        }
    }
}

