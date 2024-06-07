
package mygame;

import com.jme3.app.state.AbstractAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.UUID;

public class TowerControl extends AbstractAppState {

    private Geometry tower;
    private Node player;
    private Main app;
    private float shootingInterval = 3.0f;
    private float timeSinceLastShot = 0;
    private int health = 50;
    private String towerID; // Nuevo miembro para identificar la torre

    private String targetID; // Add this line before the constructor

    /*
    public TowerControl(Geometry tower, Node player, Main app) {
        this.tower = tower;
        this.player = player;
        this.app = app;
    this.towerID = UUID.randomUUID().toString(); // Asigna un ID único a cada torre
    }*/
    public TowerControl(Geometry tower, Node player, Main app) {
    this.tower = tower;
    this.player = player;
    this.app = app;
    this.towerID = UUID.randomUUID().toString(); // Asigna un ID único a cada torre
}


@Override
public void update(float tpf) {
    timeSinceLastShot += tpf;

    if (timeSinceLastShot >= shootingInterval) {
        timeSinceLastShot = 0;
        shootAtPlayer();
    }

    // Check for collisions with player's projectiles
    for (Geometry projectile : app.getProjectiles()) {
        if (projectile.getWorldBound().intersects(tower.getWorldBound())) {
            String projectileTowerID = projectile.getUserData("targetTowerID");
            if (projectileTowerID != null && projectileTowerID.equals(towerID)) {
                decreaseHealth(10);
                app.removeProjectile(projectile);
            }
        }
    }


}

private void shootAtPlayer() {
  Vector3f towerPosition = tower.getLocalTranslation();
  Vector3f playerPosition = player.getLocalTranslation();
  Vector3f direction = playerPosition.subtract(towerPosition).normalizeLocal();
  Geometry projectile = app.createProjectile(towerPosition, ColorRGBA.Red);
  String targetTowerID = tower.getUserData("towerID"); // Get the tower's ID
  app.addProjectile(projectile, direction);
  projectile.setUserData("targetTowerID", targetTowerID); // Set the target ID for the projectile
}

/*
    private void shootAtPlayer() {
        Vector3f towerPosition = tower.getLocalTranslation();
        Vector3f playerPosition = player.getLocalTranslation();
        Vector3f direction = playerPosition.subtract(towerPosition).normalizeLocal();

       // Geometry projectile = app.createProjectile(towerPosition, ColorRGBA.Red, towerID);

        Geometry projectile = app.createProjectile(towerPosition, ColorRGBA.Red);
        String targetTowerID = null;
        app.addProjectile(projectile, direction);
        
    }*/

    public void decreaseHealth(int amount) {
        health -= amount;
        if (health <= 0) {
            app.removeProjectilesByTower(towerID);
            app.getRootNode().detachChild(tower);
            app.getStateManager().detach(this);
        }
    }

    public Geometry getTower() {
        return tower;
    }

    public int getHealth() {
        return health;
    }

    public String getTowerID() {
        return towerID;
    }
    public void deactivate() {
    app.getRootNode().detachChild(tower);
    app.getStateManager().detach(this);
}
    public void setTargetID(String projectileID) {
        this.targetID = projectileID;
}


}
