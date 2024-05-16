/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author crist
 */
public class proyectil extends  Node{
    private Vector3f targetPosition;
    private float movementSpeed = 2f; // Velocidad de movimiento

    public proyectil() {
        // Configura la torre (carga modelo, texturas, etc.)
        // ...
    }

    public void setTargetPosition(Vector3f target) {
        this.targetPosition = target;
    }

    @Override
    public void updateLogicalState(float tpf) {
        super.updateLogicalState(tpf);

        if (targetPosition != null) {
            // Calcula la dirección hacia el objetivo
            Vector3f direction = targetPosition.subtract(getLocalTranslation()).normalizeLocal();

            // Mueve la torre hacia el objetivo
            move(direction.mult(movementSpeed * tpf));
        }
    }
}
