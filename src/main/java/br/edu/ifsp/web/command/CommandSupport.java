package br.edu.ifsp.web.command;

import br.edu.ifsp.web.model.Usuario;
import br.edu.ifsp.web.service.DadosInvalidosException;
import br.edu.ifsp.web.service.TarefaService;

import jakarta.servlet.http.HttpServletRequest;

public final class CommandSupport {

    private CommandSupport() {
    }

    public static TarefaService tarefaService(HttpServletRequest request) {
        return (TarefaService) request.getServletContext().getAttribute("tarefaService");
    }

    public static Usuario usuario(HttpServletRequest request) {
        Object usuario = request.getAttribute("usuario");
        if (!(usuario instanceof Usuario)) {
            throw new DadosInvalidosException("Usuário autenticado não encontrado.");
        }
        return (Usuario) usuario;
    }

    public static int id(HttpServletRequest request) {
        String path = request.getPathInfo();
        String[] partes = path == null ? new String[0] : path.split("/");
        String valor = partes.length > 1 ? partes[1] : "";
        try {
            int id = Integer.parseInt(valor);
            if (id <= 0) {
                throw new NumberFormatException();
            }
            return id;
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("ID de tarefa inválido.", e);
        }
    }
}
