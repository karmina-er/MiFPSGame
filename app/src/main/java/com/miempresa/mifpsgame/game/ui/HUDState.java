package com.miempresa.mifpsgame.game.ui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;

public class HUDState extends BaseAppState {

    private SimpleApplication app;
    private BitmapText crosshair;

    // Elementos del HUD con mejor estilo
    private BitmapText healthText;
    private BitmapText timerText;
    private BitmapText gameOverText;
    private Container dpadContainer;

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;
        if (GuiGlobals.getInstance() == null) {
            GuiGlobals.initialize(app);
        }
    }

    @Override
    protected void onEnable() {
        if (app == null) return;

        float screenW = app.getCamera().getWidth();
        float screenH = app.getCamera().getHeight();

        // Cargar fuente predeterminada del motor
        BitmapFont guiFont = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");

        // 1. Mira central (+) en color Cian Neón
        crosshair = new BitmapText(guiFont);
        crosshair.setSize(guiFont.getCharSet().getRenderedSize() * 2.0f);
        crosshair.setText("+");
        crosshair.setColor(ColorRGBA.Cyan);
        crosshair.setLocalTranslation(
                screenW / 2f - crosshair.getLineWidth() / 2f,
                screenH / 2f + crosshair.getLineHeight() / 2f,
                0
        );
        app.getGuiNode().attachChild(crosshair);

        // 2. Indicador de Salud y Vidas (Esquina superior izquierda)
        healthText = new BitmapText(guiFont);
        healthText.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);
        healthText.setColor(ColorRGBA.Green);
        healthText.setText("♥ SALUD: 100% | VIDAS: 3");
        healthText.setLocalTranslation(25, screenH - 25, 0);
        app.getGuiNode().attachChild(healthText);

        // 3. Temporizador estilo Cronómetro (Esquina superior derecha)
        timerText = new BitmapText(guiFont);
        timerText.setSize(guiFont.getCharSet().getRenderedSize() * 1.5f);
        timerText.setColor(ColorRGBA.Yellow);
        timerText.setText("⏱ TIEMPO: 60s");
        timerText.setLocalTranslation(screenW - 220, screenH - 25, 0);
        app.getGuiNode().attachChild(timerText);

        // 4. Texto Grande de Game Over / Fin de Juego (Centro de pantalla)
        gameOverText = new BitmapText(guiFont);
        gameOverText.setSize(guiFont.getCharSet().getRenderedSize() * 2.8f);
        gameOverText.setColor(ColorRGBA.Red);
        gameOverText.setText("");
        gameOverText.setLocalTranslation(screenW / 2f - 180, screenH / 2f + 80, 0);
        app.getGuiNode().attachChild(gameOverText);

        // 5. Botonera Táctil Lemur (D-Pad)
        createTouchControls();
    }

    private void createTouchControls() {
        dpadContainer = new Container();
        dpadContainer.setLocalTranslation(25, 250, 0);

        Button btnUp = dpadContainer.addChild(new Button("  ▲  "));
        Button btnDown = dpadContainer.addChild(new Button("  ▼  "));
        Button btnLeft = dpadContainer.addChild(new Button("  ◄  "));
        Button btnRight = dpadContainer.addChild(new Button("  ►  "));

        btnUp.setFontSize(22);
        btnDown.setFontSize(22);
        btnLeft.setFontSize(22);
        btnRight.setFontSize(22);

        app.getGuiNode().attachChild(dpadContainer);
    }

    /** Actualiza dinámicamente los valores y colores del HUD */
    public void updateHUD(int lives, float health, float timeRemaining) {
        if (healthText != null) {
            int hpInt = (int) health;
            healthText.setText("♥ SALUD: " + hpInt + "% | VIDAS: " + lives);

            // Cambiar color a rojo si la salud es crítica
            if (hpInt <= 30) {
                healthText.setColor(ColorRGBA.Red);
            } else if (hpInt <= 60) {
                healthText.setColor(ColorRGBA.Orange);
            } else {
                healthText.setColor(ColorRGBA.Green);
            }
        }

        if (timerText != null) {
            int timeInt = (int) timeRemaining;
            timerText.setText("⏱ TIEMPO: " + timeInt + "s");

            // Parpadear / Cambiar a rojo cuando queden menos de 10 segundos
            if (timeInt <= 10) {
                timerText.setColor(ColorRGBA.Red);
            } else {
                timerText.setColor(ColorRGBA.Yellow);
            }
        }
    }

    /** Muestra el mensaje de Game Over o Victoria con estilo */
    public void showMessage(String msg) {
        if (gameOverText != null) {
            gameOverText.setText(msg);
        }
    }

    @Override
    protected void onDisable() {
        if (crosshair != null) crosshair.removeFromParent();
        if (healthText != null) healthText.removeFromParent();
        if (timerText != null) timerText.removeFromParent();
        if (gameOverText != null) gameOverText.removeFromParent();
        if (dpadContainer != null) dpadContainer.removeFromParent();
    }

    @Override
    protected void cleanup(Application app) {}
}