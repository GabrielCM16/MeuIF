package com.example.meuif.sepae.Merenda;

public class DataPoint {
    private String mesAux;
    private int quantity;

    public DataPoint(String mesAux, int quantity) {
        this.mesAux = mesAux;
        this.quantity = quantity;
    }

    public String getMesAux() {
        return mesAux.substring(3, 5);
    }

    public String getDiaAux() {
        return mesAux;
    }

    public int getQuantity() {
        return quantity;
    }
}