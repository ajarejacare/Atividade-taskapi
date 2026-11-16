package br.edu.ifsp.web.servlet;

import br.edu.ifsp.web.service.DadosInvalidosException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class JsonRequest {

    private JsonRequest() {
    }

    public static JsonObject lerObjeto(HttpServletRequest request) throws IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            JsonElement elemento = JsonParser.parseReader(request.getReader());
            if (elemento == null || !elemento.isJsonObject()) {
                throw new DadosInvalidosException("O corpo deve ser um objeto JSON.");
            }
            return elemento.getAsJsonObject();
        } catch (JsonParseException | IllegalStateException e) {
            throw new DadosInvalidosException("O corpo da requisição não contém JSON válido.", e);
        }
    }

    public static String texto(JsonObject objeto, String campo, boolean obrigatorio) {
        JsonElement valor = objeto.get(campo);
        if (valor == null || valor.isJsonNull()) {
            if (obrigatorio) {
                throw new DadosInvalidosException("O campo \"" + campo + "\" é obrigatório.");
            }
            return null;
        }
        if (!valor.isJsonPrimitive() || !valor.getAsJsonPrimitive().isString()) {
            throw new DadosInvalidosException(
                    "O campo \"" + campo + "\" deve ser um texto.");
        }
        return valor.getAsString();
    }
}
