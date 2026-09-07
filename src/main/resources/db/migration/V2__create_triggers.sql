CREATE OR REPLACE TRIGGER trg_alerta_consumo
AFTER INSERT ON consumo_energia
FOR EACH ROW
DECLARE
    v_limite NUMBER(10,2);
BEGIN
    SELECT limite_consumo_kwh
    INTO v_limite
    FROM equipamento
    WHERE id_equipamento = :NEW.id_equipamento;

    IF :NEW.consumo_kwh > v_limite THEN
        INSERT INTO alerta (
            id_equipamento,
            tipo_alerta,
            mensagem,
            status
        )
        VALUES (
            :NEW.id_equipamento,
            'CONSUMO_ALTO',
            'Consumo acima do limite permitido.',
            'ABERTO'
        );
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_alerta_ociosidade
AFTER INSERT ON consumo_energia
FOR EACH ROW
DECLARE
    v_limite_ocioso NUMBER;
BEGIN
    SELECT limite_ociosidade_min
    INTO v_limite_ocioso
    FROM equipamento
    WHERE id_equipamento = :NEW.id_equipamento;

    IF :NEW.tempo_ocioso_min > v_limite_ocioso THEN
        INSERT INTO alerta (
            id_equipamento,
            tipo_alerta,
            mensagem,
            status
        )
        VALUES (
            :NEW.id_equipamento,
            'OCIOSIDADE_ALTA',
            'Tempo de ociosidade acima do limite permitido.',
            'ABERTO'
        );
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_status_atencao_consumo
AFTER INSERT ON alerta
FOR EACH ROW
BEGIN
    IF :NEW.tipo_alerta = 'CONSUMO_ALTO' THEN
        UPDATE equipamento
        SET status = 'ATENCAO'
        WHERE id_equipamento = :NEW.id_equipamento
          AND status <> 'DESLIGADO';
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_desligar_equipamento_ocioso
AFTER INSERT ON alerta
FOR EACH ROW
BEGIN
    IF :NEW.tipo_alerta = 'OCIOSIDADE_ALTA' THEN
        UPDATE equipamento
        SET status = 'DESLIGADO'
        WHERE id_equipamento = :NEW.id_equipamento
          AND status = 'LIGADO';
    END IF;
END;
/
