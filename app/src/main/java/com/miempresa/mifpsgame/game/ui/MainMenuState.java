package com.miempresa.mifpsgame.game.ui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.ColorRGBA;
import com.miempresa.mifpsgame.game.FPSGame;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.style.BaseStyles;

public class MainMenuState extends BaseAppState {
    private SimpleApplication app;
    private final FPSGame game;
    private Container menuContainer;

    public MainMenuState(FPSGame game) {
        this.game = game;
    }

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;
        GuiGlobals.initialize(app);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
    }

    @Override
    protected void cleanup(Application app) {}

    @Override
    protected void onEnable() {
        if (app == null) return;

        float screenW = app.getCamera().getWidth();
        float screenH = app.getCamera().getHeight();

        menuContainer = new Container();
        menuContainer.setLocalTranslation(screenW / 2f - 180, screenH / 2f + 100, 0);

        Label title = menuContainer.addChild(new Label("Neon Orb: Varita Mágica"));
        title.setFontSize(28);
        title.setColor(ColorRGBA.Cyan);

        Button startBtn = menuContainer.addChild(new Button("INICIAR JUEGO"));
        startBtn.setFontSize(24);
        startBtn.addClickCommands(source -> {
            app.getStateManager().detach(this);
            game.startGame();
        });

        app.getGuiNode().attachChild(menuContainer);
    }

    @Override
    protected void onDisable() {
        if (menuContainer != null) {
            menuContainer.removeFromParent();
        }
    }
}
