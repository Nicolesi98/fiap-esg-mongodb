package br.com.fiap.fiap_esg_spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.fiap_esg_spring.model.ConsumoEnergia;
import br.com.fiap.fiap_esg_spring.model.Equipamento;
import br.com.fiap.fiap_esg_spring.repository.ConsumoEnergiaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConsumoEnergiaServiceTest {

    @Mock
    private ConsumoEnergiaRepository repository;

    @Mock
    private EquipamentoService equipamentoService;

    @InjectMocks
    private ConsumoEnergiaService service;

    @Test
    void deveListarTodosQuandoEquipamentoNaoForInformado() {
        ConsumoEnergia consumo = consumo("662000000000000000000001", "661000000000000000000001");
        when(repository.findAll()).thenReturn(List.of(consumo));

        assertThat(service.listar(null)).containsExactly(consumo);
    }

    @Test
    void deveListarPorEquipamentoQuandoInformado() {
        ConsumoEnergia consumo = consumo("662000000000000000000001", "661000000000000000000002");
        when(repository.findByEquipamentoId("661000000000000000000002")).thenReturn(List.of(consumo));

        assertThat(service.listar("661000000000000000000002")).containsExactly(consumo);
        verify(equipamentoService).verificaEquipamento("661000000000000000000002");
    }

    @Test
    void deveCriarComEquipamentoExistenteECalcularPegadaCarbono() {
        ConsumoEnergia consumo = consumo(null, "661000000000000000000001");
        consumo.setConsumoKwh(new BigDecimal("100.00"));
        Equipamento equipamento = equipamento("661000000000000000000001");
        when(equipamentoService.buscarPorId("661000000000000000000001")).thenReturn(equipamento);
        when(repository.save(consumo)).thenReturn(consumo);

        ConsumoEnergia criado = service.criar(consumo);

        assertThat(criado.getEquipamento()).isSameAs(equipamento);
        assertThat(criado.getPegadaCarbonoKgCo2e()).isEqualByComparingTo("9.00");
        verify(repository).save(consumo);
    }

    @Test
    void deveAtualizarConsumoExistente() {
        ConsumoEnergia existente = consumo("662000000000000000000001", "661000000000000000000001");
        LocalDateTime novaData = LocalDateTime.of(2026, 5, 17, 15, 30);
        ConsumoEnergia dados = consumo(null, "661000000000000000000002");
        dados.setDataHora(novaData);
        dados.setConsumoKwh(new BigDecimal("99.90"));
        dados.setTempoOciosoMin(12);
        Equipamento equipamento = equipamento("661000000000000000000002");
        when(repository.findById("662000000000000000000001")).thenReturn(Optional.of(existente));
        when(equipamentoService.buscarPorId("661000000000000000000002")).thenReturn(equipamento);
        when(repository.save(existente)).thenReturn(existente);

        ConsumoEnergia atualizado = service.atualizar("662000000000000000000001", dados);

        assertThat(atualizado.getEquipamento()).isSameAs(equipamento);
        assertThat(atualizado.getDataHora()).isEqualTo(novaData);
        assertThat(atualizado.getConsumoKwh()).isEqualByComparingTo("99.90");
        assertThat(atualizado.getTempoOciosoMin()).isEqualTo(12);
    }

    @Test
    void deveExcluirConsumoExistente() {
        ConsumoEnergia consumo = consumo("662000000000000000000001", "661000000000000000000001");
        when(repository.findById("662000000000000000000001")).thenReturn(Optional.of(consumo));

        service.excluir("662000000000000000000001");

        verify(repository).delete(consumo);
    }

    private ConsumoEnergia consumo(String id, String equipamentoId) {
        return ConsumoEnergia.builder()
                .id(id)
                .equipamentoId(equipamentoId)
                .equipamento(equipamento(equipamentoId))
                .dataHora(LocalDateTime.of(2026, 5, 17, 10, 0))
                .consumoKwh(new BigDecimal("40.00"))
                .tempoOciosoMin(5)
                .tipoRecurso("ENERGIA_ELETRICA")
                .build();
    }

    private Equipamento equipamento(String id) {
        return Equipamento.builder()
                .id(id)
                .nome("Equipamento")
                .build();
    }
}
