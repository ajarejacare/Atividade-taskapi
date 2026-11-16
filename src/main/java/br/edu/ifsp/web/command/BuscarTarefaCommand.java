package br.edu.ifsp.web.command;

import br.edu.ifsp.web.model.Usuario;
import br.edu.ifsp.web.servlet.JsonResponse;
import br.edu.ifsp.web.service.TarefaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BuscarTarefaCommand implements Command {

    @Override
    public void executar(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = CommandSupport.id(request);
        TarefaService service = CommandSupport.tarefaService(request);
        Usuario usuario = CommandSupport.usuario(request);
        JsonResponse.escrever(response, HttpServletResponse.SC_OK,
                service.buscar(usuario, id));
    }
}
