# language: pt
Funcionalidade: Registro de Consumo de Energia
  Como sistema de telemetria ESG
  Quero registrar leituras de consumo energético dos equipamentos
  Para calcular a pegada de carbono e detectar desvios de eficiência

  Contexto:
    Dado que estou autenticado com usuário "admin" e senha "admin123"

  # ====================== CENÁRIOS POSITIVOS (caminho feliz) ======================

  Cenário: Registrar nova leitura de consumo com dados válidos
    Quando eu envio uma requisição POST para "/consumos-energia" com o corpo:
      """
      {
        "tipoRecurso": "ENERGIA_ELETRICA",
        "consumoKwh": 25.50,
        "tempoOciosoMin": 15
      }
      """
    Então o status da resposta deve ser 201
    E o campo "consumoKwh" da resposta deve conter "25"
    E o cabeçalho "Location" deve conter "/consumos-energia/"
    E o corpo da resposta deve estar de acordo com o JSON Schema "consumo-energia-schema.json"

  Cenário: Listar registros de consumo
    Dado que existe ao menos um registro de consumo cadastrado
    Quando eu envio uma requisição GET para "/consumos-energia"
    Então o status da resposta deve ser 200
    E a resposta deve ser uma lista

  # ====================== CENÁRIOS NEGATIVOS (falhas) ======================

  Cenário: Registrar consumo sem campo obrigatório "consumoKwh"
    Quando eu envio uma requisição POST para "/consumos-energia" com o corpo:
      """
      {
        "tipoRecurso": "ENERGIA_ELETRICA",
        "tempoOciosoMin": 10
      }
      """
    Então o status da resposta deve ser 400
    E o campo "message" da resposta deve ser "Dados invalidos."

  Cenário: Registrar consumo com valor negativo
    Quando eu envio uma requisição POST para "/consumos-energia" com o corpo:
      """
      {
        "tipoRecurso": "ENERGIA_ELETRICA",
        "consumoKwh": -5.00,
        "tempoOciosoMin": 0
      }
      """
    Então o status da resposta deve ser 400
    E o campo "message" da resposta deve ser "Dados invalidos."
