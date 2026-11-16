package br.edu.ifsp.web.dao;

import br.edu.ifsp.web.model.Tarefa;
import br.edu.ifsp.web.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {

    private final Connection conexao;

    public TarefaDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public List<Tarefa> listarDoUsuario(Usuario usuario) throws SQLException {
        String sql = "SELECT id, titulo, descricao, concluida FROM tarefas "
                + "WHERE usuario_id = ? ORDER BY id";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, usuario.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                List<Tarefa> tarefas = new ArrayList<>();
                while (rs.next()) {
                    tarefas.add(mapearTarefa(rs, usuario));
                }
                return tarefas;
            }
        }
    }

    public Tarefa buscarDoUsuario(int id, Usuario usuario) throws SQLException {
        return buscarDoUsuario(id, usuario, false);
    }

    public Tarefa buscarDoUsuarioParaAtualizacao(int id, Usuario usuario)
            throws SQLException {
        return buscarDoUsuario(id, usuario, true);
    }

    private Tarefa buscarDoUsuario(int id, Usuario usuario, boolean bloquear)
            throws SQLException {
        String sql = "SELECT id, titulo, descricao, concluida FROM tarefas "
                + "WHERE id = ? AND usuario_id = ?"
                + (bloquear ? " FOR UPDATE" : "");
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, usuario.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapearTarefa(rs, usuario) : null;
            }
        }
    }

    public void inserir(Tarefa tarefa, Usuario usuario) throws SQLException {
        String sql = "INSERT INTO tarefas (titulo, descricao, concluida, usuario_id) "
                + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());
            stmt.setBoolean(3, tarefa.isConcluida());
            stmt.setInt(4, usuario.getId());
            stmt.executeUpdate();
            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (!chaves.next()) {
                    throw new SQLException("A inserção não retornou o ID da tarefa.");
                }
                tarefa.setId(chaves.getInt(1));
            }
        }
    }

    public boolean atualizarDoUsuario(int id, Usuario usuario, Tarefa tarefa)
            throws SQLException {
        String sql = "UPDATE tarefas SET titulo = ?, descricao = ? "
                + "WHERE id = ? AND usuario_id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());
            stmt.setInt(3, id);
            stmt.setInt(4, usuario.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean marcarConcluidaDoUsuario(int id, Usuario usuario)
            throws SQLException {
        String sql = "UPDATE tarefas SET concluida = 1 WHERE id = ? AND usuario_id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, usuario.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deletarDoUsuario(int id, Usuario usuario) throws SQLException {
        String sql = "DELETE FROM tarefas WHERE id = ? AND usuario_id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, usuario.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    private Tarefa mapearTarefa(ResultSet rs, Usuario usuario) throws SQLException {
        Tarefa tarefa = new Tarefa(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("descricao"),
                usuario);
        if (rs.getBoolean("concluida")) {
            tarefa.concluir();
        }
        return tarefa;
    }

}