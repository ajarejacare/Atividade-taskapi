package br.edu.ifsp.web.listener;

import br.edu.ifsp.web.dao.ConnectionFactory;
import br.edu.ifsp.web.service.ServiceFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class IniciaAplicacaoListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        ServletContext contexto = sce.getServletContext();

        ServiceFactory services = new ServiceFactory(new ConnectionFactory());

        contexto.setAttribute("tarefaService", services.tarefaService());
        contexto.setAttribute("autenticacaoService", services.autenticacaoService());

        System.out.println("=== Aplicacao iniciada ===");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        System.out.println("=== Aplicacao encerrada ===");
    }
}