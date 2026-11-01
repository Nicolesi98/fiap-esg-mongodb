package br.com.fiap.fiap_esg_spring.bdd;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Armazena estado compartilhado entre passos de um mesmo cenário BDD.
 * Instanciado uma vez por cenário graças ao escopo gerenciado pelo Cucumber-Spring.
 */
@Component
public class ScenarioContext {

    /** Último response obtido pelo step de requisição. */
    private org.springframework.mock.web.MockHttpServletResponse lastResponse;

    /** ID extraído do recurso criado/buscado no cenário atual. */
    private String resourceId;

    /** ID do equipamento usado como referência em cenários de alerta. */
    private String equipamentoId;

    public org.springframework.mock.web.MockHttpServletResponse getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(org.springframework.mock.web.MockHttpServletResponse lastResponse) {
        this.lastResponse = lastResponse;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getEquipamentoId() {
        return equipamentoId;
    }

    public void setEquipamentoId(String equipamentoId) {
        this.equipamentoId = equipamentoId;
    }

    public void clear() {
        lastResponse = null;
        resourceId = null;
        equipamentoId = null;
    }
}
