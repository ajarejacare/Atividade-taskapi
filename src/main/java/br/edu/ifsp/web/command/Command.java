package br.edu.ifsp.web.command;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Command {

    // Todo comando sabe fazer UMA coisa: agir sobre a requisição e a resposta.
    // O 'throws Exception' é proposital: o Front Controller centraliza o erro.
    void executar(HttpServletRequest request, HttpServletResponse response)
            throws Exception;
}
