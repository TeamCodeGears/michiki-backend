pipeline {
    agent any

    // 푸시/PR 머지 시 빌드 트리거
    triggers {
        githubPush()
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    // Multibranch Pipeline 환경이면 BRANCH_NAME, 아닐 땐 GIT_BRANCH 사용
                    def branch = env.BRANCH_NAME ?: env.GIT_BRANCH ?: 'main'
                    echo "Cloning branch: ${branch}"

                    git(
                        url: 'https://github.com/TeamCodeGears/michiki-backend.git',
                        branch: branch,
                        credentialsId: 'GITHUB_CREDENTIALS_ID'  // ← 실제 credentialsId 로 바꿔주세요
                    )
                }
            }
        }

        stage('Build') {
            steps {
                dir('michiki') {
                    // gradlew 실행권한 + 빌드
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

                // 빌드 산출물 보관
                archiveArtifacts artifacts: 'michiki/build/libs/*.jar', fingerprint: true

                // Discord 알림
                discordSend(
                    title: "Michiki 빌드 성공 🎉",
                    description: "빌드가 성공했습니다. (작성자: ${author})",
                    footer:    "Build #${env.BUILD_NUMBER}",
                    link:      env.BUILD_URL,
                    result:    currentBuild.currentResult,
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

                discordSend(
                    title: "Michiki 빌드 실패 ❌",
                    description: "빌드가 실패했습니다. (작성자: ${author})",
                    footer:    "Build #${env.BUILD_NUMBER}",
                    link:      env.BUILD_URL,
                    result:    currentBuild.currentResult,
                    webhookURL: "https://discord.com/api/webhooks/1396075120250060822/EOu3kTw5ewpPchVWlz3TkEkgadgi7_tUDfvKHk__0H5c-FZB_3fLHTzdYD4atxM9ZUdN"
                )
            }
        }
    }
}


