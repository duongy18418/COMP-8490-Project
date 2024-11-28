package GroupProject;

import org.jogamp.java3d.*;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.vecmath.*;

public class Table {

    

    public static BranchGroup createSceneGraph() {
        BranchGroup root = new BranchGroup();

        TransformGroup tableTG = new TransformGroup();
        tableTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        // Table dimensions
        float tableLength = 3.0f;
        float tableWidth = 1.5f;
        float tableHeight = 0.1f;
        
        // Leg dimensions
        float legWidth = 0.1f;
        float legHeight = 1.0f;

        // Create the tabletop
        Appearance tableAppearance = new Appearance();
        ColoringAttributes tableColorAttr = new ColoringAttributes(new Color3f(0.6f, 0.4f, 0.2f), ColoringAttributes.NICEST);
        tableAppearance.setColoringAttributes(tableColorAttr);

        Box tabletop = new Box(tableLength / 2, tableHeight / 2, tableWidth / 2, Box.GENERATE_NORMALS, tableAppearance);
        
		Transform3D tableTransform = new Transform3D();
        
		tableTransform.setTranslation(new Vector3f(0.0f, legHeight + (tableHeight / 2), 0.0f));
        
		TransformGroup tabletopTG = new TransformGroup(tableTransform);
        
		tabletopTG.addChild(tabletop);
        
		tableTG.addChild(tabletopTG);

        // Create the legs
        for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
                Box leg = new Box(legWidth / 2, legHeight / 2, legWidth / 2, Box.GENERATE_NORMALS, tableAppearance);
                Transform3D legTransform = new Transform3D();
                legTransform.setTranslation(new Vector3f(i * (tableLength / 2 - legWidth / 2), legHeight / 2, j * (tableWidth / 2 - legWidth / 2)));
                TransformGroup legTG = new TransformGroup(legTransform);
                legTG.addChild(leg);
                tableTG.addChild(legTG);
            }
        }

        root.addChild(tableTG);


        return root;
    }

   
}