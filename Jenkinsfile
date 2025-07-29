pipeline {
    agent any

    // GitHub Push / PR 머지 이벤트로 자동 빌드
    triggers {
        githubPush()
    }

    options {
        // Declarative가 자동으로 해주는 첫 번째 checkout(scaffold)을 건너뜁니다.
        skipDefaultCheckout()
    }

    stages {
        stage('Checkout') {
            steps {
                // 기존에 남아 있던 파일들(이전 빌드 산출물, .git 등)을 모두 지웁니다.
                deleteDir()

                // 한 번만 깔끔하게 리포지토리를 내려받습니다.
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // 이제 gradlew가 workspace 루트에 분명히 존재하니 바로 실행 가능
                sh 'chmod +x gradlew || true'
                sh './gradlew clean build -x test'
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

                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true

                discordSend(
                    title:      "Michiki 백엔드 빌드 성공 🎉",
                    description:"작성자: ${author}",
                    footer:     "Build #${env.BUILD_NUMBER}",
                    link:       env.BUILD_URL,
                    result:     currentBuild.currentResult,
                    webhookURL:  "https://discord.com/api/webhooks/1396075120250060822/EOu3kTw5ewpPchVWlz3TkEkgadgi7_tUDfvKHk__0H5c-FZB_3fLHTzdYD4atxM9ZUdN"
                )
            }
        }
        failure {
            script {
                def author = sh(
                    script: "git --no-pager log -1 --pretty=format:'%an'",
                    returnStdout: true
                ).trim()

                discordSend(
                    title:      "Michiki 백엔드 빌드 실패 ❌",
                    description:"작성자: ${author}",
                    footer:     "Build #${env.BUILD_NUMBER}",
                    link:       env.BUILD_URL,
                    result:     currentBuild.currentResult,
                    webhookURL:  "https://discord.com/api/webhooks/1396075120250060822/EOu3kTw5ewpPchVWlz3TkEkgadgi7_tUDfvKHk__0H5c-FZB_3fLHTzdYD4atxM9ZUdN"
                )
            }
        }
    }
}

