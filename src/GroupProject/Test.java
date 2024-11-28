package GroupProject;

import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.objectfile.*;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;
import javax.swing.JFrame;
import java.awt.GraphicsConfiguration;

public class Test extends JFrame {
    private Canvas3D canvas;
    private SimpleUniverse universe;

    public Test() {
        // Configure the window
        setTitle("3D Object Loader");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the Canvas3D
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new Canvas3D(config);
        add(canvas);

        // Create Universe
        universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();

        // Create the scene
        BranchGroup scene = createSceneGraph();
        scene.compile();
        universe.addBranchGraph(scene);
    }

    private BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();
        
        // Create TransformGroup for object positioning
        TransformGroup objTransform = new TransformGroup();
        objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
     // Create an ObjectFile loader with flags for materials
        ObjectFile loader = new ObjectFile(ObjectFile.RESIZE | ObjectFile.LOAD_ALL);

        // Set the base path for finding MTL files
//        loader.setBasePath("src/assets");

        Scene scene = null;
        try {
            // Load the OBJ file
            scene = loader.load("src/assets/Coin.obj");
            
            // Enable materials capability
            BranchGroup objRoot = new BranchGroup();
            objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
            
            // Get the loaded scene and add material capabilities
            BranchGroup loadedScene = scene.getSceneGroup();
            enableMaterialCapabilities(loadedScene);
            
            objRoot.addChild(loadedScene);
            
        } catch (Exception e) {
            System.err.println("Error loading model: " + e.getMessage());
        }

       

        return root;
    }
 // Helper method to enable material capabilities
        private void enableMaterialCapabilities(Group group) {
            for (int i = 0; i < group.numChildren(); i++) {
                Node child = group.getChild(i);
                if (child instanceof Shape3D) {
                    Shape3D shape = (Shape3D) child;
                    shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
                    shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
                } else if (child instanceof Group) {
                    enableMaterialCapabilities((Group) child);
                }
            }
        }
    public static void main(String[] args) {
    	Test loader = new Test();
        loader.setVisible(true);
    }
}
