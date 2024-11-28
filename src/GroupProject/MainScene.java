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
import org.jogamp.java3d.utils.picking.PickCanvas;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.universe.*;
import org.jogamp.vecmath.*;

import CodesST8490.Tab;
import GroupProject.Book.BookCover;

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
	
	private static void adjustFieldOfView(SimpleUniverse universe) {
        View view = universe.getViewer().getView();
        view.setFieldOfView(Math.toRadians(90.0)); // Increase the FOV to 90 degrees
    }

	/* a function to build and return the content branch */
	private static BranchGroup create_Scene() {
		BranchGroup sceneBG = new BranchGroup();	
		BranchGroup room = Room.CreateRoom(bounds);
		TransformGroup roomTG = new TransformGroup();
		
		
		
//		roomTG.addChild(BookCover.getBookCover());
		
		Transform3D translateObject = new Transform3D(); // Translate used to shift the whole object
	    translateObject.setTranslation(new Vector3d(0, -1.63f, 0)); // Shift the object by -0.63f on y axis.
		TransformGroup sceneTG = new TransformGroup();
		roomTG.setTransform(translateObject);
		
		pickCanvas = new PickCanvas(canvas3D, sceneBG);
        pickCanvas.setMode(PickCanvas.BOUNDS);
        
        Transform3D translateRadio = new Transform3D(); // Translate used to shift the whole object
        translateRadio.setTranslation(new Vector3d(0, 1.63f, 0)); // Shift the object by -0.63f on y axis.
        TransformGroup radioTG = new TransformGroup();
		
		
		
		BranchGroup radio = Tab.createSceneGraph();
		radioTG.addChild(radio);
		radioTG.setTransform(translateRadio);
		
		roomTG.addChild(radioTG);


		roomTG.addChild(room);
		
		sceneTG.addChild(roomTG);
		sceneBG.addChild(sceneTG);                         // show object with/without rotation
		sceneBG.addChild(CommonsNP.rotate_Behavior(7500, sceneTG));
		CommonsNP.control_Rotation(false);                 // start without rotation
		
		return sceneBG;
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
		
		BranchGroup scene = create_Scene();
		
		scene.addChild(collision);
		
		navigation = new Navigation(viewTG);

		MouseMovement mouse = new MouseMovement(viewTG);
		scene.addChild(mouse);
		
		
		scene.addChild(CommonsNP.add_Lights(CommonsNP.White, 1));
		
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
