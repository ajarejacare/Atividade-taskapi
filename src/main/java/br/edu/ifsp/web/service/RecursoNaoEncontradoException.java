package br.edu.ifsp.web.service;

public class RecursoNaoEncontradoException extends AplicacaoException {

    private static final long serialVersionUID = 1L;

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
