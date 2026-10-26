package com.miempresa.mifpsgame.game;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.miempresa.mifpsgame.game.enemies.EnemyPerson;
import com.miempresa.mifpsgame.game.player.FPSCamera;
import com.miempresa.mifpsgame.game.player.PlayerControl;
import com.miempresa.mifpsgame.game.player.TouchInputHandler;
import com.miempresa.mifpsgame.game.player.WeaponControl;
import com.miempresa.mifpsgame.game.ui.HUDState;
import com.miempresa.mifpsgame.game.world.SceneLoader;

import java.util.ArrayList;
import java.util.List;

public class FPSGame extends SimpleApplication {

    private BulletAppState bulletAppState;
    private SceneLoader sceneLoader;
    private FPSCamera fpsCamera;
    private HUDState hudState;
    private EnemyPerson enemyManager;
    private PlayerControl playerControl;
    private WeaponControl weaponControl;

    // Control de la partida
    private float gameTimer = 60.0f;
    private int playerLives = 3;
    private float playerHealth = 100.0f;
    private boolean isGameActive = false;
    private boolean isGameOverState = false;

    @Override
    public void simpleInitApp() {
        // Configurar color de fondo del cielo y ocultar estadísticas
        viewPort.setBackgroundColor(new ColorRGBA(0.4f, 0.65f, 0.95f, 1.0f));
        setDisplayStatView(false);

        // 1. Inicializar motor de físicas
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, -30f, 0));
        bulletAppState.getPhysicsSpace().setAccuracy(1f / 60f);

        // 2. Cargar el escenario
        sceneLoader = new SceneLoader(this, bulletAppState);
        sceneLoader.loadScene();

        // 3. Inicializar Personaje, Varita Mágica y Cámara
        playerControl = new PlayerControl(cam, bulletAppState);
        weaponControl = new WeaponControl(this);
        playerControl.setWeapon(weaponControl);

        fpsCamera = new FPSCamera(this, bulletAppState);
        fpsCamera.initialize();

        TouchInputHandler touchHandler = new TouchInputHandler(this, fpsCamera);
        inputManager.addRawInputListener(touchHandler);

        // 4. Configurar iluminación
        setupLighting();

        // 5. Vincular la interfaz (HUD)
        hudState = new HUDState();
        stateManager.attach(hudState);

        // 6. Configurar Puntos de Aparición de Enemigos (Frente a la vista inicial)
        List<Vector3f> spawnPoints = new ArrayList<>();
        spawnPoints.add(new Vector3f(0f, 0f, -10f));
        spawnPoints.add(new Vector3f(-6f, 0f, -12f));
        spawnPoints.add(new Vector3f(6f, 0f, -12f));

        enemyManager = new EnemyPerson(this, spawnPoints);

        // Iniciar la partida inmediatamente
        startGame();
    }

    public void startGame() {
        gameTimer = 60.0f;
        playerLives = 3;
        playerHealth = 100.0f;
        if (playerControl != null) {
            playerControl.setHealth(100.0f);
        }
        if (enemyManager != null) {
            enemyManager.reset();
        }
        isGameActive = true;
        isGameOverState = false;
        if (hudState != null) {
            hudState.showMessage("");
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (!isGameActive) {
            return;
        }

        // Actualizar posición y rotación de la cámara
        if (fpsCamera != null) {
            fpsCamera.update(tpf);
        }

        // Mantener la varita mágica pegada a la vista de la cámara
        if (weaponControl != null) {
            weaponControl.updateWandPosition();
        }

        // Sincronizar la salud local del player control con el juego
        if (playerControl != null) {
            playerHealth = playerControl.getHealth();
        }

        // Cuenta regresiva del tiempo
        gameTimer -= tpf;
        if (gameTimer <= 0) {
            gameTimer = 0;
            endGame(false);
        }

        // Actualizar oleadas de enemigos
        if (enemyManager != null) {
            enemyManager.update(tpf);
        }

        // Refrescar el HUD dinámicamente
        if (hudState != null) {
            hudState.updateHUD(playerLives, playerHealth, gameTimer);
        }
    }

    public void takePlayerDamage(float damage) {
        if (!isGameActive) return;

        if (playerControl != null) {
            playerControl.takeDamage(damage);
            playerHealth = playerControl.getHealth();
        } else {
            playerHealth -= damage;
        }

        if (playerHealth <= 0) {
            playerLives--;
            if (playerLives > 0) {
                playerHealth = 100.0f;
                if (playerControl != null) {
                    playerControl.setHealth(100.0f);
                }
            } else {
                playerHealth = 0;
                endGame(true);
            }
        }
    }

    private void endGame(boolean isGameOver) {
        isGameActive = false;
        isGameOverState = true;
        if (enemyManager != null) {
            enemyManager.reset();
        }
        if (hudState != null) {
            if (isGameOver) {
                hudState.showMessage("GAME OVER\nToca para reiniciar");
            } else {
                hudState.showMessage("TIEMPO AGOTADO\nToca para reiniciar");
            }
        }
    }

    public void triggerRestart() {
        if (isGameOverState) {
            startGame();
        }
    }

    private void setupLighting() {
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(ambient);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
    }

    public PlayerControl getPlayerControl() {
        return playerControl;
    }

    public WeaponControl getWeaponControl() {
        return weaponControl;
    }

    public boolean isGameOverState() {
        return isGameOverState;
    }
}