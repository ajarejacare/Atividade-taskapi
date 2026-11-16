package br.edu.ifsp.web.service;

public class RegraDominioVioladaException extends AplicacaoException {

    private static final long serialVersionUID = 1L;

    public RegraDominioVioladaException(String mensagem) {
        super(mensagem);
    }

    public RegraDominioVioladaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
