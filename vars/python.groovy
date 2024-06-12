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

        environment{
            NEXUS= credentials('NEXUS')
        }


        stages {

            stage('code quality') {
                steps {
                    //sh 'sonar-scanner -Dsonar.projectKey=${component} -Dsonar.host.url=http://172.31.43.197:9000 -Dsonar.login=admin -Dsonar.password=admin123 -Dsonar.qualitygate.wait=true'
                    sh 'echo code Quality'
                }
            }
            stage('Unit Test cases') {
                steps {
                    sh 'echo Unit tests'
                    //sh 'python3.6 -m unittest'
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
            stage('release application') {
                when {
                    expression {
                        env.TAG_NAME ==~ ".*"
                    }
                }
                steps {
                    sh 'echo $TAG_NAME >VERSION'
                    sh 'zip -r ${component}-${TAG_NAME}.zip *.ini *py *.txt VERSION ${schema_dir}'
                    sh 'curl -f -v -u ${NEXUS_USR}:${NEXUS_PSW} --upload-file ${component}-${TAG_NAME}.zip http://172.31.12.50:8081/repository/${component}/${component}-${TAG_NAME}.zip'
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

