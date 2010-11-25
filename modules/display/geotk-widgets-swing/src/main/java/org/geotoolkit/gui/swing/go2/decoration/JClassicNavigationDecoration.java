/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gui.swing.go2.decoration;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
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
import java.awt.geom.Area;
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
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.util.logging.Logging;

/**
 *
 * @author johann sorel (Puzzle-GIS)
 * @module pending
 */
public class JClassicNavigationDecoration extends JComponent implements MapDecoration {

    public static enum THEME{
        CLASSIC,
        NEO
    }

    private static final Logger LOGGER = Logging.getLogger(JClassicNavigationDecoration.class);

    private Map2D map = null;

    private static final int MIN_SIZE = 20;
    private static final int CERCLE_WIDTH = 100;
    private static final int MARGIN = CERCLE_WIDTH / 10;
    
    private boolean minimized = false;
    private final Color teinte = Color.GRAY.brighter();
    private final Color north = Color.RED;
    private final Color teinteDark;
    private final Color teinteLight;
    private final Color text1;
    private final Color text2;
    private final Color dark = Color.BLACK;
    private final Color base;
    private final Color trans = new Color(1f, 1f, 1f, 0f);
    private final boolean inBorder;
    private double rotation = 0;
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
    

    public JClassicNavigationDecoration(){
        this(THEME.CLASSIC);
    }

