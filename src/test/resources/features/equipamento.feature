# language: pt
Funcionalidade: Gerenciamento de Equipamentos
  Como usuário autenticado da plataforma ESG
  Quero cadastrar e monitorar equipamentos industriais
  Para rastrear consumo energético e emissões por ativo

  Contexto:
    Dado que estou autenticado com usuário "admin" e senha "admin123"

  # ====================== CENÁRIOS POSITIVOS (caminho feliz) ======================

  Cenário: Criar um novo equipamento com dados válidos
    Quando eu envio uma requisição POST para "/equipamentos" com o corpo:
      """
      {
        "nome": "Compressor Industrial A1",
        "tipo": "COMPRESSOR",
        "categoria": "INDUSTRIAL",
        "status": "LIGADO",
        "limiteConsumoKwh": 150.00,
        "limiteOciosidadeMin": 60
      }
      """
    Então o status da resposta deve ser 201
    E o campo "nome" da resposta deve ser "Compressor Industrial A1"
    E o campo "status" da resposta deve ser "LIGADO"
    E o cabeçalho "Location" deve conter "/equipamentos/"
    E o corpo da resposta deve estar de acordo com o JSON Schema "equipamento-schema.json"

  Cenário: Listar equipamentos filtrando por empresa
    Dado que existe ao menos um equipamento cadastrado
    Quando eu envio uma requisição GET para "/equipamentos"
    Então o status da resposta deve ser 200
    E a resposta deve ser uma lista

  Cenário: Buscar equipamento por ID
    Dado que existe um equipamento cadastrado com nome "Ar Condicionado Central"
    Quando eu envio uma requisição GET para "/equipamentos/{id}"
    Então o status da resposta deve ser 200
    E o campo "nome" da resposta deve ser "Ar Condicionado Central"
    E o corpo da resposta deve estar de acordo com o JSON Schema "equipamento-schema.json"

  Cenário: Atualizar status de um equipamento
    Dado que existe um equipamento cadastrado com nome "Motor Elétrico B2"
    Quando eu envio uma requisição PUT para "/equipamentos/{id}" com o corpo:
      """
      {
        "nome": "Motor Elétrico B2",
        "status": "DESLIGADO",
        "limiteConsumoKwh": 200.00,
        "limiteOciosidadeMin": 30
      }
      """
    Então o status da resposta deve ser 200
    E o campo "status" da resposta deve ser "DESLIGADO"

  Cenário: Excluir um equipamento
    Dado que existe um equipamento cadastrado com nome "Equipamento Descartável"
    Quando eu envio uma requisição DELETE para "/equipamentos/{id}"
    Então o status da resposta deve ser 204

  # ====================== CENÁRIOS NEGATIVOS (falhas) ======================

  Cenário: Criar equipamento sem campos obrigatórios
    Quando eu envio uma requisição POST para "/equipamentos" com o corpo:
      """
      {
        "tipo": "COMPRESSOR"
      }
      """
    Então o status da resposta deve ser 400
    E o campo "message" da resposta deve ser "Dados invalidos."
    E o campo de erro "fields.nome" deve estar presente na resposta

  Cenário: Buscar equipamento com ID inexistente
    Quando eu envio uma requisição GET para "/equipamentos/id-inexistente-equipamento"
    Então o status da resposta deve ser 404
    E o campo "message" da resposta deve conter "Equipamento nao encontrado"
