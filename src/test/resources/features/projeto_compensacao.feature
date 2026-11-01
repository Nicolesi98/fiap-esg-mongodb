# language: pt
Funcionalidade: Gerenciamento de Projetos de Compensação de Carbono
  Como gestor de sustentabilidade ESG
  Quero cadastrar projetos de compensação de carbono
  Para rastrear as iniciativas de offset de emissões da empresa

  Contexto:
    Dado que estou autenticado com usuário "admin" e senha "admin123"

  # ====================== CENÁRIOS POSITIVOS (caminho feliz) ======================

  Cenário: Criar projeto de reflorestamento com dados válidos
    Quando eu envio uma requisição POST para "/projetos-compensacao" com o corpo:
      """
      {
        "empresaId": "660000000000000000000001",
        "nomeIniciativa": "Reflorestamento Mata Atlântica 2026",
        "tipoProjeto": "REFLORESTAMENTO_NATIVO",
        "status": "PLANEJADO",
        "metaTco2eCompensar": 500.00
      }
      """
    Então o status da resposta deve ser 201
    E o campo "nomeIniciativa" da resposta deve ser "Reflorestamento Mata Atlântica 2026"
    E o campo "tipoProjeto" da resposta deve ser "REFLORESTAMENTO_NATIVO"
    E o cabeçalho "Location" deve conter "/projetos-compensacao/"
    E o corpo da resposta deve estar de acordo com o JSON Schema "projeto-compensacao-schema.json"

  Cenário: Listar projetos de compensação
    Dado que existe ao menos um projeto de compensação cadastrado
    Quando eu envio uma requisição GET para "/projetos-compensacao"
    Então o status da resposta deve ser 200
    E a resposta deve ser uma lista

  # ====================== CENÁRIOS NEGATIVOS (falhas) ======================

  Cenário: Criar projeto sem campo obrigatório "empresaId"
    Quando eu envio uma requisição POST para "/projetos-compensacao" com o corpo:
      """
      {
        "nomeIniciativa": "Projeto Sem Empresa",
        "tipoProjeto": "ENERGIA_RENOVAVEL_IREC",
        "metaTco2eCompensar": 100.00
      }
      """
    Então o status da resposta deve ser 400
    E o campo "message" da resposta deve ser "Dados invalidos."
    E o campo de erro "fields.empresaId" deve estar presente na resposta

  Cenário: Buscar projeto com ID inexistente
    Quando eu envio uma requisição GET para "/projetos-compensacao/id-projeto-inexistente"
    Então o status da resposta deve ser 404
    E o campo "message" da resposta deve conter "ProjetoCompensacao nao encontrado"
