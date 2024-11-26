package GroupProject;


import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;

import java.awt.event.KeyEvent;
import java.util.Set;

public class Navigation {
    private TransformGroup targetTG;  // The object to be navigated
    private Transform3D transform = new Transform3D();
    

    // Movement increments
    private static final float MOVE_STEP = 0.005f;
    private static final float BOUND_STEP = 0.02f;

    public Navigation(TransformGroup targetTG) {
        this.targetTG = targetTG;
        
    }

    public void handleKeyEvent(Set<Integer> keysPressed, long deltaTime, boolean canMove) {
        

        // Retrieve the current transformation
        targetTG.getTransform(transform);

        // Create a translation vector
        Vector3f translation = new Vector3f();
        Vector3f forward = getForwardVector(transform);
        Vector3f right = getRightVector(transform);
        Vector3f up = getUpVector(transform);
        transform.get(translation);
        
        if(!canMove) {
        	if(keysPressed.contains(KeyEvent.VK_UP)||keysPressed.contains(KeyEvent.VK_W)) {
            	translation.x -= BOUND_STEP*forward.x*deltaTime;
            	translation.z -= BOUND_STEP*forward.z*deltaTime;
            	if(keysPressed.contains(KeyEvent.VK_UP)) keysPressed.remove(KeyEvent.VK_UP);
            	if(keysPressed.contains(KeyEvent.VK_W)) keysPressed.remove(KeyEvent.VK_W);
            }
            if(keysPressed.contains(KeyEvent.VK_DOWN)||keysPressed.contains(KeyEvent.VK_S)) {
            	translation.x += BOUND_STEP*forward.x*deltaTime;
            	translation.z += BOUND_STEP*forward.z*deltaTime;
            	if(keysPressed.contains(KeyEvent.VK_UP)) keysPressed.remove(KeyEvent.VK_DOWN);
            	if(keysPressed.contains(KeyEvent.VK_W)) keysPressed.remove(KeyEvent.VK_S);
            }
            if(keysPressed.contains(KeyEvent.VK_LEFT)||keysPressed.contains(KeyEvent.VK_A)) {
            	translation.x += BOUND_STEP*right.x*deltaTime;
            	translation.z += BOUND_STEP*right.z*deltaTime;   
            	if(keysPressed.contains(KeyEvent.VK_UP)) keysPressed.remove(KeyEvent.VK_LEFT);
            	if(keysPressed.contains(KeyEvent.VK_W)) keysPressed.remove(KeyEvent.VK_A);
            }
            if(keysPressed.contains(KeyEvent.VK_RIGHT)||keysPressed.contains(KeyEvent.VK_D)) {
            	translation.x -= BOUND_STEP*right.x*deltaTime;
            	translation.z -= BOUND_STEP*right.z*deltaTime;   
            	if(keysPressed.contains(KeyEvent.VK_UP)) keysPressed.remove(KeyEvent.VK_RIGHT);
            	if(keysPressed.contains(KeyEvent.VK_W)) keysPressed.remove(KeyEvent.VK_D);
            }
        }

        if(keysPressed.contains(KeyEvent.VK_UP)||keysPressed.contains(KeyEvent.VK_W)) {
        	translation.x += MOVE_STEP*forward.x*deltaTime;
        	translation.z += MOVE_STEP*forward.z*deltaTime;   
        }
        if(keysPressed.contains(KeyEvent.VK_DOWN)||keysPressed.contains(KeyEvent.VK_S)) {
        	translation.x -= MOVE_STEP*forward.x*deltaTime;
        	translation.z -= MOVE_STEP*forward.z*deltaTime;   
        }
        if(keysPressed.contains(KeyEvent.VK_LEFT)||keysPressed.contains(KeyEvent.VK_A)) {
        	translation.x -= MOVE_STEP*right.x*deltaTime;
        	translation.z -= MOVE_STEP*right.z*deltaTime;   
        }
        if(keysPressed.contains(KeyEvent.VK_RIGHT)||keysPressed.contains(KeyEvent.VK_D)) {
        	translation.x += MOVE_STEP*right.x*deltaTime;
        	translation.z += MOVE_STEP*right.z*deltaTime;   
        }
        
        
        getForwardVector(transform);
        // Set the new translation back to the transform
        transform.setTranslation(translation);
        targetTG.setTransform(transform);
    }
    
    public static Vector3f getForwardVector(Transform3D transform) {
        // Extract the rotation matrix from the Transform3D
        Matrix3f rotationMatrix = new Matrix3f();
        transform.get(rotationMatrix);

        // The forward vector corresponds to the negative Z-axis in the rotation matrix
        Vector3f forward = new Vector3f(0.0f, 0.0f, -1.0f);  // Initial forward vector (negative Z)
        
        // Apply the rotation matrix to the forward vector
        rotationMatrix.transform(forward);
        return forward;
    }

    public static Vector3f getRightVector(Transform3D transform) {
        // Extract the rotation matrix from the Transform3D
        Matrix3f rotationMatrix = new Matrix3f();
        transform.get(rotationMatrix);

        // The forward vector corresponds to the positive x-axis in the rotation matrix
        Vector3f right = new Vector3f(1.0f, 0.0f, 0.0f);  // Initial right vector (positive x)
        
        // Apply the rotation matrix to the forward vector
        rotationMatrix.transform(right);
        return right;
    }

    public static Vector3f getUpVector(Transform3D transform) {
        // Extract the rotation matrix from the Transform3D
        Matrix3f rotationMatrix = new Matrix3f();
        transform.get(rotationMatrix);

        // The forward vector corresponds to the positive y-axis in the rotation matrix
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);  // Initial forward vector (positive y)
        
        // Apply the rotation matrix to the forward vector
        rotationMatrix.transform(up);
        return up;
    }

}
