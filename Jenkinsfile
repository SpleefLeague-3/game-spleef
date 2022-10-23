pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Deploy') {
            steps {
                sh "mkdir -p /mnt/jenkins/Spleef"
                sh "rm -f ./target/original*"
                sh "cp ./target/*.jar /mnt/jenkins/Spleef/"
            }
        }
    }

}
