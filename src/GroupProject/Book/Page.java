package GroupProject.Book;

import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;

public class Page {
    private float width = 1.5f;
    private float height = 2f;
    private float thickness = 0f;
    private float turnAngle = 0.0f;
    private TransformGroup pageTG;
    private TriangleStripArray triangleStrip;
    private float scaleX = 0.2f;
    private float scaleY = 0.2f;
    private float scaleZ = 0.2f;
    private int strips = 10;
    private int verticesPerStrip = 4;
    
    public Shape3D createPaper() {
        pageTG = new TransformGroup();
        pageTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        pageTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        
        int totalVertices = strips * verticesPerStrip;
        int subStrips = 3; // Number of subdivisions per strip
        int totalSubStrips = strips * subStrips;
        
        // Create array of strip counts for subdivided strips
        int[] stripCounts = new int[totalSubStrips * 2];
        for (int i = 0; i < totalSubStrips * 2; i++) {
            stripCounts[i] = verticesPerStrip;
        }
        
        triangleStrip = new TriangleStripArray(
            totalSubStrips * verticesPerStrip * 2,
            TriangleStripArray.COORDINATES | TriangleStripArray.NORMALS,
            stripCounts
        );
        
        triangleStrip.setCapability(TriangleStripArray.ALLOW_COORDINATE_WRITE);
        triangleStrip.setCapability(TriangleStripArray.ALLOW_COORDINATE_READ);
        
        updatePageGeometry(triangleStrip, totalSubStrips, 0.0f);
        
        Appearance paperAppearance = new Appearance();
        Material paperMaterial = new Material();
        paperMaterial.setDiffuseColor(new Color3f(1.0f, 1.0f, 1.0f));
        paperMaterial.setAmbientColor(new Color3f(1.0f, 1.0f, 1.0f));
        paperMaterial.setSpecularColor(new Color3f(1.0f, 1.0f, 1.0f));
        paperMaterial.setShininess(5.0f);
        paperAppearance.setMaterial(paperMaterial);
        
        PolygonAttributes pa = new PolygonAttributes();
        pa.setCullFace(PolygonAttributes.CULL_NONE);
        paperAppearance.setPolygonAttributes(pa);
        
        Shape3D paperShape = new Shape3D(triangleStrip, paperAppearance);
        paperShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        paperShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        
        return paperShape;
    }
    
    private void updatePageGeometry(TriangleStripArray geometry, int totalStrips, float angle) {
        int index = 0;
        float curveFactor = (float)Math.sin(angle * Math.PI / 180.0);
        Point3f lastEndLeft = null;
        Point3f lastEndRight = null;
        
        // Front face strips
        for (int strip = 0; strip < totalStrips; strip++) {
            float t = strip / (float)(totalStrips - 1);
            float yStart = -height/2 + height * t;
            float yEnd = -height/2 + height * (t + 1.0f/totalStrips);
            
            // Calculate height-dependent curve
            float heightScale = (float)Math.cos(angle * Math.PI / 180.0 * t);
            float zCurveStart = (float)(Math.sin(angle * Math.PI / 180.0) * height * t * t);
            float zCurveEnd = (float)(Math.sin(angle * Math.PI / 180.0) * height * ((t + 1.0f/totalStrips) * (t + 1.0f/totalStrips)));
            
            if (lastEndLeft != null && lastEndRight != null) {
                geometry.setCoordinate(index++, lastEndLeft);
                geometry.setCoordinate(index++, lastEndRight);
            } else {
                geometry.setCoordinate(index++, new Point3f(-width/2 * scaleX, yStart * scaleY * heightScale, zCurveStart * scaleZ));
                geometry.setCoordinate(index++, new Point3f(width/2 * scaleX, yStart * scaleY * heightScale, zCurveStart * scaleZ));
            }
            
            lastEndLeft = new Point3f(-width/2 * scaleX, yEnd * scaleY * heightScale, zCurveEnd * scaleZ);
            lastEndRight = new Point3f(width/2 * scaleX, yEnd * scaleY * heightScale, zCurveEnd * scaleZ);
            geometry.setCoordinate(index++, lastEndLeft);
            geometry.setCoordinate(index++, lastEndRight);
        }
        
        // Reset for back face
        lastEndLeft = null;
        lastEndRight = null;
        
        // Back face strips
        for (int strip = 0; strip < totalStrips; strip++) {
            float t = strip / (float)(totalStrips - 1);
            float yStart = -height/2 + height * t;
            float yEnd = -height/2 + height * (t + 1.0f/totalStrips);
            
            float heightScale = (float)Math.cos(angle * Math.PI / 180.0 * t);
            float zCurveStart = (float)(Math.sin(angle * Math.PI / 180.0) * height * t * t);
            float zCurveEnd = (float)(Math.sin(angle * Math.PI / 180.0) * height * ((t + 1.0f/totalStrips) * (t + 1.0f/totalStrips)));
            
            if (lastEndLeft != null && lastEndRight != null) {
                geometry.setCoordinate(index++, lastEndRight);
                geometry.setCoordinate(index++, lastEndLeft);
            } else {
                geometry.setCoordinate(index++, new Point3f(width/2 * scaleX, yStart * scaleY * heightScale, zCurveStart * scaleZ));
                geometry.setCoordinate(index++, new Point3f(-width/2 * scaleX, yStart * scaleY * heightScale, zCurveStart * scaleZ));
            }
            
            lastEndLeft = new Point3f(-width/2 * scaleX, yEnd * scaleY * heightScale, zCurveEnd * scaleZ);
            lastEndRight = new Point3f(width/2 * scaleX, yEnd * scaleY * heightScale, zCurveEnd * scaleZ);
            geometry.setCoordinate(index++, lastEndRight);
            geometry.setCoordinate(index++, lastEndLeft);
        }
    }
    
    public void turnPage(float angle) {
        turnAngle = angle;
        updatePageGeometry(triangleStrip, strips * 3, turnAngle);
    }
}