package com.example.practicaFinal.excepciones;

public class ExcepcionPersonalizada extends RuntimeException {
    public ExcepcionPersonalizada(String mensaje) {
        super(mensaje);
    }
}
