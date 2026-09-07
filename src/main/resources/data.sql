INSERT INTO empresa (id_empresa, nome, cnpj)
VALUES (1, 'EcoTech Solucoes', '11.111.111/0001-11');

INSERT INTO empresa (id_empresa, nome, cnpj)
VALUES (2, 'Energia Verde Ltda', '22.222.222/0001-22');

INSERT INTO empresa (id_empresa, nome, cnpj)
VALUES (3, 'Cidade Sustentavel SA', '33.333.333/0001-33');

INSERT INTO equipamento (
    id_equipamento,
    id_empresa,
    nome,
    tipo,
    status,
    limite_consumo_kwh,
    limite_ociosidade_min
) VALUES (
    1,
    1,
    'Ar Condicionado',
    'CLIMATIZACAO',
    'LIGADO',
    50,
    30
);

INSERT INTO equipamento (
    id_equipamento,
    id_empresa,
    nome,
    tipo,
    status,
    limite_consumo_kwh,
    limite_ociosidade_min
) VALUES (
    2,
    2,
    'Sistema de Iluminacao',
    'ILUMINACAO',
    'LIGADO',
    20,
    15
);

INSERT INTO consumo_energia (
    id_consumo,
    id_equipamento,
    data_hora,
    consumo_kwh,
    tempo_ocioso_min
) VALUES (
    1,
    1,
    CURRENT_TIMESTAMP,
    65,
    10
);

INSERT INTO consumo_energia (
    id_consumo,
    id_equipamento,
    data_hora,
    consumo_kwh,
    tempo_ocioso_min
) VALUES (
    2,
    2,
    CURRENT_TIMESTAMP,
    12,
    25
);

INSERT INTO alerta (
    id_alerta,
    id_equipamento,
    data_hora,
    tipo_alerta,
    mensagem,
    status
) VALUES (
    1,
    1,
    CURRENT_TIMESTAMP,
    'CONSUMO_ALTO',
    'Consumo acima do limite permitido.',
    'ABERTO'
);

ALTER TABLE empresa ALTER COLUMN id_empresa RESTART WITH 4;
ALTER TABLE equipamento ALTER COLUMN id_equipamento RESTART WITH 3;
ALTER TABLE consumo_energia ALTER COLUMN id_consumo RESTART WITH 3;
ALTER TABLE alerta ALTER COLUMN id_alerta RESTART WITH 2;
