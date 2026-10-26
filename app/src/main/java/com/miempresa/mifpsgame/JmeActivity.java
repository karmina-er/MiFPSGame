package com.miempresa.mifpsgame;

import android.os.Bundle;

import com.jme3.app.AndroidHarness;

public class JmeActivity extends AndroidHarness {

    public JmeActivity() {
        // Clase principal del juego jME
        appClass = "com.miempresa.mifpsgame.game.FPSGame";

        // Configuracion de pantalla
        eglBitsPerPixel = 24;
        eglAlphaBits = 0;
        eglDepthBits = 16;
        eglSamples = 0;
        eglStencilBits = 0;

        // Pantalla completa sin titulo
        screenFullScreen = true;

        // Eventos tactiles
        mouseEventsEnabled = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}