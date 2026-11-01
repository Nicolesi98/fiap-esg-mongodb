package br.com.fiap.fiap_esg_spring.bdd;

import br.com.fiap.fiap_esg_spring.model.*;
import br.com.fiap.fiap_esg_spring.service.*;
import io.cucumber.spring.CucumberContextConfiguration;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Configuração que conecta o Cucumber ao contexto Spring Boot.
 * Todos os Step Definitions herdam este contexto de forma transparente.
 *
 * Os serviços são mockados para que os testes BDD não dependam de
 * uma instância real do MongoDB. Os stubs básicos (CRUD) são configurados
 * aqui para reutilização entre cenários.
 */
@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    // MockBeans declarados aqui ficam disponíveis para todos os Step Definitions.
    // Stubs específicos por cenário são configurados nos próprios steps via @Autowired.

    @MockBean
    public EmpresaService empresaService;

    @MockBean
    public EquipamentoService equipamentoService;

    @MockBean
    public ConsumoEnergiaService consumoEnergiaService;

    @MockBean
    public AlertaService alertaService;

    @MockBean
    public ProjetoCompensacaoService projetoCompensacaoService;
}
