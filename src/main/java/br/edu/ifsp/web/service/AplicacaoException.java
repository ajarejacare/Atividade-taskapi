package br.edu.ifsp.web.service;

public abstract class AplicacaoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected AplicacaoException(String mensagem) {
        super(mensagem);
    }

    protected AplicacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
