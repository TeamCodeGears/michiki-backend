pipeline {
    agent any

    triggers { githubPush() }
    options { skipDefaultCheckout() }

    stages {
        stage('Checkout & Build') {
            steps {
                deleteDir()
                checkout scm

                dir('michiki') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean build -x test'
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                  # 1) 서비스 디렉토리로 이동해서 최신 소스 받아오기
                  cd /home/ec2-user/michiki-backend
                  git pull origin main

                  # 2) 워크스페이스에서 방금 빌드된 JAR 경로 추출 (plain 없이)
                  JAR=$(ls ${WORKSPACE}/michiki/build/libs/*SNAPSHOT.jar | grep -v plain)
                  echo "Deploying $JAR"

                  # 3) 복사 & 서비스 재시작
                  sudo cp "$JAR" /home/ec2-user/michiki-backend/michiki.jar
                  sudo systemctl restart michiki.service
                '''

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
                    script: "git --no-pager log -1 --pretty=format:'%an'",
                    returnStdout: true
                ).trim()

                discordSend(
                    title:      "Michiki 백엔드 빌드/배포 실패 ❌",
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

