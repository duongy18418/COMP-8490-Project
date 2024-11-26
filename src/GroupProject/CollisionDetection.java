package GroupProject;
import org.jogamp.java3d.*;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3f;
import java.util.Map.Entry;

public class CollisionDetection extends Behavior {
    private TransformGroup tg1;

    private WakeupCriterion criterion;

    public CollisionDetection(TransformGroup tg1) {
        this.tg1 = tg1;
        BoundingSphere bs = new BoundingSphere(new Point3d(0,0,0), 1);
    }

    @Override
    public void initialize() {
        criterion = new WakeupOnCollisionEntry(tg1);
        wakeupOn(criterion);
    }

    @Override
    public void processStimulus(java.util.Iterator<WakeupCriterion> criteria) {
        System.out.println("Collision detected between objects!");
        wakeupOn(criterion);
    }
    
 // Function to check if the ViewingPlatform (camera) collides with the Box
    public static boolean checkCollision(TransformGroup cameraTG, Entry<String, Bounds> bounds) {
        // Get the camera's position (ViewPlatform Transform)
        Transform3D cameraTransform = new Transform3D();
        cameraTG.getTransform(cameraTransform);
        Vector3f cameraPosition = new Vector3f();
        cameraTransform.get(cameraPosition);
        
        Point3d cam = new Point3d(cameraPosition);

        // Get the camera's bounding volume (a small sphere around the camera)
        BoundingSphere cameraBounds = new BoundingSphere(cam, 0.1); // Small radius for the camera

        // Check for intersection between the camera's bounding sphere and the box's bounding box
        if (cameraBounds.intersect(bounds.getValue())) {
            return true;
        }
        return false;
    }
}