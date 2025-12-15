# 🍔 Tech Challenge - Food Service API

[![Java Version](https://img.shields.io/badge/Java-17-%23ED8B00?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-%236DB33F?logo=spring)](https://spring.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14.9-%23316192?logo=postgresql)](https://www.postgresql.org/)
[![AWS EKS](https://img.shields.io/badge/AWS-EKS-%23FF9900?logo=amazon-aws)](https://aws.amazon.com/eks/)
[![Terraform](https://img.shields.io/badge/Terraform-1.5.0-%23623CE4?logo=terraform)](https://www.terraform.io/)

Aplicação Spring Boot com **Clean Architecture** rodando em **AWS EKS** com deploy automático via **GitHub Actions** e **Terraform**.
---

## 🍔 API Endpoints

### Públicos (Sem autenticação)
- `GET  /api/health`          - Status da aplicação
- `GET  /api/categories`      - Listar categorias
- `POST /api/categories`      - Criar categoria
- `GET  /api/products`        - Listar produtos
- `POST /api/products`        - Criar produto
- `POST /api/webhook/payment` - Webhook Mercado Pago

### Protegidos (Requer JWT)
- `POST /api/orders`             - Criar pedido
- `GET  /api/orders`             - Listar pedidos
- `PUT  /api/orders/{id}/status` - Atualizar status

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem principal
- **Spring Boot 3.3.5** - Framework web
- **Spring Data JPA** - Persistência
- **Spring Security OAuth2** - Autenticação JWT
- **PostgreSQL 14.9** - Banco de dados
- **Maven** - Gerenciamento de dependências

### Teste a API
`mvn test`
`mvn clean verify`

Validar cobertura de testes
`http://localhost:8888/target/site/jacoco/index.html`

---

## 📄 Licença

MIT License - veja [LICENSE](../LICENSE.md) para detalhes.

**Desenvolvido com ❤️ pela equipe FIAP Tech Challenge**