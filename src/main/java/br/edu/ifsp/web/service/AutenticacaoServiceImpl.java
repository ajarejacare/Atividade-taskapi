package br.edu.ifsp.web.service;

import br.edu.ifsp.web.dao.ConnectionFactory;
import br.edu.ifsp.web.dao.UsuarioDAO;
import br.edu.ifsp.web.model.Usuario;
import br.edu.ifsp.web.service.AutenticacaoService;

import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.SQLException;

public class AutenticacaoServiceImpl implements AutenticacaoService {

    private static final String CREDENCIAL_DUMMY =
            "pbkdf2_sha256$600000$AAAAAAAAAAAAAAAAAAAAAA==$"
                    + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";

    private final ConnectionFactory connectionFactory;
    private final Pbkdf2CredentialHasher credentialHasher;

    public AutenticacaoServiceImpl(ConnectionFactory connectionFactory) {
        this(connectionFactory, new Pbkdf2CredentialHasher());
    }

    public AutenticacaoServiceImpl(
            ConnectionFactory connectionFactory,
            Pbkdf2CredentialHasher credentialHasher) {
        this.connectionFactory = connectionFactory;
        this.credentialHasher = credentialHasher;
    }

    @Override
    public Usuario autenticar(String email, String senha) throws SQLException {
        if (email == null || email.isBlank() || senha == null || senha.isEmpty()) {
            throw new DadosInvalidosException("E-mail e senha são obrigatórios.");
        }

        Usuario usuario;
        try (Connection conexao = connectionFactory.getConnection()) {
            usuario = new UsuarioDAO(conexao).buscarPorEmail(email.trim());
        }

        String credencial = usuario == null ? CREDENCIAL_DUMMY : usuario.getCredencial();
        boolean senhaValida;
        try {
            senhaValida = credentialHasher.verificar(senha, credencial);
        } catch (GeneralSecurityException e) {
            throw new SQLException("Não foi possível verificar a credencial.", e);
        }

        if (usuario == null || !senhaValida) {
            throw new RegraDominioVioladaException("E-mail ou senha inválidos.");
        }

        return new Usuario(usuario.getId(), usuario.getNome(), usuario.getEmail(), null);
    }
}
