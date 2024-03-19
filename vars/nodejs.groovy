def call() {
    pipeline {

        agent {
            node {
                label 'workstation'
            }
        }

        options {
            ansiColor('xterm')
        }


        stages {

            stage('code quality') {
                steps {
                     //sh 'sonar-scanner -Dsonar.projectKey=${component} -Dsonar.host.url=http://172.31.43.197:9000 -Dsonar.login=admin -Dsonar.password=admin123 -Dsonar.qualitygate.wait=true'
                    sh ' echo code quality'
                }
            }
            stage('Unit Test cases') {
                steps {
                    sh 'echo Unit tests'
                    //sh 'npm test'
               }
            }
            stage('Checkmarx SAST Scan') {
                steps {
                    sh 'echo Checkmarx scan'
                }
            }
            stage('Checkmarx SCA Scan') {
                steps {
                    sh 'echo Checkmarx SCA Scan'
                }
            }
            stage('release Application') {
                when {
                    expression {
                        env.TAG_NAME ==~ ".*"
                    }
                }
                steps {
                    sh 'env'
                    sh 'echo Release application'
                }
            }

        }

        post {
            always {
                cleanWs()
            }
        }

    }


}

