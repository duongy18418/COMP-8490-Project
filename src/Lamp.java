import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.*;
import org.jogamp.java3d.utils.geometry.Cylinder;
import org.jogamp.java3d.utils.geometry.Cone;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

import java.awt.*;
import javax.swing.*;

public class Lamp {
    public static void main(String[] args) {
        // Create a Simple Universe
        SimpleUniverse universe = new SimpleUniverse();

        // Create a BranchGroup for the scene
        BranchGroup scene = createSceneGraph();

        // Set up the viewing platform
        universe.getViewingPlatform().setNominalViewingTransform();

        // Add the scene to the universe
        universe.addBranchGraph(scene);
    }

    private static BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

        // Create the lamp TransformGroup
        TransformGroup lampTG = new TransformGroup();

        // Create the lamp base (cylinder)
        Appearance baseAppearance = new Appearance();
        Color3f baseColor = new Color3f(0.2f, 0.2f, 0.2f);
        ColoringAttributes baseColoring = new ColoringAttributes(baseColor, ColoringAttributes.SHADE_FLAT);
        baseAppearance.setColoringAttributes(baseColoring);

        Cylinder lampBase = new Cylinder(0.2f, 0.05f, Primitive.GENERATE_NORMALS, baseAppearance);

        // Create the lamp neck (thin cylinder)
        Appearance neckAppearance = new Appearance();
        Color3f neckColor = new Color3f(0.0f, 0.5f, 0.0f);
        ColoringAttributes neckColoring = new ColoringAttributes(neckColor, ColoringAttributes.SHADE_FLAT);
        neckAppearance.setColoringAttributes(neckColoring);

        Cylinder lampNeck = new Cylinder(0.05f, 0.6f, Primitive.GENERATE_NORMALS, neckAppearance);

        // Create the lampshade (green cone)
        Appearance shadeAppearance = new Appearance();
        Color3f shadeColor = new Color3f(0.0f, 1.0f, 0.0f);
        ColoringAttributes shadeColoring = new ColoringAttributes(shadeColor, ColoringAttributes.SHADE_FLAT);
        shadeAppearance.setColoringAttributes(shadeColoring);

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
        baseTransform.setTranslation(new Vector3f(0.0f, 0.02f, 0.0f)); // Adjust the base position
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
        floorTransform.setTranslation(new Vector3f(0.0f, -0.5f, 0.0f)); // Position floor lower
        TransformGroup floorTG = new TransformGroup(floorTransform);
        floorTG.addChild(floor);

        // Add the lamp as a child of the floor TransformGroup, so it sits on the floor
        floorTG.addChild(lampTG); // Lamp is now a child of the floor

        root.addChild(floorTG); // Add the floor (and lamp) to the root BranchGroup

        // Set up lighting
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        // Ambient light
        Color3f ambientColor = new Color3f(0.2f, 0.2f, 0.2f);
        AmbientLight ambientLight = new AmbientLight(ambientColor);
        ambientLight.setInfluencingBounds(bounds);
        root.addChild(ambientLight);

        // Point light to simulate the lamp's light
        Color3f lightColor = new Color3f(1.0f, 1.0f, 1.0f);
        PointLight pointLight = new PointLight(lightColor, new Point3f(0.0f, 0.6f, 0.0f), new Point3f(1.0f, 0.0f, 0.0f));
        pointLight.setInfluencingBounds(bounds);
        root.addChild(pointLight);

        return root;
    }
}
