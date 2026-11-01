# language: pt
Funcionalidade: Gerenciamento de Alertas ESG
  Como sistema de monitoramento ESG
  Quero criar e gerenciar alertas automáticos de consumo e desvio
  Para que a equipe de sustentabilidade possa agir rapidamente

  Contexto:
    Dado que estou autenticado com usuário "admin" e senha "admin123"

  # ====================== CENÁRIOS POSITIVOS (caminho feliz) ======================

  Cenário: Criar um novo alerta com dados válidos
    Quando eu envio uma requisição POST para "/alertas" com o corpo:
      """
      {
        "tipoAlerta": "CONSUMO_ALTO",
        "mensagem": "Consumo 40% acima do limite estabelecido no turno noturno",
        "severidade": "ALTA",
        "status": "ABERTO",
        "pilarEsg": "ENVIRONMENTAL"
      }
      """
    Então o status da resposta deve ser 201
    E o campo "tipoAlerta" da resposta deve ser "CONSUMO_ALTO"
    E o campo "status" da resposta deve ser "ABERTO"
    E o cabeçalho "Location" deve conter "/alertas/"
    E o corpo da resposta deve estar de acordo com o JSON Schema "alerta-schema.json"

  Cenário: Listar alertas com status ABERTO
    Dado que existe ao menos um alerta cadastrado com status "ABERTO"
    Quando eu envio uma requisição GET para "/alertas?status=ABERTO"
    Então o status da resposta deve ser 200
    E a resposta deve ser uma lista
    E o corpo da resposta deve estar de acordo com o JSON Schema "alerta-lista-schema.json"

  Cenário: Buscar alerta por ID
    Dado que existe um alerta cadastrado com tipo "OCIOSIDADE_DETECTADA"
    Quando eu envio uma requisição GET para "/alertas/{id}"
    Então o status da resposta deve ser 200
    E o campo "tipoAlerta" da resposta deve ser "OCIOSIDADE_DETECTADA"
    E o corpo da resposta deve estar de acordo com o JSON Schema "alerta-schema.json"

  Cenário: Atualizar status de um alerta para RESOLVIDO
    Dado que existe um alerta cadastrado com tipo "CONSUMO_ALTO"
    Quando eu envio uma requisição PUT para "/alertas/{id}" com o corpo:
      """
      {
        "tipoAlerta": "CONSUMO_ALTO",
        "mensagem": "Consumo normalizado após ajuste do equipamento",
        "severidade": "BAIXA",
        "status": "RESOLVIDO"
      }
      """
    Então o status da resposta deve ser 200
    E o campo "status" da resposta deve ser "RESOLVIDO"

  Cenário: Excluir um alerta existente
    Dado que existe um alerta cadastrado com tipo "TESTE_ALERTA"
    Quando eu envio uma requisição DELETE para "/alertas/{id}"
    Então o status da resposta deve ser 204

  Cenário: Filtrar alertas por equipamento
    Dado que existe um equipamento cadastrado com nome "Gerador ESG"
    E que existe um alerta vinculado ao equipamento "Gerador ESG"
    Quando eu envio uma requisição GET para "/alertas?equipamentoId={equipamentoId}"
    Então o status da resposta deve ser 200
    E a resposta deve ser uma lista

  # ====================== CENÁRIOS NEGATIVOS (falhas) ======================

  Cenário: Criar alerta sem campos obrigatórios
    Quando eu envio uma requisição POST para "/alertas" com o corpo:
      """
      {
        "severidade": "ALTA"
      }
      """
    Então o status da resposta deve ser 400
    E o campo "message" da resposta deve ser "Dados invalidos."
    E o campo de erro "fields.tipoAlerta" deve estar presente na resposta
    E o campo de erro "fields.mensagem" deve estar presente na resposta

  Cenário: Criar alerta com mensagem excedendo 250 caracteres
    Quando eu envio uma requisição POST para "/alertas" com o corpo:
      """
      {
        "tipoAlerta": "CONSUMO_ALTO",
        "mensagem": "Esta mensagem possui mais de duzentos e cinquenta caracteres propositalmente para testar a validação de tamanho máximo do campo mensagem no sistema de alertas ESG. O texto continua até ultrapassar o limite máximo permitido pela anotação de validação Size.",
        "severidade": "MEDIA",
        "status": "ABERTO"
      }
      """
    Então o status da resposta deve ser 400
    E o campo "message" da resposta deve ser "Dados invalidos."

  Cenário: Buscar alerta com ID inexistente
    Quando eu envio uma requisição GET para "/alertas/id-alerta-inexistente-000"
    Então o status da resposta deve ser 404
    E o campo "message" da resposta deve conter "Alerta nao encontrado"
