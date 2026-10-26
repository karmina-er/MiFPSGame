package com.miempresa.mifpsgame.game.enemies;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class EnemyControl extends AbstractControl {
    private enum State { PATROL, CHASE, ATTACK, DEAD }
    private State currentState = State.PATROL;
    private float health = 100f;
    private final float detectionRange = 30f;
    private final float attackRange = 2f;

    private final Vector3f playerPosition = new Vector3f();
    private CharacterControl physicsControl;
    private final BulletAppState bulletAppState;
    private SimpleApplication app;

    public EnemyControl(BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
    }

    public EnemyControl(BulletAppState bulletAppState, SimpleApplication app) {
        this.bulletAppState = bulletAppState;
        this.app = app;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (currentState == State.DEAD || spatial == null) return;

        if (app != null && app.getCamera() != null) {
            playerPosition.set(app.getCamera().getLocation());
        }

        float distToPlayer = spatial.getWorldTranslation().distance(playerPosition);
        switch (currentState) {
            case PATROL:
                if (distToPlayer < detectionRange) currentState = State.CHASE;
                patrol(tpf);
                break;
            case CHASE:
                if (distToPlayer < attackRange) currentState = State.ATTACK;
                else if (distToPlayer > detectionRange * 1.5f) currentState = State.PATROL;
                chasePlayer(tpf);
                break;
            case ATTACK:
                if (distToPlayer > attackRange) currentState = State.CHASE;
                attackPlayer(tpf);
                break;
            default:
                break;
        }
    }

    public void takeDamage(float amount) {
        health -= amount;
        if (health <= 0) {
            die();
        }
    }

    private void die() {
        currentState = State.DEAD;
        if (physicsControl != null && bulletAppState != null && bulletAppState.getPhysicsSpace() != null) {
            bulletAppState.getPhysicsSpace().remove(physicsControl);
        }
        if (spatial != null) {
            spatial.removeFromParent();
        }
    }

    private void chasePlayer(float tpf) {
        if (spatial == null) return;
        Vector3f enemyPos = spatial.getLocalTranslation();
        Vector3f dir = playerPosition.subtract(enemyPos);
        dir.y = 0; // Mantener movimiento horizontal
        if (dir.lengthSquared() > 0.01f) {
            dir.normalizeLocal();
            spatial.move(dir.mult(3f * tpf)); // Mover enemigo hacia el jugador
            spatial.lookAt(playerPosition, Vector3f.UNIT_Y);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    private void patrol(float tpf) { }
    private void attackPlayer(float tpf) { }

    public float getHealth() {
        return health;
    }

    public void setPhysicsControl(CharacterControl physicsControl) {
        this.physicsControl = physicsControl;
    }

    public void setApp(SimpleApplication app) {
        this.app = app;
    }
}
