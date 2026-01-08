# Tech Challenge - Orders

Repositório responsável pelo microserviço de pedidos.

## O que este repositório faz

- **API de Pedidos** - CRUD de pedidos
- **API de Produtos** - CRUD de produtos
- **API de Categorias** - CRUD de categorias
- **Deployment K8s** - Deploy no EKS via Terraform

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
- `DB_USERNAME` - Usuário do PostgreSQL
- `DB_PASSWORD` - Senha do PostgreSQL
