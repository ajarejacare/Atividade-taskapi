package br.edu.ifsp.web.model;

public class Usuario {

    private int id;
    private String nome;
    private String email;
    private transient String credencial;

    public Usuario(int id, String nome, String email, String credencial) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.credencial = credencial;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getCredencial() {
        return credencial;
    }
}
