import jenkins.model.*
import hudson.model.*
import hudson.plugins.sonar.*
import hudson.util.Secret
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl
import java.nio.file.Files
import java.nio.file.Paths

// Obtém a instância do Jenkins
def instance = Jenkins.getInstance()

// 📌 Caminho dos arquivos que contêm o token e a URL
def tokenFilePath = "/var/jenkins_home/sonarqube-token.txt"
def hostUrlFilePath = "/var/jenkins_home/sonarqube-host-url.txt"

// Lê o conteúdo do arquivo para obter o token do SonarQube
def SONAR_TOKEN = ""
def tokenFile = new File(tokenFilePath)
if (tokenFile.exists()) {
    SONAR_TOKEN = tokenFile.text.trim()  // Lê e remove espaços extras no início e fim
} else {
    println "❌ O arquivo '${tokenFilePath}' não foi encontrado! Não foi possível configurar o SonarQube."
    return  // Interrompe o script caso o arquivo não exista
}

// Lê o conteúdo do arquivo para obter a URL do SonarQube
def SONAR_HOST_URL = ""
def hostUrlFile = new File(hostUrlFilePath)
if (hostUrlFile.exists()) {
    SONAR_HOST_URL = hostUrlFile.text.trim()  // Lê e remove espaços extras no início e fim
} else {
    println "❌ O arquivo '${hostUrlFilePath}' não foi encontrado! Não foi possível configurar o SonarQube."
    return  // Interrompe o script caso o arquivo não exista
}

// 🛠️ Criar credenciais do SonarQube (TOKEN)
def tokenCredentials = new StringCredentialsImpl(
    CredentialsScope.GLOBAL, 
    "SONAR_TOKEN",  // ID das credenciais
    null,  // Descrição opcional
    Secret.fromString("${SONAR_TOKEN}")
)

// 🛠️ Criar credenciais do SonarQube (HOST_URL)
def urlCredentials = new StringCredentialsImpl(
    CredentialsScope.GLOBAL, 
    "SONAR_HOST_URL",  // ID das credenciais
    null,  // Descrição opcional
    Secret.fromString("${SONAR_HOST_URL}")
)

SystemCredentialsProvider systemCredentialsProvider = SystemCredentialsProvider.getInstance()
systemCredentialsProvider.getStore().addCredentials(Domain.global(), tokenCredentials)
systemCredentialsProvider.getStore().addCredentials(Domain.global(), urlCredentials)
systemCredentialsProvider.save()

// 🔧 Configurar a instalação do SonarQube no Jenkins
SonarInstallation sonarInstallation = new SonarInstallation(
    "sonar",                      // Nome da instalação
    "${SONAR_HOST_URL}",           // URL do SonarQube
    "SONAR_TOKEN",                 // ID das credenciais (criado acima)
    null, null, null, null, null, null  // Outros parâmetros podem ser nulos ou configurados conforme necessário
)

// Obter a configuração global do SonarQube
SonarGlobalConfiguration sonarGlobalConfiguration = instance.getDescriptor(SonarGlobalConfiguration.class)
sonarGlobalConfiguration.setInstallations(sonarInstallation)
sonarGlobalConfiguration.migrateCredentials()
sonarGlobalConfiguration.save()

// Salvar as configurações do Jenkins
instance.save()

println "✅ Configuração do SonarQube concluída com sucesso!"
