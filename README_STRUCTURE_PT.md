# Estrutura do Projeto de Testes de Performance

*Leia em outros idiomas: [English](README_STRUCTURE.md), [Português](README_STRUCTURE_PT.md)*

Este documento descreve a estrutura do projeto de testes de performance com Gatling, explicando a organização dos arquivos e a separação de responsabilidades.

## Visão Geral da Arquitetura
O projeto segue uma arquitetura limpa com tipos padrão de testes de performance:

```
src/test/java/example/
├── PerformanceBase.java # Classe base com configurações comuns
├── TestUtils.java       # Métodos utilitários genéricos (não-Gatling)
├── BaseUtils.java       # Utilitários específicos do Gatling e cenários
├── LoadTest.java        # Teste de carga
├── StressTest.java      # Teste de estresse
├── SpikeTest.java       # Teste de pico
├── EnduranceTest.java   # Teste de resistência
├── ScalabilityTest.java # Teste de escalabilidade
└── VolumeTest.java      # Teste de volume
```

## Separação de Responsabilidades

### PerformanceBase.java
Esta classe é responsável por:
- Definir configurações comuns para todos os testes
- Carregar parâmetros de teste do arquivo de configuração
- Configurar o protocolo HTTP com cabeçalhos padrão
- Definir asserções comuns para validação de resultados

É a classe base que todos os testes específicos estendem e usa métodos de TestUtils e BaseUtils.

### TestUtils.java
Esta classe contém utilitários verdadeiramente genéricos sem dependências do Gatling:
- Funções de geração de dados aleatórios
- Carregamento de parâmetros de configuração
- Leitura e análise de arquivos
- Geração de payload JSON

Não contém nenhuma lógica específica do Gatling, ChainBuilder ou chamadas exec().

### BaseUtils.java
Esta classe contém utilitários específicos do Gatling:
- Métodos ChainBuilder (usando exec())
- Métodos de interação com API
- Definições de jornadas de usuário
- Construtores de cenários
- Definições de requisições HTTP
- Inicialização e manipulação de sessão

Usa TestUtils para operações genéricas, mas contém todo o código específico do Gatling.

### Classes de Teste Específicas
Cada classe de teste específica (LoadTest, StressTest, etc.) é responsável por:
- Definir o perfil de injeção de usuários específico para o tipo de teste
- Configurar asserções específicas para o tipo de teste
- Combinar os cenários apropriados para o objetivo do teste

## Fluxo de Execução
1. Um teste específico (ex: LoadTest) é iniciado
2. Ele herda configurações da classe PerformanceBase
3. Utiliza cenários de BaseUtils
4. BaseUtils usa utilitários genéricos de TestUtils quando necessário
5. O teste executa com o perfil de injeção definido e valida os resultados com as asserções configuradas

## Vantagens desta Arquitetura
1. **Clara Separação de Responsabilidades**: 
   - TestUtils: Utilitários independentes de framework
   - BaseUtils: Código específico do Gatling
   - PerformanceBase: Configuração comum de teste
   - Classes de teste: Implementações específicas de teste
2. **Manutenibilidade**: Alterações em cenários ou configurações são feitas em um único lugar
3. **Nomenclatura Padrão**: Apenas classes de teste têm o sufixo "Test"
4. **Facilidade de Extensão**: Novos testes podem ser adicionados facilmente estendendo PerformanceBase
5. **Reusabilidade**: Utilitários genéricos podem ser reutilizados em outros frameworks de teste

## Como Adicionar um Novo Teste
Para adicionar um novo teste:
1. Crie uma nova classe que estende PerformanceBase
2. Utilize os cenários existentes em BaseUtils ou crie novos se necessário
3. Defina o perfil de injeção de usuários específico para o seu teste
4. Configure as asserções apropriadas para validar os resultados

Exemplo:
```java
public class MeuNovoTeste extends PerformanceBase {
    {
        setUp(
            BaseUtils.browsingScenario().injectOpen(
                rampUsers(USERS).during(Duration.ofSeconds(RAMP_DURATION))
            ).protocols(httpProtocol)
        )
        .assertions(
            global().responseTime().mean().lt(1000)
        )
        .maxDuration(Duration.ofSeconds(TEST_DURATION));
    }
}
```

## Como Adicionar um Novo Cenário
Para adicionar um novo cenário:
1. Adicione novos métodos em BaseUtils.java para definir as interações com a API
2. Crie um método que combine essas interações em uma jornada de usuário completa
3. Adicione um método que retorne um ScenarioBuilder para o novo cenário
4. Use o novo cenário em seus testes

Exemplo:
```java
// Em BaseUtils.java
public static ChainBuilder searchProducts() {
    return exec(http("Search Products")
        .get("/api/v1/products/search?q=#{searchTerm}")
        .check(status().is(200))
        .check(jsonPath("$.products[*]").count().gt(0)));
}

public static ChainBuilder searchJourney() {
    return initializeSession()
        .exec(session -> session.set("searchTerm", "phone"))
        .exec(searchProducts())
        .pause(1, 2);
}

public static ScenarioBuilder searchScenario() {
    return scenario("Search Scenario")
        .exec(searchJourney());
}
```