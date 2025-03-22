FROM jenkins/jenkins:lts-slim-jdk21

USER root

# Instalar dependências
RUN apt-get update -qq \
    && apt-get install -qqy apt-transport-https ca-certificates curl software-properties-common gnupg2 curl jq tini && \
    rm -rf /var/lib/apt/lists/*

# Adicionar chave GPG do Docker
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Adicionar o repositório do Docker para Debian
RUN echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian bookworm stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

# Atualizar o apt e instalar Docker e CLI
RUN apt-get update -qq \
    && apt-get install -y docker-ce docker-ce-cli containerd.io

# Verificar versão do Docker
RUN docker --version

# Adicionar o usuário Jenkins ao grupo Docker e permitir acesso ao socket
RUN usermod -aG docker jenkins 

# Criar diretório de configuração
RUN mkdir -p /var/jenkins_home/casc_configs

# Desabilitar o assistente de configuração do Jenkins
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false

# Definir a configuração do Jenkins como código
ENV CASC_JENKINS_CONFIG /var/jenkins_home/casc_configs/jenkins.yaml

# Instalar plugins do Jenkins
COPY --chown=jenkins:jenkins plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt

# Copiar configurações do Jenkins
COPY casc.yaml /var/jenkins_home/casc_configs/jenkins.yaml

# Copiar scripts de inicialização
COPY 01_add_git_credentials.groovy /var/jenkins_home/init.groovy.d/01_add_git_credentials.groovy
COPY 02_setup_credentials.groovy /var/jenkins_home/init.groovy.d/02_setup_credentials.groovy
COPY 03_create_pipeline.groovy /var/jenkins_home/init.groovy.d/03_create_pipeline.groovy
COPY sonar-init.sh /usr/local/bin/sonar-init.sh


# Definir permissões corretas
RUN chown -R jenkins:jenkins /var/jenkins_home

USER jenkins

# Set tini as the entrypoint for better process management
ENTRYPOINT ["/usr/bin/tini", "--", "/usr/local/bin/jenkins.sh"]
