def call(int buildNumber) {
    pipeline {
        agent {
            node {
                label 'workstation'

            }
        }

        parameters {
            choice(name: 'env', choices: ['dev', 'prod'], description: 'pick environment')
        }

        stages {

            stage('TERRAFORM INIT') {
                steps {
                    sh 'terraform init -backend-config=env-${env}/state.tfvars'
                }
            }

            stage('TERRAFORM APPLY') {
                steps {
                    sh 'terraform apply -auto-approve -var-file=env-${env}/main.tf'
                }
            }
        }


        post {
            always {
                cleanWS()
            }
        }
    }
}