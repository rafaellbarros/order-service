#!/bin/bash

SONARQUBE_URL="http://sonar:9000"
SONARQUBE_USER="admin"
SONARQUBE_OLD_PASSWORD="admin"
SONARQUBE_NEW_PASSWORD="#Xablau8001@"
TOKEN_NAME="SONAR_TOKEN"
TOKEN_FILE="/var/jenkins_home/sonarqube-token.txt"
HOST_FILE="/var/jenkins_home/sonarqube-host-url.txt"

# Função para verificar se o Quality Gate existe
check_quality_gate_exists() {
  quality_gate_name=$1
  response=$(curl -s -u "$SONARQUBE_USER:$SONARQUBE_PASSWORD" "$SONARQUBE_URL/api/qualitygates/search")
  echo $response | grep -q "\"name\":\"$quality_gate_name\""
}

# Função para criar um novo Quality Gate
create_quality_gate() {
  echo "✅ Criando o Quality Gate com 75% de cobertura"
  curl -u "$SONARQUBE_USER:$SONARQUBE_PASSWORD" -X POST "$SONARQUBE_URL/api/qualitygates/create" \
    -d "name=Java-Quality-Gate" \
    -d "conditions=coverage>75"
}

# Função para definir o Quality Profile padrão para Java
set_java_quality_profile() {
  echo "✅ Definindo Quality Profile para Java como padrão"
  curl -u "$SONARQUBE_USER:$SONARQUBE_PASSWORD" -X POST "$SONARQUBE_URL/api/qualityprofiles/set_default" \
    -d "language=java" -d "profile=Sonar way"
}

# Função para definir o Quality Gate como padrão
set_quality_gate_default() {
  echo "✅ Definindo Java-Quality-Gate como padrão"
  curl -u "$SONARQUBE_USER:$SONARQUBE_PASSWORD" -X POST "$SONARQUBE_URL/api/qualitygates/set_as_default" \
    -d "name=Java-Quality-Gate"
}

# Aguarda o SonarQube estar pronto
echo "$(date '+%Y-%m-%d %H:%M:%S') 🕒 Aguardando o SonarQube iniciar..."
until curl -s "$SONARQUBE_URL/api/system/status" | grep -q '"status":"UP"'; do
    sleep 10
done

echo "$(date '+%Y-%m-%d %H:%M:%S') ✅ SonarQube está UP!"

# Desativar política de senha forte antes de alterar a senha (se possível)
echo "$(date '+%Y-%m-%d %H:%M:%S') 🔄 Desativando política de senha forte..."

DISABLE_STRONG_PASSWORD_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -u "$SONARQUBE_USER:$SONARQUBE_OLD_PASSWORD" \
    -X POST "$SONARQUBE_URL/api/settings/set" \
    -d "key=sonar.security.localUsersDisableStrongPasswords&value=true")

if [ "$DISABLE_STRONG_PASSWORD_RESPONSE" == "204" ]; then
    echo "✅ Política de senha forte desativada."
else
    echo "⚠️ Não foi possível desativar a política de senha forte."
fi

# Alterar a senha diretamente (caso não tenha sido desativada a política)
echo "$(date '+%Y-%m-%d %H:%M:%S') 🔄 Alterando senha do usuário admin..."

CHANGE_PASSWORD_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -u "$SONARQUBE_USER:$SONARQUBE_OLD_PASSWORD" \
    -X POST "$SONARQUBE_URL/api/users/change_password" \
    -d "login=$SONARQUBE_USER&previousPassword=$SONARQUBE_OLD_PASSWORD&password=$SONARQUBE_NEW_PASSWORD")

if [ "$CHANGE_PASSWORD_RESPONSE" == "204" ]; then
    echo "✅ Senha alterada para '$SONARQUBE_NEW_PASSWORD'"
    SONARQUBE_PASSWORD=$SONARQUBE_NEW_PASSWORD
else
    echo "⚠️ Senha não foi alterada. Pode já ter sido modificada."
    SONARQUBE_PASSWORD=$SONARQUBE_OLD_PASSWORD
fi

# Gerar Token de autenticação
echo "$(date '+%Y-%m-%d %H:%M:%S') 🔄 Gerando token..."

if [ -f "$TOKEN_FILE" ]; then
    echo "⚠️ O arquivo $TOKEN_FILE já existe. O token não será recriado."
    TOKEN=$(cat "$TOKEN_FILE")
else
    TOKEN=$(curl -s -u "$SONARQUBE_USER:$SONARQUBE_PASSWORD" \
        -X POST "$SONARQUBE_URL/api/user_tokens/generate" \
        -d "name=$TOKEN_NAME" | jq -r '.token')

    if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
        echo "❌ Erro ao gerar o token do SonarQube"
        exit 1
    fi

    echo "✅ Token gerado: $TOKEN"
    echo "$TOKEN" > "$TOKEN_FILE"
    chmod 600 "$TOKEN_FILE"
fi

# Definir Quality Profile padrão para Java
set_java_quality_profile

# Verifica se o Quality Gate já existe
if ! check_quality_gate_exists "Java-Quality-Gate"; then
  create_quality_gate
else
  echo "✅ Quality Gate já existe."
fi

# Definir Java-Quality-Gate como padrão
set_quality_gate_default

# Salvar URL do SonarQube
echo "$SONARQUBE_URL" > "$HOST_FILE"
chmod 600 "$HOST_FILE"

# Exportar variáveis de ambiente
export SONAR_TOKEN="$TOKEN"
export SONAR_HOST_URL="$SONARQUBE_URL"

echo "✅ $(date '+%Y-%m-%d %H:%M:%S') Configuração do SonarQube concluída com sucesso!"

