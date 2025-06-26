package com.ifmt.acesso.dao;

import com.ifmt.acesso.model.Estudante;
import com.ifmt.acesso.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public class EstudanteDAO implements Serializable {

    // Este método agora devolve o estudante atualizado e gerido pela persistência.
    public Estudante salvar(Estudante estudante) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        Estudante estudanteGerenciado = null;
        try {
            transaction.begin();
            // A operação merge devolve uma nova instância que está no estado "managed".
            estudanteGerenciado = em.merge(estudante);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Erro ao persistir o estudante: " + e.getMessage(), e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return estudanteGerenciado;
    }

    public void remover(Estudante estudante) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Estudante estudanteGerenciado = em.find(Estudante.class, estudante.getId());
            if (estudanteGerenciado != null) {
                em.remove(estudanteGerenciado);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Erro ao remover o estudante: " + e.getMessage(), e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public Estudante buscarPorId(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Estudante.class, id);
        } finally {
            em.close();
        }
    }

    public List<Estudante> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Estudante> query = em.createQuery("SELECT e FROM Estudante e ORDER BY e.nomeCompleto", Estudante.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
