package com.devldots.controleestoque.Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Produto {

    private int id;
    private String nome;
    private BigDecimal precoUnidade;
    private int quantidade;

    private int idCategoria;
    private Categoria categoria;    // Mapping Attribute.

    private String uidFoto;

    private LocalDateTime dataCadastro;
    private LocalDateTime dataAlteracao;

    public Produto(){};

    public Produto(int id, String nome, BigDecimal precoUnidade, int quantidade, int idCategoria, Categoria categoria, String uidFoto, LocalDateTime dataCadastro, LocalDateTime dataAlteracao) {
        this.id = id;
        this.nome = nome;
        this.precoUnidade = precoUnidade;
        this.quantidade = quantidade;
        this.idCategoria = idCategoria;
        this.categoria = categoria;
        this.uidFoto = uidFoto;
        this.dataCadastro = dataCadastro;
        this.dataAlteracao = dataAlteracao;
    }

    public Produto(String nome, BigDecimal precoUnidade, int quantidade, Categoria categoria) {
        this.nome = nome;
        this.precoUnidade = precoUnidade;
        this.quantidade = quantidade;
        this.categoria = categoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPrecoUnidade() {
        return precoUnidade;
    }

    public void setPrecoUnidade(BigDecimal precoUnidade) {
        this.precoUnidade = precoUnidade;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getUidFoto() {
        return uidFoto;
    }

    public void setUidFoto(String uidFoto) {
        this.uidFoto = uidFoto;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(LocalDateTime dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

}
