package com.miempresa.mifpsgame.game.enemies;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.miempresa.mifpsgame.game.FPSGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Gestiona oleadas de enemigos y puntuacion. */
public class EnemyPerson {

    private final FPSGame game;
    private final List<Vector3f> spawnPoints;
    private final List<Node> enemies = new ArrayList<>();
    private final Random rnd = new Random();

    private int wave = 0, kills = 0, alive = 0;
    private float nextWaveTimer = 2f;
    private float bannerTimer = 0f;

    public EnemyPerson(FPSGame game, List<Vector3f> spawnPoints) {
        this.game = game;
        this.spawnPoints = spawnPoints;
    }

    public void update(float tpf) {
        if (bannerTimer > 0) bannerTimer -= tpf;
        for (int i = enemies.size() - 1; i >= 0; i--) {
            if (enemies.get(i).getParent() == null) enemies.remove(i); // cadaver ya retirado
        }

        if (alive <= 0) {
            nextWaveTimer -= tpf;
            if (nextWaveTimer <= 0) startNextWave();
        }
    }

    private void startNextWave() {
        wave++;
        int count = Math.min(2 + wave, 12);
        // Operación matemática corregida correctamente
        float speed = Math.min(3.0f + wave * 0.25f, 6f);

        for (int i = 0; i < count; i++) {
            if (spawnPoints == null || spawnPoints.isEmpty()) break;

            // Elegir un punto de aparicion aleatorio
            Vector3f sp = spawnPoints.get(rnd.nextInt(spawnPoints.size()));
            Vector3f p = sp.add((rnd.nextFloat() - 0.5f) * 3f, 0f, (rnd.nextFloat() - 0.5f) * 3f);

            // Instanciar un nuevo nodo de enemigo
            Node enemyNode = new Node("Enemy_" + i);
            EnemyControl ec = new EnemyControl(null); // Instancia el control de enemigo basico
            enemyNode.addControl(ec);

            game.getRootNode().attachChild(enemyNode);
            enemies.add(enemyNode);
            alive++;
        }
        bannerTimer = 2.5f;
        nextWaveTimer = 3f;
    }

    /** Llamado al morir un enemigo. */
    public void onEnemyKilled() {
        alive--;
        kills++;
    }

    public void reset() {
        for (Node n : enemies) {
            n.removeFromParent();
        }
        enemies.clear();
        wave = 0;
        kills = 0;
        alive = 0;
        nextWaveTimer = 2f;
        bannerTimer = 0f;
    }

    public int getWave() { return wave; }
    public int getKills() { return kills; }
    public int getAlive() { return alive; }
    public float getBannerTimer() { return bannerTimer; }
}