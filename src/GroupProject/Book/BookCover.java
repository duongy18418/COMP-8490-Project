package GroupProject.Book;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.vecmath.Color3f;


public class BookCover{
	
	public static float ScaleX = 0.2f, ScaleY = 0.2f, ScaleZ = 0.2f;
	
	public static Box getBookCover() {
		Appearance app = new Appearance();
		
        ColoringAttributes clr = new ColoringAttributes(new Color3f(1,0,0), ColoringAttributes.NICEST);
        app.setColoringAttributes(clr);
		
		float[] coverScale = {1.5f,2f,0.1f};
		
		
		return new Box(ScaleX*coverScale[0],ScaleY*coverScale[1],ScaleZ*coverScale[2], Box.GENERATE_NORMALS, app);
		
	}

}
