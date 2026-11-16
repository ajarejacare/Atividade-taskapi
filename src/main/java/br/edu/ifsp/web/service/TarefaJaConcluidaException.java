package br.edu.ifsp.web.service;

public class TarefaJaConcluidaException extends RegraDominioVioladaException {

    private static final long serialVersionUID = 1L;

    public TarefaJaConcluidaException() {
        super("A tarefa já está concluída.");
    }

    public TarefaJaConcluidaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
