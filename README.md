# oficina-billing-service

**Responsabilidade:** Orçamentos, aprovações e pagamentos via Mercado Pago.

Tech Challenge SOAT — Fase 4 | Microsserviço 2 de 3

---

## Stack

| Componente | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4 |
| Banco | PostgreSQL (relacional) |
| Migrations | Liquibase |
| Mensageria | RabbitMQ (Spring AMQP) |
| Pagamentos | Mercado Pago SDK v2 |
| Segurança | OAuth2 / JWT (Keycloak) |
| Observabilidade | Micrometer + Prometheus |
| Testes | JUnit 5 + Cucumber (BDD) + Testcontainers |
| Qualidade | JaCoCo ≥ 80% + SonarCloud |

---

## Eventos publicados / consumidos

| Direção | Routing Key | Descrição |
|---|---|---|
| Consome | `os.aberta` | Gera orçamento automaticamente |
| Publica | `orcamento.gerado` | Notifica geração do orçamento |
| Publica | `orcamento.aprovado` | Cliente aprovou + preferência MP criada |
| Publica | `orcamento.recusado` | Cliente recusou orçamento |
| Publica | `orcamento.falhou` | Falha ao criar preferência MP (compensação) |
| Publica | `pagamento.confirmado` | Pagamento aprovado pelo Mercado Pago |
| Publica | `pagamento.falhou` | Pagamento recusado pelo Mercado Pago |
| Consome | `execucao.finalizada` | Encerra o ciclo de cobrança |

---

## Integração Mercado Pago

Configure a variável de ambiente `MERCADO_PAGO_ACCESS_TOKEN` com seu token de sandbox.

Webhook de notificações: `POST /api/webhooks/mercadopago`

Documentação: https://www.mercadopago.com.br/developers/pt/docs

---

## Como rodar localmente

```bash
# Subir infraestrutura (do oficina-os-service)
docker compose -f ../oficina-os-service/docker-compose.infra.yml up -d

# Configurar token MP
export MERCADO_PAGO_ACCESS_TOKEN=TEST-seu-token

./mvnw spring-boot:run
```

Porta: **8082** | Swagger: `http://localhost:8082/api/swagger-ui.html`

---

## CI/CD

- `.github/workflows/ci.yml` — build, testes, SonarCloud, push Docker
- Deploy manual por workflow dispatch com tag de imagem
