package br.edu.ifsp.web.servlet;

import br.edu.ifsp.web.service.AplicacaoException;
import br.edu.ifsp.web.service.DadosInvalidosException;
import br.edu.ifsp.web.service.RecursoNaoEncontradoException;
import br.edu.ifsp.web.service.RegraDominioVioladaException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class JsonResponse {

    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = Logger.getLogger(JsonResponse.class.getName());

    private JsonResponse() {
    }

    public static void escrever(HttpServletResponse response, int status, Object corpo)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(GSON.toJson(corpo));
    }

    public static void erro(HttpServletResponse response, int status, String mensagem)
            throws IOException {
        JsonObject corpo = new JsonObject();
        corpo.addProperty("erro", mensagem);
        escrever(response, status, corpo);
    }

    public static void tratar(HttpServletResponse response, Exception erro)
            throws IOException {
        if (erro instanceof DadosInvalidosException) {
            erro(response, HttpServletResponse.SC_BAD_REQUEST, erro.getMessage());
        } else if (erro instanceof RecursoNaoEncontradoException) {
            erro(response, HttpServletResponse.SC_NOT_FOUND, erro.getMessage());
        } else if (erro instanceof RegraDominioVioladaException) {
            erro(response, HttpServletResponse.SC_CONFLICT, erro.getMessage());
        } else if (erro instanceof AplicacaoException) {
            erro(response, HttpServletResponse.SC_BAD_REQUEST, erro.getMessage());
        } else {
            if (erro instanceof SQLException) {
                LOGGER.log(Level.SEVERE, "Falha ao acessar o banco de dados.", erro);
            } else {
                LOGGER.log(Level.SEVERE, "Falha interna ao processar a requisição.", erro);
            }
            erro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Não foi possível processar a requisição.");
        }
    }
}
