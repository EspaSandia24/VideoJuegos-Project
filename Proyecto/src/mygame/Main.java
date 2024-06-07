
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
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
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
//import java.util.UUID;

public class Main extends SimpleApplication {

    private Node player;
    private List<ProjectileControl> projectiles = new ArrayList<>();
    private PlayerControl playerControl;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private final static String MAPPING_ROTATE = "Rotate";
    private final static Trigger TRIGGER_ROTATE = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
   private Geometry projectile;
Geometry target;
CollisionResults results ;
private boolean shoot = false; 
Node scene;


    public static void main(String[] args) {
        AppSettings setting =new AppSettings(true);
        setting.setTitle("Defensores de la Cristalina");
        setting.setWidth(1280); // Ancho deseado
        setting.setHeight(720);
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.White);
        initKeys();
        inputManager.addMapping(MAPPING_ROTATE, TRIGGER_ROTATE);
        inputManager.addListener(analogListener, new String[]{MAPPING_ROTATE});
        scene = new Node("MiEscenario");
        rootNode.attachChild(scene);
        player = (Node) assetManager.loadModel("Models/Oto.mesh.xml");
        player.setLocalScale(0.3f);
        rootNode.attachChild(player);
        player.rotate(0, FastMath.PI, 0);

        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.5f, -1f, -1).normalizeLocal());
        rootNode.addLight(dl);

        cam.lookAt(player.getLocalTranslation(), Vector3f.UNIT_Y);
        ChaseCamera chaseCam = new ChaseCamera(cam, player, inputManager);
        chaseCam.setDefaultDistance(10); // Distancia de la cámara al personaje
        chaseCam.setMaxDistance(10); // Distancia máxima de la cámara
        chaseCam.setMinDistance(2);
        chaseCam.setDefaultVerticalRotation(0.2f);
        chaseCam.setDefaultHorizontalRotation(FastMath.PI / 2);

        // Actualiza la posición de la cámara según la posición del jugador
        chaseCam.setSpatial(player);

        flyCam.setDragToRotate(true);
        inputManager.setCursorVisible(true);


        // Crear torres
        createTower(new Vector3f(-5, -1.5f, -20));
        createTower(new Vector3f(5, -1.5f, -20));
        createTower(new Vector3f(-15, -1.5f, -20));
        createTower(new Vector3f(15, -1.5f, -20));
        
        // Crear y agregar el suelo
        createFloor();

        playerControl = new PlayerControl(player, this);
    }

    private void createTower(Vector3f position) {
        // Carga el modelo de la torre y ajusta su escala
        Node towerModel = (Node) assetManager.loadModel("Models/model_torre.glb");
        float towerScale = 0.20f; // Factor de escala para ajustar el tamaño de las torres
        towerModel.setLocalTranslation(position);
        towerModel.setLocalScale(towerScale); // Ajusta la escala según sea necesario

        // Adjunta la torre al nodo de la escena
        scene.attachChild(towerModel);

        stateManager.attach(new TowerControl(towerModel, player, this)); //towerMode antes era 'tower'
    }
    
    //funcion para la creacion del suelo
    private void createFloor() {
        // Crear el mesh para el suelo
        Quad floorMesh = new Quad(100, 100); // Tamaño del suelo (ancho, alto)

        // Crear la geometría para el suelo
        Geometry floor = new Geometry("Floor", floorMesh);

        // Crear el material para el suelo       
        Material floorMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        // Cargar la textura de pasto
        Texture grassTexture = assetManager.loadTexture("Textures/pasto.jpg");
        floorMaterial.setTexture("DiffuseMap", grassTexture);

        // Asignar el material a la geometría del suelo
        floor.setMaterial(floorMaterial);

        // Rotar el suelo para que esté en el plano XZ (horizontal)
        floor.rotate(-FastMath.HALF_PI, 0, 0);

        // Posicionar el suelo en la escena
        floor.setLocalTranslation(-50, -1.45f, 50); // Ajusta la posición del suelo

        // Añadir el suelo al nodo raíz
        rootNode.attachChild(floor); // Añade el suelo a la escena
    }

    public Geometry createProjectile(Vector3f position, ColorRGBA Red) {
        Sphere sphere = new Sphere(16, 16, 0.2f);
        Geometry projectile = new Geometry("Projectile", sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        projectile.setMaterial(mat);
        projectile.setLocalTranslation(position);
        rootNode.attachChild(projectile);
        return projectile;
    }
    public void addProjectile(Geometry projectile, Vector3f direction) {
        projectiles.add(new ProjectileControl(projectile, direction));
    }

    public void removeProjectile(Geometry projectile) {
        rootNode.detachChild(projectile);
        projectiles.removeIf(proj -> proj.getProjectile().equals(projectile));
    }

    public void removeProjectilesByTower(String towerID) {
    List<ProjectileControl> toRemove = new ArrayList<>();
    for (ProjectileControl proj : projectiles) {
        String projTowerID = proj.getProjectile().getUserData("targetTowerID");
        if (towerID.equals(projTowerID)) {
            rootNode.detachChild(proj.getProjectile());
            toRemove.add(proj);
        }
    }
    projectiles.removeAll(toRemove);
}

    
    public List<Geometry> getProjectiles() {
        List<Geometry> geometries = new ArrayList<>();
        for (ProjectileControl proj : projectiles) {
            geometries.add(proj.getProjectile());
            ////
        }
        return geometries;
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    @Override
    public void simpleUpdate(float tpf) {
        for (ProjectileControl proj : projectiles) {
            proj.update(tpf);
        }
        playerControl.update(tpf);
        
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
            ///
            projectile=null;
            
            // Elimina el cubo disparado
            if (results.getClosestCollision().getGeometry().getUserData("towerID") != null) {
        shoot = false;
        rootNode.detachChild(projectile);
        scene.detachChild(target);

            }
        }
        }
    }

   /* @Override
public void simpleUpdate(float tpf) {
  for (ProjectileControl proj : projectiles) {
    proj.update(tpf);
  }
  playerControl.update(tpf);

  if (upPressed) {
    player.move(0, 0, -speed * tpf * 4); // Move the player upwards
  }
  // ... (rest of the movement code)

  if (shoot) {
    Vector3f targetPosition = results.getClosestCollision().getContactPoint(); // Position of the tower
    Vector3f projectilePosition = projectile.getLocalTranslation();
    Vector3f direction = targetPosition.subtract(projectilePosition).normalizeLocal();
    float speed = 20f; // Projectile speed

    projectile.move(direction.mult(speed * tpf));

    float distanceToTarget = targetPosition.distance(projectilePosition);
    if (distanceToTarget < 0.5f) {
      // Check if the target is a tower
      if (results.getClosestCollision().getGeometry().getUserData("towerID") != null) {
        shoot = false;
        rootNode.detachChild(projectile);
        scene.detachChild(target);
      }
    }
  }
}*/

    @Override
    public void simpleRender(RenderManager rm) {}

    private void initKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_DOWN));
        
        
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

