package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.input.Input;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {


  public static void main(String[] args) {
    AppSettings setting =new AppSettings(true);
    setting.setTitle("Defensores de la Cristalina");
    Main app = new Main();
    app.setSettings(setting);
    app.start();
  }
   public Geometry cube=null;
   //private InputManager inputManager;

  @Override
  public void simpleInitApp() {
    
    Node scene = new Node("MiEscenario");
    rootNode.attachChild(scene);
    
    Box cubeMesh = new Box(1, 1, 1); // Tamaño del cubo
    cube = new Geometry("MiCubo", cubeMesh);
    Material cubeMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    cubeMaterial.setColor("Color", ColorRGBA.Blue); // Color del cubo
    cube.setMaterial(cubeMaterial);
    scene.attachChild(cube);
    cube.setLocalTranslation(0, 0, 0); // Posición del cubo (x, y, z)
    
    Box towerMesh = new Box(1, 3, 1); // Tamaño de la torre (ancho, alto, profundidad)
    Geometry tower = new Geometry("Torre", towerMesh);
    Material towerMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    towerMaterial.setColor("Color", ColorRGBA.Gray); // Color de la torre
    tower.setMaterial(towerMaterial);
    
    tower.setLocalTranslation(5, 0, 0);// Posición de la torre (x, y, z)
    
    ChaseCamera chaseCam = new ChaseCamera(cam, cube, inputManager);
    chaseCam.setDefaultDistance(10); // Distancia de la cámara al personaje
    chaseCam.setMaxDistance(20); // Distancia máxima de la cámara
    chaseCam.setMinDistance(5); // Distancia mínima de la cámara
 // Mira hacia el origen desde arriba

    scene.attachChild(tower);
    
    //inputManager = getInputManager();



  }

    @Override
     public void simpleUpdate(float tpf) {
        float speed = 0.1f * tpf; // Movement speed per frame

        // Use Input.KEY_LEFT for correct key mapping in JME 3.6.1
       /**
        if (inputManager.isKeyDown(Input.KEY_LEFT)) {
            cube.move(-speed, 0, 0); // Move left
        } else if (inputManager.isKeyDown(Input.KEY_RIGHT)) {
            cube.move(speed, 0, 0); // Move right
        }
        */
    }
   



    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
