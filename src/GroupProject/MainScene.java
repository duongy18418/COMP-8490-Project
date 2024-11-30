package GroupProject;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import javax.swing.*;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Cylinder;
import org.jogamp.java3d.utils.geometry.Sphere;
import org.jogamp.java3d.utils.picking.PickCanvas;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.universe.*;
import org.jogamp.vecmath.*;

import GroupProject.Book.Book;

import java.util.Map.Entry;


public class MainScene extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private static boolean r_tag = false;
	private static Timer timer;
	private static long deltaTime, previousTime = System.currentTimeMillis();
	private static final Set<Integer> keysPressed = new HashSet<Integer>();
	private Navigation navigation;
	private static TransformGroup cam;
	private static Map<String, Bounds> bounds = new HashMap<String, Bounds>();
	private static Canvas3D canvas3D;
	private static PickCanvas pickCanvas;
	private static Book book;
	private static float angle = 0;
	private static boolean direction = false, open = false;
	private static int pageCount = 30;
	
	private static void adjustFieldOfView(SimpleUniverse universe) {
        View view = universe.getViewer().getView();
        view.setFieldOfView(Math.toRadians(90.0)); // Increase the FOV to 90 degrees
    }

	/* a function to build and return the content branch */
	private static BranchGroup create_Scene(Canvas3D canvas) {
		BranchGroup sceneBG = new BranchGroup();	
		BranchGroup room = Room.CreateRoom(bounds);
		TransformGroup roomTG = new TransformGroup();
		roomTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Tab.initial_Sound();
		book = new Book(pageCount);
		Transform3D tableTransform = new Transform3D();
		tableTransform.setTranslation(new Vector3d(1f, 0, 0)); // Shift the object by -0.63f on y axis.
		
		TransformGroup tableTG = new TransformGroup(tableTransform);
		
		Transform3D pageTransform = new Transform3D();
		pageTransform.rotY(Math.PI/2);
		
		TransformGroup pageTG = new TransformGroup(pageTransform);
//		tableTG.addChild(pageTG);
		
		pageTG.addChild(book.getBookGroup());
		
		roomTG.addChild(Table.createSceneGraph());
		
		roomTG.addChild(tableTG);
		
		
		
//		
		
		Transform3D translateObject = new Transform3D(); // Translate used to shift the whole object
	    translateObject.setTranslation(new Vector3d(0, -1.63f, 0)); // Shift the object by -0.63f on y axis.
		TransformGroup sceneTG = new TransformGroup();
		roomTG.setTransform(translateObject);
		
		pickCanvas = new PickCanvas(canvas3D, sceneBG);
        pickCanvas.setMode(PickCanvas.GEOMETRY);
        
        Transform3D translateRadio = new Transform3D(); // Translate used to shift the whole object
        translateRadio.setTranslation(new Vector3d(-1, 1.4, 0)); // Shift the object by -0.63f on y axis.
        TransformGroup radioTG = new TransformGroup();
		
		
		BranchGroup radio = Tab.createSceneGraph();
		radioTG.addChild(radio);
		radioTG.setTransform(translateRadio);
		
		tableTG.addChild(radioTG);
		
		Transform3D translateLamp = new Transform3D(); // Translate used to shift the whole object
        translateLamp.setTranslation(new Vector3d(-2, 1.6, 0)); // Shift the object by -0.63f on y axis.
		
        TransformGroup lampTG = new TransformGroup();
		
		BranchGroup lamp = Lamp.createSceneGraph(canvas);
		lampTG.setTransform(translateLamp);
		lampTG.addChild(lamp);
		tableTG.addChild(lampTG);

		
		Transform3D translateCoin = new Transform3D(); // Translate used to shift the whole object
        translateCoin.setTranslation(new Vector3d(-2, 1.2, 0.7)); // Shift the object by -0.63f on y axis.
		
        TransformGroup coinTG = new TransformGroup();
		
		BranchGroup coin = coinSpin.createSceneGraph();
		Transform3D scaleTransform = new Transform3D();
        float scaleFactor = 0.05f; // Scale down by 50%
        scaleTransform.setScale(scaleFactor);
        
        TransformGroup scaleDown = new TransformGroup(scaleTransform);
        
        coinTG.setTransform(translateCoin);
        scaleDown.addChild(coin);
        coinTG.addChild(scaleDown);
		tableTG.addChild(coinTG);
		
		coinSpin.startCoinSpin();
		
		Transform3D translateTest = new Transform3D(); // Translate used to shift the whole object
        translateTest.setTranslation(new Vector3d(-3, 1.6, 0)); // Shift the object by -0.63f on y axis.
		
        TransformGroup TestTG = new TransformGroup();
		
		BranchGroup Test = createSceneGraph(pageTG);
		Transform3D lodsTransform = new Transform3D();
		lodsTransform.setTranslation(new Vector3d(0, 1.3f, 0));
		
		TestTG.setTransform(lodsTransform);
		
		TestTG.addChild(Test);
		tableTG.addChild(TestTG);
		
		roomTG.addChild(room);
		
		sceneTG.addChild(roomTG);
		sceneBG.addChild(sceneTG);                         // show object with/without rotation
		sceneBG.addChild(CommonsNP.rotate_Behavior(7500, sceneTG));
		CommonsNP.control_Rotation(false);                 // start without rotation
		
		return sceneBG;
	}
	
	/* a function to create a rotation behavior and refer it to 'rot_TG' */
	public static RotationInterpolator rotate_Behavior(int r_num, TransformGroup rot_TG) {

		rot_TG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D yAxis = new Transform3D();
		Alpha rotationAlpha = new Alpha(-1, r_num);              // rotate at 'r_num'/millisecond
		RotationInterpolator rot_beh = new RotationInterpolator(
				rotationAlpha, rot_TG, yAxis, 0.0f, (float) Math.PI * 2.0f);
		rot_beh.setSchedulingBounds(CommonsNP.hundredBS);            // start rotation at 0- and end at 360-degrees
		return rot_beh;
	}
	
	private static BranchGroup createSceneGraph(TransformGroup testTG) {
		BranchGroup baseBG = new BranchGroup();

		Appearance tableAppearance = new Appearance();
        ColoringAttributes tableColorAttr = new ColoringAttributes(new Color3f(1f, 0.4f, 0.2f), ColoringAttributes.NICEST);
        tableAppearance.setColoringAttributes(tableColorAttr);

		TransformGroup low = new TransformGroup();
		low.addChild(new Box(0.01f,0.2f,0.2f,Box.GENERATE_NORMALS, tableAppearance));
		
		
		TransformGroup translateTG = new TransformGroup();
		translateTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		
		

		float[] distances = { 2.0f };
		Switch SwitchTarget = new Switch();                // create DistanceLOD target object
		SwitchTarget.setCapability(Switch.ALLOW_SWITCH_WRITE);
		SwitchTarget.addChild(testTG);
		SwitchTarget.addChild(low);
		DistanceLOD distanceLOD = new DistanceLOD(distances, new Point3f());
		distanceLOD.addSwitch(SwitchTarget);
		distanceLOD.setSchedulingBounds(CommonsNP.twentyBS);
		if ((SwitchTarget.numChildren() - 1) != distanceLOD.numDistances()) {
			System.out.println("Error in creating LOD ");
		}

		baseBG.addChild(translateTG);
		translateTG.addChild(distanceLOD); translateTG.addChild(SwitchTarget);
		
		return baseBG;
    }

	/* a constructor to set up for the application */
	public MainScene() {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas3D = new Canvas3D(config);
		canvas3D.setSize(800, 800);                        // set size of canvas
		SimpleUniverse su = new SimpleUniverse(canvas3D);  // create a SimpleUniverse
		                                                   // set the viewer's location
		CommonsNP.define_Viewer(su, new Point3d(-1, 0, 0)); 
		
		Tab.setup();
		
		
		
		canvas3D.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pickCanvas.setShapeLocation(e);
                PickResult result = pickCanvas.pickClosest();
                if (result != null) {
                    Tab.processPickResult(result);
                }
            }
        });
		
		// Add keyboard listeners to the Canvas3D
        canvas3D.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keysPressed.add(e.getKeyCode());
                if(e.getKeyCode() == KeyEvent.VK_O) {
                	open = true;
                	direction = true;
                }
                if(e.getKeyCode() == KeyEvent.VK_P) {
                	open = false;
                	direction = true;
                }
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keysPressed.remove(e.getKeyCode());
            }
        });
        
     // Simulate a mouse drag event
        SwingUtilities.invokeLater(() -> {
            // Start of drag
            MouseEvent dragStart = new MouseEvent(
                canvas3D,                         // Target component
                MouseEvent.MOUSE_PRESSED,         // Event type
                System.currentTimeMillis(),       // Timestamp
                0,                                // Modifiers (e.g., Shift, Ctrl)
                100, 100,                         // Start coordinates
                0,                                // Click count
                false                             // Is popup trigger
            );
            canvas3D.dispatchEvent(dragStart);

            // Dragging to a new position
            MouseEvent dragging = new MouseEvent(
                canvas3D,                         // Target component
                MouseEvent.MOUSE_DRAGGED,         // Event type
                System.currentTimeMillis(),       // Timestamp
                0,                                // Modifiers
                200, 200,                         // End coordinates
                0,                                // Click count
                false                             // Is popup trigger
            );
            canvas3D.dispatchEvent(dragging);

            // End of drag
            MouseEvent dragEnd = new MouseEvent(
                canvas3D,                         // Target component
                MouseEvent.MOUSE_RELEASED,        // Event type
                System.currentTimeMillis(),       // Timestamp
                0,                                // Modifiers
                200, 200,                         // End coordinates
                0,                                // Click count
                false                             // Is popup trigger
            );
            canvas3D.dispatchEvent(dragEnd);
        });
		
		adjustFieldOfView(su);
		
		TransformGroup viewTG = su.getViewingPlatform().getViewPlatformTransform();
		cam = viewTG;
		CollisionDetection collision = new CollisionDetection(viewTG);
		
		BranchGroup scene = create_Scene(canvas3D);
		
		scene.addChild(collision);
		
		navigation = new Navigation(viewTG);

		MouseMovement mouse = new MouseMovement(viewTG);
		scene.addChild(mouse);
		
		
