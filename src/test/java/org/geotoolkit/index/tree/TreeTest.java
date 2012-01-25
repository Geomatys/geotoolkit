/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import org.geotoolkit.util.ArgumentChecks;

/**
 *
 * @author rmarech
 */
public abstract class TreeTest extends TestCase{
    protected final Tree tree;
    protected final List<Shape> lData = new ArrayList<Shape>();
    
    public TreeTest(Tree tree) {
        ArgumentChecks.ensureNonNull("tree", tree);
        this.tree = tree;
//        Shape s1  = new Ellipse2D.Double(-60, -21, 5, 5);
//        Shape s2  = new Ellipse2D.Double(-60, 0, 5, 5);
//        Shape s3  = new Ellipse2D.Double(-60, 21, 5, 5);
//        Shape s4  = new Ellipse2D.Double(-60, 45, 5, 5);
//        Shape s5  = new Ellipse2D.Double(-60, 60, 5, 5);
//        Shape s6  = new Ellipse2D.Double(-45, 60, 5, 5);
//        Shape s7  = new Ellipse2D.Double(-21, 60, 5, 5);
//        Shape s8  = new Ellipse2D.Double(0, 60, 5, 5);
//        Shape s9  = new Ellipse2D.Double(21, 60, 5, 5);
//        Shape s10 = new Ellipse2D.Double(45, 60, 5, 5);
//        Shape s11 = new Ellipse2D.Double(60, 60, 5, 5);
//        Shape s12 = new Ellipse2D.Double(60, 45, 5, 5);
//        Shape s13 = new Ellipse2D.Double(60, 21, 5, 5);
//        Shape s14 = new Ellipse2D.Double(60, 0, 5, 5);
//        Shape s15 = new Ellipse2D.Double(60, -21, 5, 5);
//        Shape s16 = new Ellipse2D.Double(60, -45, 5, 5);
//        Shape s17 = new Ellipse2D.Double(60, -60, 5, 5);
//        Shape s18 = new Ellipse2D.Double(45, -60, 5, 5);
//        Shape s19 = new Ellipse2D.Double(21, -60, 5, 5);
//        Shape s20 = new Ellipse2D.Double(0, -60, 5, 5);
//        Shape s21 = new Ellipse2D.Double(-21, -60, 5, 5);
//        Shape s22 = new Ellipse2D.Double(-21, 45, 5, 5);
//        Shape s23 = new Ellipse2D.Double(-21, -21, 5, 5);
//        Shape s24 = new Ellipse2D.Double(-21, 0, 5, 5);
//        Shape s25 = new Ellipse2D.Double(-21, 21, 5, 5);
//        Shape s26 = new Ellipse2D.Double(0, 21, 5, 5);
//        Shape s27 = new Ellipse2D.Double(21, 21, 5, 5);
//        Shape s28 = new Ellipse2D.Double(21, 0, 5, 5);
//        Shape s29 = new Ellipse2D.Double(21, -21, 5, 5);
//        Shape s30 = new Ellipse2D.Double(0, -21, 5, 5);
//        Shape s31 = new Ellipse2D.Double(0, 0, 5, 5);
//        Shape s32 = new Ellipse2D.Double(-60, -45, 5, 5);
//        lData.add(s1); lData.add(s2); lData.add(s3); lData.add(s4);
//        lData.add(s5); lData.add(s6); lData.add(s7); lData.add(s8);
//        lData.add(s9); lData.add(s10);lData.add(s11);lData.add(s12);
//        lData.add(s13);lData.add(s14);lData.add(s15);lData.add(s16);
//        lData.add(s17);lData.add(s18);lData.add(s19);lData.add(s20);
//        lData.add(s24);lData.add(s23);lData.add(s22);lData.add(s21);
//        lData.add(s25);lData.add(s26);lData.add(s27);lData.add(s28);
//        lData.add(s29);lData.add(s30);lData.add(s31);lData.add(s32);
        for(int j= -120;j<=120;j+=4){
            for(int i = -200;i<=200;i+=4){
                lData.add(new Ellipse2D.Double(i, j, 1, 1));
            }
        }
        Collections.shuffle(lData);
        insert();
    }
    
    private void insert(){
        for(Shape shape : lData){
            tree.insert(shape);
        }
    }
    
    protected void insertTest(){
        assertTrue(tree.getRoot().getBoundary().getBounds2D().equals(TreeUtils.getEnveloppeMin(lData)));
        List<Shape> listSearch = new ArrayList<Shape>();
        tree.search(tree.getRoot().getBoundary(), listSearch);
        assertTrue(listSearch.size() == lData.size());
//        System.out.println("tree test = "+tree);
        System.out.println("rect root = "+tree.getRoot().getBoundary().getBounds2D());
        System.out.println("rect getEnvelop min = "+TreeUtils.getEnveloppeMin(lData));
    }
}
