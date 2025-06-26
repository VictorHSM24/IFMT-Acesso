package com.ifmt.acesso.bean;

import com.ifmt.acesso.dao.RegistroAcessoDAO;
import com.ifmt.acesso.model.RegistroAcesso;
import com.ifmt.acesso.model.TipoMovimentacao;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class ConsultaBean implements Serializable {

    // Campos para guardar os valores dos filtros da tela
    private String nomeMatriculaFiltro;
    private Date dataInicioFiltro; // Usamos java.util.Date para compatibilidade com p:calendar
    private Date dataFimFiltro;
    private TipoMovimentacao tipoMovimentacaoFiltro;
    
    // Lista para guardar os resultados da consulta
    private List<RegistroAcesso> registrosFiltrados;
    
    private RegistroAcessoDAO registroAcessoDAO;
    
    @PostConstruct
    public void inicializar() {
        registroAcessoDAO = new RegistroAcessoDAO();
        registrosFiltrados = new ArrayList<>();
        // Inicia a tela carregando todos os registros
        buscar();
    }
    
    /**
     * Ação executada pelo botão "Buscar". Chama o DAO com os filtros.
     */
    public void buscar() {
        // Converte java.util.Date para java.time.LocalDate, que é o tipo usado no DAO
        LocalDate dataInicio = (dataInicioFiltro != null) ? dataInicioFiltro.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate dataFim = (dataFimFiltro != null) ? dataFimFiltro.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        
        registrosFiltrados = registroAcessoDAO.filtrar(nomeMatriculaFiltro, dataInicio, dataFim, tipoMovimentacaoFiltro);
    }
    
    /**
     * Limpa todos os campos de filtro e recarrega a lista completa.
     */
    public void limparFiltros() {
        nomeMatriculaFiltro = null;
        dataInicioFiltro = null;
        dataFimFiltro = null;
        tipoMovimentacaoFiltro = null;
        buscar();
    }
    
    // Método para popular o dropdown de tipo de movimentação
    public TipoMovimentacao[] getTiposMovimentacao() {
        return TipoMovimentacao.values();
    }

    // --- Getters e Setters para os filtros e resultados ---
    public String getNomeMatriculaFiltro() { return nomeMatriculaFiltro; }
    public void setNomeMatriculaFiltro(String nomeMatriculaFiltro) { this.nomeMatriculaFiltro = nomeMatriculaFiltro; }
    public Date getDataInicioFiltro() { return dataInicioFiltro; }
    public void setDataInicioFiltro(Date dataInicioFiltro) { this.dataInicioFiltro = dataInicioFiltro; }
    public Date getDataFimFiltro() { return dataFimFiltro; }
    public void setDataFimFiltro(Date dataFimFiltro) { this.dataFimFiltro = dataFimFiltro; }
    public TipoMovimentacao getTipoMovimentacaoFiltro() { return tipoMovimentacaoFiltro; }
    public void setTipoMovimentacaoFiltro(TipoMovimentacao tipoMovimentacaoFiltro) { this.tipoMovimentacaoFiltro = tipoMovimentacaoFiltro; }
    public List<RegistroAcesso> getRegistrosFiltrados() { return registrosFiltrados; }
}
