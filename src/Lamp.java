import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Cone;
import org.jogamp.java3d.utils.geometry.Cylinder;
import org.jogamp.java3d.utils.geometry.Sphere;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.*;

public class Lamp extends Frame implements KeyListener {

    private PointLight lampLight;
    private boolean isLightOn = true;

    public Lamp() {
        // Setup Java3D
        SimpleUniverse universe = new SimpleUniverse();
        BranchGroup scene = createSceneGraph();
        universe.getViewingPlatform().setNominalViewingTransform();

        universe.addBranchGraph(scene);
        addKeyListener(this);
        setSize(800, 600);
        setVisible(true);
    }

    private BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

        // Create a simple lamp model
        TransformGroup lampGroup = new TransformGroup();
        Cylinder lampBase = new Cylinder(0.1f, 0.4f);
        Cone lampShade = new Cone(0.15f, 0.3f);

        Transform3D shadePosition = new Transform3D();
        shadePosition.setTranslation(new Vector3f(0.0f, 0.3f, 0.0f));
        TransformGroup shadeGroup = new TransformGroup(shadePosition);
        shadeGroup.addChild(lampShade);

        lampGroup.addChild(lampBase);
        lampGroup.addChild(shadeGroup);
        root.addChild(lampGroup);

        // Setup the light source
        lampLight = new PointLight(
            new Color3f(1.0f, 1.0f, 0.8f), // Light color
            new Point3f(0.0f, 0.4f, 0.0f), // Light position
            new Point3f(1.0f, 0.0f, 0.0f)  // Attenuation
        );
        lampLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 10.0));
        lampLight.setCapability(Light.ALLOW_STATE_WRITE);
        root.addChild(lampLight);

        // Add a simple object to demonstrate lighting
        Sphere sphere = new Sphere(0.2f);
        Transform3D spherePosition = new Transform3D();
        spherePosition.setTranslation(new Vector3f(0.5f, 0.2f, 0.0f));
        TransformGroup sphereGroup = new TransformGroup(spherePosition);
        sphereGroup.addChild(sphere);
        root.addChild(sphereGroup);

        // Set the background color to grey
        Background background = new Background(new Color3f(0.5f, 0.5f, 0.5f)); // Grey background
        background.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        root.addChild(background);

        return root;
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 's' || e.getKeyChar() == 'S') {
            isLightOn = !isLightOn;
            lampLight.setEnable(isLightOn);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new Lamp();
    }
}
