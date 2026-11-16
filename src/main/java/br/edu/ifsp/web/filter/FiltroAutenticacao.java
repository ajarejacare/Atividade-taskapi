package br.edu.ifsp.web.filter;

import br.edu.ifsp.web.model.Usuario;
import br.edu.ifsp.web.servlet.JsonResponse;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class FiltroAutenticacao implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession sessao = httpRequest.getSession(false);
        Object usuario = sessao == null ? null : sessao.getAttribute("usuario");
        if (!(usuario instanceof Usuario)) {
            JsonResponse.erro((HttpServletResponse) response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Autenticação necessária.");
            return;
        }

        request.setAttribute("usuario", usuario);
        chain.doFilter(request, response);
    }
}
