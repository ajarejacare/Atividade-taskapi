package br.edu.ifsp.web.servlet;

import br.edu.ifsp.web.model.Usuario;
import br.edu.ifsp.web.listener.ContadorSessoesListener;
import br.edu.ifsp.web.service.AutenticacaoService;
import br.edu.ifsp.web.service.RegraDominioVioladaException;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class AutenticacaoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String path = request.getPathInfo();
        if ("/login".equals(path)) {
            login(request, response);
        } else if ("/logout".equals(path)) {
            logout(request, response);
        } else if ("/me".equals(path)) {
            me(request, response);
        } else {
            JsonResponse.erro(response, HttpServletResponse.SC_NOT_FOUND,
                    "Recurso de autenticação não encontrado.");
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!"POST".equals(request.getMethod())) {
            response.setHeader("Allow", "POST");
            JsonResponse.erro(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "Método não permitido para este recurso.");
            return;
        }

        try {
            JsonObject dados = JsonRequest.lerObjeto(request);
            String email = JsonRequest.texto(dados, "email", true);
            String senha = JsonRequest.texto(dados, "senha", true);
            HttpSession sessao = request.getSession(true);
            sessao.removeAttribute("usuario");
            AutenticacaoService service = (AutenticacaoService) request
                    .getServletContext().getAttribute("autenticacaoService");
            Usuario usuario = service.autenticar(email, senha);

            request.changeSessionId();
            sessao.setAttribute("usuario",
                    new Usuario(usuario.getId(), usuario.getNome(), usuario.getEmail(), null));
            JsonResponse.escrever(response, HttpServletResponse.SC_OK, usuario);
        } catch (RegraDominioVioladaException e) {
            JsonResponse.erro(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            JsonResponse.tratar(response, e);
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!"POST".equals(request.getMethod())) {
            response.setHeader("Allow", "POST");
            JsonResponse.erro(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "Método não permitido para este recurso.");
            return;
        }
        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private void me(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!"GET".equals(request.getMethod())) {
            response.setHeader("Allow", "GET");
            JsonResponse.erro(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "Método não permitido para este recurso.");
            return;
        }
        HttpSession sessao = request.getSession(false);
        Object usuario = sessao == null ? null : sessao.getAttribute("usuario");
        if (!(usuario instanceof Usuario)) {
            JsonResponse.erro(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Autenticação necessária.");
            return;
        }
        JsonResponse.escrever(response, HttpServletResponse.SC_OK,
                new DadosSessao((Usuario) usuario,
                        ContadorSessoesListener.getSessoesAtivas()));
    }

    private static final class DadosSessao {

        private final Usuario usuario;
        private final int sessoesAtivas;

        private DadosSessao(Usuario usuario, int sessoesAtivas) {
            this.usuario = usuario;
            this.sessoesAtivas = sessoesAtivas;
        }
    }
}
