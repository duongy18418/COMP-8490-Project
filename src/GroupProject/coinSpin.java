package GroupProject;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.jogamp.java3d.*;
import org.jogamp.java3d.loaders.*;
import org.jogamp.java3d.loaders.objectfile.*;
import org.jogamp.java3d.utils.geometry.*;
import org.jogamp.java3d.utils.universe.*;
import org.jogamp.vecmath.*;
import javax.swing.JFrame;

import org.jogamp.java3d.Alpha;

public class coinSpin {
    private static TransformGroup coinTG = new TransformGroup(); // TransformGroup for coin
    private static boolean isAnimating = false;

    public static void main(String[] args) {
        // Create the main application window (Frame)
        JFrame frame = new JFrame("Coin Spin Application");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create Canvas3D
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas.setSize(800, 600);
        frame.add(canvas, BorderLayout.CENTER);

        // Create the SimpleUniverse
        SimpleUniverse universe = new SimpleUniverse(canvas);
        adjustCamera(universe);

        // Create the scene graph and add it to the universe
        BranchGroup scene = createSceneGraph();
        scene.compile();
        universe.addBranchGraph(scene);

        // Add KeyListener for interaction
        canvas.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_T && !isAnimating) {
                    isAnimating = true;
                    playAudio("src\\assets\\coinSpinng.wav");
                    startCoinSpin();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        // Display the application window
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private static void adjustCamera(SimpleUniverse universe) {
        ViewingPlatform viewingPlatform = universe.getViewingPlatform();
        TransformGroup viewTransform = viewingPlatform.getViewPlatformTransform();
        Transform3D cameraTransform = new Transform3D();

        // Position the camera and tilt it
        cameraTransform.setTranslation(new Vector3d(0.0, 0.5, 7.5)); // Back and up
        viewTransform.setTransform(cameraTransform);
    }

    public static BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

       

        // Load and add the coin OBJ file
        coinTG = loadCoinObj();
        root.addChild(coinTG);

        // Add a white background
        Background background = new Background(new Color3f(1.0f, 1.0f, 1.0f));
        background.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        root.addChild(background);

        return root;
    }

    private static TransformGroup createCombinedStage() {
        TransformGroup stageTG = new TransformGroup();

        // Brown box
        Appearance brownAppearance = new Appearance();
        Material brownMaterial = new Material(new Color3f(0.6f, 0.3f, 0.1f), new Color3f(0.0f, 0.0f, 0.0f),
                new Color3f(0.6f, 0.3f, 0.1f), new Color3f(1.0f, 1.0f, 1.0f), 64.0f);
        brownAppearance.setMaterial(brownMaterial);
        Box brownBox = new Box(1.5f, 0.1f, 1.5f, Primitive.GENERATE_NORMALS, brownAppearance);

        Transform3D brownTransform = new Transform3D();
        brownTransform.setTranslation(new Vector3f(0.0f, -1.0f, 0.0f));
        TransformGroup brownTG = new TransformGroup(brownTransform);
        brownTG.addChild(brownBox);

        // Green box
        Appearance greenAppearance = new Appearance();
        Material greenMaterial = new Material(new Color3f(0.0f, 1.0f, 0.0f), new Color3f(0.0f, 0.0f, 0.0f),
                new Color3f(0.0f, 0.1f, 0.0f), new Color3f(1.0f, 1.0f, 1.0f), 64.0f);
        greenAppearance.setMaterial(greenMaterial);
        Box greenBox = new Box(1.5f, 0.1f, 1.5f, Primitive.GENERATE_NORMALS, greenAppearance);

        Transform3D greenTransform = new Transform3D();
        greenTransform.setTranslation(new Vector3f(0.0f, -0.98f, 0.0f));
        TransformGroup greenTG = new TransformGroup(greenTransform);
        greenTG.addChild(greenBox);

        stageTG.addChild(brownTG);
        stageTG.addChild(greenTG);

        return stageTG;
    }

    private static TransformGroup loadCoinObj() {
        TransformGroup coinTG = new TransformGroup();

        // Enable capabilities to allow adding/removing children and transformations
        coinTG.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        coinTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        coinTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        try {
            ObjectFile loader = new ObjectFile(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
//            loader.setBasePath("src/assets/");
            Scene coinScene = loader.load("src\\assets\\Coin.obj");
            BranchGroup coinBG = coinScene.getSceneGroup();

            Transform3D coinTransform = new Transform3D();
            coinTransform.setTranslation(new Vector3f(0.0f, 0.75f, 0.0f)); // Position above green box
            coinTG.setTransform(coinTransform);

            coinTG.addChild(coinBG);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load coin.obj");
        }

        return coinTG;
    }

    static void startCoinSpin() {
        Transform3D spinAxis = new Transform3D();
        spinAxis.setTranslation(new Vector3f(0.0f, 0.75f, 0.0f)); // Coin stays at 0.75f above the green box

        Alpha spinAlpha = new Alpha(-1, 12000); // Spin for 10 seconds
        spinAlpha.setIncreasingAlphaDuration(7000);
        spinAlpha.setDecreasingAlphaDuration(5000);
        spinAlpha.setIncreasingAlphaRampDuration(6000);
        spinAlpha.setDecreasingAlphaRampDuration(6000);

        // Create the RotationInterpolator
        RotationInterpolator spinner = new RotationInterpolator(spinAlpha, coinTG, spinAxis, 0.0f, (float) Math.PI * 20); // Spin dynamically
        spinner.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0)); // Adjusted bounds for height

        // Wrap the RotationInterpolator in a BranchGroup
        BranchGroup spinGroup = new BranchGroup();
        spinGroup.setCapability(BranchGroup.ALLOW_DETACH); // Allow detaching after spin completes
        spinGroup.addChild(spinner);

        coinTG.addChild(spinGroup);
    }

    private static void playAudio(String filePath) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(filePath)));
            clip.start();

            // Stop the audio after 6 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(6000); // 6000 milliseconds = 6 seconds
                    clip.stop(); // Stop the clip
                    clip.close(); // Release resources
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}