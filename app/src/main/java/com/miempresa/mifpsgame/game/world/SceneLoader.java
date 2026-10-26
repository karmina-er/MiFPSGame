package com.miempresa.mifpsgame.game.world;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class SceneLoader {

    private final SimpleApplication app;
    private final BulletAppState bulletAppState;

    public SceneLoader(SimpleApplication app, BulletAppState bullet) {
        this.app = app;
        this.bulletAppState = bullet;
    }

    public void loadScene() {
        // 1. Cargar cielo panorámico
        loadSky();

        // 2. Cargar modelo 3D del nivel si existe
        try {
            Spatial scene = app.getAssetManager().loadModel("Models/Level/nivel1.j3o");
            if (scene != null) {
                RigidBodyControl landscape = new RigidBodyControl(0.0f); // masa 0 = estático
                scene.addControl(landscape);
                app.getRootNode().attachChild(scene);
                if (bulletAppState != null && bulletAppState.getPhysicsSpace() != null) {
                    bulletAppState.getPhysicsSpace().add(landscape);
                }
            }
        } catch (Exception ignored) {
            // Si no existe el .j3o, el terreno plano y los obstáculos creados abajo sirven de mapa
        }

        // 3. Crear suelo base plano
        Box floorBox = new Box(50f, 0.1f, 50f);
        floorBox.scaleTextureCoordinates(new Vector2f(25f, 25f));

        Geometry floor = new Geometry("Floor", floorBox);
        Material matFloor = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");

        Texture grassTex = null;
        try {
            grassTex = app.getAssetManager().loadTexture("Textures/terrain_diffuse.jpg");
        } catch (Exception e1) {
            try {
                grassTex = app.getAssetManager().loadTexture("Textures/terrain_diffuse.png");
            } catch (Exception ignored) {
            }
        }

        if (grassTex != null) {
            grassTex.setWrap(Texture.WrapMode.Repeat);
            matFloor.setTexture("ColorMap", grassTex);
        } else {
            matFloor.setColor("Color", new ColorRGBA(0.2f, 0.6f, 0.2f, 1.0f));
        }

        floor.setMaterial(matFloor);
        floor.setLocalTranslation(0f, -0.1f, 0f);

        RigidBodyControl floorPhys = new RigidBodyControl(0f);
        floor.addControl(floorPhys);
        app.getRootNode().attachChild(floor);
        if (bulletAppState != null && bulletAppState.getPhysicsSpace() != null) {
            bulletAppState.getPhysicsSpace().add(floorPhys);
        }

        // 4. CREACIÓN Y ORGANIZACIÓN COHERENTE DE OBSTÁCULOS
        // La coordenada Y se calcula exactamente como el alto de la caja para apoyar el objeto en el suelo.

        // Cobertura frontal central
        createObstacle("Muro_Central", new Vector3f(3.0f, 1.5f, 0.5f), new Vector3f(0f, 1.5f, -8f), ColorRGBA.Brown);

        // Coberturas laterales para proteger los flancos del jugador
        createObstacle("Cobertura_Izquierda", new Vector3f(1.5f, 1.0f, 1.5f), new Vector3f(-6f, 1.0f, -4f), ColorRGBA.DarkGray);
        createObstacle("Cobertura_Derecha", new Vector3f(1.5f, 1.0f, 1.5f), new Vector3f(6f, 1.0f, -4f), ColorRGBA.DarkGray);

        // Parapetos cercanos a los puntos de aparicion de enemigos (SpawnPoints)
        createObstacle("Obstaculo_Spawn_Izq", new Vector3f(1.0f, 1.2f, 1.0f), new Vector3f(-8f, 1.2f, -12f), ColorRGBA.Gray);
        createObstacle("Obstaculo_Spawn_Der", new Vector3f(1.0f, 1.2f, 1.0f), new Vector3f(8f, 1.2f, -12f), ColorRGBA.Gray);
    }

    /**
     * Genera obstáculos sólidos con masa 0 apoyados correctamente sobre el terreno plano
     */
    private void createObstacle(String name, Vector3f halfExtents, Vector3f position, ColorRGBA color) {
        Box box = new Box(halfExtents.x, halfExtents.y, halfExtents.z);
        Geometry obstacle = new Geometry(name, box);

        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        obstacle.setMaterial(mat);

        // Posicionar el objeto
        obstacle.setLocalTranslation(position);

        // Asignar colisión estática en el motor Bullet (Masa 0 = Inamovible)
        RigidBodyControl phys = new RigidBodyControl(0f);
        obstacle.addControl(phys);

        app.getRootNode().attachChild(obstacle);
        if (bulletAppState != null && bulletAppState.getPhysicsSpace() != null) {
            bulletAppState.getPhysicsSpace().add(phys);
        }
    }

    private void loadSky() {
        try {
            Texture skyTexture = app.getAssetManager().loadTexture("Textures/sky.jpg");
            if (skyTexture != null) {
                Spatial sky = SkyFactory.createSky(
                        app.getAssetManager(),
                        skyTexture,
                        Vector3f.UNIT_XYZ,
                        SkyFactory.EnvMapType.SphereMap
                );
                app.getRootNode().attachChild(sky);
            }
        } catch (Exception ignored) {
        }
    }
}