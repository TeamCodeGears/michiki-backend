pipeline {
    agent any
    triggers { githubPush() }
    stages {
        stage('Checkout')   { steps { checkout scm } }
        stage('Build')      {
            steps {
                sh 'chmod +x gradlew || true'
                sh './gradlew clean build -x test'
            }
        }
    }
    post { success { archiveArtifacts artifacts: '**/build/libs/*.jar' } }
}
