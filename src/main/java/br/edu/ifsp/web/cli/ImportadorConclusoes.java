package br.edu.ifsp.web.cli;

import br.edu.ifsp.web.model.Usuario;
import br.edu.ifsp.web.service.DadosInvalidosException;
import br.edu.ifsp.web.service.RecursoNaoEncontradoException;
import br.edu.ifsp.web.service.RegraDominioVioladaException;
import br.edu.ifsp.web.service.ServiceFactory;
import br.edu.ifsp.web.service.TarefaJaConcluidaException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public final class ImportadorConclusoes {

    private ImportadorConclusoes() {
    }

    public static void main(String[] args) {
        int resultado = executar(args, System.out, System.err);
        if (resultado != 0) {
            System.exit(resultado);
        }
    }

    static int executar(String[] args, PrintStream out, PrintStream err) {
        if (args.length != 3) {
            err.println("Uso: ImportadorConclusoes <arquivo-ids.txt> <email> <senha>");
            return 2;
        }

        ServiceFactory services = new ServiceFactory();
        Usuario usuario;
        try {
            usuario = services.autenticacaoService().autenticar(args[1], args[2]);
        } catch (RegraDominioVioladaException e) {
            err.println("Falha na autenticação: " + e.getMessage());
            return 1;
        } catch (SQLException | DadosInvalidosException e) {
            err.println("Não foi possível autenticar o usuário: " + e.getMessage());
            return 1;
        }

        Path arquivo = Path.of(args[0]);
        try (BufferedReader reader = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            String linha;
            int numeroLinha = 0;
            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                if (numeroLinha == 1 && linha.startsWith("\uFEFF")) {
                    linha = linha.substring(1);
                }
                concluirLinha(linha, numeroLinha, usuario, services, out, err);
            }
        } catch (IOException e) {
            err.println("Não foi possível ler o arquivo de IDs: " + e.getMessage());
            return 1;
        }
        return 0;
    }

    private static void concluirLinha(
            String linha, int numeroLinha, Usuario usuario, ServiceFactory services,
            PrintStream out, PrintStream err) {
        String valor = linha.trim();
        try {
            int id = Integer.parseInt(valor);
            services.tarefaService().concluir(usuario, id);
            out.println("linha " + numeroLinha + ": tarefa " + id + " concluída");
        } catch (NumberFormatException | DadosInvalidosException e) {
            out.println("linha " + numeroLinha + ": ID inválido");
        } catch (RecursoNaoEncontradoException e) {
            out.println("linha " + numeroLinha + ": tarefa " + valor + " não encontrada");
        } catch (TarefaJaConcluidaException e) {
            out.println("linha " + numeroLinha + ": tarefa " + valor + " já estava concluída");
        } catch (RegraDominioVioladaException e) {
            out.println("linha " + numeroLinha + ": não foi possível concluir tarefa " + valor);
        } catch (SQLException e) {
            out.println("linha " + numeroLinha + ": erro ao concluir tarefa " + valor);
            err.println("Erro de banco na linha " + numeroLinha + ": " + e.getMessage());
        }
    }
}
