package com.example.demo;


public class Quotation {
    private String server;
    private int integer;

    public Quotation(String server, int integer) {
        this.server = server;
        this.integer = integer;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    @Override
    public String toString() {
        return "{\"Quotation\":{"
                + "\"server\":\"" + server + "\""
                + ",\"integer\":\"" + integer + "\""
                + "}}";
    }
}
