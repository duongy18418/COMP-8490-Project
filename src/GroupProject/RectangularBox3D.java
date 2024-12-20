package GroupProject;


import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Cylinder;
import org.jogamp.java3d.utils.geometry.Text2D;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.picking.*;
import org.jogamp.vecmath.*;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.JFrame;
 
public class RectangularBox3D {
    private static Canvas3D canvas;
    private static PickCanvas pickCanvas;
    private static TransformGroup[] buttonGroups;
    
    public static void setup() {
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        SimpleUniverse universe = new SimpleUniverse(canvas);
        
        buttonGroups = new TransformGroup[4];
        
        TransformGroup viewingPlatform = universe.getViewingPlatform().getViewPlatformTransform();
        Transform3D viewTransform = new Transform3D();
        viewTransform.setTranslation(new Vector3f(0.0f, 0.3f, 2.5f));
        viewingPlatform.setTransform(viewTransform);
        
        BranchGroup scene = createSceneGraph();
        scene.compile();
        universe.addBranchGraph(scene);
        
        pickCanvas = new PickCanvas(canvas, scene);
        pickCanvas.setMode(PickCanvas.BOUNDS);
        
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pickCanvas.setShapeLocation(e);
                PickResult result = pickCanvas.pickClosest();
                if (result != null) {
                    processPickResult(result);
                }
            }
        });
        
        JFrame frame = new JFrame("3D Music Player");
        frame.setSize(800, 600);
        frame.add(canvas);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private static void processPickResult(PickResult result) {
        Node node = result.getNode(PickResult.SHAPE3D);
        if (node instanceof Shape3D) {
            Shape3D shape = (Shape3D) node;
            String name = shape.getName();
            System.out.println("Clicked button: " + name);
            
            int index = -1;
            switch (name) {
                case "playButton": index = 0; break;
                case "nextButton": index = 1; break;
                case "prevButton": index = 2; break;
                case "stopButton": index = 3; break;
            }
            
            if (index >= 0) {
                animateButtonPress(index);
            }
        }
    }
    
    private static void animateButtonPress(final int index) {
        if (buttonGroups[index] != null) {
            Transform3D transform = new Transform3D();
            buttonGroups[index].getTransform(transform);
            Vector3f position = new Vector3f();
            transform.get(position);
            
            position.z += 0.02f;
            transform.setTranslation(position);
            buttonGroups[index].setTransform(transform);
            
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                    position.z -= 0.02f;
                    transform.setTranslation(position);
                    buttonGroups[index].setTransform(transform);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
 
    public static BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();
        root.setCapability(BranchGroup.ALLOW_PICKABLE_READ);
        
        TransformGroup rotGroup = new TransformGroup();
        rotGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
//        // Add rotation behavior
//        Transform3D yAxis = new Transform3D();
//        Alpha rotationAlpha = new Alpha(-1, 4000);
//        RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, rotGroup, yAxis,
//                0.0f, (float) Math.PI * 2.0f);
//        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
//        rotator.setSchedulingBounds(bounds);
//        rotGroup.addChild(rotator);
 
        Appearance boxAppearance = new Appearance();
        ColoringAttributes boxColorAttr = new ColoringAttributes(
            new Color3f(0.7f, 0.7f, 0.7f), ColoringAttributes.NICEST);
        boxAppearance.setColoringAttributes(boxColorAttr);
 
        Box mainBox = new Box(0.5f, 0.3f, 0.2f, Box.GENERATE_NORMALS, boxAppearance);
        TransformGroup mainTG = new TransformGroup();
        mainTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        mainTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        mainTG.addChild(mainBox);
        
        addSpeakerGrills(mainTG);
        addHandle(mainTG);
        addControlButtons(mainTG);
        
        rotGroup.addChild(mainTG);
        root.addChild(rotGroup);
        
        // Add ambient light
//        AmbientLight ambientLight = new AmbientLight(new Color3f(1.0f, 1.0f, 1.0f));
//        ambientLight.setInfluencingBounds(new BoundingSphere());
//        root.addChild(ambientLight);
        
        return root;
    }
 
    private static void addSpeakerGrills(TransformGroup parent) {
        Appearance speakerBackgroundApp = new Appearance();
        ColoringAttributes backgroundColor = new ColoringAttributes(
            new Color3f(0.1f, 0.1f, 0.1f), ColoringAttributes.NICEST);
        speakerBackgroundApp.setColoringAttributes(backgroundColor);
 
        float[][] speakerPositions = {
            {-0.25f, 0.0f},
            {0.25f, 0.0f}
        };
        float radius = 0.15f;
        float innerRadius = radius * 0.95f;
 
        for (float[] pos : speakerPositions) {
            Cylinder background = new Cylinder(
                radius, 0.001f, Cylinder.GENERATE_NORMALS, 60, 1, speakerBackgroundApp);
            
            TransformGroup bgTG = new TransformGroup();
            Transform3D bgTransform = new Transform3D();
            bgTransform.rotX(Math.PI/2);
            bgTransform.setTranslation(new Vector3f(pos[0], pos[1], 0.2f));
            bgTG.setTransform(bgTransform);
            bgTG.addChild(background);
            parent.addChild(bgTG);
 
            Appearance lineApp = new Appearance();
            ColoringAttributes lineColor = new ColoringAttributes(
                new Color3f(0.0f, 0.0f, 0.0f), ColoringAttributes.NICEST);
            lineApp.setColoringAttributes(lineColor);
 
            float spacing = 0.015f;
            for (float x = -innerRadius; x <= innerRadius; x += spacing) {
                float maxY = (float)Math.sqrt(innerRadius*innerRadius - x*x);
                for (float y = -maxY; y <= maxY; y += spacing) {
                    Box line1 = new Box(0.001f, 0.01f, 0.001f, lineApp);
                    TransformGroup line1TG = new TransformGroup();
                    Transform3D line1Transform = new Transform3D();
                    line1Transform.rotZ(Math.PI/4);
                    line1Transform.setTranslation(new Vector3f(pos[0] + x, pos[1] + y, 0.201f));
                    line1TG.setTransform(line1Transform);
                    line1TG.addChild(line1);
                    parent.addChild(line1TG);
 
                    Box line2 = new Box(0.001f, 0.01f, 0.001f, lineApp);
                    TransformGroup line2TG = new TransformGroup();
                    Transform3D line2Transform = new Transform3D();
                    line2Transform.rotZ(-Math.PI/4);
                    line2Transform.setTranslation(new Vector3f(pos[0] + x, pos[1] + y, 0.201f));
                    line2TG.setTransform(line2Transform);
                    line2TG.addChild(line2);
                    parent.addChild(line2TG);
                }
            }
        }
    }
 
    private static void addHandle(TransformGroup parent) {
        Appearance handleApp = new Appearance();
        ColoringAttributes handleColor = new ColoringAttributes(
            new Color3f(0.6f, 0.6f, 0.6f), ColoringAttributes.NICEST);
        handleApp.setColoringAttributes(handleColor);
 
        float handleWidth = 0.4f;
        float handleHeight = 0.5f;
 
        Box handleTop = new Box(handleWidth, 0.05f, 0.05f, handleApp);
        TransformGroup topTG = new TransformGroup();
        Transform3D topTransform = new Transform3D();
        topTransform.setTranslation(new Vector3f(0.0f, handleHeight, 0.0f));
        topTG.setTransform(topTransform);
        topTG.addChild(handleTop);
 
        float supportHeight = 0.15f;
        float xOffset = handleWidth - 0.15f;
        float yOffset = handleHeight - (supportHeight + 0.05f);
 
        for (float x : new float[]{-xOffset, xOffset}) {
            Box support = new Box(0.05f, supportHeight, 0.05f, handleApp);
            TransformGroup supportTG = new TransformGroup();
            Transform3D supportTransform = new Transform3D();
            supportTransform.setTranslation(new Vector3f(x, yOffset, 0.0f));
            supportTG.setTransform(supportTransform);
            supportTG.addChild(support);
            parent.addChild(supportTG);
        }
        parent.addChild(topTG);
    }
 
    private static void addControlButtons(TransformGroup parent) {
        float[] positions = {-0.225f, -0.075f, 0.075f, 0.225f};
        float buttonY = 0.2f;
        
        for (int i = 0; i < 4; i++) {
            buttonGroups[i] = new TransformGroup();
            buttonGroups[i].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            buttonGroups[i].setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        }
 
        createButton(parent, 0, new Color3f(0.0f, 0.8f, 0.0f), "playButton", positions[0], buttonY);
        createButton(parent, 1, new Color3f(1.0f, 1.0f, 0.0f), "nextButton", positions[1], buttonY);
        createButton(parent, 2, new Color3f(0.0f, 0.0f, 1.0f), "prevButton", positions[2], buttonY);
        createButton(parent, 3, new Color3f(0.8f, 0.0f, 0.0f), "stopButton", positions[3], buttonY);
    }
 
    private static void createButton(TransformGroup parent, int index, Color3f color, String name,
                                   float xPos, float yPos) {
        Appearance buttonApp = new Appearance();
        ColoringAttributes colorAttr = new ColoringAttributes(color, ColoringAttributes.NICEST);
        buttonApp.setColoringAttributes(colorAttr);
        
        Box button = new Box(0.03f, 0.03f, 0.01f, Box.GENERATE_NORMALS, buttonApp);
        button.setName(name);
        button.setPickable(true);
        button.setCapability(Node.ENABLE_PICK_REPORTING);
        
        Transform3D transform = new Transform3D();
        transform.setTranslation(new Vector3f(xPos, yPos, 0.201f));
        buttonGroups[index].setTransform(transform);
        buttonGroups[index].addChild(button);
        
        parent.addChild(buttonGroups[index]);
    }
}