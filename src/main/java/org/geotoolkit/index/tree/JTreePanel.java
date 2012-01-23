/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.JPanel;
import org.geotoolkit.index.tree.hilbert.HilbertNode2D;

/**
 *
 * @author rmarech
 */
public class JTreePanel extends JPanel{
    
    
    Color cF = Color.RED;
    Color cO = Color.GREEN;
    Color cL = Color.BLUE;
    
    private Tree tree;
    private List<Shape> listsearch;
    
    public JTreePanel(Tree tree, List<Shape> listsearch) {
        this.tree = tree;
        this.listsearch = listsearch;
    }

    @Override
    protected void paintComponent(Graphics g) {
               
        Graphics2D g2d = (Graphics2D)g;
        super.paintComponent(g);
        final Node2D nod = tree.getRoot();
        Object boundnode = nod.getBoundary();
        final AffineTransform trs = new AffineTransform();
        if(boundnode instanceof Shape){
            Rectangle2D rect = ((Shape)boundnode).getBounds2D();
            final double height = this.getHeight();
            final double scaley = (float)height/rect.getHeight();
//            System.out.println("scaley = "+scaley);
            trs.translate(this.getWidth()/2, this.getHeight()/2);
            trs.scale(3, 3);
            trs.translate( -rect.getCenterX(), -rect.getCenterY());
        }
        g2d.setColor(Color.blue);
        g2d.draw(trs.createTransformedShape(nod.getBoundary().getBounds2D()));
        if(nod instanceof HilbertNode2D){
            paintHilberNode(nod, g2d, trs);
        }else{
            paintNode(nod, g2d, trs);
        }
        if(listsearch!=null){
            g2d.setColor(Color.yellow);
            for(Shape sh : listsearch){
                g2d.draw(trs.createTransformedShape(sh));
            }
        }
            
        
    }
    
    public void setTree(Tree tree){
        this.tree = tree;
    }
    
    private void paintNode(Node2D node, Graphics2D g2d, AffineTransform trs){
        Rectangle2D boundnode = node.getBoundary().getBounds2D();
        for(Shape ent : node.getEntries()){
            g2d.setColor(cO);
            g2d.draw(trs.createTransformedShape(ent));
        }
        
        for(Node2D nod : node.getChildren()){
            paintNode(nod, g2d,trs);
        }
        if(node.isLeaf()){//attention seulement couche inférieur
            g2d.setColor(cO);            
            g2d.draw(trs.createTransformedShape(boundnode));
        }
            
        
    }
    
    private void paintHilberNode(Node2D node, Graphics2D g2d, AffineTransform trs){
        HilbertNode2D hn2d = (HilbertNode2D)node;
        Rectangle2D boundnode = hn2d.getBoundary().getBounds2D();
        
        if((Boolean)hn2d.getUserProperty("isleaf")){
            for(Node2D nod : (List<Node2D>)hn2d.getUserProperty("cells")){
                if(nod.getBoundary()!=null){
                    paintNode(nod, g2d, trs);
                }
            }
            g2d.setColor(cL); 
            List<Point2D> lPC = (List<Point2D>)hn2d.getUserProperty("centroids");
            if (!lPC.isEmpty()) {
                for (int i = 0; i < lPC.size() - 1; i++) {
                    g2d.draw(trs.createTransformedShape(new Line2D.Double(lPC.get(i), lPC.get(i + 1))));
                }
            }
            g2d.setColor(cF);            
            g2d.draw(trs.createTransformedShape(boundnode));
        }else{
            for(Node2D nod : node.getChildren()){
                paintHilberNode(nod, g2d, trs);
            }
        }
        
//        for(Shape ent : node.getEntries()){
//            g2d.setColor(cO);
//            g2d.draw(trs.createTransformedShape(ent));
//        }
//        
//        for(Node2D nod : node.getChildren()){
//            paintNode(nod, g2d,trs);
//        }
//        if(node.isLeaf()){//attention seulement couche inférieur
//            g2d.setColor(cF);            
//            g2d.draw(trs.createTransformedShape(boundnode));
//        }
            
        
    }
    
}
