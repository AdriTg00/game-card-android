package com.dedigo.pmdm06_juego;
// Paquete de la aplicación (organización del proyecto)

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
// Imports necesarios: animaciones, sonido, temporización, UI y estructuras de datos

public class GameActivity extends Activity {
    // Activity del juego (pantalla del memory)

    Toast toast;
    // Para mostrar mensajes al usuario (feedback)

    // ImageButtons individuales (enlazan con el XML)
    ImageButton imb00, imb01, imb02,
            imb10, imb11, imb12,
            imb20, imb21, imb22,
            imb30, imb31, imb32;

    ImageButton[] tablero = new ImageButton[12];
    // Array que agrupa todas las cartas para recorrerlas con bucles

    TextView txtRecord, txtIntentos, txtAciertos;
    // Textos que muestran los datos del juego

    int record, intentos, aciertos;
    // Variables lógicas del juego

    int[] imagenes;
    // IDs de imágenes (NO son imágenes, son identificadores)

    int[] sonidos;
    // IDs de sonidos asociados a cada imagen

    int fondo;
    // Imagen de la carta boca abajo

    ArrayList<Integer> arrayDesordenado;
    // Mapea posiciones del tablero con imágenes aleatorias

    ImageButton primero, segundo;
    // Referencias a las dos cartas seleccionadas

    int numeroPrimero, numeroSegundo;
    // Índices de las imágenes seleccionadas

    boolean bloqueo = false;
    // Evita que el usuario pulse mientras se comparan cartas

    Bundle datos;
    // Datos recibidos desde MainActivity

    final Handler temporizador = new Handler();
    // Permite ejecutar acciones con retardo (postDelayed)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Carga el layout del juego

        datos = getIntent().getExtras();
        // Recibe datos enviados desde la otra Activity

        record = datos.getInt("record");
        // Obtiene el récord actual

        iniciar();
        // Método central que prepara el juego
    }

    private void cargarTablero() {
        // Enlaza cada ImageButton del XML con Java
        // y los guarda también en el array tablero

        tablero[0] = imb00 = findViewById(R.id.imb00);
        tablero[1] = imb01 = findViewById(R.id.imb01);
        tablero[2] = imb02 = findViewById(R.id.imb02);

        tablero[3] = imb10 = findViewById(R.id.imb10);
        tablero[4] = imb11 = findViewById(R.id.imb11);
        tablero[5] = imb12 = findViewById(R.id.imb12);

        tablero[6] = imb20 = findViewById(R.id.imb20);
        tablero[7] = imb21 = findViewById(R.id.imb21);
        tablero[8] = imb22 = findViewById(R.id.imb22);

        tablero[9]  = imb30 = findViewById(R.id.imb30);
        tablero[10] = imb31 = findViewById(R.id.imb31);
        tablero[11] = imb32 = findViewById(R.id.imb32);
    }

    private void cargarTextos() {
        // Inicializa contadores
        intentos = 0;
        aciertos = 0;

        // Enlaza TextViews
        txtRecord = findViewById(R.id.txtRecord);
        txtIntentos = findViewById(R.id.txtIntentos);
        txtAciertos = findViewById(R.id.txtAciertos);

        // Muestra valores iniciales
        txtRecord.setText("" + record);
        txtIntentos.setText("" + intentos);
        txtAciertos.setText("" + aciertos);
    }

    private void cargarImagenes() {
        // Carga los IDs de las imágenes (parejas)
        imagenes = new int[] {
                R.drawable.caballo,
                R.drawable.gato,
                R.drawable.cerdo,
                R.drawable.pato,
                R.drawable.perro,
                R.drawable.vaca
        };

        fondo = R.drawable.cara;
        // Imagen común para todas las cartas boca abajo
    }

    private void cargarSonidos() {
        // Sonidos asociados a cada imagen (misma posición)
        sonidos = new int[] {
                R.raw.caballo,
                R.raw.gato,
                R.raw.cerdo,
                R.raw.pato,
                R.raw.perro,
                R.raw.vaca
        };
    }

    private ArrayList<Integer> barajar(int longitud) {
        // Crea pares de índices y los mezcla
        ArrayList<Integer> result = new ArrayList<>();

        for (int i = 0; i < longitud * 2; i++) {
            result.add(i % longitud);
            // Ejemplo: 0,1,2,3,4,5,0,1,2,3,4,5
        }

        Collections.shuffle(result);
        // Orden aleatorio

        return result;
    }

    private void comprobarSeleccion(int i, final ImageButton imb) {

        // Reproduce el sonido de la carta pulsada
        MediaPlayer.create(this,
                sonidos[arrayDesordenado.get(i)]).start();

        // Carga y ejecuta animación de giro
        @SuppressLint("ResourceType")
        Animator animator = AnimatorInflater.loadAnimator(this, R.anim.rotar_y);
        animator.setTarget(imb);
        animator.start();

        if (primero == null) {
            // PRIMERA carta seleccionada

            primero = imb;
            primero.setScaleType(ImageView.ScaleType.CENTER_CROP);
            primero.setImageResource(imagenes[arrayDesordenado.get(i)]);
            primero.setEnabled(false);
            numeroPrimero = arrayDesordenado.get(i);

        } else {
            // SEGUNDA carta

            bloqueo = true; // bloquea más pulsaciones
            segundo = imb;
            segundo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            segundo.setImageResource(imagenes[arrayDesordenado.get(i)]);
            segundo.setEnabled(false);
            numeroSegundo = arrayDesordenado.get(i);

            intentos++;
            txtIntentos.setText("" + intentos);

            if (numeroPrimero == numeroSegundo) {
                // COINCIDEN

                primero = null;
                segundo = null;
                bloqueo = false;

                aciertos++;
                txtAciertos.setText("" + aciertos);

                if (aciertos == imagenes.length) {
                    // FIN DEL JUEGO

                    Toast.makeText(this,
                            "Enhorabuena!\nHas ganado.",
                            Toast.LENGTH_LONG).show();

                    temporizador.postDelayed(() -> {
                        Intent data = new Intent();

                        if (intentos < record || record == 0) {
                            data.putExtra("record", intentos);
                        } else {
                            data.putExtra("record", record);
                        }

                        setResult(RESULT_OK, data);
                        finish(); // vuelve a MainActivity
                    }, 2000);
                }

            } else {
                // NO COINCIDEN → se ocultan tras un retardo

                temporizador.postDelayed(() -> {

                    primero.setImageResource(fondo);
                    primero.setEnabled(true);

                    segundo.setImageResource(fondo);
                    segundo.setEnabled(true);

                    primero = null;
                    segundo = null;
                    bloqueo = false;

                }, 500);
            }
        }
    }

    private void iniciar() {
        // Método central de inicialización

        cargarTablero();
        cargarTextos();
        cargarImagenes();
        cargarSonidos();

        arrayDesordenado = barajar(imagenes.length);

        // Pone todas las cartas boca abajo
        for (ImageButton b : tablero) {
            b.setImageResource(fondo);
        }

        // Asigna listeners
        for (int i = 0; i < tablero.length; i++) {
            int finalI = i;
            tablero[i].setOnClickListener(v -> {
                if (!bloqueo) {
                    comprobarSeleccion(finalI, tablero[finalI]);
                }
            });
        }
    }
}
