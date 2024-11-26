package GroupProject;

import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseMovement extends Behavior implements MouseMotionListener {
    private TransformGroup targetTG;  // The object to be moved
    private Transform3D transform = new Transform3D();
    private WakeupCriterion dragCondition, sleepCondition;
    private WakeupCondition wakeupCondition;
    
    private float angleX = 0, angleY = 0, clamp = 45;

    private int lastMouseX = -1, lastMouseY = -1; // Store the last mouse position
    private static final float MOVE_SCALE = 0.003f; // Scaling factor for movement

    public MouseMovement(TransformGroup targetTG) {
        this.targetTG = targetTG;
        this.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        this.dragCondition = new WakeupOnAWTEvent(MouseEvent.MOUSE_DRAGGED);
        this.sleepCondition = new WakeupOnAWTEvent(MouseEvent.MOUSE_RELEASED);
        this.wakeupCondition = new WakeupOr(new WakeupCriterion[]{dragCondition, sleepCondition});
        
    }

    @Override
    public void initialize() {
        wakeupOn(wakeupCondition);
    }

    @Override
    public void processStimulus(java.util.Iterator<WakeupCriterion> criteria) {
        // Process mouse events
        while (criteria.hasNext()) {
            WakeupCriterion wakeup = (WakeupCriterion) criteria.next();
            if (wakeup instanceof WakeupOnAWTEvent) {
                AWTEvent[] events = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
                for (AWTEvent event : events) {
                	System.out.println(event.toString());
                    if (event instanceof MouseEvent) {                    	
                        if(event.paramString().split(",")[0].equals("MOUSE_RELEASED")) {
                        	lastMouseX = -1;
                        	lastMouseY = -1;
                        	continue;
                        }
                    	handleMouseDrag((MouseEvent) event);
                    }
                }
            }
        }
        wakeupOn(wakeupCondition);
    }

    private void handleMouseDrag(MouseEvent e) {
        if (lastMouseX == -1 || lastMouseY == -1) {
            // Initialize last position if it's the first drag
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            return;
        }

        // Calculate movement deltas
        int deltaX = e.getX() - lastMouseX;
        int deltaY = e.getY() - lastMouseY;

        // Update the last position
        lastMouseX = e.getX();
        lastMouseY = e.getY();

        // Retrieve the current transformation
        targetTG.getTransform(transform);


        // Adjust the rotation based on mouse movement
        angleX += deltaY*MOVE_SCALE;
        angleY += deltaX*MOVE_SCALE;
        
        angleX = (float) Math.min(Math.max(angleX, Math.toRadians(-clamp)), Math.toRadians(clamp));
        
     // Create new rotation quaternions
        Quat4f newXRotation = new Quat4f();
        Vector3f right = getRightVector(transform);
        AxisAngle4d axis = new AxisAngle4d(right.x, right.y, right.z ,angleX);
        newXRotation.set(axis);

        Quat4f newYRotation = new Quat4f();
        axis = new AxisAngle4d(0,1,0,angleY);
        newYRotation.set(axis);

        // Combine the quaternions with the existing Z rotation
        Quat4f combinedRotation = new Quat4f();
        combinedRotation.mul(newXRotation, newYRotation);
        
        
        transform.setRotation(combinedRotation);
        targetTG.setTransform(transform);
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
    
    @Override
    public void mouseDragged(MouseEvent e) {
        handleMouseDrag(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // No specific action required for mouse movement without dragging
    }
}
