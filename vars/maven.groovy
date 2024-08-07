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

            stage('code compile') {
                steps {
                    sh 'mvn  compile'
                }
            }

            stage('code quality') {
                steps {
//                    sh 'sonar-scanner -Dsonar.projectKey=${component} -Dsonar.host.url=http://172.31.43.197:9000 -Dsonar.login=admin -Dsonar.password=admin123 -Dsonar.qualitygate.wait=true -Dsonar.java.binaries=./target'
                    sh 'echo Code quality'
                }
            }
            stage('Unit Test cases') {
                steps {
                    sh 'echo Unit tests'
                    //sh 'mvn test'
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
                    sh 'mvn package ; cp target/${component}-1.0.jar ${component}.jar'
                    sh 'echo $TAG_NAME >VERSION'
                    sh 'if [ -n "${schema_dir}" ]; then  aws ssm put-parameter --name "${component}.schema.checksum" --type "String" --value "$(md5sum schema/*.sql | awk "{print \\$1}")" --overwrite; fi '
                    //sh 'zip -r ${component}-${TAG_NAME}.zip ${component}.jar VERSION ${schema_dir}'
                    //sh 'curl -f -v -u ${NEXUS_USR}:${NEXUS_PSW} --upload-file ${component}-${TAG_NAME}.zip http://172.31.12.50:8081/repository/${component}/${component}-${TAG_NAME}.zip'
                    sh 'aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 710990938946.dkr.ecr.us-east-1.amazonaws.com'
                    sh 'docker build -t 710990938946.dkr.ecr.us-east-1.amazonaws.com/${component}:${TAG_NAME} .'
                    sh 'docker push 710990938946.dkr.ecr.us-east-1.amazonaws.com/${component}:${TAG_NAME}'
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

