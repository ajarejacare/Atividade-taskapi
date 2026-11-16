package br.edu.ifsp.web.command;

import br.edu.ifsp.web.model.Usuario;
import br.edu.ifsp.web.service.TarefaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DeletarTarefaCommand implements Command {

    @Override
    public void executar(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {

        int id = CommandSupport.id(req);
        TarefaService service = CommandSupport.tarefaService(req);
        Usuario usuario = CommandSupport.usuario(req);
        service.excluir(usuario, id);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}