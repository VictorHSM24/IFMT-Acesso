package com.ifmt.acesso.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Classe utilitária para gerenciar o EntityManagerFactory e fornecer instâncias do EntityManager.
 * O EntityManagerFactory é criado uma única vez (singleton), o que é uma prática recomendada
 * para otimizar o desempenho da aplicação.
 */
public class JPAUtil {

    // O nome da unidade de persistência deve ser o mesmo definido no arquivo persistence.xml
    private static final String PERSISTENCE_UNIT_NAME = "ifmt_acesso_pu";
    
    private static EntityManagerFactory factory;

    // Bloco estático para inicializar o EntityManagerFactory uma única vez quando a classe é carregada.
    static {
        try {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Throwable ex) {
            System.err.println("Falha ao criar o EntityManagerFactory." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Fornece uma instância do EntityManager para realizar operações de persistência.
     * Cada operação (ou requisição) deve usar seu próprio EntityManager.
     * * @return Uma nova instância do EntityManager.
     */
    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }
    
    /**
     * Fecha o EntityManagerFactory quando a aplicação for finalizada.
     * Isso deve ser chamado em um listener de contexto do Servlet para liberar todos os recursos.
     */
    public static void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}
