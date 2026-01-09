# Tech Challenge - Orders

Repositório responsável pelo microserviço de pedidos.

## 🍔 API Endpoints

### Públicos (Sem autenticação)
@@ -50,36 +17,13 @@ GitHub Secrets → Terraform → Kubernetes Secrets → Pods
- `POST /api/categories`      - Criar categoria
- `GET  /api/products`        - Listar produtos
- `POST /api/products`        - Criar produto
- `POST /api/webhook/payment` - Webhook Mercado Pago

### Protegidos (Requer JWT)
- `POST /api/orders`             - Criar pedido
- `GET  /api/orders`             - Listar pedidos
- `PUT  /api/orders/{id}/status` - Atualizar status

## Dependências

| Dependência | Descrição |
|-------------|-----------|
| tech-challenge-infra | EKS Cluster e ECR (via remote state) |
| tech-challenge-rds | PostgreSQL (via remote state) |
| Terraform >= 1.10.0 | Ferramenta de IaC |
| Java 17 | Runtime da aplicação |
| Maven | Build da aplicação |

## Secrets Necessários (GitHub)

- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_SESSION_TOKEN` (obrigatório para AWS Academy Learner Lab)
- `DB_USERNAME` - Usuário do PostgreSQL
- `DB_PASSWORD` - Senha do PostgreSQL

### Teste a API
`mvn test`
`mvn clean verify`

#### Validar cobertura de testes

`http://localhost:8888/target/site/jacoco/index.html`