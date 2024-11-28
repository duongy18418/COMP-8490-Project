package GroupProject;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Sphere;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

import javax.swing.*;

public class Temp {

    public static void main(String[] args) {
        new Temp();
    }

    public Temp() {
        JFrame frame = new JFrame("LOD Example");
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        frame.add(canvas);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        SimpleUniverse universe = new SimpleUniverse(canvas);
        BranchGroup scene = createSceneGraph();
        scene.compile();
        universe.addBranchGraph(scene);
    }

    private BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

        // Create a Switch node to hold different levels of detail
        Switch lodSwitch = new Switch();
        lodSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);

        // Add different levels of detail to the Switch node
        Appearance appearance = new Appearance();
        
		ColoringAttributes colorAttr =
            new ColoringAttributes(new Color3f(0.0f,
                                               1.0f,
                                               0.0f),
                                   ColoringAttributes.NICEST); 
        
		appearance.
            setColoringAttributes(colorAttr); 

		// High detail (more subdivisions)
		Sphere highDetailSphere =
            new Sphere(0.5f,
                       Sphere.GENERATE_NORMALS,
                       64,
                       appearance); 
		lodSwitch.
            addChild(highDetailSphere); 

		// Medium detail
		Sphere mediumDetailSphere =
            new Sphere(0.5f,
                       Sphere.GENERATE_NORMALS,
                       32,
                       appearance); 
		lodSwitch.
            addChild(mediumDetailSphere); 

		// Low detail (fewer subdivisions)
		Sphere lowDetailSphere =
            new Sphere(0.5f,
                       Sphere.GENERATE_NORMALS,
                       16,
                       appearance); 
		lodSwitch.
            addChild(lowDetailSphere); 

		root.addChild(lodSwitch);

		// Define distances for switching LOD
		float[] distances = {2.0f, 5.0f};

		// Create an LOD object and attach it to the Switch node
		Point3f position =
            new Point3f(0.0f,
                        0.0f,
                        10.0f); // Position of the viewer
		LOD lod =
            new DistanceLOD(distances,
                            position);
        
		lod.addSwitch(lodSwitch);
        
		root.addChild(lod);

		addLighting(root);

		return root;
    }

    private void addLighting(BranchGroup root) {
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        AmbientLight ambientLight = new AmbientLight(new Color3f(1.0f, 1.0f, 1.0f));
        
		ambientLight.setInfluencingBounds(bounds);
        
		root.addChild(ambientLight);

		DirectionalLight directionalLight =
            new DirectionalLight(new Color3f(1.0f,
                                             1.0f,
                                             1.0f),
                                 new Vector3f(-1.0f,
                                              -1.0f,
                                              -1.0f)); 
        
		directionalLight.
            setInfluencingBounds(bounds); 
        
		root.
            addChild(directionalLight); 
    }
}