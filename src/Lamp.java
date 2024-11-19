import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.*;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

import java.awt.*;
import java.awt.event.*;

public class Lamp {
    static Color3f lightColor = new Color3f(1.0f, 1.0f, 1.0f);
    private static PointLight pointLight = new PointLight(lightColor, new Point3f(0.0f, 0.6f, 0.0f), new Point3f(1.0f, 0.0f, 0.0f));
    
    static {
        pointLight.setCapability(PointLight.ALLOW_STATE_READ);
        pointLight.setCapability(PointLight.ALLOW_STATE_WRITE);
    }

    public static void main(String[] args) {
        // Create the main application window (Frame)
        Frame frame = new Frame("Lamp");
        frame.setLayout(new BorderLayout());

        // Create Canvas3D and set its size
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas.setSize(800, 600);

        // Add the Canvas3D to the main Frame
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);

        // Create the SimpleUniverse using the Canvas3D
        SimpleUniverse universe = new SimpleUniverse(canvas);

        // Add mouse click interaction for turning the light on and off
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleLight();
            }
        });

        // Create the scene and add it to the universe
        BranchGroup scene = createSceneGraph();
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(scene);

        // Close the application when the window is closed
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private static BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

        // Create the lamp TransformGroup
        TransformGroup lampTG = new TransformGroup();

        // Create the lamp base (cylinder) with orange color
        Appearance baseAppearance = createMaterialAppearance(new Color3f(1.0f, 0.6f, 0.0f)); // Orange base
        Cylinder lampBase = new Cylinder(0.2f, 0.05f, Primitive.GENERATE_NORMALS, baseAppearance);

        // Create the lamp neck (thin cylinder) with light brown color
        Appearance neckAppearance = createMaterialAppearance(new Color3f(0.8f, 0.5f, 0.2f)); // Light brown neck
        Cylinder lampNeck = new Cylinder(0.05f, 0.6f, Primitive.GENERATE_NORMALS, neckAppearance);

        // Create the lamp shade (cone) with yellow color (no transparency)
        Appearance shadeAppearance = createMaterialAppearance(new Color3f(1.0f, 1.0f, 0.0f)); // Yellow shade
        Cone lampShade = new Cone(0.3f, 0.5f, Primitive.GENERATE_NORMALS, shadeAppearance);

        // Position the neck above the base
        Transform3D neckTransform = new Transform3D();
        neckTransform.setTranslation(new Vector3f(0.0f, 0.3f, 0.0f));
        TransformGroup neckTG = new TransformGroup(neckTransform);
        neckTG.addChild(lampNeck);

        // Position the lampshade above the neck
        Transform3D shadeTransform = new Transform3D();
        shadeTransform.setTranslation(new Vector3f(0.0f, 0.6f, 0.0f));
        TransformGroup shadeTG = new TransformGroup(shadeTransform);
        shadeTG.addChild(lampShade);

        // Position the lamp base on the floor (table surface)
        Transform3D baseTransform = new Transform3D();
        baseTransform.setTranslation(new Vector3f(0.0f, 0.02f, 0.0f));
        TransformGroup baseTG = new TransformGroup(baseTransform);
        baseTG.addChild(lampBase);

        // Add base, neck, and shade to the lamp TransformGroup
        lampTG.addChild(baseTG);
        lampTG.addChild(neckTG);
        lampTG.addChild(shadeTG);

        // Create a floor (box) with brown color
        Appearance floorAppearance = createMaterialAppearance(new Color3f(0.6f, 0.3f, 0.1f)); // Brown floor
        Box floor = new Box(1.5f, 0.02f, 1.5f, Primitive.GENERATE_NORMALS, floorAppearance);
        Transform3D floorTransform = new Transform3D();
        floorTransform.setTranslation(new Vector3f(0.0f, -0.5f, 0.0f));
        TransformGroup floorTG = new TransformGroup(floorTransform);
        floorTG.addChild(floor);

        // Add the lamp as a child of the floor TransformGroup, so it sits on the floor
        floorTG.addChild(lampTG);
        root.addChild(floorTG);

        // Set up lighting
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        // Ambient light (low light for general environment)
        Color3f ambientColor = new Color3f(0.2f, 0.2f, 0.2f);
        AmbientLight ambientLight = new AmbientLight(ambientColor);
        ambientLight.setInfluencingBounds(bounds);
        root.addChild(ambientLight);

        // Point light to simulate lamp light shining through shade
        pointLight.setInfluencingBounds(bounds);
        root.addChild(pointLight);

        return root;
    }

    // Helper method to create a material appearance (for objects like the lamp base and neck)
    private static Appearance createMaterialAppearance(Color3f color) {
        Appearance appearance = new Appearance();
        Material material = new Material();
        material.setDiffuseColor(color);
        material.setSpecularColor(new Color3f(1.0f, 1.0f, 1.0f)); // Specular highlight for reflectivity
        material.setShininess(64.0f); // High shininess for reflective effect
        appearance.setMaterial(material);
        return appearance;
    }

    // Helper method to toggle the light on and off
    private static void toggleLight() {
        if (pointLight.getEnable()) {
            pointLight.setEnable(false);
            System.out.println("Lamp is Off");
        } else {
            pointLight.setEnable(true);
            System.out.println("Lamp is On");
        }
    }
}


