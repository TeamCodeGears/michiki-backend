pipeline {
    agent any

    // GitHub Push / PR 머지 이벤트로 자동 빌드
    triggers { githubPush() }

    options {
        // 기본 체크아웃 건너뛰기
        skipDefaultCheckout()
    }

    stages {
        stage('Checkout') {
            steps {
                deleteDir()
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

        stage('Deploy') {
            steps {
                // 워크스페이스 루트에서 pull
                sh 'git pull origin main'

                // 다시 subproject 빌드
                dir('michiki') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean build -x test'
                }

                // 서비스 재시작 (jenkins 유저가 sudoers에 등록되어 있어야 함)
                sh 'sudo systemctl restart michiki.service'
            }
        }
    }

    post {
        success {
            script {
                def author = sh(
                    script: "cd michiki && git --no-pager log -1 --pretty=format:'%an'",
                    returnStdout: true
                ).trim()

                archiveArtifacts artifacts: 'michiki/build/libs/*.jar', fingerprint: true

                discordSend(
                    title:      "Michiki 백엔드 빌드 & 배포 성공 🎉",
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
                    script: "cd michiki && git --no-pager log -1 --pretty=format:'%an'",
                    returnStdout: true
                ).trim()

                discordSend(
                    title:      "Michiki 백엔드 빌드 또는 배포 실패 ❌",
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

