INSERT INTO empresa (id_empresa, nome, cnpj)
VALUES (1, 'EcoTech Solucoes', '11.111.111/0001-11');

INSERT INTO empresa (id_empresa, nome, cnpj)
VALUES (2, 'Energia Verde Ltda', '22.222.222/0001-22');

INSERT INTO empresa (id_empresa, nome, cnpj)
VALUES (3, 'Cidade Sustentavel SA', '33.333.333/0001-33');

INSERT INTO empresa (id_empresa, nome, cnpj)
VALUES (4, 'Industria Solar BR', '44.444.444/0001-44');

INSERT INTO empresa (id_empresa, nome, cnpj)
VALUES (5, 'Consumo Inteligente ME', '55.555.555/0001-55');

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

INSERT INTO equipamento (
    id_equipamento,
    id_empresa,
    nome,
    tipo,
    status,
    limite_consumo_kwh,
    limite_ociosidade_min
) VALUES (
    3,
    3,
    'Servidor Local',
    'TI',
    'LIGADO',
    80,
    45
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
    4,
    4,
    'Motor Industrial',
    'PRODUCAO',
    'LIGADO',
    100,
    20
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
    5,
    5,
    'Computador Administrativo',
    'INFORMATICA',
    'LIGADO',
    10,
    25
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
    SYSDATE,
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
    SYSDATE,
    12,
    25
);

INSERT INTO consumo_energia (
    id_consumo,
    id_equipamento,
    data_hora,
    consumo_kwh,
    tempo_ocioso_min
) VALUES (
    3,
    3,
    SYSDATE,
    95,
    15
);

INSERT INTO consumo_energia (
    id_consumo,
    id_equipamento,
    data_hora,
    consumo_kwh,
    tempo_ocioso_min
) VALUES (
    4,
    4,
    SYSDATE,
    70,
    35
);

INSERT INTO consumo_energia (
    id_consumo,
    id_equipamento,
    data_hora,
    consumo_kwh,
    tempo_ocioso_min
) VALUES (
    5,
    5,
    SYSDATE,
    18,
    5
);
