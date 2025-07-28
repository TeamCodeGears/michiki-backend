pipeline {
    agent any

    // GitHub Push / PR 머지 이벤트로 자동 빌드
    triggers {
        githubPush()
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    // PR 머지 대상 브랜치, Multibranch라면 BRANCH_NAME, 아니면 GIT_BRANCH 토큰 분리 → 없으면 main
                    def branchName = env.CHANGE_TARGET
                                     ?: env.BRANCH_NAME
                                     ?: (env.GIT_BRANCH?.tokenize('/')[-1])
                                     ?: 'main'
                    echo "▶ Checking out branch: ${branchName}"

                    git(
                        url: 'https://github.com/TeamCodeGears/michiki-backend.git',
                        branch: branchName,
                        credentialsId: 'ffea7d54-dc6d-441f-8e4f-e5612a6ee6b2'
                    )
                }
            }
        }

        stage('Build') {
            steps {
                dir('michiki') {
                    // gradlew 실행권한 부여 후 빌드 (테스트 제외)
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

                // 빌드 산출물 아카이브
                archiveArtifacts artifacts: 'michiki/build/libs/*.jar', fingerprint: true

                // Discord 알림 (성공)
                discordSend(
                    title:       "Michiki 백엔드 빌드 성공 🎉",
                    description: "작성자: ${author}",
                    footer:      "Build #${env.BUILD_NUMBER}",
                    link:        env.BUILD_URL,
                    result:      currentBuild.currentResult,
                    webhookURL:  "https://discord.com/api/webhooks/1396075120250060822/EOu3kTw5ewpPchVWlz3TkEkgadgi7_tUDfvKHk__0H5c-FZB_3fLHTzdYD4atxM9ZUdN"
                )
            }
        }
        failure {
            script {
                // 실패 시에도 작성자 포함
                def author = sh(
                    script: "git --no-pager log -1 --pretty=format:'%an'",
                    returnStdout: true
                ).trim()

                discordSend(
                    title:       "Michiki 백엔드 빌드 실패 ❌",
                    description: "작성자: ${author}",
                    footer:      "Build #${env.BUILD_NUMBER}",
                    link:        env.BUILD_URL,
                    result:      currentBuild.currentResult,
                    webhookURL:  "https://discord.com/api/webhooks/1396075120250060822/EOu3kTw5ewpPchVWlz3TkEkgadgi7_tUDfvKHk__0H5c-FZB_3fLHTzdYD4atxM9ZUdN"
                )
            }
        }
    }
}