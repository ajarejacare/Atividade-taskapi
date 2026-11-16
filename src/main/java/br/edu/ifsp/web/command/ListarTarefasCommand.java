package br.edu.ifsp.web.command;

import br.edu.ifsp.web.model.Usuario;
import br.edu.ifsp.web.servlet.JsonResponse;
import br.edu.ifsp.web.service.TarefaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ListarTarefasCommand implements Command {

    @Override
    public void executar(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {

        TarefaService service = CommandSupport.tarefaService(req);
        Usuario usuario = CommandSupport.usuario(req);
        JsonResponse.escrever(resp, HttpServletResponse.SC_OK, service.listar(usuario));
    }
}