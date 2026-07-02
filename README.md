# Projeto CN 2026 Grupo 16 #

Este é o repositório de src code. Para verificar o repositório de scripts, consultar o link presente no relatório da fase 10.

## Workflows ##

Sempre que há uma mudança num dos serviços dos programa empurrada para o repositório, o GitActions corre os testes referentes ao mesmo serviço (se aplicável). Caso os testes passem, a nova imagem docker deste serviço é compilada e empurrada para o artifact registry. Caso algum teste falhe, a imagem antiga mantém-se.

## DataBase ##

Foi implementado uma database em Postgres. Para tal foi criado um container com a base de dados e um script de python para importar os dados dos datasets para a base de dados.

## Auth-Forum Service ##

Serviço implementado pela Juliana. Cuida dos serviços de registro, login (apenas mock) e eliminação de contas. É também repsonsável pela gestão dos fóruns. Comunica por GRPC com User-service. Originalmente era o serviço com o secrets manager configurado, que entretanto teve de ser retirado por motivos vários. Detalhes da implementação disponíveis no relatório. Implementa log tracking através do Prometheus e o Grafana.

## Location Service ##

Desenvolvido pelo André. Trata de todos os métodos relativos a informação sobre os países da base de dados e as respetivas métricas. Funciona como server GRPC para User-service retirar informação. Uma vez que os dados de localização são "estáticos" (não possui métdos POST, PUT, PATCH ou DELETE), este implementa um serviço de cache-aside.

## Search Service ##

Semi Deprecado. Serve de server GRPC para um método de User service.

## User Service ##

Serviço desenvolvido pelo Pedro. Contém os métodos de pesquisa e customização de perfis de utilizadores. Funciona como servidor GRPC para auth-forum retirar informação sobre utilizadores, e utiliza o server do Location Service para retirar informações sobre localizações. Utiliza também o servidor de Search Service para retirar informações sobre métricas.

## Pasta General Notes ##

Contém uma lista de comandos uteis para manipular e explorar a aplicação

## Notas ##

Todas os serviços devem ter um ficheiro .env dentro das respetivas pastas, no formato de .env.example
Ports definidos nos env devem ser os mesmo dos port definidos nos manifests/configmaps do repositório de scripts.
