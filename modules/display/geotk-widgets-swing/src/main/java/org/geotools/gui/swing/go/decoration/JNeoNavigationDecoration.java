/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2004-2008, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.gui.swing.go.decoration;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;

import org.geotoolkit.display.canvas.AbstractCanvas;
import org.geotools.gui.swing.go.GoMap2D;
import org.geotools.gui.swing.map.map2d.Map2D;
import org.geotools.gui.swing.map.map2d.decoration.MapDecoration;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

/**
 *
 * @author johann sorel (Puzzle-GIS)
 */
public class JNeoNavigationDecoration extends JComponent implements MapDecoration {

    private final JNeoNavigationDecoration THIS = this;
    private GoMap2D map = null;
    
    
    private boolean minimized = false;
    private float alpha = 1.0f;
    private final Color TEINTE = Color.GRAY.brighter();
    private final Color NORTH = Color.RED;
    private final Color TEINTE_B = TEINTE.darker();
    private final Color TEINTE_L = TEINTE.brighter();
    private final Color BLACK = Color.BLACK;
    private final int MIN_SIZE = 20;
    private final int CERCLE_WIDTH = 100;
    private final int MARGIN = CERCLE_WIDTH / 10;
    private double rotation = 0;
    private Shape bigTriangle;
    private Shape smallTriangle;
    private Shape innerCercle;
    private Shape outerCercle;
    private Shape arrow;
    private Shape resetShape;

    private final BufferedImage buffer = new BufferedImage(MARGIN + CERCLE_WIDTH+1, MARGIN + CERCLE_WIDTH+1, BufferedImage.TYPE_INT_ARGB);
    private boolean mustUpdate = true;
    
    
    /**
     * 0 = drag scale
     * 1 = drag rotation
     * -1 = no drag
     */
    private short actionFlag = -1;
    
    /**
     * -1 = no button
     * 0 = top
     * 1 = right
     * 2 = down
     * 3 = left
     */
    private short overButton = -1;
    
    
    public JNeoNavigationDecoration() {
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseMotionListener);
        setOpaque(false);
        setAlpha((minimized)? 0f : 1f);
        
        bigTriangle = new Polygon(new int[]{0, 0, MIN_SIZE + 1}, new int[]{0, MIN_SIZE + 1, 0}, 3);
        smallTriangle = new Polygon(new int[]{0, MIN_SIZE / 2 - 1, MIN_SIZE / 2 - 1}, new int[]{MIN_SIZE / 2 - 1, MIN_SIZE / 2 - 1, 0}, 3);
        innerCercle = new java.awt.geom.Ellipse2D.Float(3 * MARGIN, 3 * MARGIN, CERCLE_WIDTH - 4 * MARGIN, CERCLE_WIDTH - 4 * MARGIN);
        outerCercle = new java.awt.geom.Ellipse2D.Float(MARGIN, MARGIN, CERCLE_WIDTH, CERCLE_WIDTH);
        arrow = new Polygon(new int[]{0, MIN_SIZE / 2, MIN_SIZE / 4 }, new int[]{MIN_SIZE / 2 , MIN_SIZE / 2 , 0}, 3);
        
        int centerX = MARGIN+CERCLE_WIDTH/2;
        int decalage = MARGIN+CERCLE_WIDTH/2 - 10;
        resetShape = new java.awt.geom.Ellipse2D.Float(centerX-10,centerX-10,20,20);
//        northLosange = new Polygon(new int[]{centerX,centerX+7,centerX,centerX-7}, new int[]{decalage,decalage+15,decalage+20,decalage+15}, 4);
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Rectangle clip = g2d.getClipBounds();

        if(minimized && !(clip.intersects(0, 0, MIN_SIZE+1,MIN_SIZE+1))) return;
        if(!clip.intersects(0,0,MARGIN+CERCLE_WIDTH,CERCLE_WIDTH + 2*MARGIN+5)) return;

