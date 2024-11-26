package GroupProject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.TransparencyAttributes;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.picking.PickTool;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

import CodesNP8940.A3BaseCodeNP;
import CodesNP8940.A4CupCodeNP;
import CodesNP8940.Code4A4NP;
import CodesNP8940.CommonsNP;

public class Temp extends JPanel {

	protected float sc;                                    // define scaling factor
	protected Shape3D obj_shape;                           // need Shape3D to set appearance/transparency

	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	
	private Canvas3D canvas3D;                             // need for mouse picking
	private static PickTool pickTool;	
	private static BranchGroup base;
	private static A3BaseCodeNP baseObject;
	private static TransformGroup sceneTG;
	
	/* a function to create and return the scene BranchGroup */
	private static BranchGroup create_Scene() {
		BranchGroup sceneBG = new BranchGroup();		   // create 'sceneBG' as content branch
		BranchGroup cupBG = new BranchGroup();             // separate 'cupBG' for cup segment(s)

        Transform3D translateObject = new Transform3D(); // Translate used to shift the whole object
        translateObject.setTranslation(new Vector3d(0, -0.63f, 0)); // Shift the object by -0.63f on y axis.

        sceneTG = new TransformGroup(translateObject);     // create 'sceneTG' TransformGroup
        sceneTG.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        sceneTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        
		
        sceneTG.addChild(load_Object("bugatti", CommonsNP.Black));
        
		pickTool = new PickTool(cupBG);                    // make object(s) in 'cupBG' pickable
		pickTool.setMode(PickTool.GEOMETRY);               // set to pick by geometry
		
		sceneTG.addChild(cupBG);                           // add 'cupBG' to 'sceneBG'

		return sceneBG;
	}
	
	/* a specialized constructor that enables sound and (mouse-based and keyboard-based) interaction  */
	public Temp(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas3D = new Canvas3D(config);                
				
		SimpleUniverse su = new SimpleUniverse(canvas3D);  // create a SimpleUniverse		
		CommonsNP.define_Viewer(su, new Point3d(1, 1, 4)); // position the viewer	
		
		sceneBG.addChild(CommonsNP.solid_Background(CommonsNP.Grey));
		sceneBG.addChild(CommonsNP.add_Lights(CommonsNP.White, 1));
		sceneBG.compile();		                           // optimize the BranchGroup
		su.addBranchGraph(sceneBG);                        // attach 'sceneBG' to SimpleUniverse

		setLayout(new BorderLayout());
		add("Center", canvas3D);
		frame.setSize(800, 800);                           // set the size of the JFrame
		frame.setVisible(true);
	}

	/* the main entrance of the application with specified window dimension */
	public static void main(String[] args) {
		frame = new JFrame("NP's Assignment 4");           // NOTE: identify student's work with initials
		frame.getContentPane().add(new Code4A4NP(create_Scene()));  // create an instance of the class
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}	
	
	/* a function to attach the current object to 'objTG' and return 'objTG' */ 
	public TransformGroup position_Object() {              
		Transform3D scaler = new Transform3D();
		scaler.setScale(new Vector3d(sc, sc, sc));
		TransformGroup scaleTG = new TransformGroup(scaler); // set 'scaleTG' for scaling of the object
		scaleTG.addChild(obj_shape);                       // scale the object by attaching to 'scaleTG'	

		return scaleTG;                                    // return 'scaleTG'
	}

    /* a function to load external object and return as a Shape3D */
	public static Shape3D load_Object(String obj_name, Color3f clr) {		
		BranchGroup objBG = load_Shape(obj_name);          // load external object to 'objBG'
		Shape3D cup_shape = (Shape3D) objBG.getChild(0).cloneNode(true); ;  // convert to Shape3D
		cup_shape.setName(obj_name);                       // A4: need to set name for mouse-picking
		
		Appearance app = new Appearance();
		app.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NONE,0));
		app.getTransparencyAttributes().setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
		app.getTransparencyAttributes().setCapability(TransparencyAttributes.ALLOW_MODE_WRITE);
		app.setMaterial(CommonsNP.set_Material(clr));
		cup_shape.setAppearance(app);                      // set appearance

		return cup_shape;
	}

	/* a function to load and return object shape from the file named 'obj_name' */
	private static BranchGroup load_Shape(String obj_name) {
		ObjectFile f = new ObjectFile(ObjectFile.RESIZE, 
				(float) (60 * Math.PI / 180.0));
		Scene s = null;
		try {                                              // load object's definition file to 's'
			s = f.load("src/images/" + obj_name + ".obj");
		} catch (FileNotFoundException e) {
			System.err.println(e);
			System.exit(1);
		} catch (ParsingErrorException e) {
			System.err.println(e);
			System.exit(1);
		} catch (IncorrectFormatException e) {
			System.err.println(e);
			System.exit(1);
		}
		return s.getSceneGroup();
	}

}
