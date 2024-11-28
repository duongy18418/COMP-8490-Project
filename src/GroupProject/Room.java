package GroupProject;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.*;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;
import java.util.Map.Entry;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {

    private static TransformGroup cameraTransformGroup;
    private static Transform3D cameraTransform = new Transform3D();
    private static Vector3f cameraPosition = new Vector3f(0f, 1.5f, 0f); // Center of the room
    private static String floorTexture = "src/img/room/floor.jpg";
    private static String wallTexture = "src/img/room/wall.jpg";

    private static float yaw = (float) Math.PI;;   // Horizontal rotation
    private static float pitch = 0f; // Vertical rotation

    public static BranchGroup CreateRoom(Map<String, Bounds> bounds) {
    	// Create the scene
        BranchGroup scene = new BranchGroup();
        scene.addChild(createFloor(bounds));
        scene.addChild(createWall(new Vector3f(0f, 2.5f, -5f), 10f, 5f, 0.1f, bounds, "w1")); // Back wall
        scene.addChild(createWall(new Vector3f(5f, 2.5f, 0f), 0.1f, 5f, 10f, bounds, "w2"));  // Right wall
        scene.addChild(createWall(new Vector3f(-5f, 2.5f, 0f), 0.1f, 5f, 10f, bounds,"w3")); // Left wall
        scene.addChild(createWall(new Vector3f(0f, 2.5f, 5f), 10f, 5f, 0.1f, bounds, "w4"));  // Front wall
        scene.addChild(createCeiling(new Vector3f(0f, 5f, 0f), 10f, 0.1f, 10f, bounds)); // Ceiling
        addLighting(scene);
        
        return scene;
    }
    
    public static void main(String[] args) {
        // Create a JFrame to hold the Canvas3D
    	Map<String, Bounds> bounds = new HashMap<String, Bounds>();
        Frame mainFrame = new Frame("Room");
        mainFrame.setSize(1024, 768); // Set the window size
        mainFrame.setLayout(new BorderLayout());
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });

        // Create the Canvas3D and add it to the frame
        Canvas3D canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        mainFrame.add(canvas3D, BorderLayout.CENTER);
        mainFrame.setVisible(true);

        // Create a SimpleUniverse with the Canvas3D
        SimpleUniverse universe = new SimpleUniverse(canvas3D);

        // Increase the field of view
        adjustFieldOfView(universe);

        // Set up the camera transform group
        cameraTransformGroup = universe.getViewingPlatform().getViewPlatformTransform();
        updateCameraTransform();

        // Create the scene
        BranchGroup scene = new BranchGroup();
        scene.addChild(createFloor(bounds));
        scene.addChild(createWall(new Vector3f(0f, 2.5f, -5f), 10f, 5f, 0.1f, bounds, "w1")); // Back wall
        scene.addChild(createWall(new Vector3f(5f, 2.5f, 0f), 0.1f, 5f, 10f, bounds, "w2"));  // Right wall
        scene.addChild(createWall(new Vector3f(-5f, 2.5f, 0f), 0.1f, 5f, 10f, bounds, "w3")); // Left wall
        scene.addChild(createWall(new Vector3f(0f, 2.5f, 5f), 10f, 5f, 0.1f, bounds, "w4"));  // Front wall
        scene.addChild(createCeiling(new Vector3f(0f, 5f, 0f), 10f, 0.1f, 10f, bounds)); // Ceiling
        addLighting(scene);

        // Compile and attach the scene
        scene.compile();
        universe.addBranchGraph(scene);

        // Add input controls for movement and rotation
        addKeyboardControls(canvas3D);
        addMouseControls(canvas3D);
    }

    private static void adjustFieldOfView(SimpleUniverse universe) {
        View view = universe.getViewer().getView();
        view.setFieldOfView(Math.toRadians(90.0)); // Increase the FOV to 90 degrees
    }

    private static void updateCameraTransform() {
        // Update the camera's position and orientation
        Transform3D rotation = new Transform3D();
        rotation.rotY(yaw); // Apply yaw (horizontal rotation)

        Transform3D pitchTransform = new Transform3D();
        pitchTransform.rotX(pitch); // Apply pitch (vertical rotation)
        rotation.mul(pitchTransform);

        cameraTransform.setTranslation(cameraPosition);
        rotation.mul(cameraTransform);

        cameraTransformGroup.setTransform(rotation);
    }

    private static void addKeyboardControls(Canvas3D canvas3D) {
        canvas3D.addKeyListener(new KeyAdapter() {
            private final float moveStep = 0.2f;

            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                if (keyCode == KeyEvent.VK_W) { // Move backward instead of forward
                    cameraPosition.z += moveStep * Math.cos(yaw);
                    cameraPosition.x += moveStep * Math.sin(yaw);
                } else if (keyCode == KeyEvent.VK_S) { // Move forward instead of backward
                    cameraPosition.z -= moveStep * Math.cos(yaw);
                    cameraPosition.x -= moveStep * Math.sin(yaw);
                } else if (keyCode == KeyEvent.VK_A) { // Move right instead of left
                    cameraPosition.x += moveStep * Math.cos(yaw);
                    cameraPosition.z -= moveStep * Math.sin(yaw);
                } else if (keyCode == KeyEvent.VK_D) { // Move left instead of right
                    cameraPosition.x -= moveStep * Math.cos(yaw);
                    cameraPosition.z += moveStep * Math.sin(yaw);
                }
                updateCameraTransform();
            }
        });
    }

    private static void addMouseControls(Canvas3D canvas3D) {
        canvas3D.addMouseMotionListener(new MouseAdapter() {
            private Point lastMousePosition;

            public void mouseDragged(MouseEvent e) {
                if (lastMousePosition == null) {
                    lastMousePosition = e.getPoint();
                    return;
                }

                int dx = e.getX() - lastMousePosition.x;
                int dy = e.getY() - lastMousePosition.y;

                yaw -= dx * 0.01f;  // Adjust yaw based on horizontal mouse movement
                pitch += dy * 0.01f; // Adjust pitch based on vertical mouse movement

                // Clamp the pitch to avoid extreme angles
                pitch = (float) Math.max(-Math.PI / 2, Math.min(Math.PI / 2, pitch));

                updateCameraTransform();
                lastMousePosition = e.getPoint();
            }

            public void mouseMoved(MouseEvent e) {
                lastMousePosition = e.getPoint();
            }
        });
    }

    private static TransformGroup createFloor(Map<String, Bounds> bounds) {
        TransformGroup tg = new TransformGroup();
        Appearance floorAppearance = createTextureAppearance(floorTexture);
        Box floor = new Box(5f, 0.01f, 5f, Box.GENERATE_TEXTURE_COORDS | Box.GENERATE_NORMALS, floorAppearance);
        Point3d lower = new Point3d(0, 0, 0);
        Point3d upper = new Point3d(10, 0.02f, 10);
        BoundingBox bound = new BoundingBox(lower, upper);
        Transform3D transform = new Transform3D();
        bounds.put("floor", bound);
        transform.setTranslation(new Vector3f(0f, -0.01f, 0f));
        tg.setTransform(transform);
        tg.addChild(floor);
        bound.transform(transform);
        return tg;
    }

    private static TransformGroup createWall(Vector3f position, float width, float height, float depth, Map<String, Bounds> bounds, String key) {
        TransformGroup tg = new TransformGroup();
        Appearance wallAppearance = createTextureAppearance(wallTexture);
        Point3d lower = new Point3d(-width / 2, -height / 2, -depth / 2);
        Point3d upper = new Point3d(width / 2, height / 2, depth / 2);
        Box wall = new Box(width / 2, height / 2, depth / 2, Box.GENERATE_TEXTURE_COORDS | Box.GENERATE_NORMALS, wallAppearance);
        BoundingBox box = new BoundingBox(lower, upper);
        bounds.put(key,box);
        Transform3D transform = new Transform3D();
        transform.setTranslation(position);
        tg.setTransform(transform);
        tg.addChild(wall);
        box.transform(transform);
        return tg;
    }
    
    private static TransformGroup createCeiling(Vector3f position, float width, float height, float depth, Map<String, Bounds> bounds) {
        TransformGroup tg = new TransformGroup();
        Appearance ceilingAppearance = createWhiteAppearance(); // Use a white color for the ceiling
        Box ceiling = new Box(width / 2, height / 2, depth / 2, Box.GENERATE_NORMALS, ceilingAppearance);
        Point3d lower = new Point3d(-width / 2, -height / 2, -depth / 2);
        Point3d upper = new Point3d(width / 2, height / 2, depth / 2);
        BoundingBox box = new BoundingBox(lower, upper);
        bounds.put("ceiling",box);
        Transform3D transform = new Transform3D();
        transform.setTranslation(position);
        tg.setTransform(transform);
        tg.addChild(ceiling);
        box.transform(transform);
        return tg;
    }

    private static Appearance createTextureAppearance(String textureFile) {
        TextureLoader loader = new TextureLoader(textureFile, "LUMINANCE", new Container());
        Texture texture = loader.getTexture();
        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);
        texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
        TextureAttributes texAttr = new TextureAttributes();
        Appearance appearance = new Appearance();
        appearance.setTexture(texture);
        appearance.setTextureAttributes(texAttr);
        return appearance;
    }
    
    private static Appearance createWhiteAppearance() {
        Appearance appearance = new Appearance();
        ColoringAttributes white = new ColoringAttributes(new Color3f(1f, 1f, 1f), ColoringAttributes.NICEST);
        appearance.setColoringAttributes(white);
        return appearance;
    }

    private static void addLighting(BranchGroup root) {
        AmbientLight ambientLight = new AmbientLight(new Color3f(0.1f, 0.1f, 0.1f));
        ambientLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        root.addChild(ambientLight);
        DirectionalLight directionalLight = new DirectionalLight(
                new Color3f(0.1f, 0.1f, 0.1f),
                new Vector3f(-1.0f, -1.0f, -1.0f)
        );
        directionalLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        root.addChild(directionalLight);
    }
}
