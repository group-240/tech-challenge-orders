package com.fiap.techchallenge;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Tech Challenge
 * <p>
 * Esta é a classe de inicialização da aplicação Spring Boot que implementa
 * um sistema de serviço de alimentação utilizando arquitetura hexagonal.
 * </p>
 * <p>
 * A aplicação segue os princípios da arquitetura hexagonal (ou ports and adapters),
 * separando claramente:
 * <ul>
 *   <li>Domínio: Entidades e regras de negócio centrais</li>
 *   <li>Aplicação: Casos de uso que orquestram o domínio</li>
 *   <li>Adaptadores: Componentes que conectam o mundo externo à aplicação</li>
 * </ul>
 * </p>
 * <p>
 * A documentação da API está disponível através do Swagger UI em:
 * <code>http://localhost:8888/api/swagger-ui.html</code>
 * </p>
 */
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Tech Challenge - Food Service API",
        version = "1.0.0",
        description = "API para sistema de serviço de alimentação desenvolvida com arquitetura hexagonal"
    )
)
public class TechChallengeApplication {

    /**
     * Método principal que inicia a aplicação Spring Boot
     *
     * @param args Argumentos de linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(TechChallengeApplication.class, args);
    }
}
