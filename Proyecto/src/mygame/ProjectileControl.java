package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

public class ProjectileControl {

    private Geometry projectile;
    private Vector3f direction;
    private float speed = 5f;
    private AssetManager assetManager;

    public ProjectileControl(Geometry projectile, Vector3f direction) {
        this.projectile = projectile;
        this.direction = direction;
    }

    ProjectileControl(Geometry projectile, Vector3f direction, String targetTowerID) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void update(float tpf) {
        Vector3f translation = projectile.getLocalTranslation().add(direction.mult(speed * tpf));
        projectile.setLocalTranslation(translation);
    }

    public Geometry getProjectile() {
        return projectile;
    }
public Geometry createProjectile(Vector3f position, Vector3f direction, String name, String targetTowerID) {
    Sphere sphere = new Sphere(16, 16, 0.2f);
    Geometry projectile = new Geometry(name, sphere);
    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", ColorRGBA.Blue);
    projectile.setMaterial(mat);
    projectile.setLocalTranslation(position);
    projectile.setUserData("targetTowerID", targetTowerID); // Establecer el identificador de la torre como datos de usuario
   // rootNode.attachChild(projectile);
    return projectile;
}


}


