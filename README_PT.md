# Testes de Performance com Gatling

*Leia em outros idiomas: [English](README.md), [Português](README_PT.md)*

Este projeto contém testes de performance utilizando Gatling para validar o comportamento de APIs sob diferentes condições de carga.

## Estrutura do Projeto

O projeto segue uma arquitetura modular com clara separação de responsabilidades:

```
├── src/test/java/example/
│   ├── PerformanceBase.java     # Classe base com configurações comuns
│   ├── TestUtils.java           # Métodos utilitários genéricos (não-Gatling)
│   ├── BaseUtils.java           # Utilitários específicos do Gatling e cenários
│   ├── LoadTest.java            # Teste de carga
│   ├── StressTest.java          # Teste de estresse
│   ├── SpikeTest.java           # Teste de pico
│   ├── EnduranceTest.java       # Teste de resistência
│   ├── ScalabilityTest.java     # Teste de escalabilidade
│   └── VolumeTest.java          # Teste de volume
│
├── src/test/resources/
│   ├── gatling.conf             # Configuração do Gatling
│   ├── logback-test.xml         # Configuração de logging
│   ├── products.csv             # Dados de teste para produtos
│   └── performance-config.json  # Configurações de parâmetros de teste
│
├── results/                     # Diretório de resultados organizado por tipo de teste
│   ├── load/                    # Resultados de teste de carga
│   ├── stress/                  # Resultados de teste de estresse
│   └── ...                      # Outros resultados
│
├── run-tests.sh / run-tests.bat     # Scripts para executar testes com Maven
├── run-docker.sh / run-docker.bat   # Scripts para executar testes com Docker
├── clean.sh / clean.bat             # Scripts para limpar o ambiente
└── .gitignore                       # Arquivo de exclusão do Git para arquivos desnecessários
```

Para uma explicação detalhada da arquitetura do projeto e separação de responsabilidades, veja [README_STRUCTURE_PT.md](README_STRUCTURE_PT.md).

## Configuração Inicial

### Pré-requisitos

- Java 25+ (JDK)
- Maven 3.6+ (opcional, o projeto inclui Maven Wrapper)
- Docker (opcional, para execução em contêiner)
- Git (para clonar o repositório)

### Passo 1: Clonar o Repositório

```bash
git clone https://github.com/josiasvfigueredo1985/gatling-java-performance-demo.git
cd gatling-java-performance-demo
```

### Passo 2: Usar os Scripts Incluídos

O projeto inclui os seguintes arquivos de script no diretório raiz:

#### Para Linux/macOS:

- **run-tests.sh**: Executa testes com o Maven Wrapper e requer um JDK 25 configurado localmente. Exemplo: `./run-tests.sh LoadTest load --quick`.
- **run-docker.sh**: Executa testes em contêiner e requer Docker em execução e o comando `docker-compose` disponível. Exemplo: `./run-docker.sh LoadTest load --quick --rebuild`.
- **clean.sh**: Limpa resultados ou artefatos conforme a opção informada. Exemplo: `./clean.sh --results` solicita confirmação antes de excluir os resultados.

Execute os comandos no diretório raiz do projeto usando Bash. Nos scripts de teste, a classe (`LoadTest`) e a configuração (`load`) são obrigatórias; `--quick` reduz a duração e `--rebuild` reconstrói a imagem Docker.

#### Para Windows (PowerShell):

- **run-tests.bat**: Executa testes com o Maven Wrapper e requer um JDK 25 configurado localmente. Exemplo: `.\run-tests.bat LoadTest load --quick`.
- **run-docker.bat**: Executa testes em contêiner e requer Docker em execução e o comando `docker-compose` disponível. Exemplo: `.\run-docker.bat LoadTest load --quick --rebuild`.
- **clean.bat**: Limpa resultados ou artefatos conforme a opção informada. Exemplo: `.\clean.bat --results` solicita confirmação antes de excluir os resultados.

