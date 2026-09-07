package br.com.fiap.fiap_esg_spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.fiap_esg_spring.exception.ResourceNotFoundException;
import br.com.fiap.fiap_esg_spring.model.Empresa;
import br.com.fiap.fiap_esg_spring.model.ProjetoCompensacao;
import br.com.fiap.fiap_esg_spring.repository.ProjetoCompensacaoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjetoCompensacaoServiceTest {

    @Mock
    private ProjetoCompensacaoRepository repository;

    @Mock
    private EmpresaService empresaService;

    @InjectMocks
    private ProjetoCompensacaoService service;

    @Test
    void deveListarTodosQuandoFiltrosNaoForemInformados() {
        ProjetoCompensacao projeto = projeto("664000000000000000000001", "660000000000000000000001");
        when(repository.findAll()).thenReturn(List.of(projeto));

        assertThat(service.listar(null, null)).containsExactly(projeto);
    }

    @Test
    void deveListarPorEmpresaQuandoInformada() {
        ProjetoCompensacao projeto = projeto("664000000000000000000001", "660000000000000000000001");
        when(repository.findByEmpresaId("660000000000000000000001")).thenReturn(List.of(projeto));

        assertThat(service.listar("660000000000000000000001", null)).containsExactly(projeto);
        verify(empresaService).verificaEmpresa("660000000000000000000001");
    }

    @Test
    void deveCriarComEmpresaValida() {
        ProjetoCompensacao projeto = projeto(null, "660000000000000000000001");
        Empresa empresa = Empresa.builder().id("660000000000000000000001").nome("EcoTech").build();
        when(empresaService.buscarPorId("660000000000000000000001")).thenReturn(empresa);
        when(repository.save(projeto)).thenReturn(projeto);

        ProjetoCompensacao criado = service.criar(projeto);

        assertThat(criado.getEmpresaId()).isEqualTo("660000000000000000000001");
        verify(repository).save(projeto);
    }

    @Test
    void deveBuscarPorIdComSucesso() {
        ProjetoCompensacao projeto = projeto("664000000000000000000001", "660000000000000000000001");
        when(repository.findById("664000000000000000000001")).thenReturn(Optional.of(projeto));

        assertThat(service.buscarPorId("664000000000000000000001")).isSameAs(projeto);
    }

    @Test
    void deveFalharQuandoProjetoNaoExiste() {
        when(repository.findById("99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId("99"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Projeto de compensacao nao encontrado: 99");
    }

    @Test
    void deveExcluirProjetoExistente() {
        ProjetoCompensacao projeto = projeto("664000000000000000000001", "660000000000000000000001");
        when(repository.findById("664000000000000000000001")).thenReturn(Optional.of(projeto));

        service.excluir("664000000000000000000001");

        verify(repository).delete(projeto);
    }

    private ProjetoCompensacao projeto(String id, String empresaId) {
        return ProjetoCompensacao.builder()
                .id(id)
                .empresaId(empresaId)
                .nomeIniciativa("Corredor Ecologico")
                .tipoProjeto("REFLORESTAMENTO_NATIVO")
                .status("EM_ANDAMENTO")
                .metaTco2eCompensar(new BigDecimal("2500.0"))
                .tco2eCompensadoAcumulado(new BigDecimal("1400.0"))
                .investimentoBrl(new BigDecimal("480000.00"))
                .build();
    }
}
