# Definição dos arquivos do Docker Compose
COMPOSE_LOCAL = --env-file=.env.local -f docker-compose.local.yml
COMPOSE_STG = --env-file=.env.stg -f docker-compose.stg.yml
COMPOSE_PROD = --env-file=.env.prod -f docker-compose.prod.yml

# Função para verificar pré-requisitos antes de rodar os comandos
check_requirements:
	@echo "🔍 Verificando pré-requisitos..."
	@command -v docker >/dev/null 2>&1 || { echo "❌ Docker não está instalado!"; exit 1; }
	@command -v docker-compose >/dev/null 2>&1 || { echo "❌ Docker Compose não está instalado!"; exit 1; }
	@java -version 2>&1 | grep '21\.' >/dev/null || { echo "❌ JDK 21 não encontrado!"; exit 1; }
	@echo "✅ Todos os pré-requisitos estão atendidos."

# Função para iniciar os serviços
define start_service
	@make check_requirements
	@echo "🚀 Iniciando $(1)..."
	docker-compose $(2) up -d
	@echo "✅ $(1) iniciado com sucesso!"
endef

# Função para parar os serviços
define stop_service
	@echo "🛑 Parando $(1)..."
	docker-compose $(2) down $(3)
	@echo "✅ $(1) desligado com sucesso!"
endef

# Comandos para subir e desligar os serviços de cada ambiente
up_local:
	$(call start_service, Ambiente LOCAL, $(COMPOSE_LOCAL))

down_local:
	$(call stop_service, Ambiente LOCAL, $(COMPOSE_LOCAL))

downv_local:
	$(call stop_service, Ambiente LOCAL, $(COMPOSE_LOCAL), -v)

up_stg:
	$(call start_service, Ambiente STAGING, $(COMPOSE_STG))

down_stg:
	$(call stop_service, Ambiente STAGING, $(COMPOSE_STG))

downv_stg:
	$(call stop_service, Ambiente STAGING, $(COMPOSE_STG), -v)

up_prod:
	$(call start_service, Ambiente PRODUÇÃO, $(COMPOSE_PROD))

down_prod:
	$(call stop_service, Ambiente PRODUÇÃO, $(COMPOSE_PROD))

downv_prod:
	$(call stop_service, Ambiente PRODUÇÃO, $(COMPOSE_PROD), -v)

# Comandos para visualizar logs de cada ambiente
logs_local:
	docker-compose $(COMPOSE_LOCAL) logs -f

logs_stg:
	docker-compose $(COMPOSE_STG) logs -f

logs_prod:
	docker-compose $(COMPOSE_PROD) logs -f

# Comando para reiniciar os serviços de cada ambiente
restart_local:
	@make down_local && make up_local

restart_stg:
	@make down_stg && make up_stg

restart_prod:
	@make down_prod && make up_prod
