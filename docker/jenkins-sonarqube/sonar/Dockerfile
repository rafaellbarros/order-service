FROM sonarqube:community

# Instalar o tini
USER root
RUN apt-get update && apt-get install -y tini

COPY sonar-init-qg.sh /usr/local/bin/sonar-init-qg.sh

# Define o tini como o inicializador
ENTRYPOINT ["/usr/bin/tini", "--", "/opt/sonarqube/bin/sonar.sh"]
