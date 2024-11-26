package GroupProject;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.universe.*;
import org.jogamp.vecmath.*;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.Timer;

public class CollisionDetectionExample {

    public static void main(String[] args) {
        // Create the canvas and universe
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        SimpleUniverse universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();

        // Create the scene and add a box to it
        BranchGroup scene = new BranchGroup();
        scene.addChild(createBox());
        
        // Add collision detection logic
        TransformGroup cameraTG = universe.getViewingPlatform().getViewPlatformTransform();
        BoundingBox boxBounds = getBoundingBoxForBox();

        // Animation loop to check for collisions
        new Timer(16, e -> {
            checkCollision(cameraTG, boxBounds);
        }).start();

        // Setup the JFrame and canvas
        JFrame frame = new JFrame("Java 3D Collision Detection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(canvas);
        frame.setSize(800, 800);
        frame.setVisible(true);

        // Add the scene to the universe
        scene.compile();
        universe.addBranchGraph(scene);
    }

    // Function to create a Box object
    private static TransformGroup createBox() {
        TransformGroup boxTG = new TransformGroup();
        boxTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        Appearance appearance = new Appearance();
        Box box = new Box(0.5f, 0.5f, 0.5f, appearance);
        boxTG.addChild(box);

        // Position the box
        Transform3D boxTransform = new Transform3D();
        boxTransform.setTranslation(new Vector3f(0.0f, 0.0f, -3.0f)); // Move box back in Z direction
        boxTG.setTransform(boxTransform);

        return boxTG;
    }

    // Function to get the bounding box for the Box
    private static BoundingBox getBoundingBoxForBox() {
        // For simplicity, we define the bounding box as the same size as the Box object
        return new BoundingBox(new Point3d(-0.5, -0.5, -0.5), new Point3d(0.5, 0.5, 0.5));
    }

    // Function to check if the ViewingPlatform (camera) collides with the Box
    private static void checkCollision(TransformGroup cameraTG, BoundingBox boxBounds) {
        // Get the camera's position (ViewPlatform Transform)
        Transform3D cameraTransform = new Transform3D();
        cameraTG.getTransform(cameraTransform);
        Vector3f cameraPosition = new Vector3f();
        cameraTransform.get(cameraPosition);
        
        Point3d cam = new Point3d(cameraPosition);

        // Get the camera's bounding volume (a small sphere around the camera)
        BoundingSphere cameraBounds = new BoundingSphere(cam, 0.1); // Small radius for the camera

        // Check for intersection between the camera's bounding sphere and the box's bounding box
        if (cameraBounds.intersect(boxBounds)) {
            System.out.println("Collision Detected!");
        }
    }
}


