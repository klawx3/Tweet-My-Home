package com.qmpc.core;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Klaw Strife
 */
public class Juego {
    public static final int RANGO_LIMITE_DE_NUMEROS = 10;

    private List<Problema> problemas;
    private List<Problema> problemasIncorrectos;
    private int cantidadDeProblemas;
    private Integer tablaEstudiar;

    private float TOTALpuntajePorEjerciciosCorrecto;
    private float TOTALpuntajePorTiempo;
    private float TOTALPuntaje;
    private float puntajeTotalAprovacion;

    private int millisegAprovadosPorEjercicio;

    private int maxPuntajePorTiempo;
    private int maxPuntajePorEjerciciosCorrecto;
    
    private Integer resultadosCorrectos;
    private int millisegundosTotales;
    private boolean juegoTerminado;



    public Juego(int cantidadDeProblemas,Integer tablaEstudiar,
            int maxPuntajePorTiempo,int maxPuntajePorEjerciciosCorrecto
            ,int millisegAprovadosPorEjercicio,int puntajeTotalAprovacion) throws Exception{
        
        if (cantidadDeProblemas < 1) throw new Exception("Debe tener almenos 1 problema");
        this.cantidadDeProblemas = cantidadDeProblemas;
        this.tablaEstudiar = tablaEstudiar;
        this.maxPuntajePorTiempo = maxPuntajePorTiempo;
        this.maxPuntajePorEjerciciosCorrecto = maxPuntajePorEjerciciosCorrecto;
        this.millisegAprovadosPorEjercicio = millisegAprovadosPorEjercicio;
        this.puntajeTotalAprovacion = puntajeTotalAprovacion;
        
    }
    
    public void generarProblemas() {
        problemas = new ArrayList<>();

//        Collections.fill(problemas, new Problema(RANGO_LIMITE_DE_NUMEROS,tablaEstudiar,Problema.MULTIPLICACION));
        for (int i = 0; i < cantidadDeProblemas; i++) {
            problemas.add(new Problema(RANGO_LIMITE_DE_NUMEROS,tablaEstudiar,Problema.MULTIPLICACION));
        }
    }
    
    public List<Problema> getProblemas(){
        return problemas;        
    }
    
    public List<Problema> getProblemasIncorrectos(){
        return problemasIncorrectos;
    }
    

    
    public Map<Problema, Integer> getFrecuenciaProblemasIncorrectos() {
        Map<Problema, Integer> mapa = new HashMap<>();
        problemasIncorrectos.forEach((p) -> {
            int e = 1;
            for (Problema p2 : problemasIncorrectos) {
                if (p2.equals(p)) {
                    if (e == 1) {
                        mapa.put(p, e);
                    } else {
                        mapa.replace(p, e);
                    }
                    e++;
                }
            }
        });
        return mapa;
    }

    private void setProblemasIncorrectos() {
        problemasIncorrectos = new ArrayList<>();
        problemas.stream().forEach((p) -> {
            try {
                if (!p.isResultadoCorrecto()) {
                    problemasIncorrectos.add(p);
                }
            } catch (Exception ex) {
                Logger.getLogger(Juego.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    public float getPuntajeUsuario() throws Exception {
        return TOTALPuntaje;
    }

    public float getNota10_70() {
        if (juegoTerminado) {
            if (TOTALPuntaje >= puntajeTotalAprovacion) {
                float maxpuntaje = TOTALpuntajePorTiempo + TOTALpuntajePorEjerciciosCorrecto;
                float m = (30 / (maxpuntaje - puntajeTotalAprovacion));
                float c = 70 - (m * maxpuntaje);
                return m * TOTALPuntaje + c;
            } else {
                float m = (30/puntajeTotalAprovacion);
                float c = 10;
                return m * TOTALPuntaje + c;
            }
        } else {
            return -1;
        }
    }
    
    public boolean isAprovado(){
        return TOTALPuntaje >= puntajeTotalAprovacion;
    }

    public int getResultadosCorrectos() {
        return resultadosCorrectos;
    }

    public int getMillisegundosPromedio() {
        return millisegundosTotales /cantidadDeProblemas;
    }
    
    public void terminarJuego() throws Exception {
        resultadosCorrectos = 0;
        millisegundosTotales = 0;
        for (Problema p : problemas) {
            if (p.isResultadoCorrecto()) {
                resultadosCorrectos++;
            }
            millisegundosTotales += p.getTiempoDeResolucion();
        }
        TOTALpuntajePorTiempo = determinarPuntajePorTiempo(millisegundosTotales);
        TOTALpuntajePorEjerciciosCorrecto = determinarPuntajePorEjerciciosCorrectos(resultadosCorrectos);
        TOTALPuntaje = TOTALpuntajePorTiempo+TOTALpuntajePorEjerciciosCorrecto;
        juegoTerminado = true;
        /*-----------*/
        setProblemasIncorrectos();
    }
    
    public boolean isJuegoTerminado(){
        return juegoTerminado;
    }

    private float determinarPuntajePorTiempo(int tiempoTotal) {
        float tiempoPromedioPorEjercicio = (tiempoTotal / cantidadDeProblemas);
        if(millisegAprovadosPorEjercicio/tiempoPromedioPorEjercicio >= 1f){
            return maxPuntajePorTiempo;
        }
        return millisegAprovadosPorEjercicio/tiempoPromedioPorEjercicio  * maxPuntajePorTiempo;
    }

    private float determinarPuntajePorEjerciciosCorrectos(int resultadosCorrectos) {
        float k = ((float)resultadosCorrectos) / ((float)cantidadDeProblemas);
        float puntaje = k * maxPuntajePorEjerciciosCorrecto;
        return puntaje;
    }


    
}