Execute os comandos no diretório raiz do projeto usando PowerShell com o prefixo `.\`. Nos scripts de teste, a classe (`LoadTest`) e a configuração (`load`) são obrigatórias; `--quick` reduz a duração e `--rebuild` reconstrói a imagem Docker.

### Passo 3: Configurar Scripts e JDK

#### Linux/macOS

```bash
chmod +x mvnw run-tests.sh run-docker.sh clean.sh
```

Para executar testes localmente com Maven, configure `JAVA_HOME` com o diretório do JDK 25, sem incluir `/bin`.

No macOS, com o JDK 25 instalado:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 25)
```

No Linux, substitua o caminho abaixo pelo diretório real do seu JDK 25:

```bash
export JAVA_HOME="/caminho/para/jdk-25"
```

Em ambos os sistemas, configure o `PATH` e confirme que o Maven usa Java 25:

```bash
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -version
```

Para persistir a configuração, adicione as linhas `export` correspondentes ao arquivo de inicialização do seu shell (por exemplo, `~/.bashrc` ou `~/.zshrc`). A execução via Docker não exige um JDK local.

#### Windows (PowerShell)

Não é necessário executar `chmod`. Para executar testes localmente com Maven, substitua o caminho abaixo pelo diretório do JDK 25 instalado, sem incluir `\bin`. Configure o ambiente do terminal atual e confirme que o Maven usa Java 25:

```powershell
$env:JAVA_HOME = "C:\caminho\para\jdk-25"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd -version
```

Salve `JAVA_HOME` para sua conta de usuário do Windows:

```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", $env:JAVA_HOME, "User")
```

O terminal atual já está pronto para uso. Reinicie o VS Code (ou seu aplicativo de terminal) para que os próximos terminais herdem a configuração salva. Se o Maven informar `JAVA_HOME not found`, configure a variável conforme mostrado acima; ter apenas `java` no `PATH` não é suficiente para este wrapper. A execução via Docker não exige um JDK local.

##### Corrigindo `JAVA_HOME not found` em um terminal já aberto

Salvar `JAVA_HOME` não atualiza os terminais que já estão abertos. Se você salvou a variável com o comando acima, recarregue-a no **mesmo terminal PowerShell** em que o teste falhou:

```powershell
$env:JAVA_HOME = [Environment]::GetEnvironmentVariable("JAVA_HOME", "User")
```

Verifique se `$env:JAVA_HOME` contém o diretório do seu JDK 25. Se estiver vazio, conclua a configuração acima antes de continuar. Depois, atualize o `PATH` do terminal atual e verifique o Maven:

```powershell
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd -version
```

Confirme que o Maven informa Java 25 antes de tentar novamente:

```powershell
.\run-tests.bat LoadTest load --quick
```

Reinicie o VS Code depois para que os próximos terminais herdem a configuração salva. Atualmente, o script exibe `Test completed` mesmo quando o Maven falha; essa mensagem, sozinha, não significa que os testes foram executados com sucesso.

### Passo 4: Criar Maven Wrapper (se não estiver presente)

Se o Maven Wrapper não estiver incluído no repositório, você pode criá-lo com:

```bash
mvn wrapper:wrapper
```

### Passo 5: Criar Arquivo de Dados de Teste

Crie um arquivo chamado `products.csv` no diretório `src/test/resources`:

```csv
productId,name,price,category
1,Smartphone,699.99,Electronics
2,Laptop,1299.99,Electronics
3,Headphones,149.99,Electronics
4,T-shirt,24.99,Clothing
5,Jeans,59.99,Clothing
6,Sneakers,89.99,Footwear
7,Watch,199.99,Accessories
8,Backpack,49.99,Accessories
9,Water Bottle,15.99,Sports
10,Yoga Mat,29.99,Sports
```

### Passo 6: Configurar a API Alvo

Por padrão, os testes estão configurados para serem executados contra uma API de demonstração em `https://api-ecomm.gatling.io`. Para alterar isso:

1. Você pode definir a URL base via propriedade do sistema ao executar os testes:
   ```bash
   ./run-tests.sh LoadTest load -DbaseUrl=https://sua-api-url.com
   ```

