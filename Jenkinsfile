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
                    // 사용할 브랜치 결정
                    def branchName = env.CHANGE_TARGET
                                     ?: env.BRANCH_NAME
                                     ?: (env.GIT_BRANCH?.tokenize('/')[-1])
                                     ?: 'main'
                    echo "▶ Checking out branch: ${branchName}"

                    // 1) 워크스페이스 완전 삭제
                    deleteDir()

                    // 2) 클론할 폴더(예: 'michiki')가 필요하면 dir() 사용
                    dir('michiki') {
                        // 3) Git 클론
                        git(
                            url:          'https://github.com/TeamCodeGears/michiki-backend.git',
                            branch:       branchName,
                            credentialsId:'ffea7d54-dc6d-441f-8e4f-e5612a6ee6b2'
                        )
                    }
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
                def author = sh(
                    script: "git --no-pager log -1 --pretty=format:'%an'",
                    returnStdout: true
                ).trim()

                archiveArtifacts artifacts: 'michiki/build/libs/*.jar', fingerprint: true

                discordSend(
                    title:      "Michiki 백엔드 빌드 성공 🎉",
                    description:"작성자: ${author}",
                    footer:     "Build #${env.BUILD_NUMBER}",
                    link:       env.BUILD_URL,
                    result:     currentBuild.currentResult,
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
                    title:      "Michiki 백엔드 빌드 실패 ❌",
                    description:"작성자: ${author}",
                    footer:     "Build #${env.BUILD_NUMBER}",
                    link:       env.BUILD_URL,
                    result:     currentBuild.currentResult,
                    webhookURL: "https://discord.com/api/webhooks/1396075120250060822/EOu3kTw5ewpPchVWlz3TkEkgadgi7_tUDfvKHk__0H5c-FZB_3fLHTzdYD4atxM9ZUdN"
                )
            }
        }
    }
}
