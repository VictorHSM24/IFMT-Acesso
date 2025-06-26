package com.ifmt.acesso.dao;

import com.ifmt.acesso.model.Estudante;
import com.ifmt.acesso.model.RegistroAcesso;
import com.ifmt.acesso.model.TipoMovimentacao;
import com.ifmt.acesso.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistroAcessoDAO implements Serializable {

    /**
     * Contém a lógica de negócio para registrar um novo acesso. Busca os dados,
     * valida e persiste, tudo dentro de uma única transação para garantir
     * consistência.
     * @param estudanteId O ID do estudante que está acessando.
     * @param tipoMovimentacao O tipo de acesso (ENTRADA ou SAIDA).
     */
    public void registrarNovoAcesso(Integer estudanteId, TipoMovimentacao tipoMovimentacao) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            Estudante estudante = em.find(Estudante.class, estudanteId);
            if (estudante == null) {
                throw new RuntimeException("Estudante com ID " + estudanteId + " não encontrado.");
            }

            TypedQuery<RegistroAcesso> query = em.createQuery(
                "SELECT r FROM RegistroAcesso r WHERE r.estudante = :estudante ORDER BY r.dataHora DESC",
                RegistroAcesso.class
            );
            query.setParameter("estudante", estudante);
            query.setMaxResults(1);
            RegistroAcesso ultimoRegistro = null;
            try {
                ultimoRegistro = query.getSingleResult();
            } catch (NoResultException e) {
                // Normal se for o primeiro acesso.
            }

            // ========================= MUDANÇA NA VALIDAÇÃO =========================
            // Validação 1: O primeiro acesso de um estudante não pode ser uma SAÍDA.
            if (ultimoRegistro == null && tipoMovimentacao == TipoMovimentacao.SAIDA) {
                throw new RuntimeException("Operação Inválida: Não é possível registrar uma saída sem uma entrada prévia.");
            }

            // Validação 2: Não podem haver duas movimentações iguais consecutivas.
            if (ultimoRegistro != null && ultimoRegistro.getTipo() == tipoMovimentacao) {
                String tipoStr = tipoMovimentacao.toString().toLowerCase();
                throw new RuntimeException("Operação Inválida: O último registro para este estudante já é uma " + tipoStr + ".");
            }
            // ======================================================================

            RegistroAcesso novoRegistro = new RegistroAcesso();
            novoRegistro.setEstudante(estudante);
            novoRegistro.setDataHora(LocalDateTime.now());
            novoRegistro.setTipo(tipoMovimentacao);
            
            em.persist(novoRegistro);

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Erro ao registrar acesso: " + e.getMessage(), e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Filtra os registros de acesso com base em vários critérios opcionais.
     * @param nomeMatricula Filtra por nome ou matrícula do estudante.
     * @param dataInicio Data inicial para o filtro de período.
     * @param dataFim Data final para o filtro de período.
     * @param tipo Filtra por tipo de movimentação (ENTRADA ou SAIDA).
     * @return Uma lista de registros de acesso que correspondem aos filtros.
     */
    public List<RegistroAcesso> filtrar(String nomeMatricula, LocalDate dataInicio, LocalDate dataFim, TipoMovimentacao tipo) {
        EntityManager em = JPAUtil.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RegistroAcesso> cq = cb.createQuery(RegistroAcesso.class);
        Root<RegistroAcesso> registroRoot = cq.from(RegistroAcesso.class);
        
        registroRoot.fetch("estudante", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (nomeMatricula != null && !nomeMatricula.trim().isEmpty()) {
            Join<RegistroAcesso, Estudante> estudanteJoin = registroRoot.join("estudante");
            Predicate nomePredicate = cb.like(cb.lower(estudanteJoin.get("nomeCompleto")), "%" + nomeMatricula.toLowerCase() + "%");
            Predicate matriculaPredicate = cb.like(estudanteJoin.get("matricula"), "%" + nomeMatricula + "%");
            predicates.add(cb.or(nomePredicate, matriculaPredicate));
        }
        
        if (dataInicio != null) {
            predicates.add(cb.greaterThanOrEqualTo(registroRoot.get("dataHora"), dataInicio.atStartOfDay()));
        }

        if (dataFim != null) {
            predicates.add(cb.lessThanOrEqualTo(registroRoot.get("dataHora"), dataFim.atTime(23, 59, 59)));
        }
        
        if (tipo != null) {
            predicates.add(cb.equal(registroRoot.get("tipo"), tipo));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(registroRoot.get("dataHora")));
        
        try {
            TypedQuery<RegistroAcesso> query = em.createQuery(cq);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
