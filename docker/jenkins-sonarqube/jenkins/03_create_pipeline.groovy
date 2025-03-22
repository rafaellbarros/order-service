import jenkins.model.*
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition

def jenkins = Jenkins.instance
def jobName = "order-service-pipeline"

if (jenkins.getItem(jobName) == null) {
    def job = jenkins.createProject(WorkflowJob, jobName)
    def pipelineScript = '''
    pipeline {
        agent any
        
        environment {
            ENVIRONMENT = "${params.ENVIRONMENT ?: 'dev'}"
            ENV_FILE = ".env.${ENVIRONMENT}"
            SONAR_HOST_URL = credentials('SONAR_HOST_URL') 
            SONAR_TOKEN = credentials('SONAR_TOKEN') 
        }
        
        parameters {
            choice(name: 'ENVIRONMENT', choices: ['dev', 'stg', 'prod'], description: 'Selecione o ambiente para o deploy')
        }
        
        stages {
            stage('Checkout') {
                steps {
                    script {
                        echo '🔄 Clonando o repositório do Git...'
                        checkout([
                            $class: 'GitSCM',
                            branches: [[name: '*/main']], 
                            userRemoteConfigs: [[
                                url: 'https://github.com/rafaellbarros/order-service.git',
                                credentialsId: 'GIT_CREDENTIALS_ID'
                            ]]
                        ])
                    }
                }
            }
            stage('Build') {
                steps {
                    script {
                        echo "🔨 Iniciando Build sem testes usando ${ENV_FILE}..."
                        sh './gradlew build -x test'
                    }
                }
            }
            stage('Test') {
                steps {
                    script {
                        echo "🧪 Executando Testes usando ${ENV_FILE}..."
                        sh './gradlew test'
                    }
                }
            }
            stage('SonarQube Analysis') {
                steps {
                    script {
                        echo '📊 Executando análise no SonarQube...'
                        // sh './gradlew sonar -Dsonar.projectKey=order-service -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.token=${SONAR_TOKEN} -Dsonar.qualitygate=Java-Quality-Gate'
                        sh './gradlew sonar -Dsonar.projectKey=order-service -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.token=${SONAR_TOKEN} -Dsonar.qualitygate=Java-Quality-Gate -Dsonar.analysis.mode=publish'
                    }
                }
            }
            stage('Deploy') {
                steps {
                    script {
                        echo "🚀 Deploy para ambiente ${ENVIRONMENT.toUpperCase()} usando ${ENV_FILE}..."
                        // sh './deploy.sh' // Adapte conforme necessário
                    }
                }
            }
        }
        
        post {
            failure {
                echo '🚨 Falha na pipeline! Verifique os logs.'
            }
        }
    }
    '''
    job.setDefinition(new CpsFlowDefinition(pipelineScript, true))
    job.save()
    println "✅🚀 Pipeline '${jobName}' criada automaticamente com sucesso! 🎉"
} else {
    println "⚠️ Pipeline '${jobName}' já existe, ignorando criação."
}
