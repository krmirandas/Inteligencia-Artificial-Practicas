/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia;

import processing.core.PApplet;
import processing.core.PFont;

import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;
import java.util.EmptyStackException;
import java.util.Stack;
/**
 *
 * @author Kevin
 */
public class Laberinto extends PApplet {

    PFont fuente;  // Fuente para mostrar texto en pantalla
    
    // Propiedades del modelo de termitas.
    int alto = 10;         // Altura (en celdas) de la cuadricula.
    int ancho = 21;        // Anchura (en celdas) de la cuadricula.
    int celda = 20;          // Tamanio de cada celda cuadrada (en pixeles).
    ModeloLaberinto modelo;  // El objeto que representa el modelo de termitas.

    public void settings() {
        size( ancho*celda, (alto*celda)+32);
    }

    @Override
    public void setup() {
        background(20);
        fuente = createFont("Arial",12,true);
        modelo = new ModeloLaberinto(ancho, alto, celda);
    }
    
    /**
     * Pintar el mundo del modelo (la cuadricula y las astillas).
     */
    @Override
    public void draw() {
        for (int i = 0; i < alto; i++) {
            for (int j = 0; j < ancho; j++) {
                if (modelo.posX == j && modelo.posY == i) {
                    fill(255, 67, 88);
                } else if (modelo.mundo[i][j].queda) {
                    fill(200, 250, 250);
                } else if (modelo.mundo[i][j].visitada) {
                    fill(70,240,240);
                    stroke(70,240,240);
                } else {
                    fill(0,240,240);
                }
                rect(j * modelo.tamanio, i * modelo.tamanio, modelo.tamanio, modelo.tamanio);
                if (modelo.mundo[i][j].paredIzq) {
                    stroke(0);
                    //if (i == 0) line(j*modelo.tamanio, i*modelo.tamanio, j*modelo.tamanio, modelo.tamanio);
                    line(j * modelo.tamanio, i * modelo.tamanio, j * modelo.tamanio, ((i + 1) * modelo.tamanio));
                }
                if (modelo.mundo[i][j].paredArriba) {
                    stroke(0);
                    //if (j == 0) line(j*modelo.tamanio, i*modelo.tamanio, modelo.tamanio, i*modelo.tamanio);
                    line(j * modelo.tamanio, i * modelo.tamanio, ((j + 1) * modelo.tamanio), i * modelo.tamanio);
                }
                if (modelo.mundo[i][j].paredDer) {
                    stroke(0);
                    //if (i == 0) line((j*modelo.tamanio) + modelo.tamanio, i*modelo.tamanio, (j*modelo.tamanio) + modelo.tamanio, modelo.tamanio);
                    line((j * modelo.tamanio) + modelo.tamanio, i * modelo.tamanio, (j + 1) * modelo.tamanio, (((i + 1) * modelo.tamanio)));
                }
                if (modelo.mundo[i][j].paredAbajo) {
                    stroke(0);
                    //if (j == 0) line(j*modelo.tamanio, (i*modelo.tamanio) + modelo.tamanio, modelo.tamanio, i*modelo.tamanio);
                    line(j * modelo.tamanio, (i * modelo.tamanio) + modelo.tamanio, ((j + 1) * modelo.tamanio), ((i + 1) * modelo.tamanio));
                }
                stroke(100,230,230);
            }
        }
        // Pintar informacion del modelo en la parte inferior de la ventana.
        fill(0);
        rect(0, alto*celda, (ancho*celda), 32);        
        fill(255);
        textFont(fuente,10);
        text("Cuadricula: " + modelo.ancho + " x " + modelo.alto, 5, (alto*celda)+12);
        text("Generacion " + modelo.generacion, 5, (alto*celda)+24);
        
        //ejecucion
        modelo.genera();
    }
    
    
    
    // --- Clase Celda ---
    /**
     * Representación de cada celda de la cuadrícula.
     */
    class Celda{
      int celdaX, celdaY;
      boolean queda;
      boolean visitada;
      boolean paredIzq;
      boolean paredArriba;
      boolean paredDer;
      boolean paredAbajo;

      /** Constructor de una celda
        @param celdaX Coordenada en x
        @param celdaY Coordenada en y
      */
      Celda(int celdaX, int celdaY){
        this.celdaX = celdaX;
        this.celdaY = celdaY;
        // Como todas las celdas comienzan con el mismo estado mejor los pongo por 'default'
        this.visitada = false;
        this.paredAbajo = true;
        this.paredArriba = true;
        this.paredDer = true;
        this.paredIzq = true;
        this.queda = true;
      }
    }

    // --- Clase ModeloLaberinto ---
    /**
     * Representa la creacion de un laberinto desde cero.
     */
    class ModeloLaberinto{
      int ancho, alto;  // Tamaño de celdas a lo largo y ancho de la cuadrícula.
      int tamanio;  // Tamaño en pixeles de cada celda.
      int generacion;  // Conteo de generaciones (cantidad de iteraciones) del modelo.
      Celda[][] mundo;  // Mundo de celdas.
      Random rnd = new Random();  // Auxiliar para decisiones aleatorias.
      int posX; // Posicion en la recta X en la que esta el agente
      int posY; // Posicion en la recta Y en la que esta el agente
      int direccion; // auxiliar para saber a donde se mueve el agente
      Stack<Celda> pila = new Stack<Celda>();
      

