package com.qmpc.gui;


import com.qmpc.core.Juego;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import print.Printer;
import print.Printer.Types;
import print.color.Ansi.*;
import print.color.ColoredPrinter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Klaw Strife
 */
public class NewMain {

    public static final boolean DEBUGGIN = false;
    public static final int PUNTAJE_POR_TIEMPO = 3000;
    public static final int PUNTAJE_POR_CORRECTOS = 7000;
    public static final int CANTIDAD_MILLISEGUNDOS_DESPUES_DE_DESCONTAR = 2500;
    public static final int PUNTAJE_APROVACION = 8500;

    public static final String CONSIDERACIONES = "Para aprovar nececita [" + PUNTAJE_APROVACION + "] Puntos";
    private static Scanner leer;
    public static Printer p;

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void modoQuieroMiPc() throws Exception{
        
            boolean terminado = false;
            int tablaParaAprender = getEntero("\nIngrese TABLA a estudiar:");
            int cantidadEjercicios = getEntero("\nIngrese CANTIDAD de ejercicios:");
            System.out.println(CONSIDERACIONES);
            do {
                Juego game = null;
                try {
                    game = new Juego(cantidadEjercicios, tablaParaAprender,
                            PUNTAJE_POR_TIEMPO,
                            PUNTAJE_POR_CORRECTOS,
                            CANTIDAD_MILLISEGUNDOS_DESPUES_DE_DESCONTAR,
                            PUNTAJE_APROVACION);
                } catch (Exception ex) {
                    Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(1);
                }
                game.generarProblemas();
                game.getProblemas().forEach(pr -> {
                    pr.startConteoProblema();
                    try {
                        pr.setPosibleResultado(getEntero(String.format("\n%d %c %d :",
                                pr.getOperando_1(),
                                pr.getOperador(),
                                pr.getOperando_2())
                        ));
                        if (DEBUGGIN) {
                            System.err.println(pr.toString());
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                game.terminarJuego();
                separador();
                p.println("EJERCICIOS:");
                p.println("\tCORRECTOS:\t" + game.getResultadosCorrectos());
                p.println("\tINCORRECTOS:\t" + (cantidadEjercicios - game.getResultadosCorrectos()));
                p.println("\tMILLISEGUNDOS PROMEDIO POR EJERCICIO:" + game.getMillisegundosPromedio());
                p.println("\nPUNTAJE TOTAL:\t\t" + game.getPuntajeUsuario());
                p.println("PUNTAJE ESPERADO:\t" + PUNTAJE_APROVACION);
                p.println(String.format("NOTA[1.0 - 7.0]:\t%.3f", game.getNota10_70() / 10F));
                separador();
                if (game.getFrecuenciaProblemasIncorrectos() != null) {
                    if (!game.getFrecuenciaProblemasIncorrectos().isEmpty()) {
                        p.println("FRECUENCIA DE PROBLEMAS INCORRECTOS:");
                        p.println("\tPROBLEMA\t\tFRECUENCIA");
                        game.getFrecuenciaProblemasIncorrectos().forEach((prom, frec) -> {

                            String fstring = String.format("\t[%d %c %d = '%d']\t\t%d",
                                    prom.getOperando_1(), prom.getOperador(),
                                    prom.getOperando_2(), prom.getPosibleResultado(),
                                    frec);
                            p.println(fstring);
                        });
                        separador();
                    }

                }

                if (game.isAprovado()) {
                    p.println("WENA VIEJO AHORA PUEDES JUGAR PC !!!!");
                } else {
                    p.println("INTENTALO DENUEVO... MUY NOOB TODABIA");
                }
                separador();
                p.println("¿ Desea intentarlo denuevo ?(s/n):");
                String f = leer.next();

                if (!f.startsWith("s")) {
                    terminado = true;
                }
                limpiarPantalla();
            } while (!terminado);
    }
    
    private static void modoEstudioTablas() throws Exception {
         boolean terminado = false;
            int tablaParaAprender = getEntero("\nIngrese TABLA a estudiar:");
            int cantidadEjercicios = getEntero("\nIngrese CANTIDAD de ejercicios:");
            System.out.println(CONSIDERACIONES);
            do {
                Juego game = null;
                try {
                    game = new Juego(cantidadEjercicios, tablaParaAprender,
                            PUNTAJE_POR_TIEMPO,
                            PUNTAJE_POR_CORRECTOS,
                            CANTIDAD_MILLISEGUNDOS_DESPUES_DE_DESCONTAR,
                            PUNTAJE_APROVACION);
                } catch (Exception ex) {
                    Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(1);
                }
                game.generarProblemas();
                game.getProblemas().forEach(pr -> {
                    pr.startConteoProblema();
                    try {
                        pr.setPosibleResultado(getEntero(String.format("\n%d %c %d :",
                                pr.getOperando_1(),
                                pr.getOperador(),
                                pr.getOperando_2())
                        ));
                        if(pr.isResultadoCorrecto()){
                            p.print("¡ Resultado correcto !");
                        }else{
                            p.println(String.format("\nError de resultado... Ccrreccion: %d %c %d : %d",
                                pr.getOperando_1(),
                                pr.getOperador(),
                                pr.getOperando_2(),
                                pr.getResultado())
                            );
                        
                        }
                        
                        
                    } catch (Exception ex) {
                        Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                game.terminarJuego();
                separador();
                p.println("EJERCICIOS:");
                p.println("\tCORRECTOS:\t" + game.getResultadosCorrectos());
                p.println("\tINCORRECTOS:\t" + (cantidadEjercicios - game.getResultadosCorrectos()));
                p.println("\tMILLISEGUNDOS PROMEDIO POR EJERCICIO:" + game.getMillisegundosPromedio());
                p.println("\nPUNTAJE TOTAL:\t\t" + game.getPuntajeUsuario());
                p.println("PUNTAJE ESPERADO:\t" + PUNTAJE_APROVACION);
                p.println(String.format("NOTA[1.0 - 7.0]:\t%.1f", game.getNota10_70() / 10));
                separador();
                if (game.getFrecuenciaProblemasIncorrectos() != null) {
                    if (!game.getFrecuenciaProblemasIncorrectos().isEmpty()) {
                        p.println("FRECUENCIA DE PROBLEMAS INCORRECTOS:");
                        p.println("\tPROBLEMA\t\tFRECUENCIA");
                        game.getFrecuenciaProblemasIncorrectos().forEach((prom, frec) -> {

                            String fstring = String.format("\t[%d %c %d = '%d']\t\t%d",
                                    prom.getOperando_1(), prom.getOperador(),
                                    prom.getOperando_2(), prom.getPosibleResultado(),
                                    frec);
                            p.println(fstring);
                        });
                        separador();
                    }

                }

                if (game.isAprovado()) {
                    p.println("NICE... INTENTA AHORA EL MODO QUIERO MI PC");
                } else {
                    p.println("TODABIA ESTAS MUY NAAB");
                }
                separador();
                p.println("¿ Desea intentarlo denuevo ?(s/n):");
                String f = leer.next();

                if (!f.startsWith("s")) {
                    terminado = true;
                }
                limpiarPantalla();
            } while (!terminado);
    }

    public static void main(String[] args) throws Exception {
        p = new Printer.Builder(Types.TERM).timestamping(false).build();
        leer = new Scanner(System.in);
        int opcion = -1;
        do {
            limpiarPantalla();
            separador();
            p.println("(1) Modo 'Quiero mi pc'");
            p.println("(2) Modo 'Estudio de las tablas'");
            p.println("(3) Modo 'Challenger' [Trabajando]");
            p.println("(4) Salir programa");
            separador();
            int op = getEntero("Ingrese opcion:");
            limpiarPantalla();
            switch (op) {
                case 1:
                    modoQuieroMiPc();
                    break;
                case 2:
                    modoEstudioTablas();
                    break;
                case 3:
                case 4:
                default:
                    System.exit(0);
            }
            limpiarPantalla();

        } while (true);

    }
    
    public static void separador() {
        System.out.println("-------------------------------------------------");
    }

    public static int getEntero(String str) {
        boolean error = true;
        int entero = -1;
        while (error) {
            System.out.print(str);
            try {
                leer = new Scanner(System.in);
                error = false;
                entero =  leer.nextInt();                
            } catch (NumberFormatException | InputMismatchException e) {
                System.err.println("Cuidado con el formato de ingreso....");
                error = true;
            }
        }
        return entero;

    }

    public static void limpiarPantalla() {

        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
                + "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

    }


}
