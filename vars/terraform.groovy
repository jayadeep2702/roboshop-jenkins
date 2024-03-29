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

        parameters {
            choice(name: 'env', choices: ['dev', 'prod'], description: 'Pick environment')
            choice(name: 'action', choices: ['apply', 'destroy'], description: 'Pick Action')
        }

        stages {
            tage('Terraform INIT') {
                steps {
                    sh 'terrasform init -backend-config=env-${env}/state.tfvars'
                }
            }

            stage('Terraform Apply') {
                
                steps {
                    sh 'terraform ${action} -auto-approve -var-file=env-${env}/main.tf'
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