        if(mustUpdate){
            final Graphics2D g2 = buffer.createGraphics();
            g2.setStroke(new BasicStroke(1));
            g2.setFont(g2d.getFont().deriveFont(Font.BOLD));
            g2.setBackground(new Color(0f,0f,0f,0f));
            g2.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
            g2.setRenderingHints(g2d.getRenderingHints());
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // draw the small top triangle -----------------------------------------
            g2.setColor(TEINTE.darker());
            g2.translate(0, 1);
            g2.fill(bigTriangle);
            g2.translate(0, -1);
            g2.setColor(TEINTE);
            g2.fill(bigTriangle);
            g2.setColor(TEINTE.darker());
            g2.translate(1, 1);
            g2.fill(smallTriangle);
            g2.setColor(TEINTE.brighter());
            g2.translate(-1, -1);
            g2.fill(smallTriangle);

            //draw the rotation cercle ---------------------------------------------
            g2.rotate(rotation, MARGIN + CERCLE_WIDTH / 2, MARGIN + CERCLE_WIDTH / 2);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha*0.90f));

            Point2D center = new Point2D.Float(MARGIN + CERCLE_WIDTH / 2, MARGIN);
            float radius = (float) CERCLE_WIDTH ;
            float[] dist = {0.0f, 0.9f};
            Color[] colors = {TEINTE_L, BLACK};
            RadialGradientPaint paint = new RadialGradientPaint(center, radius, dist, colors);
            g2.setPaint(paint);
            g2.fillOval(MARGIN, MARGIN, CERCLE_WIDTH, CERCLE_WIDTH);


            final FontMetrics metrics = g2.getFontMetrics(g2.getFont());


            final int posX = MARGIN + CERCLE_WIDTH / 2 ;
            final int posY = MARGIN + 15 ;
            final Point  rotCenter = new Point(MARGIN + CERCLE_WIDTH / 2,MARGIN + CERCLE_WIDTH / 2);
            final Shape line = new Line2D.Float(MARGIN+CERCLE_WIDTH/2, MARGIN+1, MARGIN+CERCLE_WIDTH/2, MARGIN + 5);
            final int small = 2;


            g2.setColor(NORTH);
            g2.draw(line);
            String text = "N";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY);


            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.setColor(TEINTE_B);
            g2.draw(line);
            text = "ne";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY+small);

            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.setColor(TEINTE_L);
            text = "E";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY);

            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.setColor(TEINTE_B);
            g2.draw(line);
            text = "se";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY+small);

            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.setColor(TEINTE_L);
            text = "S";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY);

            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.setColor(TEINTE_B);
            g2.draw(line);
            text = "sw";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY+small);

            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.setColor(TEINTE_L);
            text = "W";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY);

            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.setColor(TEINTE_B);
            g2.draw(line);
            text = "nw";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY +small);


            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.rotate(-rotation, rotCenter.x, rotCenter.y);

            //draw inner buttons -----------------------------------------------
            Shape oldclip = g2.getClip();
            g2.setClip(innerCercle);
            int height = (int) ((CERCLE_WIDTH - 4 * MARGIN) / 3f);
            int width = (CERCLE_WIDTH - 4 * MARGIN);

            GradientPaint fond = new GradientPaint(
                    new Point(0, 3*MARGIN), BLACK,
                    new Point(0, 3*MARGIN+height), new Color(0f,0f,0f,0f));

            g2.setPaint((overButton==0) ? TEINTE_B : fond);
            g2.fillRect(3*MARGIN, 3*MARGIN, width, height);
            g2.setPaint((overButton==3) ? TEINTE_B : fond);
            g2.fillRect(3*MARGIN, 3*MARGIN+height, width/2, height);
            g2.setPaint((overButton==1) ? TEINTE_B : fond);
            g2.fillRect(3*MARGIN+width/2, 3*MARGIN+height, width/2, height);
            g2.setPaint((overButton==2) ? TEINTE_B : fond);
            g2.fillRect(3*MARGIN, 3*MARGIN+2*height, width, height);
            g2.setColor(TEINTE_B);
            g2.drawRect(3*MARGIN, 3*MARGIN, width, height);
            g2.drawRect(3*MARGIN, 3*MARGIN+height, width/2, height);
            g2.drawRect(3*MARGIN+width/2, 3*MARGIN+height, width/2, height);
            g2.drawRect(3*MARGIN, 3*MARGIN+2*height, width, height);

            //draw arrows
            int TX = 3*MARGIN + width/2-arrow.getBounds().width/2;
            int TY = 3*MARGIN + height/6 +2;

            g2.translate(TX,TY);
            g2.setColor(TEINTE_L);
            g2.fill(arrow);
            g2.translate( -TX, -TY);

            g2.rotate(Math.PI/2, rotCenter.x, rotCenter.y);
            g2.translate(TX, TY);
            g2.setColor(TEINTE_L);
            g2.fill(arrow);
            g2.translate( -TX, -TY);

            g2.rotate(Math.PI/2, rotCenter.x, rotCenter.y);
            g2.translate(TX, TY);
            g2.setColor(TEINTE_L);
            g2.fill(arrow);
            g2.translate( -TX, -TY);

            g2.rotate(Math.PI/2, rotCenter.x, rotCenter.y);
            g2.translate(TX, TY);
            g2.setColor(TEINTE_L);
            g2.fill(arrow);
            g2.translate( -TX, -TY);

            g2.rotate(Math.PI/2, rotCenter.x, rotCenter.y);

            g2.setClip(oldclip);
            g2.setColor(TEINTE_B);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(3 * MARGIN, 3 * MARGIN, CERCLE_WIDTH - 4 * MARGIN -1, CERCLE_WIDTH - 4 * MARGIN -1);
            g2.setStroke(new BasicStroke(1));

            //draww North losange ------------------------------------------------
            
            g2.setPaint(fond);
            g2.fill(resetShape);
            g2.setColor(TEINTE_L);
            int centerX = MARGIN+CERCLE_WIDTH/2;
            g2.drawString("R", centerX - metrics.stringWidth("R")/2, centerX + metrics.getAscent()/2);

            mustUpdate = false;
        }

        g2d.drawImage(buffer, 0, 0, this);

        g2d.dispose();
        
    }
    
    public void setAlpha(float alpha) {
        this.alpha = alpha;
        mustUpdate = true;
        repaint(MARGIN,MARGIN,CERCLE_WIDTH,CERCLE_WIDTH + MARGIN+5);
    }

    public float getAlpha() {
        return this.alpha;
    }
    
    private void setRotation(double r){
        rotation = r;
        mustUpdate = true;
        repaint(MARGIN,MARGIN,CERCLE_WIDTH,CERCLE_WIDTH);
    }
    
    private double getRotation(){
        return rotation;
    }
    
    private void moveUp(){
        if(map!=null){
            try {
                map.getCanvas().getController().translateDisplay(0, getHeight() / 10);
            } catch (NoninvertibleTransformException ex) {
                Logger.getLogger(JNeoNavigationDecoration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void moveDown(){
        if(map!=null){
            try {
                map.getCanvas().getController().translateDisplay(0, -getHeight() / 10);
            } catch (NoninvertibleTransformException ex) {
                Logger.getLogger(JNeoNavigationDecoration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void moveLeft(){
        if(map!=null){
            try {
                map.getCanvas().getController().translateDisplay(getWidth() / 10, 0);
            } catch (NoninvertibleTransformException ex) {
                Logger.getLogger(JNeoNavigationDecoration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void moveRight(){
        if(map!=null){
            try {
                map.getCanvas().getController().translateDisplay(-getWidth() / 10, 0);
            } catch (NoninvertibleTransformException ex) {
                Logger.getLogger(JNeoNavigationDecoration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void mapRotate(double d){

        if (map != null) {
            try {
                map.getCanvas().getController().setRotation(d);
            } catch (NoninvertibleTransformException ex) {
                Logger.getLogger(JNeoNavigationDecoration.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    
    private double calculateAngle(int mouseX, int mouseY){
        
        Point A = new Point( (MARGIN + CERCLE_WIDTH / 2) ,0);
        Point B = new Point( (MARGIN + CERCLE_WIDTH / 2) , (MARGIN + CERCLE_WIDTH / 2) );
        Point C = new Point(mouseX,mouseY);
        
        double a = Math.pow(    Math.pow( (C.x - B.x) , 2) +  Math.pow( (C.y - B.y) , 2)    ,0.5d);
        double b = Math.pow(    Math.pow( (A.x - C.x) , 2) +  Math.pow( (A.y - C.y) , 2)    ,0.5d);
        double c = Math.pow(    Math.pow( (A.x - B.x) , 2) +  Math.pow( (A.y - B.y) , 2)    ,0.5d);
                
//        double angleA = Math.acos(  ( Math.pow(b, 2) + Math.pow(c, 2) - Math.pow(a, 2) )/(2*b*c) );
        double angleB = Math.acos(  ( Math.pow(a, 2) + Math.pow(c, 2) - Math.pow(b, 2) )/(2*a*c) );
//        double angleC = Math.acos(  ( Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2) )/(2*a*b) );
        
        if(mouseX < (MARGIN + CERCLE_WIDTH / 2)  ){
            angleB = 2* Math.PI - angleB; 
        }
        
        return angleB;
    }
    
    @Override
    public boolean contains(int x, int y) {
        
      final Point p = new Point(x, y);
        
      if (!bigTriangle.contains(p) &&
          !outerCercle.contains(p)) {
        return false;
      }
      return super.contains(x, y);
    }

    
    private MouseListener mouseListener = new MouseListener() {

        @Override
        public void mouseClicked(MouseEvent e) {
            Point mouse = e.getPoint();

            if(resetShape.contains(mouse)){
                mapRotate(0);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Point mouse = e.getPoint();

            if(resetShape.contains(mouse)) return;

            if (bigTriangle.contains(mouse)) {
                actionFlag = -1;
                minimized = !minimized;

                if (minimized) {
                    Animator animator = new Animator(500);
                    animator.addTarget(new PropertySetter(THIS, "alpha", 0.0f));
                    animator.setAcceleration(0.2f);
                    animator.setDeceleration(0.4f);
                    animator.start();
                } else {
                    Animator animator = new Animator(500);
                    animator.addTarget(new PropertySetter(THIS, "alpha", 1.0f));
                    animator.setAcceleration(0.2f);
                    animator.setDeceleration(0.4f);
                    animator.start();
                }
            }else if(!minimized){

                final int center = (MARGIN + CERCLE_WIDTH / 2);
                double Tx =   mouse.x - center ;
                double Ty =   mouse.y - center ;
                double distance = Math.hypot(Tx, Ty);

                //draging the north
                if(distance >= (CERCLE_WIDTH/2 - 2*MARGIN) &&
                   distance <= (CERCLE_WIDTH/2)){
                    actionFlag = 1;

                    mapRotate(calculateAngle(mouse.x, mouse.y));

                }

                //click on a central button
                if(distance < (CERCLE_WIDTH/2 - 2*MARGIN)){
                    actionFlag = -1;

                    int height = (int) ((CERCLE_WIDTH - 4 * MARGIN) / 3f);
                    if(mouse.y < 3*MARGIN + height){
                        moveUp();
                    }else if(mouse.y > 3*MARGIN + 2*height){
                        moveDown();
                    }else if(mouse.x < center){
                        moveLeft();
                    }else{
                        moveRight();
                    }

                }
                

            }


        }

        @Override
        public void mouseReleased(MouseEvent e) {
            actionFlag = -1;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if(overButton !=-1){
                overButton = -1;
                mustUpdate = true;
                repaint(innerCercle.getBounds());                
            }
        }
    };
    
    private MouseMotionListener mouseMotionListener = new MouseMotionListener() {

        @Override
        public void mouseDragged(MouseEvent e) {
            if (actionFlag == 1) {
                mapRotate(calculateAngle(e.getX(),e.getY()));
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Point mouse = e.getPoint();

            //we repaint inner buttons
            if(innerCercle.contains(mouse) && !resetShape.contains(mouse)){
                final int center = (MARGIN + CERCLE_WIDTH / 2);
                final int oldOver = overButton;
                final int height = (int) ((CERCLE_WIDTH - 4 * MARGIN) / 3f);
                if (mouse.y < 3 * MARGIN + height) {
                    overButton = 0;
                } else if (mouse.y > 3 * MARGIN + 2 * height) {
                    overButton = 2;
                } else if (mouse.x < center) {
                    overButton = 3;
                } else {
                    overButton = 1;
                }

                if(oldOver != overButton){
                    mustUpdate = true;
                    repaint(innerCercle.getBounds());
                }

            }else if(overButton != -1){
                overButton = -1;
                mustUpdate = true;
                repaint(innerCercle.getBounds());
            }

        }
    };
    
    private PropertyChangeListener propertyListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent arg0) {
            
            double rotation = map.getCanvas().getController().getRotation();
            
            if(rotation != getRotation()){
                setRotation(rotation);
            }
        }
    };
    

    @Override
    public void refresh() {
    }

    @Override
    public void dispose() {
        removeMouseListener(mouseListener);
        removeMouseMotionListener(mouseMotionListener);
    }

    @Override
    public void setMap2D(Map2D map) {
        
        if(this.map != null){
            this.map.getCanvas().removePropertyChangeListener(AbstractCanvas.OBJECTIVE_TO_DISPLAY_PROPERTY,propertyListener);
        }
        
        if(map instanceof GoMap2D){
            this.map = (GoMap2D) map;
            this.map.getCanvas().addPropertyChangeListener(AbstractCanvas.OBJECTIVE_TO_DISPLAY_PROPERTY,propertyListener);
        }
    }

    @Override
    public Map2D getMap2D() {
        return map;
    }

    @Override
    public JComponent geComponent() {
        return this;
    }
}
