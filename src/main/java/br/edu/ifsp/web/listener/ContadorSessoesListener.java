package br.edu.ifsp.web.listener;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;

public class ContadorSessoesListener implements HttpSessionListener {

    private static final AtomicInteger SESSOES_ATIVAS = new AtomicInteger();

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        int ativas = SESSOES_ATIVAS.incrementAndGet();
        System.out.println("Sessão criada: " + event.getSession().getId()
                + " | sessões ativas: " + ativas);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        int ativas = SESSOES_ATIVAS.updateAndGet(contagem -> Math.max(0, contagem - 1));
        System.out.println("Sessão destruída: " + event.getSession().getId()
                + " | sessões ativas: " + ativas);
    }

    public static int getSessoesAtivas() {
        return SESSOES_ATIVAS.get();
    }
}
