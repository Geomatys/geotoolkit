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
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author rmarech
 */
public class JTreePanel extends JPanel{
    
    
    Color cF = Color.red;
    Color cO = Color.BLUE;
    
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
            System.out.println("scaley = "+scaley);
            trs.translate(this.getWidth()/2, this.getHeight()/2);
            trs.scale(3, 3);
            trs.translate( rect.getCenterX(), rect.getCenterY());
        }
        g2d.setColor(Color.blue);
        g2d.draw(trs.createTransformedShape(nod.getBoundary().getBounds2D()));
        paintNode(nod, g2d, trs);
        g2d.setColor(Color.yellow);
        for(Shape sh : listsearch){
            g2d.draw(trs.createTransformedShape(sh));
        }
        
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
            g2d.setColor(cF);            
            g2d.draw(trs.createTransformedShape(boundnode));
        }
            
        
    }
    
}
