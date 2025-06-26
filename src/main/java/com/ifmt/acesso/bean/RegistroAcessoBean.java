package com.ifmt.acesso.bean;

import com.ifmt.acesso.dao.EstudanteDAO;
import com.ifmt.acesso.dao.RegistroAcessoDAO;
import com.ifmt.acesso.model.Estudante;
import com.ifmt.acesso.model.TipoMovimentacao;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class RegistroAcessoBean implements Serializable {

    private Integer estudanteId;
    private TipoMovimentacao tipoMovimentacao;
    private List<Estudante> estudantes;
    
    private EstudanteDAO estudanteDAO;
    private RegistroAcessoDAO registroAcessoDAO;

    @PostConstruct
    public void inicializar() {
        estudanteDAO = new EstudanteDAO();
        registroAcessoDAO = new RegistroAcessoDAO();
        estudantes = estudanteDAO.listarTodos();
        tipoMovimentacao = TipoMovimentacao.ENTRADA;
    }

    public void registrar() {
        if (estudanteId == null) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Seleção necessária", "Por favor, selecione um estudante.");
            return;
        }

        try {
            // A única responsabilidade do bean é chamar o método do DAO.
            registroAcessoDAO.registrarNovoAcesso(estudanteId, tipoMovimentacao);
            
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Registro de acesso realizado com sucesso!");

        } catch (Exception e) {
            // Pega a causa raiz do erro para uma mensagem mais útil.
            Throwable causaRaiz = e;
            while (causaRaiz.getCause() != null) {
                causaRaiz = causaRaiz.getCause();
            }
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao Registrar", "Causa: " + causaRaiz.getMessage());
            e.printStackTrace();
        }
    }

    private void adicionarMensagem(FacesMessage.Severity severidade, String sumario, String detalhe) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidade, sumario, detalhe));
    }

    // Getters e Setters
    public Integer getEstudanteId() { return estudanteId; }
    public void setEstudanteId(Integer estudanteId) { this.estudanteId = estudanteId; }
    public TipoMovimentacao getTipoMovimentacao() { return tipoMovimentacao; }
    public void setTipoMovimentacao(TipoMovimentacao tipoMovimentacao) { this.tipoMovimentacao = tipoMovimentacao; }
    public List<Estudante> getEstudantes() { return estudantes; }
    public TipoMovimentacao[] getTiposMovimentacao() { return TipoMovimentacao.values(); }
}
