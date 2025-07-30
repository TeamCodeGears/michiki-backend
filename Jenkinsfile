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
                // 실제 JAR 파일 경로를 뽑아서 변수에 저장한 뒤 복사
                                sh '''
                                  # plain 버전 제외한 실행용 JAR 파일 하나만 골라서 변수에 담기
                                  JAR=$(ls ${WORKSPACE}/michiki/build/libs/*SNAPSHOT.jar | grep -v plain)

                                  echo "Deploying $JAR"
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

