package br.edu.ifsp.web.service;

import br.edu.ifsp.web.model.Tarefa;
import br.edu.ifsp.web.model.Usuario;

import java.sql.SQLException;
import java.util.List;

public interface TarefaService {

    List<Tarefa> listar(Usuario usuario) throws SQLException;

    Tarefa buscar(Usuario usuario, int id) throws SQLException;

    Tarefa criar(Usuario usuario, String titulo, String descricao) throws SQLException;

    Tarefa atualizar(Usuario usuario, int id, String titulo, String descricao)
            throws SQLException;

    Tarefa concluir(Usuario usuario, int id) throws SQLException;

    void excluir(Usuario usuario, int id) throws SQLException;
}
