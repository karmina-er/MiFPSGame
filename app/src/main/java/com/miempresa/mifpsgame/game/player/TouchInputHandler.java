package com.miempresa.mifpsgame.game.player;

import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector3f;
import com.miempresa.mifpsgame.game.FPSGame;

public class TouchInputHandler implements RawInputListener {

    private final FPSGame game;
    private final FPSCamera fpsCamera;

    public TouchInputHandler(FPSGame game, FPSCamera fpsCamera) {
        this.game = game;
        this.fpsCamera = fpsCamera;
    }

    @Override
    public void onTouchEvent(TouchEvent evt) {
        float screenWidth = game.getCamera().getWidth();
        float x = evt.getX();
        float y = evt.getY();

        switch (evt.getType()) {
            case DOWN:
                // Si el juego terminó, tocar la pantalla reinicia la partida
                if (game.isGameOverState()) {
                    game.triggerRestart();
                    return;
                }


                // Zona Izquierda: D-Pad Direccional
                if (x < screenWidth / 2f) {
                    processMovement(x, y);
                } else {
                    // Zona Derecha: Disparo de Hechizo
                    if (game.getWeaponControl() != null) {
                        game.getWeaponControl().castSpell();
                    }
                }
                break;

            case MOVE:
                if (x < screenWidth / 2f) {
                    processMovement(x, y);
                } else if (fpsCamera != null) {
                    // Rotación suave de la cámara
                    fpsCamera.rotateCamera(evt.getDeltaX() * 0.005f, evt.getDeltaY() * 0.005f);
                }
                break;

            case UP:
                if (x < screenWidth / 2f && game.getPlayerControl() != null) {
                    game.getPlayerControl().move(Vector3f.ZERO);
                }
                break;
            default:
                break;
        }
    }

    private void processMovement(float x, float y) {
        Vector3f camDir = game.getCamera().getDirection().clone().setY(0).normalizeLocal();
        Vector3f camLeft = game.getCamera().getLeft().clone().setY(0).normalizeLocal();
        Vector3f walkDir = new Vector3f(0, 0, 0);

        // Botón Adelante (Arriba)
        if (x >= 90 && x <= 150 && y >= 190 && y <= 250) {
            walkDir.addLocal(camDir.mult(0.2f));
        }
        // Botón Atrás (Abajo)
        else if (x >= 90 && x <= 150 && y >= 50 && y <= 110) {
            walkDir.addLocal(camDir.mult(-0.2f));
        }
        // Botón Izquierda
        else if (x >= 20 && x <= 80 && y >= 120 && y <= 180) {
            walkDir.addLocal(camLeft.mult(0.2f));
        }
        // Botón Derecha
        else if (x >= 160 && x <= 220 && y >= 120 && y <= 180) {
            walkDir.addLocal(camLeft.mult(-0.2f));
        }

        if (game.getPlayerControl() != null) {
            game.getPlayerControl().move(walkDir);
        }
    }

    @Override public void beginInput() {}
    @Override public void endInput() {}
    @Override public void onJoyAxisEvent(JoyAxisEvent evt) {}
    @Override public void onJoyButtonEvent(JoyButtonEvent evt) {}
    @Override public void onMouseMotionEvent(MouseMotionEvent evt) {}
    @Override public void onMouseButtonEvent(MouseButtonEvent evt) {}
    @Override public void onKeyEvent(KeyInputEvent evt) {}
}
