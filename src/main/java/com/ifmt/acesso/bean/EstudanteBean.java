package com.ifmt.acesso.bean;

import com.ifmt.acesso.dao.EstudanteDAO;
import com.ifmt.acesso.model.Estudante;
import com.ifmt.acesso.model.Turno;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class EstudanteBean implements Serializable {

    // Revertido para um único objeto 'estudante' para cadastro e edição
    private Estudante estudante; 
    private List<Estudante> estudantes;
    private EstudanteDAO estudanteDAO;

    @PostConstruct
    public void inicializar() {
        estudante = new Estudante();
        estudantes = new ArrayList<>();
        estudanteDAO = new EstudanteDAO();
        carregarEstudantes();
    }

    private void carregarEstudantes() {
        this.estudantes = estudanteDAO.listarTodos();
    }

    // O método salvar agora lida tanto com novos cadastros quanto com edições.
    public void salvar() {
        try {
            estudanteDAO.salvar(this.estudante);
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Estudante salvo com sucesso!");
            carregarEstudantes();
            limpar();
        } catch (Exception e) {
            adicionarMensagemDeErro(e, "Erro ao Salvar");
        }
    }

    public void excluir(Estudante estudanteParaExcluir) {
        try {
            estudanteDAO.remover(estudanteParaExcluir);
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Estudante removido com sucesso!");
            carregarEstudantes();
        } catch (Exception e) {
            adicionarMensagemDeErro(e, "Erro ao Excluir");
        }
    }
    
    // Carrega os dados do estudante selecionado para o formulário no topo
    public void carregarParaEdicao(Estudante estudanteParaEditar) {
        this.estudante = estudanteParaEditar;
    }
    
    public void limpar() {
        this.estudante = new Estudante();
    }
    
    private void adicionarMensagem(FacesMessage.Severity severidade, String sumario, String detalhe) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidade, sumario, detalhe));
    }
    
    private void adicionarMensagemDeErro(Exception ex, String sumario) {
        Throwable causaRaiz = ex;
        while (causaRaiz.getCause() != null) {
            causaRaiz = causaRaiz.getCause();
        }
        adicionarMensagem(FacesMessage.SEVERITY_ERROR, sumario, "Causa: " + causaRaiz.getMessage());
        ex.printStackTrace();
    }

    public Turno[] getTurnos() {
        return Turno.values();
    }

    // Getters e Setters
    public Estudante getEstudante() { return estudante; }
    public void setEstudante(Estudante estudante) { this.estudante = estudante; }
    public List<Estudante> getEstudantes() { return estudantes; }
    public void setEstudantes(List<Estudante> estudantes) { this.estudantes = estudantes; }
}
