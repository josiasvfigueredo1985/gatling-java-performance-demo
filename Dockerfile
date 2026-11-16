FROM maven:3.9-eclipse-temurin-26

# Definir variáveis de ambiente
ENV MAVEN_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC --add-opens=java.base/java.lang=ALL-UNNAMED"
ENV GATLING_HOME="/app"

WORKDIR /app

# Copiar settings.xml primeiro para garantir configuração correta
COPY settings.xml /root/.m2/settings.xml

# Copiar apenas o pom.xml primeiro para resolver dependências
COPY pom.xml ./

# Resolver dependências com configurações explícitas
RUN mvn dependency:go-offline -B -s /root/.m2/settings.xml

# Copiar o resto dos arquivos
COPY src ./src
COPY mvnw mvnw.cmd ./
COPY .mvn ./.mvn

# Criar diretórios necessários com permissões adequadas
RUN mkdir -p /app/results && \
    chmod -R 777 /app/results

# Criar um arquivo de configuração Gatling mínimo
RUN mkdir -p /root/.gatling && \
    echo "gatling.core.outputDirectoryBaseName = \"\"" > /root/.gatling/gatling.conf

# Comando padrão
ENTRYPOINT ["mvn"]
CMD ["gatling:test", "-s", "/root/.m2/settings.xml", "-Dgatling.simulationClass=example.LoadTest", "-Djackson.version=2.16.0"]