2. Ou modificar a constante `BASE_URL` em `PerformanceBase.java`:
   ```java
   protected static final String BASE_URL = System.getProperty("baseUrl", "https://sua-api-url.com");
   ```

### Passo 7: Configurar Autenticação (se necessário)

Se sua API requer autenticação:

1. Você pode definir a chave de API via propriedade do sistema:
   ```bash
   ./run-tests.sh LoadTest load -DapiKey=sua-chave-api
   ```

2. Ou modificar a constante `API_KEY` em `PerformanceBase.java`:
   ```java
   protected static final String API_KEY = System.getProperty("apiKey", "sua-chave-api");
   ```

3. Para outros métodos de autenticação, modifique o `httpProtocol` em `PerformanceBase.java`:
   ```java
   protected static final HttpProtocolBuilder httpProtocol = http
           .baseUrl(BASE_URL)
           // ... outros cabeçalhos
           .header("Authorization", "Bearer " + System.getProperty("bearerToken", "seu-token"))
           // ... outras configurações
   ```

### Passo 8: Personalizar Parâmetros de Teste

Para personalizar os parâmetros de teste:

1. Abra `src/test/resources/performance-config.json`
2. Modifique os parâmetros para cada tipo de teste de acordo com suas necessidades:

```json
{
  "load": {
    "users": 50,           // Número de usuários virtuais
    "rampDuration": 30,    // Duração da rampa em segundos
    "testDuration": 60,    // Duração total do teste em segundos
    "constantUsers": 20    // Número de usuários constantes
  },
  // ... outras configurações de teste
}
```

### Passo 9: Verificar Configuração

Execute um teste rápido para verificar se tudo está configurado corretamente:

#### No Linux/macOS:
```bash
./run-tests.sh LoadTest load --quick
```

#### No Windows (PowerShell):
```powershell
.\run-tests.bat LoadTest load --quick
```

Execute este comando no diretório do projeto. O PowerShell exige o prefixo `.\` para executar scripts no diretório atual; caso contrário, informa que o comando não foi reconhecido. Os argumentos `LoadTest` (classe de teste) e `load` (configuração) são obrigatórios. A opção `--quick` é opcional e reduz a duração do teste.

Se o teste for executado com sucesso, você verá uma mensagem indicando onde encontrar os resultados.

## Executando os Testes

### No Linux/macOS

```bash
# Usando Maven
./run-tests.sh [TipoTeste] [ConfigTeste] [--quick]

# Usando Docker
./run-docker.sh [TipoTeste] [ConfigTeste] [--quick] [--rebuild]

# Limpando
./clean.sh [--all|--results|--maven|--docker]
```

### No Windows (PowerShell)

```powershell
# Usando Maven
.\run-tests.bat <TipoTeste> <ConfigTeste> [--quick]

# Usando Docker
.\run-docker.bat <TipoTeste> <ConfigTeste> [--quick] [--rebuild]

# Limpando
.\clean.bat [--all|--results|--maven|--docker]
```

Substitua os marcadores entre sinais de menor e maior por valores reais (por exemplo, `LoadTest load`); os colchetes indicam argumentos opcionais. Não digite esses delimitadores.

### Exemplos

```bash
# Teste de carga completo
./run-tests.sh LoadTest load
# ou no Windows
.\run-tests.bat LoadTest load

# Teste de pico com configuração de estresse
./run-tests.sh SpikeTest stress
# ou no Windows
.\run-tests.bat SpikeTest stress

# Teste rápido para verificar configuração
./run-tests.sh LoadTest load --quick
# ou no Windows
.\run-tests.bat LoadTest load --quick

# Forçar reconstrução da imagem Docker
./run-docker.sh LoadTest load --rebuild
# ou no Windows
.\run-docker.bat LoadTest load --rebuild

