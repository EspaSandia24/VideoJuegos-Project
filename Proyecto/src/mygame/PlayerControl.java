package mygame;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

public class PlayerControl {

    private Node player;
    private int health = 100;
    private BitmapText healthText;
    private Main app;

    public PlayerControl(Node player, Main app) {
        this.player = player;
        this.app = app;

        BitmapFont font = app.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        healthText = new BitmapText(font, false);
        healthText.setSize(font.getCharSet().getRenderedSize());
        healthText.setColor(ColorRGBA.Red);
        healthText.setText("Health: " + health);
        healthText.setLocalTranslation(10, app.getCamera().getHeight() - healthText.getLineHeight(), 0);
        app.getGuiNode().attachChild(healthText);
    }

    public void decreaseHealth(int amount) {
        health -= amount;
        healthText.setText("Health: " + health);
        if (health <= 0) {
            app.getRootNode().detachChild(player);
            healthText.setText("Health: 0 - Game Over");
        }
    }

    public void update(float tpf) {
        float speed = 8.0f;
        Vector3f direction = new Vector3f(0, 0, 0);

        if (app.isUpPressed()) {
            direction.z -= speed * tpf;
        }
        if (app.isDownPressed()) {
            direction.z += speed * tpf;
        }
        if (app.isLeftPressed()) {
            direction.x -= speed * tpf;
        }
        if (app.isRightPressed()) {
            direction.x += speed * tpf;
        }

        player.move(direction);

        for (Geometry projectile : app.getProjectiles()) {
            if (projectile.getWorldBound().intersects(player.getWorldBound())) {
                decreaseHealth(10);
                app.removeProjectile(projectile);
            }
        }
    }
}

