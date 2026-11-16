package br.edu.ifsp.web.service;

import br.edu.ifsp.web.dao.ConnectionFactory;

public final class ServiceFactory {

    private final TarefaService tarefaService;
    private final AutenticacaoService autenticacaoService;

    public ServiceFactory() {
        this(new ConnectionFactory());
    }

    public ServiceFactory(ConnectionFactory connectionFactory) {
        Pbkdf2CredentialHasher hasher = new Pbkdf2CredentialHasher();
        tarefaService = new TarefaServiceImpl(connectionFactory);
        autenticacaoService = new AutenticacaoServiceImpl(connectionFactory, hasher);
    }

    public TarefaService tarefaService() {
        return tarefaService;
    }

    public AutenticacaoService autenticacaoService() {
        return autenticacaoService;
    }
}
