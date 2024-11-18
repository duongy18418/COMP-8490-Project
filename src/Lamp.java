import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.*;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

import java.awt.*;
import java.awt.event.*;

public class Lamp {
    private static boolean lightOn = true;
    private static PointLight pointLight;

    public static void main(String[] args) {
        // Create the main application window (Frame)
        Frame frame = new Frame("Interactive Lamp");
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

        // Load textures
        Texture baseTexture = loadTexture("img/lamp_base.jpg");
        Texture neckTexture = loadTexture("img/lamp_neck.jpg");
        Texture shadeTexture = loadTexture("img/lamp_shade.jpg");

        // Create the lamp base (cylinder)
        Appearance baseAppearance = createTextureAppearance(baseTexture);
        Cylinder lampBase = new Cylinder(0.2f, 0.05f, Primitive.GENERATE_TEXTURE_COORDS, baseAppearance);

        // Create the lamp neck (thin cylinder)
        Appearance neckAppearance = createTextureAppearance(neckTexture);
        Cylinder lampNeck = new Cylinder(0.05f, 0.6f, Primitive.GENERATE_TEXTURE_COORDS, neckAppearance);

     // Create the lamp shade (cone) with 50% transparency in texture
        Appearance shadeAppearance = createTextureAppearance(shadeTexture);

        // Set transparency attributes for the texture
        TransparencyAttributes transparencyAttributes = new TransparencyAttributes();
        transparencyAttributes.setTransparencyMode(TransparencyAttributes.BLENDED);
        transparencyAttributes.setTransparency(0.5f); // 50% transparency
        shadeAppearance.setTransparencyAttributes(transparencyAttributes);

        // Create the lamp shade (cone)
        Cone lampShade = new Cone(0.3f, 0.5f, Primitive.GENERATE_TEXTURE_COORDS, shadeAppearance);

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

        // Create a floor (gray box)
        Appearance floorAppearance = new Appearance();
        Color3f floorColor = new Color3f(0.7f, 0.7f, 0.7f);
        ColoringAttributes floorColoring = new ColoringAttributes(floorColor, ColoringAttributes.SHADE_FLAT);
        floorAppearance.setColoringAttributes(floorColoring);

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

        // Ambient light
        Color3f ambientColor = new Color3f(0.2f, 0.2f, 0.2f);
        AmbientLight ambientLight = new AmbientLight(ambientColor);
        ambientLight.setInfluencingBounds(bounds);
        root.addChild(ambientLight);

        // Point light to simulate the lamp's light
        Color3f lightColor = new Color3f(1.0f, 1.0f, 1.0f);
        pointLight = new PointLight(lightColor, new Point3f(0.0f, 0.6f, 0.0f), new Point3f(1.0f, 0.0f, 0.0f));
        pointLight.setInfluencingBounds(bounds);
        root.addChild(pointLight);

        return root;
    }

    // Helper method to load a texture
    private static Texture loadTexture(String filename) {
        TextureLoader loader = new TextureLoader(filename, "RGB", new Container());
        Texture texture = loader.getTexture();
        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);
        texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);
        texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
        return texture;
    }

    // Helper method to create an appearance with a texture
    private static Appearance createTextureAppearance(Texture texture) {
        Appearance appearance = new Appearance();
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        appearance.setTexture(texture);
        appearance.setTextureAttributes(texAttr);
        return appearance;
    }

    // Helper method to toggle the light on and off
    private static void toggleLight() {
        lightOn = !lightOn;
        pointLight.setEnable(lightOn);
        System.out.println("Light is now " + (lightOn ? "ON" : "OFF"));
    }
}
