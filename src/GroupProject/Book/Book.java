package GroupProject.Book;

import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;

public class Book {
    private Page[] pages;
    private Cover frontCover;
    private Cover backCover;
    private int numPages;
    private TransformGroup bookGroup;
    private float pageSpacing = 0.001f;
    private float coverOffset = 0.02f;
    
    public Book(int numberOfPages) {
        numPages = numberOfPages;
        pages = new Page[numPages];
        frontCover = new Cover();
        backCover = new Cover();
        bookGroup = new TransformGroup();
        bookGroup.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        bookGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        
        createBook();
    }
    
    private void createBook() {
        // Create front cover
        TransformGroup frontCoverTG = new TransformGroup();
        frontCoverTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Transform3D frontTransform = new Transform3D();
        frontTransform.setTranslation(new Vector3f(0.0f, 0.0f, coverOffset));
        frontCoverTG.setTransform(frontTransform);
        frontCoverTG.addChild(frontCover.createCover());
        bookGroup.addChild(frontCoverTG);
        
        // Create pages
        for(int i = 0; i < numPages; i++) {
            pages[i] = new Page();
            TransformGroup pageTG = new TransformGroup();
            pageTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            
            Transform3D pageTransform = new Transform3D();
            pageTransform.setTranslation(new Vector3f(0.0f, 0.0f, coverOffset + pageSpacing * (i + 1)));
            pageTG.setTransform(pageTransform);
            pageTG.addChild(pages[i].createPaper());
            bookGroup.addChild(pageTG);
        }
        
        // Create back cover
        TransformGroup backCoverTG = new TransformGroup();
        backCoverTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        Transform3D backTransform = new Transform3D();
        backTransform.setTranslation(new Vector3f(0.0f, 0.0f, coverOffset + pageSpacing * (numPages + 1)));
        backCoverTG.setTransform(backTransform);
        backCoverTG.addChild(backCover.createCover());
        bookGroup.addChild(backCoverTG);
    }
    
    public void turnPage(int pageIndex, float angle) {
        if(pageIndex >= 0 && pageIndex < numPages) {
            pages[pageIndex].turnPage(angle);
        }
    }
    
    public void openFrontCover(float angle) {
        frontCover.turnPage(angle);
    }
    
    public void openBackCover(float angle) {
        backCover.turnPage(angle);
    }
    
    public TransformGroup getBookGroup() {
        return bookGroup;
    }
    
    public int getNumberOfPages() {
        return numPages;
    }
    
    public Page getPage(int index) {
        if(index >= 0 && index < numPages) {
            return pages[index];
        }
        return null;
    }
}