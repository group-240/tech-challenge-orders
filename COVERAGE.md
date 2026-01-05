# Relatório de Cobertura de Testes - JaCoCo

## Status Atual

- ✅ **Cobertura de Linhas**: 85% (Meta: 80%)
- ⚠️ **Cobertura de Branches**: 68% (Meta: 80%)

## Pacotes que Precisam de Melhorias

### 1. com.fiap.techchallenge.external.api - 0% branches
- Necessário adicionar testes para cobrir condicionais e branches nos controladores REST
- Focar em testar diferentes cenários de validação e respostas HTTP

### 2. com.fiap.techchallenge.adapters.gateway - 0% branches
- Adicionar testes para cobrir condicionais nos gateways de repositório
- Testar cenários de sucesso e falha nas operações de banco de dados

### 3. com.fiap.techchallenge.external.datasource.mercadopago - 66% branches
- Melhorar testes para cobrir todos os caminhos de execução
- Adicionar testes para cenários de erro e exceções

### 4. com.fiap.techchallenge.application.usecases - 69% branches
- Adicionar testes para cobrir todos os caminhos condicionais nos casos de uso
- Testar cenários edge cases e validações

## Como Executar os Testes com Cobertura

```bash
# Executar testes e gerar relatório de cobertura
mvn clean verify

# Visualizar relatório HTML
open target/site/jacoco/index.html
```

## Como o JaCoCo é Validado no CI/CD

O workflow do GitHub Actions (`maven.yml`) executa:

1. `mvn clean verify` - Executa testes e valida cobertura
2. Upload do relatório JaCoCo como artefato
3. Build falha se a cobertura mínima não for atingida

## Próximos Passos

1. Adicionar testes para branches não cobertas nos pacotes listados acima
2. Aumentar gradualmente o valor de `minimum` para branches no `pom.xml`
3. Meta: atingir 80% de cobertura de branches

## Exclusões do JaCoCo

As seguintes classes estão excluídas da cobertura:
- Classes de configuração (`**/config/**`)
- Classe principal (`TechChallengeApplication`)
- Entidades JPA (`**/external/datasource/entities/**`)
- DTOs (`**/external/api/dto/**`)
- Interfaces de repositório (`**/external/datasource/repositories/**`)
- Cliente API (`CustomerApiClient`)
