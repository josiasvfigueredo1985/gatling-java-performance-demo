#!/bin/bash
# Script para limpar o cache do Maven e forçar o download de dependências

echo "Limpando cache do Maven..."

# Remover o diretório de repositório local do Maven
rm -rf ~/.m2/repository/org/apache/maven/plugins/maven-compiler-plugin/3.14.1/
rm -rf ./.m2/repository/org/apache/maven/plugins/maven-compiler-plugin/3.14.1/

echo "Cache limpo. Agora execute o teste novamente."