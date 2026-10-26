package com.miempresa.mifpsgame.game.player;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * Control de cámara y física para el jugador en primera persona.
 */
@SuppressWarnings("unused")
public class FPSCamera {
    private SimpleApplication app;
    private BulletAppState bulletAppState;
    private CharacterControl player;
    private final Vector3f walkDirection = new Vector3f();
    private boolean moveForward;
    private boolean moveBack;
    private boolean moveLeft;
    private boolean moveRight;
    private float playerSpeed = 8f;
    private float jumpForce = 12f;

    public FPSCamera() {
    }

    public FPSCamera(SimpleApplication app, BulletAppState bulletAppState) {
        this.app = app;
        this.bulletAppState = bulletAppState;
    }

    public void initialize() {
        // Cápsula de colisión del jugador
        CapsuleCollisionShape capsule = new CapsuleCollisionShape(0.5f, 1.8f, 1);
        player = new CharacterControl(capsule, 0.05f);
        player.setJumpSpeed(jumpForce);
        player.setFallSpeed(30f);
        player.setGravity(30f);
        player.setPhysicsLocation(new Vector3f(0f, 2f, 0f));

        if (app != null && app.getRootNode() != null) {
            app.getRootNode().addControl(player);
        }
        if (bulletAppState != null && bulletAppState.getPhysicsSpace() != null) {
            bulletAppState.getPhysicsSpace().add(player);
        }

        // Apuntar la cámara hacia adelante
        if (app != null && app.getCamera() != null) {
            app.getCamera().setLocation(player.getPhysicsLocation().add(0, 1.5f, 0));
        }
    }

    public void update(float tpf) {
        if (app == null || app.getCamera() == null || player == null) {
            return;
        }

        // Calcular dirección de movimiento
        Vector3f camDir = app.getCamera().getDirection().clone().multLocal(0.6f);
        Vector3f camLeft = app.getCamera().getLeft().clone().multLocal(0.4f);
        walkDirection.set(0, 0, 0);

        if (moveForward) walkDirection.addLocal(camDir);
        if (moveBack)    walkDirection.addLocal(camDir.negate());
        if (moveLeft)    walkDirection.addLocal(camLeft);
        if (moveRight)   walkDirection.addLocal(camLeft.negate());

        player.setWalkDirection(walkDirection.multLocal(playerSpeed));

        // Actualizar posición de la cámara
        app.getCamera().setLocation(player.getPhysicsLocation().add(0, 1.5f, 0));
    }

    public void setMoveDirection(float dx, float dy, float maxRadius) {
        if (maxRadius <= 0f) return;
        float nx = Math.max(-1f, Math.min(1f, dx / maxRadius));
        float ny = Math.max(-1f, Math.min(1f, dy / maxRadius));

        moveRight = (nx > 0.2f);
        moveLeft = (nx < -0.2f);
        moveForward = (ny > 0.2f);
        moveBack = (ny < -0.2f);
    }

    public void rotateCamera(float rotX, float rotY) {
        if (app == null || app.getCamera() == null) return;

        Quaternion yaw = new Quaternion();
        yaw.fromAngleAxis(-rotX, Vector3f.UNIT_Y);

        Quaternion pitch = new Quaternion();
        pitch.fromAngleAxis(-rotY, app.getCamera().getLeft());

        Quaternion rot = yaw.mult(app.getCamera().getRotation()).mult(pitch);
        app.getCamera().setRotation(rot);
    }

    public void jump() {
        if (player != null && player.onGround()) {
            player.jump();
        }
    }

    public SimpleApplication getApp() {
        return app;
    }

    public void setApp(SimpleApplication app) {
        this.app = app;
    }

    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    public void setBulletAppState(BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
    }

    public CharacterControl getPlayer() {
        return player;
    }

    public boolean isMoveForward() {
        return moveForward;
    }

    public void setMoveForward(boolean moveForward) {
        this.moveForward = moveForward;
    }

    public boolean isMoveBack() {
        return moveBack;
    }

    public void setMoveBack(boolean moveBack) {
        this.moveBack = moveBack;
    }

    public boolean isMoveLeft() {
        return moveLeft;
    }

    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    public boolean isMoveRight() {
        return moveRight;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }

    public float getPlayerSpeed() {
        return playerSpeed;
    }

    public void setPlayerSpeed(float playerSpeed) {
        this.playerSpeed = playerSpeed;
    }

    public float getJumpForce() {
        return jumpForce;
    }

    public void setJumpForce(float jumpForce) {
        this.jumpForce = jumpForce;
    }
}
