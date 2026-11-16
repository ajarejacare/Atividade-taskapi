package br.edu.ifsp.web.command;

import br.edu.ifsp.web.model.Tarefa;
import br.edu.ifsp.web.model.Usuario;
import br.edu.ifsp.web.service.TarefaService;
import br.edu.ifsp.web.servlet.JsonRequest;
import br.edu.ifsp.web.servlet.JsonResponse;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CriarTarefaCommand implements Command {

    @Override
    public void executar(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {

        JsonObject dados = JsonRequest.lerObjeto(req);
        String titulo = JsonRequest.texto(dados, "titulo", true);
        String descricao = JsonRequest.texto(dados, "descricao", false);
        TarefaService service = CommandSupport.tarefaService(req);
        Usuario usuario = CommandSupport.usuario(req);
        Tarefa tarefa = service.criar(usuario, titulo, descricao);
        resp.setHeader("Location", req.getContextPath() + "/tarefas/" + tarefa.getId());
        JsonResponse.escrever(resp, HttpServletResponse.SC_CREATED, tarefa);
    }
}