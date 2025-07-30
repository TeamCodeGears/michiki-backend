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
                // Jenkins 워크스페이스에서 바로 복사만 하고 서비스 재시작
                sh """
                  # 빌드된 JAR 파일 하나만 골라서 복사
                  JAR=\$(ls ${WORKSPACE}/michiki/build/libs/*SNAPSHOT.jar | grep -v plain)
                  echo "Deploying \$JAR to /home/ec2-user/michiki-backend/michiki.jar"

                  sudo cp "\$JAR" /home/ec2-user/michiki-backend/michiki.jar
                  sudo systemctl restart michiki.service
                """
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

