package br.com.fiap.fiap_esg_spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.fiap_esg_spring.model.Alerta;
import br.com.fiap.fiap_esg_spring.model.AlertaStatus;
import br.com.fiap.fiap_esg_spring.model.Equipamento;
import br.com.fiap.fiap_esg_spring.repository.AlertaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private AlertaRepository repository;

    @Mock
    private EquipamentoService equipamentoService;

    @InjectMocks
    private AlertaService service;

    @Test
    void deveListarTodosQuandoFiltroNaoForInformado() {
        Alerta alerta = alerta("663000000000000000000001", "661000000000000000000001", AlertaStatus.ABERTO);
        when(repository.findAll()).thenReturn(List.of(alerta));

        assertThat(service.listar(null, null)).containsExactly(alerta);
    }

    @Test
    void deveListarPorEquipamentoComPrioridadeSobreStatus() {
        Alerta alerta = alerta("663000000000000000000001", "661000000000000000000002", AlertaStatus.RESOLVIDO);
        when(repository.findByEquipamentoId("661000000000000000000002")).thenReturn(List.of(alerta));

        assertThat(service.listar("661000000000000000000002", AlertaStatus.ABERTO)).containsExactly(alerta);
        verify(equipamentoService).verificaEquipamento("661000000000000000000002");
    }

    @Test
    void deveListarPorStatus() {
        Alerta alerta = alerta("663000000000000000000001", "661000000000000000000001", AlertaStatus.ABERTO);
        when(repository.findByStatus(AlertaStatus.ABERTO)).thenReturn(List.of(alerta));

        assertThat(service.listar(null, AlertaStatus.ABERTO)).containsExactly(alerta);
    }

    @Test
    void deveCriarComEquipamentoExistente() {
        Alerta alerta = alerta(null, "661000000000000000000001", AlertaStatus.ABERTO);
        Equipamento equipamento = equipamento("661000000000000000000001");
        when(equipamentoService.buscarPorId("661000000000000000000001")).thenReturn(equipamento);
        when(repository.save(alerta)).thenReturn(alerta);

        Alerta criado = service.criar(alerta);

        assertThat(criado.getEquipamento()).isSameAs(equipamento);
        verify(repository).save(alerta);
    }

    @Test
    void deveAtualizarAlertaExistente() {
        Alerta existente = alerta("663000000000000000000001", "661000000000000000000001", AlertaStatus.ABERTO);
        LocalDateTime novaData = LocalDateTime.of(2026, 5, 17, 18, 0);
        Alerta dados = alerta(null, "661000000000000000000002", AlertaStatus.RESOLVIDO);
        dados.setDataHora(novaData);
        dados.setTipoAlerta("OCIOSIDADE_ALTA");
        dados.setMensagem("Equipamento ocioso atualizado");
        dados.setSeveridade("MEDIA");
        dados.setStatus(AlertaStatus.RESOLVIDO);
        Equipamento equipamento = equipamento("661000000000000000000002");
        when(repository.findById("663000000000000000000001")).thenReturn(Optional.of(existente));
        when(equipamentoService.buscarPorId("661000000000000000000002")).thenReturn(equipamento);
        when(repository.save(existente)).thenReturn(existente);

        Alerta atualizado = service.atualizar("663000000000000000000001", dados);

        assertThat(atualizado.getEquipamento()).isSameAs(equipamento);
        assertThat(atualizado.getDataHora()).isEqualTo(novaData);
        assertThat(atualizado.getTipoAlerta()).isEqualTo("OCIOSIDADE_ALTA");
        assertThat(atualizado.getMensagem()).isEqualTo("Equipamento ocioso atualizado");
        assertThat(atualizado.getStatus()).isEqualTo(AlertaStatus.RESOLVIDO);
    }

    @Test
    void deveExcluirAlertaExistente() {
        Alerta alerta = alerta("663000000000000000000001", "661000000000000000000001", AlertaStatus.ABERTO);
        when(repository.findById("663000000000000000000001")).thenReturn(Optional.of(alerta));

        service.excluir("663000000000000000000001");

        verify(repository).delete(alerta);
    }

    private Alerta alerta(String id, String equipamentoId, AlertaStatus status) {
        return Alerta.builder()
                .id(id)
                .equipamentoId(equipamentoId)
                .equipamento(equipamento(equipamentoId))
                .pilarEsg("ENVIRONMENTAL")
                .dataHora(LocalDateTime.of(2026, 5, 17, 12, 0))
                .tipoAlerta("CONSUMO_EXCESSIVO")
                .mensagem("Consumo elevado")
                .severidade("ALTA")
                .status(status)
                .build();
    }

    private Equipamento equipamento(String id) {
        return Equipamento.builder()
                .id(id)
                .nome("Equipamento")
                .build();
    }
}
