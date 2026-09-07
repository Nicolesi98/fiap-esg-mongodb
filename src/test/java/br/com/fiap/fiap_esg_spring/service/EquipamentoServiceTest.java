package br.com.fiap.fiap_esg_spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.Empresa;
import br.com.fiap.fiap_esg_spring.model.Equipamento;
import br.com.fiap.fiap_esg_spring.model.EquipamentoStatus;
import br.com.fiap.fiap_esg_spring.repository.EquipamentoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EquipamentoServiceTest {

    @Mock
    private EquipamentoRepository repository;

    @Mock
    private EmpresaService empresaService;

    @InjectMocks
    private EquipamentoService service;

    @Test
    void deveListarTodosQuandoEmpresaNaoForInformada() {
        Equipamento equipamento = equipamento("661000000000000000000001", "660000000000000000000001");
        when(repository.findAll()).thenReturn(List.of(equipamento));

        assertThat(service.listar(null)).containsExactly(equipamento);
    }

    @Test
    void deveListarPorEmpresaQuandoEmpresaForInformada() {
        Equipamento equipamento = equipamento("661000000000000000000001", "660000000000000000000002");
        when(repository.findByEmpresaId("660000000000000000000002")).thenReturn(List.of(equipamento));

        assertThat(service.listar("660000000000000000000002")).containsExactly(equipamento);
        verify(empresaService).verificaEmpresa("660000000000000000000002");
    }

    @Test
    void deveCriarComEmpresaExistente() {
        Equipamento equipamento = equipamento(null, "660000000000000000000001");
        Empresa empresa = empresa("660000000000000000000001");
        when(empresaService.buscarPorId("660000000000000000000001")).thenReturn(empresa);
        when(repository.save(equipamento)).thenReturn(equipamento);

        Equipamento criado = service.criar(equipamento);

        assertThat(criado.getEmpresa()).isSameAs(empresa);
        verify(repository).save(equipamento);
    }

    @Test
    void deveAtualizarEquipamentoExistente() {
        Equipamento existente = equipamento("661000000000000000000001", "660000000000000000000001");
        Equipamento dados = equipamento(null, "660000000000000000000002");
        dados.setNome("Sensor");
        dados.setTipo("MEDICAO");
        dados.setStatus(EquipamentoStatus.ATENCAO);
        dados.setLimiteConsumoKwh(new BigDecimal("12.50"));
        dados.setLimiteOciosidadeMin(7);
        Empresa empresa = empresa("660000000000000000000002");
        when(repository.findById("661000000000000000000001")).thenReturn(Optional.of(existente));
        when(empresaService.buscarPorId("660000000000000000000002")).thenReturn(empresa);
        when(repository.save(existente)).thenReturn(existente);

        Equipamento atualizado = service.atualizar("661000000000000000000001", dados);

        assertThat(atualizado.getEmpresa()).isSameAs(empresa);
        assertThat(atualizado.getNome()).isEqualTo("Sensor");
        assertThat(atualizado.getStatus()).isEqualTo(EquipamentoStatus.ATENCAO);
        assertThat(atualizado.getLimiteConsumoKwh()).isEqualByComparingTo("12.50");
        assertThat(atualizado.getLimiteOciosidadeMin()).isEqualTo(7);
    }

    @Test
    void deveFalharQuandoEquipamentoNaoExiste() {
        when(repository.findById("99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId("99"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Equipamento nao encontrado: 99");
    }

    private Equipamento equipamento(String id, String empresaId) {
        return Equipamento.builder()
                .id(id)
                .empresaId(empresaId)
                .empresa(empresa(empresaId))
                .nome("Ar Condicionado")
                .tipo("CLIMATIZACAO")
                .categoria("CLIMATIZACAO")
                .status(EquipamentoStatus.LIGADO)
                .limiteConsumoKwh(new BigDecimal("50.00"))
                .limiteOciosidadeMin(30)
                .build();
    }

    private Empresa empresa(String id) {
        return Empresa.builder()
                .id(id)
                .nome("Empresa")
                .cnpj("11.111.111/0001-11")
                .build();
    }
}
