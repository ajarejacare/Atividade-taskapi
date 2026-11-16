package br.edu.ifsp.web.service;

import br.edu.ifsp.web.model.Usuario;

import java.sql.SQLException;

public interface AutenticacaoService {

    Usuario autenticar(String email, String senha) throws SQLException;
}
