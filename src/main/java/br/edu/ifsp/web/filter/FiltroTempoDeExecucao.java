package br.edu.ifsp.web.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class FiltroTempoDeExecucao implements Filter {

    @Override
    public void doFilter(
            ServletRequest req,
            ServletResponse resp,
            FilterChain chain)
            throws IOException, ServletException {

        long inicio = System.currentTimeMillis();

        try {
            chain.doFilter(req, resp);
        } finally {
            HttpServletRequest request = (HttpServletRequest) req;
            System.out.println(
                    request.getMethod()
                    + " "
                    + request.getRequestURI()
                    + " levou (ms): "
                    + (System.currentTimeMillis() - inicio));
        }
    }
}