package GroupProject;
import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.*;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

import java.awt.*;
import java.awt.event.*;

public class Lamp {
    static Color3f lightColor = new Color3f(1.0f, 1.0f, 1.0f);
    private static PointLight pointLight = new PointLight(lightColor, new Point3f(0.0f, 0.6f, 0.0f), new Point3f(1.0f, 0.0f, 0.0f));
    private static TransformGroup baseTG = new TransformGroup(); // TransformGroup for the base

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

        // Create the scene and add it to the universe
        BranchGroup scene = createSceneGraph(canvas);
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

    public static BranchGroup createSceneGraph(Canvas3D canvas) {
        BranchGroup root = new BranchGroup();

        // Create the lamp TransformGroup
        TransformGroup lampTG = createLamp(canvas);

        // Create a floor (box) with brown color
        Transform3D floorTransform = new Transform3D();
        floorTransform.setTranslation(new Vector3f(0.0f, -0.5f, 0.0f));
        TransformGroup floorTG = new TransformGroup(floorTransform);

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

    public static TransformGroup createLamp(Canvas3D canvas) {
        TransformGroup lampTG = new TransformGroup();

        // Create the lamp base (cylinder) with texture
        Appearance baseAppearance = createTexturedAppearance("src/img/wood.jpg");
        Cylinder lampBase = new Cylinder(0.2f, 0.05f, Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS, baseAppearance);

        // Create the lamp neck (thin cylinder) with texture
        Appearance neckAppearance = createTexturedAppearance("src/img/gold.jpg");
        Cylinder lampNeck = new Cylinder(0.05f, 0.6f, Primitive.GENERATE_NORMALS | Primitive.GENERATE_TEXTURE_COORDS, neckAppearance);

        // Create the lamp shade (cone) with yellow color (no transparency)
        Appearance shadeAppearance = createMaterialAppearance(new Color3f(1.0f, 1.0f, 0.0f)); // Yellow shade
        Cone lampShade = new Cone(0.3f, 0.5f, Primitive.GENERATE_NORMALS, shadeAppearance);

        // Position the base
        Transform3D baseTransform = new Transform3D();
        baseTransform.setTranslation(new Vector3f(0.0f, 0.02f, 0.0f));
        baseTG.setTransform(baseTransform);
        baseTG.addChild(lampBase);

        // Add a MouseListener to the base TransformGroup
        baseTG.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        canvas.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyCode() == KeyEvent.VK_L) {
					toggleLight();
				}
			}
		});

        // Position the neck
        Transform3D neckTransform = new Transform3D();
        neckTransform.setTranslation(new Vector3f(0.0f, 0.3f, 0.0f));
        TransformGroup neckTG = new TransformGroup(neckTransform);
        neckTG.addChild(lampNeck);

        // Position the lampshade
        Transform3D shadeTransform = new Transform3D();
        shadeTransform.setTranslation(new Vector3f(0.0f, 0.6f, 0.0f));
        TransformGroup shadeTG = new TransformGroup(shadeTransform);
        shadeTG.addChild(lampShade);

        // Add all components directly to the lamp TransformGroup
        lampTG.addChild(baseTG);
        lampTG.addChild(neckTG);
        lampTG.addChild(shadeTG);

        return lampTG;
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

    // Helper method to create a textured appearance
    private static Appearance createTexturedAppearance(String texturePath) {
        Appearance appearance = new Appearance();
        TextureLoader loader = new TextureLoader(texturePath, null);
        Texture texture = loader.getTexture();
        appearance.setTexture(texture);
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