    public JClassicNavigationDecoration(THEME theme) {

        if(theme == THEME.CLASSIC){
            teinteDark = Color.GRAY;
            teinteLight = Color.WHITE;
            text1 = Color.GRAY;
            text2 = Color.LIGHT_GRAY;
            base = Color.WHITE;
            inBorder = true;
        }else{
            teinteDark = teinte.darker();
            teinteLight = teinte.brighter();
            text1 = teinteDark;
            text2 = teinteLight;
            base = Color.BLACK;
            inBorder = false;
        }


        addMouseListener(mouseListener);
        addMouseMotionListener(mouseMotionListener);
        setOpaque(false);
        
        innerCercle = new java.awt.geom.Ellipse2D.Float(3 * MARGIN, 3 * MARGIN, CERCLE_WIDTH - 4 * MARGIN, CERCLE_WIDTH - 4 * MARGIN);
        outerCercle = new java.awt.geom.Ellipse2D.Float(MARGIN, MARGIN, CERCLE_WIDTH, CERCLE_WIDTH);
        arrow = new Polygon(new int[]{0, MIN_SIZE / 2, MIN_SIZE / 4 }, new int[]{MIN_SIZE / 2 , MIN_SIZE / 2 , 0}, 3);
        
        final int centerX = MARGIN+CERCLE_WIDTH/2;
        resetShape = new java.awt.geom.Ellipse2D.Float(centerX-10,centerX-10,20,20);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        final Rectangle clip = g2d.getClipBounds();

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


            //draw the rotation cercle ---------------------------------------------
            g2.rotate(rotation, MARGIN + CERCLE_WIDTH / 2, MARGIN + CERCLE_WIDTH / 2);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            final Point2D center = new Point2D.Float(MARGIN + CERCLE_WIDTH / 2, MARGIN + CERCLE_WIDTH / 2);
            final float radius = (float) CERCLE_WIDTH/2 ;
            final float[] dist = {0.2f, 1f};
            final Color[] colors = {teinteLight, base};
            final RadialGradientPaint paint = new RadialGradientPaint(center, radius, dist, colors);
            g2.setPaint(paint);
            g2.fillOval(MARGIN, MARGIN, CERCLE_WIDTH, CERCLE_WIDTH);
            g2.setPaint(teinteDark);
            g2.drawOval(MARGIN, MARGIN, CERCLE_WIDTH, CERCLE_WIDTH);


            final FontMetrics metrics = g2.getFontMetrics(g2.getFont());


            final int posX = MARGIN + CERCLE_WIDTH / 2 ;
            final int posY = MARGIN + 15 ;
            final Point  rotCenter = new Point(MARGIN + CERCLE_WIDTH / 2,MARGIN + CERCLE_WIDTH / 2);
            final Shape line = new Line2D.Float(MARGIN+CERCLE_WIDTH/2, MARGIN+1, MARGIN+CERCLE_WIDTH/2, MARGIN + 5);
            final int small = 2;


            g2.setColor(north);
            g2.draw(line);
            String text = "N";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY);


            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.setColor(text2);
            g2.draw(line);
            text = "ne";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY+small);

            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.setColor(text1);
            text = "E";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY);

            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.setColor(text2);
            g2.draw(line);
            text = "se";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY+small);

            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.setColor(text1);
            text = "S";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY);

            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.setColor(text2);
            g2.draw(line);
            text = "sw";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY+small);

            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.setColor(text1);
            text = "W";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY);

            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.setColor(text2);
            g2.draw(line);
            text = "nw";
            g2.drawString(text, posX - metrics.stringWidth(text)/2, posY +small);


            g2.rotate(Math.PI/4, rotCenter.x, rotCenter.y);
            g2.rotate(-rotation, rotCenter.x, rotCenter.y);

            //draw inner buttons -----------------------------------------------
            final Shape oldclip = g2.getClip();
            final Area area = new Area(innerCercle);
            area.subtract(new Area(resetShape));
            g2.setClip(area);
            final int height = (int) ((CERCLE_WIDTH - 4 * MARGIN) / 3f);
            final int width = (CERCLE_WIDTH - 4 * MARGIN);

            g2.setPaint((overButton==0) ? teinteDark : trans);
            g2.fillRect(3*MARGIN, 3*MARGIN, width, height);
            g2.setPaint((overButton==3) ? teinteDark : trans);
            g2.fillRect(3*MARGIN, 3*MARGIN+height, width/2, height);
            g2.setPaint((overButton==1) ? teinteDark : trans);
            g2.fillRect(3*MARGIN+width/2, 3*MARGIN+height, width/2, height);
            g2.setPaint((overButton==2) ? teinteDark : trans);
            g2.fillRect(3*MARGIN, 3*MARGIN+2*height, width, height);
            g2.setColor(teinteDark);
            if(inBorder){
                g2.drawRect(3*MARGIN, 3*MARGIN, width, height);
                g2.drawRect(3*MARGIN, 3*MARGIN+height, width/2, height);
                g2.drawRect(3*MARGIN+width/2, 3*MARGIN+height, width/2, height);
                g2.drawRect(3*MARGIN, 3*MARGIN+2*height, width, height);
            }

            //draw arrows
            final int topX = 3*MARGIN + width/2-arrow.getBounds().width/2;
            final int topY = 3*MARGIN + height/6 +2;

            g2.translate(topX,topY);
            g2.setColor(text2);
            g2.fill(arrow);
            g2.translate( -topX, -topY);

            g2.rotate(Math.PI/2, rotCenter.x, rotCenter.y);
            g2.translate(topX, topY);
            g2.setColor(text2);
            g2.fill(arrow);
            g2.translate( -topX, -topY);

            g2.rotate(Math.PI/2, rotCenter.x, rotCenter.y);
            g2.translate(topX, topY);
            g2.setColor(text2);
            g2.fill(arrow);
            g2.translate( -topX, -topY);

            g2.rotate(Math.PI/2, rotCenter.x, rotCenter.y);
            g2.translate(topX, topY);
            g2.setColor(text2);
            g2.fill(arrow);
            g2.translate( -topX, -topY);

            g2.rotate(Math.PI/2, rotCenter.x, rotCenter.y);

            g2.setClip(oldclip);
            g2.setColor(teinteDark);
            g2.setStroke(new BasicStroke(2));
            if(inBorder){
                g2.drawOval(3 * MARGIN, 3 * MARGIN, CERCLE_WIDTH - 4 * MARGIN -1, CERCLE_WIDTH - 4 * MARGIN -1);
            }
            g2.setStroke(new BasicStroke(1));

            //draw reset button ------------------------------------------------

            g2.setPaint((overButton==4) ? teinteDark : trans);
            g2.fill(resetShape);
            if(inBorder){
                g2.setPaint(teinteDark);
                g2.draw(resetShape);
            }
            final int centerX = MARGIN+CERCLE_WIDTH/2;
            g2.setPaint(text1);
            g2.drawString("R", centerX - metrics.stringWidth("R")/2+1, centerX + metrics.getAscent()/2-1);

            mustUpdate = false;
        }

        g2d.drawImage(buffer, 0, 0, this);

        g2d.dispose();
        
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
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
    }
    
    private void moveDown(){
        if(map!=null){
            try {
                map.getCanvas().getController().translateDisplay(0, -getHeight() / 10);
            } catch (NoninvertibleTransformException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
    }
    
    private void moveLeft(){
        if(map!=null){
            try {
                map.getCanvas().getController().translateDisplay(getWidth() / 10, 0);
            } catch (NoninvertibleTransformException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
    }
    
    private void moveRight(){
        if(map!=null){
            try {
                map.getCanvas().getController().translateDisplay(-getWidth() / 10, 0);
            } catch (NoninvertibleTransformException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
    }
    
    private void mapRotate(double d){

        if (map != null) {
            try {
                map.getCanvas().getController().setRotation(d);
            } catch (NoninvertibleTransformException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }

        }
    }
    
    private double calculateAngle(int mouseX, int mouseY){
        
        final Point pa = new Point( (MARGIN + CERCLE_WIDTH / 2) ,0);
        final Point pb = new Point( (MARGIN + CERCLE_WIDTH / 2) , (MARGIN + CERCLE_WIDTH / 2) );
        final Point pc = new Point(mouseX,mouseY);
        
        final double a = Math.pow(    Math.pow( pc.x - pb.x , 2) +  Math.pow( pc.y - pb.y , 2)    ,0.5d);
        final double b = Math.pow(    Math.pow( pa.x - pc.x , 2) +  Math.pow( pa.y - pc.y , 2)    ,0.5d);
        final double c = Math.pow(    Math.pow( pa.x - pb.x , 2) +  Math.pow( pa.y - pb.y , 2)    ,0.5d);
                
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
      return outerCercle.contains(x,y);
    }

    
    private final MouseListener mouseListener = new MouseListener() {

        @Override
        public void mouseClicked(final MouseEvent e) {
            final Point mouse = e.getPoint();

            if(resetShape.contains(mouse)){
                mapRotate(0);
            }
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            final Point mouse = e.getPoint();

            if(resetShape.contains(mouse)) return;


                final int center = (MARGIN + CERCLE_WIDTH / 2);
                final double tx =   mouse.x - center ;
                final double ty =   mouse.y - center ;
                final double distance = Math.hypot(tx, ty);

                //draging the north
                if(distance >= (CERCLE_WIDTH/2 - 2*MARGIN) &&
                   distance <= (CERCLE_WIDTH/2)){
                    actionFlag = 1;

                    mapRotate(calculateAngle(mouse.x, mouse.y));
                }

                //click on a central button
                if(distance < (CERCLE_WIDTH/2 - 2*MARGIN)){
                    actionFlag = -1;

                    final int height = (int) ((CERCLE_WIDTH - 4 * MARGIN) / 3f);
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
    
    private final MouseMotionListener mouseMotionListener = new MouseMotionListener() {

        @Override
        public void mouseDragged(final MouseEvent e) {
            if (actionFlag == 1) {
                mapRotate(calculateAngle(e.getX(),e.getY()));
            }
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            final Point mouse = e.getPoint();

            //we repaint inner buttons
            if(innerCercle.contains(mouse) ){
                final int center = (MARGIN + CERCLE_WIDTH / 2);
                final int oldOver = overButton;
                final int height = (int) ((CERCLE_WIDTH - 4 * MARGIN) / 3f);
                if(resetShape.contains(mouse)){
                    overButton = 4;
                }else if (mouse.y < 3 * MARGIN + height) {
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
    
    private final PropertyChangeListener propertyListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(final PropertyChangeEvent arg0) {
            
            final double rotation = map.getCanvas().getController().getRotation();
            
            if(rotation != getRotation()){
                setRotation(rotation);
            }
        }
    };

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(buffer.getWidth(),buffer.getHeight());
    }

    @Override
    public Dimension getSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getSize(Dimension rv) {
        if(rv != null){
            rv.height = buffer.getHeight();
            rv.width = buffer.getWidth();
            return rv;
        }else{
            return getSize();
        }
    }

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
            this.map.getCanvas().removePropertyChangeListener(ReferencedCanvas2D.OBJECTIVE_TO_DISPLAY_PROPERTY,propertyListener);
        }
        
        this.map = map;
        this.map.getCanvas().addPropertyChangeListener(ReferencedCanvas2D.OBJECTIVE_TO_DISPLAY_PROPERTY,propertyListener);
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
