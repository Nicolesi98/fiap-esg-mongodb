# language: pt
Funcionalidade: Gerenciamento de Empresas
  Como usuário autenticado da plataforma ESG
  Quero gerenciar o cadastro de empresas
  Para manter atualizado o registro de organizações participantes do programa

  Contexto:
    Dado que estou autenticado com usuário "admin" e senha "admin123"

  # ====================== CENÁRIOS POSITIVOS (caminho feliz) ======================

  Cenário: Criar uma nova empresa com dados válidos
    Quando eu envio uma requisição POST para "/empresas" com o corpo:
      """
      {
        "nome": "Empresa Verde Ltda",
        "cnpj": "11.222.333/0001-44",
        "setor": "Energia Renovável",
        "statusAdesaoEsg": "ATIVO"
      }
      """
    Então o status da resposta deve ser 201
    E o campo "nome" da resposta deve ser "Empresa Verde Ltda"
    E o campo "cnpj" da resposta deve ser "11.222.333/0001-44"
    E o cabeçalho "Location" deve conter "/empresas/"
    E o corpo da resposta deve estar de acordo com o JSON Schema "empresa-schema.json"

  Cenário: Listar todas as empresas cadastradas
    Dado que existe ao menos uma empresa cadastrada
    Quando eu envio uma requisição GET para "/empresas"
    Então o status da resposta deve ser 200
    E a resposta deve ser uma lista
    E o corpo da resposta deve estar de acordo com o JSON Schema "empresa-lista-schema.json"

  Cenário: Buscar empresa existente por ID
    Dado que existe uma empresa cadastrada com nome "EcoTech Solutions"
    Quando eu envio uma requisição GET para "/empresas/{id}"
    Então o status da resposta deve ser 200
    E o campo "nome" da resposta deve ser "EcoTech Solutions"
    E o corpo da resposta deve estar de acordo com o JSON Schema "empresa-schema.json"

  Cenário: Atualizar dados de uma empresa existente
    Dado que existe uma empresa cadastrada com nome "Empresa Antiga"
    Quando eu envio uma requisição PUT para "/empresas/{id}" com o corpo:
      """
      {
        "nome": "Empresa Atualizada",
        "cnpj": "99.888.777/0001-66",
        "setor": "Indústria"
      }
      """
    Então o status da resposta deve ser 200
    E o campo "nome" da resposta deve ser "Empresa Atualizada"

  Cenário: Excluir uma empresa existente
    Dado que existe uma empresa cadastrada com nome "Empresa Para Excluir"
    Quando eu envio uma requisição DELETE para "/empresas/{id}"
    Então o status da resposta deve ser 204

  # ====================== CENÁRIOS NEGATIVOS (falhas) ======================

  Cenário: Tentar criar empresa sem o campo obrigatório "nome"
    Quando eu envio uma requisição POST para "/empresas" com o corpo:
      """
      {
        "cnpj": "11.222.333/0001-44"
      }
      """
    Então o status da resposta deve ser 400
    E o campo "message" da resposta deve ser "Dados invalidos."
    E o campo de erro "fields.nome" deve estar presente na resposta

  Cenário: Tentar criar empresa com CNPJ excedendo o tamanho máximo
    Quando eu envio uma requisição POST para "/empresas" com o corpo:
      """
      {
        "nome": "Empresa X",
        "cnpj": "00.000.000/0000-00.INVALIDO"
      }
      """
    Então o status da resposta deve ser 400
    E o campo "message" da resposta deve ser "Dados invalidos."

  Cenário: Buscar empresa com ID inexistente
    Quando eu envio uma requisição GET para "/empresas/id-que-nao-existe-000"
    Então o status da resposta deve ser 404
    E o campo "message" da resposta deve conter "Empresa nao encontrada"

  Cenário: Acessar endpoint sem autenticação
    Dado que não estou autenticado
    Quando eu envio uma requisição GET para "/empresas"
    Então o status da resposta deve ser 401
