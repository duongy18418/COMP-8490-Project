package GroupProject.Book;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.PolygonAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.TriangleStripArray;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3f;

import GroupProject.CommonsNP;

public class Page {
	
	// Define the dimensions of the paper
    private float width = 1.5f;  // Width of the paper (along the x-axis)
    private float height = 2f; // Height of the paper (along the y-axis)
    private float thickness = 0f;
    public float ScaleX = 0.2f, ScaleY = 0.2f, ScaleZ = 0.2f;
    
    
    public Shape3D createPaper() {
        // Define the grid size
        int strips = 10; // Number of strips (height divisions)
        int verticesPerStrip = 4; // Number of vertices per strip
        int totalVertices = strips * verticesPerStrip;

        // Create a TriangleStripArray
        TriangleStripArray triangleStrip = new TriangleStripArray(
                totalVertices*2,
                TriangleStripArray.COORDINATES,
                new int[] {totalVertices*2}
        );    

        // Generate points along the breadths
        int index = 0;
        for (int strip = 0; strip < strips; strip++) {
            float yStart = -height / 2 + (height / (strips - 1)) * strip;
            float yEnd = -height / 2 + (height / (strips - 1)) * (strip + 1);
            // Points for one strip
            triangleStrip.setCoordinate(index++, new Point3f(-width / 2*ScaleX, yStart*ScaleY, 0)); // Left of start
            triangleStrip.setCoordinate(index++, new Point3f(width / 2*ScaleX, yStart*ScaleY, 0.0f));  // Right of start
            triangleStrip.setCoordinate(index++, new Point3f(-width / 2*ScaleX, yEnd*ScaleY, 0.0f));   // Left of end
            triangleStrip.setCoordinate(index++, new Point3f(width / 2*ScaleX, yEnd*ScaleY, 0.0f));    // Right of end
        }
        
        for (int strip = 0; strip < strips; strip++) {
            float yStart = -height / 2 + (height / (strips - 1)) * strip;
            float yEnd = -height / 2 + (height / (strips - 1)) * (strip + 1);
            // Points for one strip
            triangleStrip.setCoordinate(index++, new Point3f(width / 2*ScaleX, yStart*ScaleY, 0.0f));  // Right of start
            triangleStrip.setCoordinate(index++, new Point3f(-width / 2*ScaleX, yStart*ScaleY, 0)); // Left of start
            triangleStrip.setCoordinate(index++, new Point3f(width / 2*ScaleX, yEnd*ScaleY, 0.0f));    // Right of end
            triangleStrip.setCoordinate(index++, new Point3f(-width / 2*ScaleX, yEnd*ScaleY, 0.0f));   // Left of end
        }

        // Create an appearance for the paper
        Appearance paperAppearance = new Appearance();

        // Define a material for the paper
        Material paperMaterial = new Material();
        paperMaterial.setDiffuseColor(new Color3f(0.9f, 0.9f, 0.9f)); // Light gray color
        paperMaterial.setAmbientColor(new Color3f(0.8f, 0.8f, 0.8f)); // Slightly darker gray
        paperMaterial.setSpecularColor(new Color3f(0.5f, 0.5f, 0.5f)); // Subtle shine
        paperMaterial.setShininess(5.0f); // Low shininess for matte effect
        paperAppearance.setMaterial(paperMaterial);

        // Create the Shape3D object
        Shape3D paperShape = new Shape3D(triangleStrip, paperAppearance);

        return paperShape;
    }
    
    protected static Appearance polygon_Appearance(Color3f clr, boolean t) {
		Appearance app = new Appearance();
		PolygonAttributes pa = new PolygonAttributes();
		if (t == true)
			pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);  // show only polylines
		pa.setCullFace(PolygonAttributes.CULL_NONE);       // show both sides
		app.setPolygonAttributes(pa);
		ColoringAttributes ca = new ColoringAttributes(clr,
				ColoringAttributes.FASTEST);
		app.setColoringAttributes(ca);

		return app;
	}
}
