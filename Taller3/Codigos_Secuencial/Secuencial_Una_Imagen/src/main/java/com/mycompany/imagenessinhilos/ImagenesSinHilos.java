/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.imagenessinhilos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author andrespillajo
 */
public class ImagenesSinHilos {

    public static void main(String[] args) {
        try {
            // Cargar la imagen desde un archivo
            // Se crea un objeto File que apunta a la ruta de la imagen de entrada
            File archivoEntrada = new File("./imagenes/imagen.jpg"); // Cambia "imagen.png" por la ruta de tu imagen
            // Se lee la imagen y se almacena en un BufferedImage
            BufferedImage imagenOriginal = ImageIO.read(archivoEntrada);

            // Verifica si la imagen se cargó correctamente
            if (imagenOriginal == null) {
                System.out.println("No se pudo cargar la imagen. Verifica la ruta y el formato.");
                return;
            }

            // Obtener dimensiones de la imagen
            int ancho = imagenOriginal.getWidth();
            int alto = imagenOriginal.getHeight();

            System.out.println("Procesando imagen de " + ancho + "x" + alto + " píxeles.");

            // Lista para almacenar los tiempos de procesamiento de cada iteración
            List<Long> tiempos = new ArrayList<>();

            // Realiza 100 iteraciones del procesamiento de la imagen
            for (int iter = 1; iter <= 100; iter++) {
                // Se crea una copia de la imagen original para no modificarla directamente
                BufferedImage imagen = new BufferedImage(ancho, alto, imagenOriginal.getType());
                // Copia pixel por pixel la imagen original a la nueva imagen
                for (int y = 0; y < alto; y++) {
                    for (int x = 0; x < ancho; x++) {
                        imagen.setRGB(x, y, imagenOriginal.getRGB(x, y));
                    }
                }

                // Marca el tiempo de inicio del procesamiento
                long inicio = System.nanoTime();

                // Procesar la imagen para convertirla a escala de grises
                for (int y = 0; y < alto; y++) {
                    for (int x = 0; x < ancho; x++) {
                        // Obtener el valor ARGB del píxel
                        int pixel = imagen.getRGB(x, y);

                        // Extraer componentes de color usando operaciones de bits
                        int alpha = (pixel >> 24) & 0xff; // Componente Alpha
                        int red = (pixel >> 16) & 0xff;   // Componente Rojo
                        int green = (pixel >> 8) & 0xff;  // Componente Verde
                        int blue = pixel & 0xff;          // Componente Azul

                        // Calcular el promedio de los componentes de color para obtener el valor en gris
                        int gris = (red + green + blue) / 3;

                        // Crear el nuevo valor de píxel en escala de grises manteniendo el canal alpha
                        int nuevoPixel = (alpha << 24) | (gris << 16) | (gris << 8) | gris;

                        // Asignar el nuevo color al píxel en la imagen
                        imagen.setRGB(x, y, nuevoPixel);
                    }
                }

                // Marca el tiempo de fin del procesamiento
                long fin = System.nanoTime();
                // Calcula el tiempo transcurrido en milisegundos
                long tiempoMs = (fin - inicio) / 1_000_000;
                // Agrega el tiempo de esta iteración a la lista
                tiempos.add(tiempoMs);

                // Guarda la imagen procesada en escala de grises con un nombre diferente por iteración
                String nombreSalida = String.format("C:\\Users\\Lenovo LOQ\\Documents\\Semestre Abril-Ago\\PARALELA\\Taller3\\PDI-secuencial-main\\imagenes_gris\\imagen_gris_%03d.jpg", iter);
                ImageIO.write(imagen, "jpg", new File(nombreSalida));
                System.out.println("Iteración " + iter + " completada en " + tiempoMs + " ms");
            }

            // Guardar los tiempos de procesamiento en un archivo CSV
            String rutaCSV = "C:\\Users\\Lenovo LOQ\\Documents\\Semestre Abril-Ago\\PARALELA\\Taller3\\PDI-secuencial-main\\imagenes_gris\\tiempos.csv";
            try (PrintWriter writer = new PrintWriter(new FileWriter(rutaCSV))) {
                writer.println("Iteracion,Tiempo_ms");
                for (int i = 0; i < tiempos.size(); i++) {
                    writer.println((i + 1) + "," + tiempos.get(i));
                }
            }

            System.out.println("Procesamiento finalizado. Tiempos guardados en 'tiempos.csv'.");

        } catch (java.io.IOException | IllegalArgumentException e) {
            // Captura y muestra cualquier error que ocurra durante el procesamiento
            System.err.println("Error al procesar la imagen: " + e.getMessage());
        }
    }
}
