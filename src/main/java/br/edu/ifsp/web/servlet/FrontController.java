package br.edu.ifsp.web.servlet;

import br.edu.ifsp.web.command.AtualizarTarefaCommand;
import br.edu.ifsp.web.command.BuscarTarefaCommand;
import br.edu.ifsp.web.command.Command;
import br.edu.ifsp.web.command.ConcluirTarefaCommand;
import br.edu.ifsp.web.command.CriarTarefaCommand;
import br.edu.ifsp.web.command.DeletarTarefaCommand;
import br.edu.ifsp.web.command.ListarTarefasCommand;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FrontController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            Command comando = resolverComando(req.getMethod(), req.getPathInfo());
            if (comando == null) {
                if (!recursoConhecido(req.getPathInfo())) {
                    JsonResponse.erro(
                            resp, HttpServletResponse.SC_NOT_FOUND,
                            "Recurso não encontrado.");
                } else {
                    resp.setHeader("Allow", metodosPermitidos(req.getPathInfo()));
                    JsonResponse.erro(
                            resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                            "Método não permitido para este recurso.");
                }
                return;
            }
            comando.executar(req, resp);
        } catch (Exception e) {
            JsonResponse.tratar(resp, e);
        }
    }

    private Command resolverComando(String metodo, String caminho) {
        String path = caminho == null || "/".equals(caminho) ? "" : caminho;
        if (path.isEmpty()) {
            if ("GET".equals(metodo)) {
                return new ListarTarefasCommand();
            }
            if ("POST".equals(metodo)) {
                return new CriarTarefaCommand();
            }
            return null;
        }
        if (path.matches("/\\d+")) {
            if ("GET".equals(metodo)) {
                return new BuscarTarefaCommand();
            }
            if ("PUT".equals(metodo)) {
                return new AtualizarTarefaCommand();
            }
            if ("PATCH".equals(metodo)) {
                return new ConcluirTarefaCommand();
            }
            if ("DELETE".equals(metodo)) {
                return new DeletarTarefaCommand();
            }
            return null;
        }
        return null;
    }

    private boolean recursoConhecido(String caminho) {
        String path = caminho == null || "/".equals(caminho) ? "" : caminho;
        return path.isEmpty() || path.matches("/\\d+");
    }

    private String metodosPermitidos(String caminho) {
        String path = caminho == null || "/".equals(caminho) ? "" : caminho;
        if (path.isEmpty()) {
            return "GET, POST";
        }
        if (path.matches("/\\d+")) {
            return "GET, PUT, PATCH, DELETE";
        }
        return "";
    }
}