//		scene.addChild(CommonsNP.add_Lights(CommonsNP.White, 1));
		
		scene.compile();		                           // optimize the BranchGroup
		su.addBranchGraph(scene);                          // attach 'scene' to 'su'

		setLayout(new BorderLayout());
		add("Center", canvas3D);
		frame.setSize(810, 800);                           // set the size of the frame
		MenuBar menuBar = build_MenuBar();                 // build and set the menu bar
		frame.setMenuBar(menuBar);
		frame.setVisible(true);
		
		
		// Set up the game loop
        timer = new Timer(16, this); // Approximately 60 FPS
        timer.start();
	}

	/* The main entrance of the program */
	public static void main(String[] args) {         
		frame = new JFrame("NP's Testing Program");
		frame.getContentPane().add(new MainScene());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}	

	/* a function to build the menu bar of the demo */
	private MenuBar build_MenuBar() {
		MenuBar menuBar = new MenuBar();
		Menu m = new Menu("Menu");                         // set menu's label
		m.addActionListener(this);

		m.add("Exit");		                               // specify menu items
		m.add("Pause/Rotate");
		menuBar.add(m);                                    // add items to the menu

		return menuBar;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String chosen_item = e.getActionCommand();
		
		if(chosen_item != null) {
			if (chosen_item == "Exit")                         // quit the application
				System.exit(0);

			switch(chosen_item) {                              // handle different menu items
			case "Pause/Rotate":
				if (r_tag == true) {
					CommonsNP.control_Rotation(false);
					r_tag = false;
				}
				else {
					CommonsNP.control_Rotation(true);
					r_tag = true;				
				}
				return;
			default:
				return;
			}

		}else {
			deltaTime = System.currentTimeMillis()-previousTime;
			previousTime = System.currentTimeMillis();
			
			if(direction && !open) {
				angle -= 0.1*deltaTime;
				if(angle < 0) {
					angle = 0;
					direction = false;
				}
			}
			if(direction && open) {
				angle += 0.1*deltaTime;
				if(angle > 90) {
					angle = 90;
					direction = true;
				}
			}
			book.openFrontCover(-angle);
			book.openBackCover(angle);
			
			for(int i = 0;i<pageCount/2;i++) {
				book.turnPage(i, -angle+angle/(i*pageCount/10));
			}
			
			for(int i = pageCount/2;i<pageCount;i++) {
				book.turnPage(i, angle-angle/((pageCount-i)*pageCount/10));
			}
			
			boolean canMove = true;
			for(Entry<String, Bounds> bound: bounds.entrySet()) {
				if(CollisionDetection.checkCollision(cam, bound)) {
					
//					System.out.println(bound.getKey()+" "+ canMove);
					if(bound.getKey().contains("w")) {
						canMove = false;
					}
				}
			}
			navigation.handleKeyEvent(keysPressed, deltaTime, canMove);
		}
	}	
	
}
