package com.miempresa.mifpsgame.game.player;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.miempresa.mifpsgame.game.enemies.EnemyControl;

public class WeaponControl {

    private final SimpleApplication app;
    private Node wandNode;
    private int ammoReserve = 100;
    private float damage = 35f;

    public WeaponControl(SimpleApplication app) {
        this.app = app;
        createAndAttachWand();
    }

    /** Construye el modelo visual de la varita mágica y lo coloca en pantalla */
    private void createAndAttachWand() {
        wandNode = new Node("WandNode");

        try {
            // Intenta cargar un modelo 3D personalizado si existe en tus assets
            Spatial wandModel = app.getAssetManager().loadModel("Models/Wand/wand.j3o");
            wandNode.attachChild(wandModel);
        } catch (Exception e) {
            // Generación de varita mediante geometría 3D nativa si no hay archivo .j3o

            // 1. Cuerpo/Báculo de madera (Cilindro delgado)
            Cylinder stick = new Cylinder(16, 16, 0.02f, 0.7f, true);
            Geometry stickGeo = new Geometry("WandStick", stick);
            Material matStick = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            matStick.setColor("Color", new ColorRGBA(0.4f, 0.2f, 0.05f, 1.0f)); // Café madera
            stickGeo.setMaterial(matStick);

            // 2. Orbe Mágico luminoso en la punta (Esfera Cian)
            Sphere orb = new Sphere(16, 16, 0.06f);
            Geometry orbGeo = new Geometry("WandOrb", orb);
            Material matOrb = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            matOrb.setColor("Color", ColorRGBA.Cyan); // Azul mágico brillante
            orbGeo.setMaterial(matOrb);
            orbGeo.setLocalTranslation(0f, 0.35f, 0f); // Posicionarlo en la punta superior del cilindro

            wandNode.attachChild(stickGeo);
            wandNode.attachChild(orbGeo);
        }

        // Adjuntar el nodo de la varita al nodo raíz del mundo 3D
        app.getRootNode().attachChild(wandNode);
    }

    /**
     * Mantiene la varita mágica anclada a la vista de la cámara.
     * Se llama en cada frame desde simpleUpdate().
     */
    public void updateWandPosition() {
        if (wandNode == null || app.getCamera() == null) return;

        Vector3f camLoc = app.getCamera().getLocation();
        Vector3f camDir = app.getCamera().getDirection();
        Vector3f camUp = app.getCamera().getUp();
        Vector3f camLeft = app.getCamera().getLeft();

        // Calcular posición en el espacio 3D frente a los ojos (Abajo a la derecha)
        Vector3f wandPos = camLoc.add(camDir.mult(0.6f))      // 0.6m hacia adelante
                .add(camLeft.mult(-0.25f))   // 0.25m a la derecha
                .add(camUp.mult(-0.25f));    // 0.25m abajo

        wandNode.setLocalTranslation(wandPos);
        wandNode.setLocalRotation(app.getCamera().getRotation());
    }

    /** Lanzar hechizo mágico */
    public void castSpell() {
        if (ammoReserve <= 0) return;
        ammoReserve--;

        Ray ray = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
        CollisionResults results = new CollisionResults();
        app.getRootNode().collideWith(ray, results);

        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            createMagicSpark(closest.getContactPoint());

            Node targetNode = closest.getGeometry().getParent();
            if (targetNode != null) {
                EnemyControl enemy = targetNode.getControl(EnemyControl.class);
                if (enemy != null) {
                    enemy.takeDamage(damage);
                }
            }
        }
    }

    public void shoot() {
        castSpell();
    }

    private void createMagicSpark(Vector3f position) {
        Sphere sparkSphere = new Sphere(8, 8, 0.15f);
        Geometry spark = new Geometry("MagicSpark", sparkSphere);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Cyan);
        spark.setMaterial(mat);
        spark.setLocalTranslation(position);

        app.getRootNode().attachChild(spark);

        new Thread(() -> {
            try {
                Thread.sleep(120);
                app.enqueue(() -> spark.removeFromParent());
            } catch (InterruptedException ignored) {}
        }).start();
    }

    public void addReserve(int amount) { this.ammoReserve += amount; }
    public int getAmmoReserve() { return ammoReserve; }
}