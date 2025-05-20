package com.mycompany.imagenessinhilos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.imageio.ImageIO;

public class ImagenesSinHilos {

    public static void main(String[] args) {
        try {
            // Se definen las carpetas de entrada (imagenes originales) y salida (imagenes en gris)
            File carpetaImagenes = new File("./imagenes");
            File carpetaDestino = new File("./imagenes_gris");

            // Si la carpeta de destino no existe, se crea
            if (!carpetaDestino.exists()) {
                carpetaDestino.mkdirs();
            }

            // Se obtienen todos los archivos de la carpeta de imágenes
            File[] archivos = carpetaImagenes.listFiles();

            // Si no hay archivos, se muestra un mensaje y termina el programa
            if (archivos == null || archivos.length == 0) {
                System.out.println("No se encontraron imágenes en la carpeta: " + carpetaImagenes.getAbsolutePath());
                return;
            }

            // Contar archivos .jpg en la carpeta de imágenes
            int totalJpg = 0;
            for (File archivo : archivos) {
                if (archivo.getName().toLowerCase().endsWith(".jpg")) {
                    totalJpg++;
                }
            }
            System.out.println("Total de imágenes .jpg encontradas: " + totalJpg);

            // Arreglo para almacenar los tiempos de ejecución de cada iteración
            long[] tiempos = new long[25];

            // Se realizan 25 iteraciones para medir el tiempo de procesamiento
            for (int iter = 0; iter < 25; iter++) {
                long inicio = System.nanoTime(); // Se toma el tiempo inicial

                // Procesa cada archivo de imagen .jpg
                for (File archivoEntrada : archivos) {
                    String nombreArchivo = archivoEntrada.getName().toLowerCase();
                    if (nombreArchivo.endsWith(".jpg")) {
                        // Lee la imagen desde el archivo
                        BufferedImage imagen = ImageIO.read(archivoEntrada);
                        if (imagen == null) {
                            continue; // Si no se pudo leer la imagen, pasa a la siguiente
                        }

                        int ancho = imagen.getWidth();
                        int alto = imagen.getHeight();

                        // Convierte cada píxel de la imagen a escala de grises
                        for (int y = 0; y < alto; y++) {
                            for (int x = 0; x < ancho; x++) {
                                int pixel = imagen.getRGB(x, y);
                                int alpha = (pixel >> 24) & 0xff;
                                int red = (pixel >> 16) & 0xff;
                                int green = (pixel >> 8) & 0xff;
                                int blue = pixel & 0xff;

                                // Calcula el valor promedio para obtener el gris
                                int gris = (red + green + blue) / 3;

                                // Crea el nuevo valor del píxel en gris manteniendo el canal alfa
                                int nuevoPixel = (alpha << 24) | (gris << 16) | (gris << 8) | gris;

                                imagen.setRGB(x, y, nuevoPixel);
                            }
                        }

                        // Crea una nueva imagen compatible con JPEG (sin canal alfa)
                        BufferedImage imagenRGB = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
                        for (int y = 0; y < alto; y++) {
                            for (int x = 0; x < ancho; x++) {
                                // Elimina el canal alfa para guardar como JPG
                                imagenRGB.setRGB(x, y, imagen.getRGB(x, y) & 0x00FFFFFF);
                            }
                        }

                        // Guarda la imagen procesada en la carpeta de destino
                        File archivoSalida = new File(carpetaDestino, nombreArchivo);
                        ImageIO.write(imagenRGB, "jpg", archivoSalida);
                    }
                }

                long fin = System.nanoTime(); // Se toma el tiempo final
                long tiempoMs = (fin - inicio) / 1_000_000; // Se calcula el tiempo en milisegundos
                tiempos[iter] = tiempoMs;
                System.out.println("Iteración " + (iter + 1) + ": " + tiempoMs + " ms");
            }

            // Guarda los tiempos de cada iteración en un archivo CSV
            try (PrintWriter pw = new PrintWriter(new FileWriter("tiempos.csv"))) {
                pw.println("Iteracion,Tiempo(ms)");
                for (int i = 0; i < tiempos.length; i++) {
                    pw.println((i + 1) + "," + tiempos[i]);
                }
            }

        } catch (Exception e) {
            // Imprime la traza de la excepción si ocurre algún error
            e.printStackTrace();
        }
    }
}
