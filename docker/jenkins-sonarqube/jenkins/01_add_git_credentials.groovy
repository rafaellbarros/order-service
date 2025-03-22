import jenkins.model.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*

def jenkins = Jenkins.instance
def credentialsStore = jenkins.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

def gitUsername = "rafaellbarros"  // 🛑 Substitua pelo seu usuário do GitHub/GitLab
def gitToken = ""  // 🛑 Substitua pelo seu Personal Access Token (PAT)
def credentialId = "GIT_CREDENTIALS_ID"

def existingCredentials = credentialsStore.getCredentials(Domain.global()).find { it.id == credentialId }

if (existingCredentials) {
    println "✅ Credencial '${credentialId}' já existe. Ignorando criação."
} else {
    println "🔑 Criando credencial '${credentialId}'..."
    def gitCredentials = new UsernamePasswordCredentialsImpl(
        CredentialsScope.GLOBAL,
        credentialId,
        "Credenciais Git para Jenkins via HTTPS",
        gitUsername,
        gitToken
    )
    credentialsStore.addCredentials(Domain.global(), gitCredentials)
    println "✅ Credencial '${credentialId}' adicionada com sucesso!"
}