# Teste com URL de API e chave de API personalizadas
./run-tests.sh LoadTest load -DbaseUrl=https://api.example.com -DapiKey=minha-chave-secreta
```

### GitHub Actions (execução manual)

O workflow em [gatling.yml](.github/workflows/gatling.yml) só executa quando solicitado manualmente; pushes e pull requests não iniciam testes de carga.

1. Faça commit e push do workflow para a branch padrão do repositório (`main`).
2. No GitHub, abra **Actions → Gatling performance tests → Run workflow**.
3. Informe a URL base HTTP/HTTPS de um serviço que você tem autorização para testar sob carga. Ele deve estar acessível pelo runner hospedado no GitHub; `localhost` se refere ao runner, não ao seu computador. Não inclua credenciais, parâmetros de consulta ou fragmentos na URL.
4. Selecione a simulação e a configuração. O modo rápido vem habilitado e passa as mesmas substituições de duração de `--quick`; desabilite-o para usar as durações configuradas.
5. Inicie o workflow. O Java 25 e o cache de dependências Maven são configurados automaticamente.

Erros do Maven e falhas nas asserções do Gatling fazem o workflow falhar. Os relatórios e logs de simulação disponíveis são enviados mesmo em caso de falha como um artefato `gatling-results-...`, mantido por 7 dias. Baixe e extraia o artefato e abra o `index.html` da pasta da simulação gerada. Se a execução falhar antes de criar relatórios, consulte os logs do Actions.

A execução também publica o relatório HTML no GitHub Pages. Após o deploy, **Print report URL** exibe a URL para o navegador e adiciona um link clicável **Open report** ao resumo do job **publish-report**. O site mostra o último relatório publicado e é substituído na próxima publicação. Os relatórios são publicados mesmo quando as asserções do Gatling falham, desde que um relatório HTML tenha sido gerado; uma execução sem relatório HTML mantém o site existente e emite um aviso.

Habilite o Pages uma vez em **Settings → Pages → Build and deployment → Source → GitHub Actions**. Execute o workflow a partir de `main` e verifique se o ambiente `github-pages` permite deploy dessa branch.

A etapa **Print download URL** continua exibindo a URL de download do artefato e adicionando um link **Download report** ao resumo do job **gatling** para consulta offline. Se nenhum artefato for enviado, ela mostra um aviso.

O relatório do Pages é público; não teste endpoints que exponham dados sensíveis no site publicado, nos logs ou nos artefatos.

## Tipos de Teste Disponíveis

| Tipo de Teste | Classe | Descrição |
|-----------|-------|-------------|
| Carga | `LoadTest` | Teste de carga padrão para verificar o comportamento do sistema sob carga esperada |
| Estresse | `StressTest` | Teste de estresse para verificar o comportamento do sistema sob carga extrema |
| Pico | `SpikeTest` | Teste de pico para verificar como o sistema lida com picos repentinos de tráfego |
| Resistência | `EnduranceTest` | Teste de resistência para verificar a estabilidade do sistema por longos períodos |
| Escalabilidade | `ScalabilityTest` | Teste de escalabilidade para verificar como o sistema se comporta com aumento gradual de carga |
| Volume | `VolumeTest` | Teste de volume para verificar como o sistema lida com grandes volumes de dados |

## Configurações de Teste Disponíveis

O arquivo `src/test/resources/performance-config.json` contém diferentes configurações de teste:

| Configuração | Descrição | Parâmetros |
|--------------|-----------|------------|
| `load`       | Teste de carga padrão | users=50, rampDuration=30, testDuration=60, constantUsers=20 |
| `stress`     | Teste de estresse com alta carga | users=100, rampDuration=60, testDuration=120, constantUsers=50 |
| `spike`      | Teste de pico com rampa rápida | users=150, rampDuration=15, testDuration=90, constantUsers=30 |
| `endurance`  | Teste de resistência de longa duração | users=30, rampDuration=30, testDuration=300, constantUsers=30 |
| `scalability`| Teste de escalabilidade incremental | users=200, testDuration=180, incrementUsers=20, incrementInterval=30, incrementSteps=10 |
| `volume`     | Teste de volume focado em dados | users=50, testDuration=120, constantUsers=50, dataSize="large", requestsPerSecond=100 |

## Organização dos Resultados

Os resultados são organizados em diretórios baseados na configuração do teste:

```
results/
├── load/    # Resultados de teste de carga
├── stress/  # Resultados de teste de estresse
└── ...      # Outros resultados
```

Benefícios desta abordagem:
- Estrutura de diretório simples e limpa
- Cada configuração de teste tem seu próprio diretório dedicado
- Os resultados são preservados entre execuções de teste para comparação
- Múltiplos tipos de teste podem ser executados e comparados
- Fácil de encontrar resultados para uma configuração específica de teste

Para visualizar os resultados, abra o arquivo `index.html` no respectivo diretório usando um navegador web.

### Gerenciando Resultados de Teste

Por padrão, os resultados dos testes são preservados entre execuções para permitir comparação. Cada execução de teste cria uma nova pasta com timestamp dentro do diretório de configuração. Por exemplo:

```
results/load/
├── 2025-10-07_15-30-45/  # Execução de teste de 7 de outubro de 2025 às 15:30:45
├── 2025-10-08_09-15-22/  # Execução de teste de 8 de outubro de 2025 às 09:15:22
└── ...
```

Se você precisar limpar resultados antigos, pode usar o script de limpeza:

```bash
# Limpar todos os resultados
./clean.sh --results
# ou no Windows
.\clean.bat --results
```

## Controle de Versão e .gitignore

O projeto inclui um arquivo `.gitignore` que impede que arquivos desnecessários sejam adicionados ao repositório. Por padrão, os seguintes arquivos e diretórios são excluídos:

- **Resultados de testes**: O diretório `results/` é excluído para evitar sobrecarregar o repositório com resultados de testes.
- **Arquivos compilados**: Todas as classes Java compiladas e artefatos de build são excluídos.
- **Arquivos específicos de IDE**: Arquivos de configuração para IDEs como IntelliJ IDEA, Eclipse e VS Code são excluídos.
- **Arquivos de build do Maven**: O diretório `target/` e outros arquivos gerados pelo Maven são excluídos.
- **Arquivos do sistema**: Arquivos específicos do sistema operacional como `.DS_Store` e `Thumbs.db` são excluídos.
- **Arquivos temporários**: Vários arquivos temporários e logs são excluídos.

Se você precisar personalizar o arquivo `.gitignore`:

1. Abra o arquivo `.gitignore` no diretório raiz
2. Adicione ou remova entradas conforme necessário
3. Salve o arquivo e faça commit das alterações

Por exemplo, se você quiser incluir um diretório específico de resultados para benchmarking:

```
# Excluir todos os resultados
results/

