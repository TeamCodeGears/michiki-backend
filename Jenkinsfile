pipeline {
    agent any

    // GitHub Push / PR 머지 이벤트로 자동 빌드
    triggers { githubPush() }

    options {
        // 자동으로 해 주는 첫 체크아웃 건너뛰기
        skipDefaultCheckout()
    }

    stages {
        stage('Checkout') {
            steps {
                // 이전 빌드 파일들 다 삭제
                deleteDir()
                // 깔끔하게 한 번만 체크아웃
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // 실제 코드가 'michiki' 폴더 아래에 있으므로
                dir('michiki') {
                    sh 'chmod +x gradlew || true'
                    sh './gradlew clean build -x test'
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                // Credentials → SSH Username with private key 에 등록한 ID
                sshagent(['ec2-ssh-credential-id']) {
                    sh """
                      ssh -o StrictHostKeyChecking=no ec2-user@43.200.191.212 << 'ENDSSH'
                        # 이미 EC2에 클론되어 있는 앱 디렉토리로 이동
                        cd ~/michiki-backend

                        # 최신 커밋만 가져오기
                        git pull origin main

                        # 다시 서브모듈 있는 위치로 이동해 빌드
                        cd michiki
                        chmod +x gradlew
                        ./gradlew clean build -x test

                        # systemd 로 서비스 재시작
                        sudo systemctl restart michiki.service
                      ENDSSH
                    """
                }
            }
        }
    }

    post {
        success {
            script {
                // 마지막 커밋 작성자
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

