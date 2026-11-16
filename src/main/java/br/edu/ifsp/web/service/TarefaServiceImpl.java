package br.edu.ifsp.web.service;

import br.edu.ifsp.web.dao.ConnectionFactory;
import br.edu.ifsp.web.dao.TarefaDAO;
import br.edu.ifsp.web.model.Tarefa;
import br.edu.ifsp.web.model.Usuario;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TarefaServiceImpl implements TarefaService {

    private final ConnectionFactory connectionFactory;

    public TarefaServiceImpl(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public List<Tarefa> listar(Usuario usuario) throws SQLException {
        validarUsuario(usuario);
        try (Connection conexao = connectionFactory.getConnection()) {
            return new TarefaDAO(conexao).listarDoUsuario(usuario);
        }
    }

    @Override
    public Tarefa buscar(Usuario usuario, int id) throws SQLException {
        validarUsuario(usuario);
        validarId(id);
        try (Connection conexao = connectionFactory.getConnection()) {
            return buscarOuFalhar(new TarefaDAO(conexao), usuario, id, false);
        }
    }

    @Override
    public Tarefa criar(Usuario usuario, String titulo, String descricao)
            throws SQLException {
        validarUsuario(usuario);
        Tarefa tarefa = criarTarefa(0, titulo, descricao, usuario);

        return executarTransacao(conexao -> {
            new TarefaDAO(conexao).inserir(tarefa, usuario);
            return tarefa;
        });
    }

    @Override
    public Tarefa atualizar(Usuario usuario, int id, String titulo, String descricao)
            throws SQLException {
        validarUsuario(usuario);
        validarId(id);
        Tarefa atualizacao = criarTarefa(id, titulo, descricao, usuario);

        return executarTransacao(conexao -> {
            TarefaDAO dao = new TarefaDAO(conexao);
            buscarOuFalhar(dao, usuario, id, true);
            if (!dao.atualizarDoUsuario(id, usuario, atualizacao)) {
                throw tarefaNaoEncontrada();
            }
            return atualizacao;
        });
    }

    @Override
    public Tarefa concluir(Usuario usuario, int id) throws SQLException {
        validarUsuario(usuario);
        validarId(id);

        return executarTransacao(conexao -> {
            TarefaDAO dao = new TarefaDAO(conexao);
            Tarefa tarefa = buscarOuFalhar(dao, usuario, id, true);
            try {
                tarefa.concluir();
            } catch (IllegalStateException e) {
                throw new TarefaJaConcluidaException(e.getMessage(), e);
            }
            if (!dao.marcarConcluidaDoUsuario(id, usuario)) {
                throw tarefaNaoEncontrada();
            }
            return tarefa;
        });
    }

    @Override
    public void excluir(Usuario usuario, int id) throws SQLException {
        validarUsuario(usuario);
        validarId(id);

        executarTransacao(conexao -> {
            if (!new TarefaDAO(conexao).deletarDoUsuario(id, usuario)) {
                throw tarefaNaoEncontrada();
            }
            return null;
        });
    }

    private <T> T executarTransacao(OperacaoTransacional<T> operacao)
            throws SQLException {
        try (Connection conexao = connectionFactory.getConnection()) {
            conexao.setAutoCommit(false);
            try {
                T resultado = operacao.executar(conexao);
                conexao.commit();
                return resultado;
            } catch (SQLException | RuntimeException e) {
                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    e.addSuppressed(erroRollback);
                }
                throw e;
            }
        }
    }

    private Tarefa buscarOuFalhar(
            TarefaDAO dao, Usuario usuario, int id, boolean bloquear)
            throws SQLException {
        Tarefa tarefa = bloquear
                ? dao.buscarDoUsuarioParaAtualizacao(id, usuario)
                : dao.buscarDoUsuario(id, usuario);
        if (tarefa == null) {
            throw tarefaNaoEncontrada();
        }
        return tarefa;
    }

    private Tarefa criarTarefa(int id, String titulo, String descricao, Usuario usuario) {
        try {
            return new Tarefa(id, titulo, descricao, usuario);
        } catch (IllegalArgumentException e) {
            throw new DadosInvalidosException(e.getMessage(), e);
        }
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId() <= 0) {
            throw new DadosInvalidosException("Usuário autenticado inválido.");
        }
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new DadosInvalidosException("O ID da tarefa deve ser positivo.");
        }
    }

    private RecursoNaoEncontradoException tarefaNaoEncontrada() {
        return new RecursoNaoEncontradoException("Tarefa não encontrada.");
    }

    @FunctionalInterface
    private interface OperacaoTransacional<T> {
        T executar(Connection conexao) throws SQLException;
    }
}
