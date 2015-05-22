package com.qmpc.core;


import com.sun.webkit.Timer;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Klaw Strife
 */
public final class Problema {

    public static final char MULTIPLICACION = '*';

    private char operador;
    private int resultado;
    private int operando_1;
    private int operando_2;
    private int posibleResultado;

    
    private int rango_final;
    private Random r;
    private long tiempoCreacion;
    private long tiempoFinalizacion;
    private long tiempoResolucion;

    public Problema(int rangoDeNumerosRandom, Integer numeroMultiplicacionFija, char operador) {
        tiempoCreacion = -1;
        tiempoFinalizacion = -1;
        posibleResultado = -1;
        this.operador = operador;
        this.rango_final = rangoDeNumerosRandom;
        r = new Random();
        if (numeroMultiplicacionFija != null) {
            operando_1 = numeroMultiplicacionFija;
        } else {
            operando_1 = r.nextInt(rangoDeNumerosRandom) + 1;
        }

        operando_2 = r.nextInt(rangoDeNumerosRandom) + 1;
        switch (operador) {
            case '*':
                resultado = operando_1 * operando_2;
                break;
            default:
                //resultado = operando_1 * operando_2;
                break;
        }

    }
    
    public int getPosibleResultado() {
        return posibleResultado;
    }

    public void startConteoProblema() {
        tiempoCreacion = System.currentTimeMillis();
    }

    public void setPosibleResultado(int posibleResultado) throws Exception {
        if (tiempoCreacion == -1) {
            throw new Exception("No ha iniciado el conteo del problema aún");
        }
        tiempoFinalizacion = System.currentTimeMillis();
        tiempoResolucion = tiempoFinalizacion - tiempoCreacion;
        this.posibleResultado = posibleResultado;
    }

    public long getTiempoDeResolucion() throws Exception {
        if (tiempoFinalizacion != -1 && tiempoCreacion != -1) {
            return tiempoResolucion;
        }
        throw new Exception("No se ha iniciado el conteo de el problema");
    }

    public boolean isResultadoCorrecto() throws Exception {
        if (posibleResultado == -1) {
            throw new Exception("No se ha ingresado ningun resultado posible del problema");
        }
        return resultado == posibleResultado;
    }

    public char getOperador() {
        return operador;
    }

    public int getResultado() {
        return resultado;
    }

    public int getOperando_1() {
        return operando_1;
    }

    public int getOperando_2() {
        return operando_2;
    }

    @Override
    public String toString() {
        return "Problema{" + "resultado=" + resultado + ", operando_1=" + operando_1 + ", operando_2=" + operando_2 + ", posibleResultado=" + posibleResultado + ", tiempoResolucion=" + tiempoResolucion + '}';
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.operando_1;
        hash = 89 * hash + this.operando_2;
        hash = 89 * hash + this.posibleResultado;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Problema other = (Problema) obj;
        if (this.operando_1 != other.operando_1) {
            return false;
        }
        if (this.operando_2 != other.operando_2) {
            return false;
        }
        if (this.posibleResultado != other.posibleResultado) {
            return false;
        }
        return true;
    }

}
