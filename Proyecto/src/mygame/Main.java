package mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.anim.tween.action.BlendSpace;
import com.jme3.anim.tween.action.LinearBlendSpace;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.input.Input;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import static java.awt.SystemColor.control;
import java.util.UUID;
import static javax.swing.text.html.HTML.Attribute.N;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

private Action advance;
private AnimComposer control;
private Action advanceDerecha;
private Action advanceIzquierda;
Node player;
private boolean upPressed = false;
private boolean downPressed = false;
private boolean leftPressed = false;
private boolean rightPressed = false;
private boolean shoot = false; 
private Geometry projectile;
Geometry target;
 CollisionResults results ;

Node scene;
Geometry tower;
private proyectil ataque;
private final static String MAPPING_ROTATE = "Rotate";
private final static Trigger TRIGGER_ROTATE = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
   public static void main(String[] args) {
    AppSettings setting =new AppSettings(true);
    setting.setTitle("Defensores de la Cristalina");
    Main app = new Main();
    app.setSettings(setting);
    app.start();
  }
   public Geometry cube=null;
 

  @Override
  public void simpleInitApp() {
      
    viewPort.setBackgroundColor(ColorRGBA.White);
    initKeys();
    inputManager.addMapping(MAPPING_ROTATE, TRIGGER_ROTATE);
    inputManager.addListener(analogListener, new String[]{MAPPING_ROTATE});
    
    scene = new Node("MiEscenario");
    rootNode.attachChild(scene);
    
  
    
    Box towerMesh = new Box(1, 3, 1); // Tamaño de la torre (ancho, alto, profundidad)
    tower = new Geometry("Torre", towerMesh);
    Material towerMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    
    Texture texture = assetManager.loadTexture("Textures/torre.png"); // Ruta a tu imagen de textura
    towerMaterial.setTexture("ColorMap", texture);// Color de la torre
    tower.setMaterial(towerMaterial);
    
    Geometry tower2 = new Geometry("Torre", towerMesh);
    tower2.setMaterial(towerMaterial);
    
    Geometry tower3 = new Geometry("Torre", towerMesh);
    tower3.setMaterial(towerMaterial);
    
    Geometry tower4 = new Geometry("Torre", towerMesh);
    tower4.setMaterial(towerMaterial);
    
    
    
    
    player = (Node) assetManager.loadModel("Models/Oto.mesh.xml");
    player.setLocalScale(0.3f);
    rootNode.attachChild(player);
    player.rotate(0, FastMath.PI, 0);
    
    
    DirectionalLight dl = new DirectionalLight();
    dl.setDirection(new Vector3f(-0.5f, -1f, -1).normalizeLocal());
    rootNode.addLight(dl);
    
    
    cam.lookAt(player.getLocalTranslation(), Vector3f.UNIT_Y); // Mira hacia el jugador
    
    flyCam.setDragToRotate(true);
    inputManager.setCursorVisible(true);

    scene.attachChild(tower);
    scene.attachChild(tower2);    
    scene.attachChild(tower3);
    scene.attachChild(tower4);
    
    scene.move(0, 0, 0); // No se mueve
    
    

    player.setLocalTranslation(0, -1, 2); // Posición más cerca de la cámara (x, y, z)

// ...

    tower.setLocalTranslation(-5, 0, -20);
    tower2.setLocalTranslation(5, 0, -20);
    tower3.setLocalTranslation(-10, 0, -20);
    tower4.setLocalTranslation(10, 0, -20);

    control = player.getControl(AnimComposer.class);
    control.setCurrentAction("stand");

    BlendSpace quickBlend = new LinearBlendSpace(0f, 0.5f);
    Action halt = control.actionBlended("halt", quickBlend, "stand", "Walk");
    halt.setLength(0.5);

    Action walk = control.action("Walk");    
    Action walkDerecha = control.action("Walk");
    Action walkIzquierda = control.action("Walk");


    Tween doneTween = Tweens.callMethod(this, "onAdvanceDone");
    advance = control.actionSequence("advance", walk, halt, doneTween);
    advanceDerecha = control.actionSequence("advancederecha", walkDerecha, halt, doneTween);
    advanceIzquierda = control.actionSequence("advanceizquierda", walkIzquierda, halt, doneTween);

    
  }

    @Override
     public void simpleUpdate(float tpf) {
        if (upPressed) {
            player.move(0, 0, -speed * tpf * 4); // Mueve al jugador hacia arriba
        }
        if (leftPressed) {
            player.move(-speed * tpf*4, 0, 0);
        }
        if (rightPressed) {
            player.move(speed * tpf*4, 0, 0);
        }
        if (downPressed) {
            player.move(0, 0, speed * tpf * 4); // Mueve al jugador hacia arriba
        }
        
        if (shoot) {
            
        Vector3f targetPosition = results.getClosestCollision().getContactPoint(); // Posición de la torre
        Vector3f projectilePosition = projectile.getLocalTranslation();
        Vector3f direction = targetPosition.subtract(projectilePosition).normalizeLocal();
        float speed = 20f; // Velocidad del disparo
        projectile.move(direction.mult(speed * tpf));

        // Cuando colisiona con la torre (o alcanza la posición deseada), detén el disparo
        float distanceToTarget = targetPosition.distance(projectilePosition);
        if (distanceToTarget < 0.5f) {
            shoot = false;
            rootNode.detachChild(projectile);
            scene.detachChild(target);
            projectile=null;
            
            // Elimina el cubo disparado
        }
    }
      
    }
  


    @Override
    public void simpleRender(RenderManager rm) {}
    
    void onAdvanceDone() {
  
    control.setCurrentAction("stand");
  }

  private void initKeys() {
  inputManager.addMapping("Walk", new KeyTrigger(KeyInput.KEY_SPACE));
  inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
  inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
  inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_UP)); // Add mapping for up arrow
  inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_DOWN)); // Add mapping for down arrow

  ActionListener handler = new ActionListener() {
    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("Left") && keyPressed) {
          leftPressed = keyPressed;
      } else if (name.equals("Right") && keyPressed) {
          rightPressed = keyPressed;
      } else if (name.equals("Up")) {
           upPressed = keyPressed;
        
      } else if (name.equals("Down") && keyPressed) {
          downPressed = keyPressed;
      }
      
    if (!keyPressed) {
        if (name.equals("Left")) {
        leftPressed = false;
    } else if (name.equals("Right")) {
        rightPressed = false;
    } else if (name.equals("Up")) {
        upPressed = false;
    } else if (name.equals("Down")) {
        downPressed = false;
    }
}

    }
  };

  inputManager.addListener(handler, "Left", "Right", "Up", "Down"); // Add mappings to listener
}

  
  private final AnalogListener analogListener = new AnalogListener(){
        @Override
        public void onAnalog(String name, float intensity, float tpf){
            // creamos una lista vacia de resultado para las colisiones
            results = new CollisionResults();
            // Al hacer uso del mouse, se requiere de la posicion 2D de éste
            Vector2f click2d = inputManager.getCursorPosition();
            // Convertimos el vector2D en uno 3D para definir el origen del ray, ya que 
            // un ray requiere vector 3D
            Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.getX(), click2d.getY()), 0f);
            Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.getX(), click2d.getY()), 1f).subtractLocal(click3d);
            
            Ray ray = new Ray(click3d, dir);
            rootNode.collideWith(ray, results);
            
            if (name.equals(MAPPING_ROTATE) && results.size() > 0) {
                target = results.getClosestCollision().getGeometry();

               if (projectile == null) {
                shootCube(results.getClosestCollision().getContactPoint());
               } else {
                   shoot = true;
            }
            } else {
                System.out.println("Selection: Nothing");
            }
        }   
    };
  
    private void shootCube(Vector3f targetPosition) {
    shoot = true; // Activa el disparo
    String projectileId = "projectile_" + UUID.randomUUID().toString();
    projectile = new Geometry(projectileId, new Box(0.2f, 0.2f, 0.2f));
    Material cubeMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    cubeMaterial.setColor("Color", ColorRGBA.Blue);
    projectile.setMaterial(cubeMaterial);
    rootNode.attachChild(projectile);
    projectile.setLocalTranslation(player.getLocalTranslation());
    // Inicialmente en la posición del jugador
}
}
