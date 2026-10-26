package com.miempresa.mifpsgame.game.player;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class PlayerControl {

    private final CharacterControl character;
    private final Camera cam;
    private float health = 100f;
    private WeaponControl weapon;

    public PlayerControl(Camera cam, BulletAppState bulletAppState) {
        this.cam = cam;

        // Cápsula de colisión para el jugador (Radio: 0.5m, Altura: 1.8m)
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.5f, 1.8f, 1);

        // La altura de escalón (0.3f) se pasa directamente como segundo parámetro en el constructor
        character = new CharacterControl(capsuleShape, 0.3f);

        // Ajustes físicos para evitar flotar y atravesar paredes
        character.setGravity(30f);
        character.setFallSpeed(30f);
        character.setJumpSpeed(10f);
        character.setPhysicsLocation(new Vector3f(0f, 2f, 0f)); // Posición inicial sobre el suelo

        // Registrar en el espacio físico de Bullet
        if (bulletAppState != null && bulletAppState.getPhysicsSpace() != null) {
            bulletAppState.getPhysicsSpace().add(character);
        }
    }

    public void move(Vector3f walkDirection) {
        if (character == null) return;

        // Aplica la dirección de movimiento procesada por la entrada táctil
        character.setWalkDirection(walkDirection);

        // Sincroniza la posición de la cámara a la altura de los ojos (1.2m arriba del centro)
        cam.setLocation(character.getPhysicsLocation().add(0f, 1.2f, 0f));
    }

    public Vector3f getPosition() {
        return character.getPhysicsLocation();
    }

    public void takeDamage(float amount) {
        this.health -= amount;
        if (this.health < 0) this.health = 0;
    }

    public void heal(float amount) {
        this.health = Math.min(this.health + amount, 100f);
    }

    public boolean isDead() {
        return health <= 0;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void setWeapon(WeaponControl weapon) {
        this.weapon = weapon;
    }

    public WeaponControl getWeapon() {
        return weapon;
    }
}