# Mas incluir resultados de benchmark
!results/benchmark/
```

## Personalizando Testes

### Modificando Testes Existentes

Para modificar um teste existente:

1. Abra o arquivo de teste correspondente (por exemplo, `src/test/java/example/LoadTest.java`)
2. Modifique o perfil de injeção ou asserções conforme necessário
3. Salve o arquivo e execute o teste

### Criando Novos Testes

Para criar um novo teste:

1. Crie um novo arquivo Java no diretório `src/test/java/example`
2. Estenda a classe `PerformanceBase`
3. Use cenários da classe `BaseUtils`
4. Defina o perfil de injeção e asserções
5. Execute o teste usando os scripts

Exemplo:

```java
package example;

import static io.gatling.javaapi.core.CoreDsl.*;
import io.gatling.javaapi.core.*;

import java.time.Duration;

public class MeuTestePersonalizado extends PerformanceBase {

    {
        setUp(
                BaseUtils.browsingScenario().injectOpen(
                        rampUsers(USERS).during(Duration.ofSeconds(RAMP_DURATION))
                ).protocols(httpProtocol),

                BaseUtils.purchaseScenario().injectOpen(
                        rampUsers(USERS / 5).during(Duration.ofSeconds(RAMP_DURATION))
                ).protocols(httpProtocol)
        )
                .assertions(responseTimeAssertion)
                .maxDuration(Duration.ofSeconds(TEST_DURATION));
    }
}
```

### Adicionando Novos Cenários

Para adicionar um novo cenário:

1. Abra `src/test/java/example/BaseUtils.java`
2. Adicione novos métodos para interações com a API
3. Crie um método que combine essas interações em uma jornada de usuário
4. Adicione um método que retorne um ScenarioBuilder para o novo cenário
5. Use o cenário em seus testes

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

## Solução de Problemas

### Problemas Comuns

1. **Problemas com Versão do Java**:
   - Erro: `Unsupported class file major version XX`
   - Solução: Certifique-se de estar usando Java 25 ou posterior. Verifique com `java -version`

2. **Problemas de Permissão**:
   - Erro: `Permission denied` ao executar scripts
   - Solução: Torne os scripts executáveis com `chmod +x *.sh`

3. **Problemas com Docker**:
   - Erro: `Cannot connect to the Docker daemon`
   - Solução: Certifique-se de que o Docker está em execução com `docker info`

4. **Problemas com Maven**:
   - Erro: `Could not resolve dependencies`
   - Solução: Execute `./mvnw dependency:purge-local-repository` para limpar e baixar novamente as dependências

5. **Resultados Não Gerados**:
   - Problema: Sem resultados no diretório `results`
   - Solução: Verifique se há erros na saída do console e certifique-se de que o teste foi concluído

6. **Arquivos de Script Ausentes**:
   - Problema: Arquivos de script mencionados na documentação estão ausentes
   - Solução: Crie os arquivos de script conforme descrito na seção "Criar Scripts Necessários"

7. **Problemas com Dependências do Jackson**:
   - Erro: `java.lang.ClassNotFoundException: com.fasterxml.jackson.core.StreamReadConstraints`
   - Solução: Certifique-se de estar usando a versão 2.16.0 ou superior do Jackson no seu pom.xml

8. **Erros de Opções Desconhecidas**:
   - Erro: `Warning: Unknown option -rsf` ou `Unknown argument '/app/src/test/resources'`
   - Solução: Remova essas opções dos argumentos de linha de comando e use a configuração no gatling.conf

### Obtendo Ajuda

Se você encontrar problemas não abordados aqui:

1. Consulte a documentação do Gatling: https://gatling.io/docs/
2. Procure por problemas semelhantes no rastreador de problemas do projeto
3. Crie um novo problema com informações detalhadas sobre o problema

## Entendendo os Testes de Performance

### Tipos de Testes de Performance e Seus Propósitos

#### Teste de Carga
O teste de carga determina como o sistema se comporta sob condições de carga normal e de pico esperadas. Ajuda a identificar:
- Tempo de resposta em condições normais
- Capacidade do sistema sob carga esperada
- Se o sistema atende aos requisitos de desempenho

**Como interpretar resultados**: Procure por tempos de resposta consistentes dentro de limites aceitáveis. O sistema deve lidar com a carga esperada sem degradação significativa no desempenho.

#### Teste de Estresse
O teste de estresse pressiona o sistema além das condições de carga normal ou de pico para identificar pontos de ruptura. Ajuda a determinar:
- Capacidade máxima do sistema
- Como o sistema falha sob condições extremas
- Comportamento de recuperação após falha

**Como interpretar resultados**: Identifique o ponto onde os tempos de resposta começam a aumentar drasticamente ou os erros começam a ocorrer. Isso indica o ponto de ruptura do sistema.

#### Teste de Pico
O teste de pico avalia a resposta do sistema a aumentos súbitos e dramáticos de carga. Ajuda a entender:
- Como o sistema lida com picos repentinos de tráfego
- Tempo de recuperação após um pico
- Possíveis falhas sob mudanças rápidas de carga

**Como interpretar resultados**: Observe as taxas de erro durante o pico e o tempo de recuperação após o pico. Um sistema robusto deve se recuperar rapidamente após o pico diminuir.

#### Teste de Resistência
O teste de resistência (também chamado de teste de imersão) verifica a estabilidade do sistema por um período prolongado. Ajuda a identificar:
- Vazamentos de memória
- Problemas de esgotamento de recursos
- Degradação de desempenho ao longo do tempo

**Como interpretar resultados**: Monitore os tempos de resposta e taxas de erro durante toda a duração do teste. Eles devem permanecer estáveis sem degradação gradual.

#### Teste de Escalabilidade
O teste de escalabilidade determina quão efetivamente o sistema escala com o aumento de carga. Ajuda a entender:
- Como o desempenho muda conforme a carga aumenta
- Se o escalonamento é linear ou não-linear
- Padrões de utilização de recursos durante o escalonamento

**Como interpretar resultados**: Plote os tempos de resposta em relação à carga de usuários para identificar padrões de escalonamento. O escalonamento linear é ideal, enquanto a degradação exponencial indica problemas de escalabilidade.

#### Teste de Volume
O teste de volume concentra-se em como o sistema lida com grandes quantidades de dados. Ajuda a verificar:
- Desempenho do banco de dados com grandes conjuntos de dados
- Capacidades de processamento de arquivos
- Eficiência na transferência de dados

**Como interpretar resultados**: Monitore a taxa de transferência e tempos de resposta ao processar grandes volumes de dados. Observe padrões de uso de memória e possíveis gargalos.

### Métricas-Chave de Performance

Ao analisar resultados de testes de performance, concentre-se nestas métricas-chave:

1. **Tempo de Resposta**:
   - **Média**: O tempo médio de resposta em todas as requisições
   - **Percentis**: 50º (mediana), 95º e 99º percentis são particularmente importantes
   - **Desvio Padrão**: Indica consistência dos tempos de resposta

2. **Taxa de Transferência**:
   - **Requisições por segundo**: O número de requisições que o sistema pode lidar por segundo
   - **Transações por segundo**: O número de transações de negócio concluídas por segundo

3. **Taxa de Erro**:
   - **Porcentagem de requisições com falha**: Deve ser mínima em condições normais
   - **Tipos de erros**: Identifique padrões nos tipos de erro

4. **Utilização de Recursos**:
   - **Uso de CPU**: Não deve exceder consistentemente 70-80%
   - **Uso de memória**: Deve ser estável sem crescimento contínuo
   - **E/S de rede**: Não deve ser um gargalo
   - **E/S de disco**: Não deve ser um gargalo

### Analisando Relatórios do Gatling

O Gatling gera relatórios HTML abrangentes após cada execução de teste. Veja como interpretar as seções principais:

1. **Informações Globais**:
   - Visão geral de todas as requisições, incluindo contagens de sucesso/falha
   - Tempos de resposta mínimo, máximo, médio e percentis
   - Desvio padrão dos tempos de resposta
   - Taxa de transferência média

2. **Distribuição de Tempo de Resposta**:
   - Divisão dos tempos de resposta em faixas
   - Porcentagem de requisições em cada faixa
   - Ajuda a identificar outliers e padrões gerais de desempenho

3. **Percentis de Tempo de Resposta ao Longo do Tempo**:
   - Mostra como os tempos de resposta evoluem durante o teste
   - Ajuda a identificar padrões de degradação de desempenho

4. **Requisições Por Segundo ao Longo do Tempo**:
   - Mostra a carga real gerada durante o teste
   - Ajuda a verificar se o perfil de carga pretendido foi alcançado

5. **Respostas Por Segundo ao Longo do Tempo**:
   - Mostra quantas respostas foram recebidas por segundo
   - Ajuda a identificar gargalos e pontos de saturação

6. **Usuários Ativos ao Longo do Tempo**:
   - Mostra o número de usuários concorrentes durante o teste
   - Ajuda a correlacionar carga de usuários com métricas de desempenho

## Visualizando Resultados

Após a execução, os relatórios são gerados no diretório `results` em uma pasta nomeada de acordo com a configuração do teste.

Para visualizar os resultados:
1. Navegue até o diretório de resultados: `cd results/load` (para resultados de teste de carga)
2. Abra o arquivo `index.html` em um navegador: `open index.html` (macOS) ou `start index.html` (Windows)