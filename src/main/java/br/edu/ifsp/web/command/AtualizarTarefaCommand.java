package br.edu.ifsp.web.command;

import br.edu.ifsp.web.model.Usuario;
import br.edu.ifsp.web.service.TarefaService;
import br.edu.ifsp.web.servlet.JsonRequest;
import br.edu.ifsp.web.servlet.JsonResponse;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AtualizarTarefaCommand implements Command {

    @Override
    public void executar(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {

        int id = CommandSupport.id(req);
        JsonObject dados = JsonRequest.lerObjeto(req);
        String titulo = JsonRequest.texto(dados, "titulo", true);
        String descricao = JsonRequest.texto(dados, "descricao", false);
        TarefaService service = CommandSupport.tarefaService(req);
        Usuario usuario = CommandSupport.usuario(req);
        JsonResponse.escrever(resp, HttpServletResponse.SC_OK,
                service.atualizar(usuario, id, titulo, descricao));
    }
}