      /** Constructor del modelo
        @param ancho Cantidad de celdas a lo ancho en la cuadricula.
        @param ancho Cantidad de celdas a lo largo en la cuadricula.
        @param tamanio Tamaño (en pixeles) de cada celda cuadrada que compone la cuadricula.
      */
      ModeloLaberinto(int ancho, int alto, int tamanio){
        this.ancho = ancho;
        this.alto = alto;
        this.tamanio = tamanio;
        this.generacion = 0;
        //Inicializar mundo
        mundo = new Celda[alto][ancho];
        for(int i = 0; i < alto; i++)
          for(int j = 0; j < ancho; j++)
            mundo[i][j] = new Celda(i,j);
        // Colocamos al 'agente' en una posicion aleatoria
        this.posX = rnd.nextInt(ancho);
        this.posY = rnd.nextInt(alto);
      }

      /*
       * Mueve al agente
       * Buscamos si existe una celda arriba, abajo, derecha o izquierda
       * Si hay una que no hemos visitado movemos al agente a esa celda
       *  --- --- ---
       * |   | 0 |   |
       *  --- --- ---
       * | 3 | a | 1 |
       *  --- --- ---
       * |   | 2 |   |
       *  --- --- ---
       */
      void moverAgente(){
        // Buscamos una celda sin visitar
        int direccion = busca();
        switch(direccion) {
          case 0:  
                   mundo[posY][posX].paredArriba = false;
                   pila.push(mundo[posY][posX]);
                   posY = posY-1;
                   mundo[posY][posX].paredAbajo = false;
                   break;
          case 1:  
                   mundo[posY][posX].paredDer = false;
                   pila.push(mundo[posY][posX]);
                   posX = posX+1;
                   mundo[posY][posX].paredIzq = false;                   
                   break;
          case 2:  
                   mundo[posY][posX].paredAbajo = false;
                   pila.push(mundo[posY][posX]);
                   posY+=1;
                   mundo[posY][posX].paredArriba = false;
                   break;
          case 3:  
                   mundo[posY][posX].paredIzq = false;
                   pila.push(mundo[posY][posX]);
                   posX-=1;
                   mundo[posY][posX].paredDer = false;
                   break;
        }
      }

      /** Auxiliar que genera un numero que corresponde a la direccion
        "arriba", "izquierda", "derecha" o "abajo", correspondiente a una celda sin visitar.
        @return Entero que representa la direccion 'aleatoria' escogida.
      */
      int busca(){
        // ##### IMPLEMENTACION #####
        // primero intenta escoger una aleatoria
        int dir = rnd.nextInt(5);
        // si es valida devuelve su posicion
        if(4-dir>=0 && posible(4-dir)) return 4-dir;
        // si no es valida busca arriba, derecha e izquierda
        for(int i = 0; i < 4; i++)
            if(posible(i)){
                // si hay una celda 'libre' devuelve su direccion
                return i;
            }
        // si no hay celdas devuelve un -1 para que no se intente mover
        return -1;
      }

      /** Determina si la celda en la direccion dada es una celda no visitada.
        @param dir La direccion en la que deseamos observar si hay una astilla (con valor entre 0 y 7).
        @return True si nos podemos mover a la celda, falso en otro caso.
      */
      boolean posible(int dir){
         // ##### IMPLEMENTACION #####
         try{
             int x = 0;
             int y = 0;
             switch (dir) {
                 case 0:
                     x = (posX);
                     y = (posY - 1);
                     break;
                 case 1:
                     x = (posX + 1);
                     y = (posY);
                     break;
                 case 2:
                     x = (posX);
                     y = (posY + 1);
                     break;
                 case 3:
                     x = (posX - 1);
                     y = (posY);
                     break;
             }
             //System.out.println("x:"+x+", y:"+ y+" = "+ queda(x,y));
             return !mundo[y][x].visitada;
             
         } catch (Exception e){
             // si es una celda invalida (fuera de rango) regresamos falso
             return false;
         }
       }
      
      /** Determina si la celda en la posicion (x,y)
       * tiene como 'vecina' una celda no visitada.
        @param x Posicion en x
        @param y Posicion en y.
        @return True si hay un vecino (celda) donde nos podemos mover, falso en otro caso.
      */
      boolean queda(int x,int y){
          boolean arriba = false, derecha = false, abajo = false, izquierda = false;
          if(!mundo[y][x].visitada) return true;
          try {
               arriba = !mundo[y-1][x].visitada;
          } catch(Exception e){
              // Por si es una celda invalida (fuera de rango)
          }
          try {
               derecha = !mundo[y][x+1].visitada;
          } catch(Exception e){
          }
          try {
               abajo = !mundo[y+1][x].visitada;
          } catch(Exception e){
          }
          try {
               izquierda = !mundo[y][x-1].visitada;
          } catch(Exception e){
          }
          return arriba || derecha || abajo || izquierda;
      }
      
      /** Método que construye el laberinto.
      */
      void genera(){
          try{
              // Marcamos como visitada la celda en la que estamos
              mundo[posY][posX].visitada = true;
              // Actualizamos su estado por si hay celdas donde movernos
              mundo[posY][posX].queda = queda(posX, posY);
              //System.out.println("estoy en X:" + posX + ", Y" + posY + " = " + mundo[posY][posX].queda);
              if (mundo[posY][posX].queda == false) {
                  // Si ya no hay celdas donde movernos volvemos a la celda anterior
                  Celda mover = pila.pop();
                  posX = mover.celdaY;
                  posY = mover.celdaX;
                  // actualizamos en numero de acciones
                  generacion += 1;
              } else {
                  // Si hay celdas disponibles movemos al 'agente' a una de ellas
                  this.moverAgente();
                  // actualizamos en numero de acciones
                  generacion += 1;
              }
          } catch(EmptyStackException e){
              // Por si ya no hay celdas en el stack
          }
      }
    }
    
    static public void main(String args[]) {
        PApplet.main(new String[] { "ia.Laberinto" });
    }
}
