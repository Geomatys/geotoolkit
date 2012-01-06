/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.rtree2d;

import com.mycompany.hilbeRTree.Bound;
import com.mycompany.hilbeRTree.HilbertRTree;
import com.mycompany.utilsRTree.Entry;
import com.mycompany.utilsRTree.Rtree;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author marechal
 */
public class Panel extends JPanel {
Rtree arbre;

public void setArbre(Rtree tree){
    this.arbre = tree;
}
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setTransform(new AffineTransform(3, 0, 0, -3, (this.getWidth()/2), (this.getHeight()/2)));
        g2d.setStroke(new BasicStroke(1/10));
//            Rectangle2D searc = new Rectangle2D.Double(-10, -50, 100, 150);
//            List<Entry> lE = new ArrayList<Entry>();
//            arbre.search(searc, lE);
//            g.setColor(Color.ORANGE);
//            g2d.draw(searc);
//            for(Entry ent : lE){
//                g2d.draw((Shape)ent.getObj());
//            }
            arbre.paint(g2d);
    }

}
