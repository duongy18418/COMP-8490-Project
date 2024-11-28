/* For students to study and work on assignments and projects *
 * \\\\\ Copyright material (contact xyuan@uwindsor.ca) ///// */
package GroupProject;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.ColorCube;
import org.jogamp.java3d.utils.universe.*;
import org.jogamp.vecmath.*;

public class CommonsNP extends JPanel {

	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	
	private static Alpha rotationAlpha;                    // use for rotation behavior
	                                                       // useful colors
	public final static Color3f White = new Color3f(1.0f, 1.0f, 1.0f);
	public final static Color3f Red = new Color3f(1.0f, 0.0f, 0.0f);
	public final static Color3f Orange = new Color3f(1.0f, 0.6f, 0.0f);
	public final static Color3f Yellow = new Color3f(1.0f, 1.0f, 0.0f);
	public final static Color3f Green = new Color3f(0.0f, 1.0f, 0.0f);
	public final static Color3f Blue = new Color3f(0.0f, 0.0f, 1.0f);
	public final static Color3f Purple = new Color3f(0.5f, 0.0f, 0.5f);
	public final static Color3f Grey = new Color3f(0.35f, 0.35f, 0.35f);
	public final static Color3f Black = new Color3f(0.0f, 0.0f, 0.0f);
	public final static Color3f Magenta = new Color3f(1.0f, 0.0f, 1.0f);
	public final static Color3f Lime = new Color3f(0.0f, 1.0f, 0.5f);
	public final static Color3f Cyan = new Color3f(0.0f, 1.0f, 1.0f);
	public static Color3f[] list_clrs = {White, Red, Orange, Yellow, Green, Blue, Purple, Black};

	public final static BoundingSphere hundredBS = new BoundingSphere(new Point3d(), 100.0);
	public final static BoundingSphere twentyBS = new BoundingSphere(new Point3d(), 20.0);

	/* a function to create and return material definition */
	public static Material set_Material(Color3f m_clr) {
		Material mtl = new Material();
		mtl.setShininess(128);                              // try 10 or 128
		mtl.setAmbientColor(White);
		mtl.setDiffuseColor(m_clr);
		mtl.setSpecularColor(Grey);
		mtl.setEmissiveColor(Black);                       // non-emissive
		mtl.setLightingEnable(true);
		return mtl;
	}
	
    /* a function to set appearance with specific color ('clr') and predefined material */
	public static Appearance set_Appearance(Color3f clr) {		
		Appearance app = new Appearance();
		app.setMaterial(set_Material(clr));                // set appearance's material
		return app;
	}	
	
    /* a function to pause ('tag'==false) or resume ('tag'==true) the rotation behavior */
	public static void control_Rotation(boolean tag) {
		if (tag == true)
			rotationAlpha.resume();
		else
			rotationAlpha.pause();			
	}
	
	/* a function to create a rotation behavior and refer it to 'rot_TG' */
	public static RotationInterpolator rotate_Behavior(int r_num, TransformGroup rot_TG) {

		rot_TG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D yAxis = new Transform3D();
		rotationAlpha = new Alpha(-1, r_num);              // rotate at 'r_num'/millisecond
		RotationInterpolator rot_beh = new RotationInterpolator(
				rotationAlpha, rot_TG, yAxis, 0.0f, (float) Math.PI * 2.0f);
		rot_beh.setSchedulingBounds(hundredBS);            // start rotation at 0- and end at 360-degrees
		return rot_beh;
	}
	
	/* a function to place one light or two point lights at opposite locations */
	public static BranchGroup add_Lights(Color3f clr, int p_num) {
		BranchGroup lightBG = new BranchGroup();
		Point3f atn = new Point3f(1f, 0.0f, 0.0f);
		PointLight ptLight;
		float adjt = 1f;
		for (int i = 0; (i < p_num) && (i < 2); i++) {
			if (i > 0) 
				adjt = -1f;                                // use 'adjt' to change light position 
			ptLight = new PointLight(clr, new Point3f(3.0f * adjt, 1.0f, 3.0f  * adjt), atn);
			ptLight.setInfluencingBounds(hundredBS);
			lightBG.addChild(ptLight);                     // attach the point light to 'lightBG'
		}
		return lightBG;
	}

	/* a function to position viewer at 'eye' location */
	public static void define_Viewer(SimpleUniverse simple_U, Point3d eye) {
	    TransformGroup viewTransform = simple_U.getViewingPlatform().getViewPlatformTransform();
		Point3d center = new Point3d(0, 0, 0);             // define the point where the eye looks at
		Vector3d up = new Vector3d(0, 1, 0);               // define camera's up direction
		Transform3D view_TM = new Transform3D();
		view_TM.lookAt(eye, center, up);                   // look at 'center' from 'eye'
//		view_TM.invert();
	    viewTransform.setTransform(view_TM);               // set the TransformGroup of ViewingPlatform
	}

	/* a function to build the content branch and attach to 'scene' */
	private static BranchGroup create_Scene() {
		BranchGroup sceneBG = new BranchGroup();
		TransformGroup sceneTG = new TransformGroup();
		
		sceneTG.addChild(new ColorCube(0.5));
		sceneBG.addChild(rotate_Behavior(7500, sceneTG));
		sceneBG.addChild(sceneTG);
		
		return sceneBG;
	}

	/* a constructor to set up for the application */
	public CommonsNP(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);		
		SimpleUniverse su = new SimpleUniverse(canvas);    // create a SimpleUniverse
		
		define_Viewer(su, new Point3d(1, 1, 3));           // set the viewer's location		
		sceneBG.addChild(add_Lights(White, 1));	
		sceneBG.compile();		                           // optimize the BranchGroup
		su.addBranchGraph(sceneBG);                        // attach the scene to SimpleUniverse

		setLayout(new BorderLayout());
		add("Center", canvas);
		frame.setSize(800, 800);                           // set the size of the JFrame
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		frame = new JFrame("XY's Common File");            // NOTE: change XY to student's initials
		frame.getContentPane().add(new CommonsNP(create_Scene()));  // create an instance of the class
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
