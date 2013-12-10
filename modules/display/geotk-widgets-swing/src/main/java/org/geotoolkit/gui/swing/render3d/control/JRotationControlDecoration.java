/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.gui.swing.render3d.control;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.geotoolkit.display3d.scene.camera.TrackBallCamera;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Thomas Rouby (Geomatys)
 */
public class JRotationControlDecoration extends JComponent implements PropertyChangeListener {

    private static final int[] MARGIN = new int[]{5,5,5,5};                 // margin left, bottom, right, top
    private static final int RESET_RADIUS = 5;
    private static final int INNER_RADIUS = 20 + RESET_RADIUS;
    private static final int OUTER_RADIUS = 12 + INNER_RADIUS;
    private static final int ARROW_SIZE = 8;
    private static final int[] CENTER = new int[]{MARGIN[0]+OUTER_RADIUS, MARGIN[3]+OUTER_RADIUS};   // center on x,y
    private static final int[] SIZE = new int[]{MARGIN[0] + MARGIN[2] + OUTER_RADIUS*2, MARGIN[1] + MARGIN[3] + OUTER_RADIUS*2}; // width, height

    private final TrackBallCamera camera;

    private final Color north = Color.RED;
    private final Color teinteDark;
    private final Color teinteLight;
    private final Color text1;
    private final Color text2;
    private final Color trans = new Color(1f, 1f, 1f, 0f);
    private double rotation = 0.0;
    private final Shape innerCercle;
    private final Shape outerCercle;
    private final Shape resetCercle;
    private final Shape arrow;

    private final BufferedImage buffer = new BufferedImage(SIZE[0], SIZE[1], BufferedImage.TYPE_INT_ARGB);
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

    public JRotationControlDecoration(TrackBallCamera camera) {

        this.camera = camera;
        this.camera.addPropertyChangeListener(this);

        teinteDark = Color.LIGHT_GRAY;
        teinteLight = Color.WHITE;
        text1 = Color.DARK_GRAY;
        text2 = Color.GRAY;

        addMouseListener(mouseListener);
        addMouseMotionListener(mouseMotionListener);
        setOpaque(false);

        outerCercle = new java.awt.geom.Ellipse2D.Float(CENTER[0]-OUTER_RADIUS, CENTER[1]-OUTER_RADIUS, OUTER_RADIUS*2, OUTER_RADIUS*2);
        innerCercle = new java.awt.geom.Ellipse2D.Float(CENTER[0]-INNER_RADIUS, CENTER[1]-INNER_RADIUS, INNER_RADIUS*2, INNER_RADIUS*2);
        resetCercle = new java.awt.geom.Ellipse2D.Float(CENTER[0]-RESET_RADIUS, CENTER[1]-RESET_RADIUS, RESET_RADIUS*2, RESET_RADIUS*2);

        arrow = new Polygon(new int[]{0, ARROW_SIZE, ARROW_SIZE / 2 }, new int[]{ARROW_SIZE , ARROW_SIZE , 0}, 3);

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //event from camera
        this.setRotation(Math.toRadians(360.0f-camera.getRotateZ()), false);
    }


    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        final Rectangle clip = g2d.getClipBounds();

        if(!clip.intersects(0,0,SIZE[0],SIZE[1])) return;

        if(mustUpdate){
            final Graphics2D g2 = buffer.createGraphics();
            g2.setStroke(new BasicStroke(1));
            g2.setFont(g2d.getFont().deriveFont(Font.BOLD));
            g2.setBackground(new Color(0f,0f,0f,0f));
            g2.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
            g2.setRenderingHints(g2d.getRenderingHints());
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            final int justOuterRadius = OUTER_RADIUS-INNER_RADIUS;
            final int justInnerRadius = INNER_RADIUS-RESET_RADIUS;

            final Point2D center = new Point2D.Float(CENTER[0], CENTER[1]);
            final float[] dist = {0.5f, 1.0f};
            final Color[] colors = {teinteLight, teinteDark};
            final RadialGradientPaint paint = new RadialGradientPaint(center, INNER_RADIUS, dist, colors);

            g2.setPaint(paint);
            g2.fillOval(CENTER[0]-INNER_RADIUS, CENTER[1]-INNER_RADIUS, INNER_RADIUS*2, INNER_RADIUS*2);

            g2.setPaint(teinteDark);
            g2.drawOval(CENTER[0]-INNER_RADIUS-justOuterRadius/2, CENTER[1]-INNER_RADIUS-justOuterRadius/2, INNER_RADIUS*2+justOuterRadius-1, INNER_RADIUS*2+justOuterRadius-1);


            //draw arrows
            final int topX = CENTER[0] - ARROW_SIZE/2;
            final int topY = CENTER[1] - (int)(0.8*INNER_RADIUS);

            g2.translate(topX,topY);
            g2.setColor((overButton==0) ? text1 : text2);
            g2.fill(arrow);
            g2.translate( -topX, -topY);

            g2.rotate(Math.PI/2, CENTER[0], CENTER[1]);
            g2.translate(topX, topY);
            g2.setColor((overButton==1) ? text1 : text2);
            g2.fill(arrow);
            g2.translate( -topX, -topY);

            g2.rotate(Math.PI/2, CENTER[0], CENTER[1]);
            g2.translate(topX, topY);
            g2.setColor((overButton==2) ? text1 : text2);
            g2.fill(arrow);
            g2.translate( -topX, -topY);

            g2.rotate(Math.PI/2, CENTER[0], CENTER[1]);
            g2.translate(topX, topY);
            g2.setColor((overButton==3) ? text1 : text2);
            g2.fill(arrow);
            g2.translate( -topX, -topY);

            g2.rotate(Math.PI/2, CENTER[0], CENTER[1]);

            //draw reset button ------------------------------------------------

            g2.setPaint((overButton==4) ? text1 : text2);
            g2.fill(resetCercle);

            //draw the rotation cercle ---------------------------------------------
            g2.rotate(rotation, CENTER[0], CENTER[1]);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            final int posX = CENTER[0] - justOuterRadius/4;
            final int posY = CENTER[1] - OUTER_RADIUS + justOuterRadius/4 ;
            g2.setColor(north);
            g2.fillOval(posX, posY, justOuterRadius/2, justOuterRadius/2);
            g2.rotate(-rotation, CENTER[0], CENTER[1]);

            mustUpdate = false;
        }

        g2d.drawImage(buffer, 0, 0, this);

        g2d.dispose();

    }

    private void setRotation(final double r, boolean updateMap){
        rotation = r%(2.0*Math.PI);

        if (updateMap){
            camera.setRotateZ(360.0f-(float)Math.toDegrees(r));
        }

        mustUpdate = true;
        repaint(MARGIN[0],MARGIN[3],OUTER_RADIUS*2,OUTER_RADIUS*2);
    }

    private double getRotation(){
        return rotation;
    }

    private void moveUp(){
        camera.rotateUp(1.0f);
    }

    private void moveDown(){
        camera.rotateDown(1.0f);
    }

    private void moveLeft(){
        setRotation((float) getRotation() + Math.toRadians(1.0), true);
    }

    private void moveRight(){
        setRotation((float)getRotation()-Math.toRadians(1.0), true);
    }

    private void mapRotate(final double d){
        setRotation((float)d, true);
    }

    private double calculateAngle(final int mouseX, final int mouseY){

        final Point pa = new Point( CENTER[0] ,0);
        final Point pb = new Point( CENTER[0] , CENTER[1] );
        final Point pc = new Point(mouseX,mouseY);

        final double a = Math.pow(    Math.pow( pc.x - pb.x , 2) +  Math.pow( pc.y - pb.y , 2)    ,0.5d);
        final double b = Math.pow(    Math.pow( pa.x - pc.x , 2) +  Math.pow( pa.y - pc.y , 2)    ,0.5d);
        final double c = Math.pow(    Math.pow( pa.x - pb.x , 2) +  Math.pow( pa.y - pb.y , 2)    ,0.5d);

//        double angleA = Math.acos(  ( Math.pow(b, 2) + Math.pow(c, 2) - Math.pow(a, 2) )/(2*b*c) );
        double angleB = Math.acos(  ( Math.pow(a, 2) + Math.pow(c, 2) - Math.pow(b, 2) )/(2*a*c) );
//        double angleC = Math.acos(  ( Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2) )/(2*a*b) );

        if(mouseX < CENTER[1]  ){
            angleB = 2* Math.PI - angleB;
        }

        return angleB;
    }

    @Override
    public boolean contains(final int x, final int y) {
        return outerCercle.contains(x,y);
    }


    private final MouseListener mouseListener = new MouseListener() {

        @Override
        public void mouseClicked(final MouseEvent e) {
            final Point mouse = e.getPoint();

            if(resetCercle.contains(mouse)){
                mapRotate(0);
            }
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            final Point mouse = e.getPoint();

            if(resetCercle.contains(mouse)) return;

            final double tx =   mouse.x - CENTER[0] ;
            final double ty =   mouse.y - CENTER[1] ;
            final double distance = Math.hypot(tx, ty);

            if(distance >= INNER_RADIUS &&
                    distance <= OUTER_RADIUS){
                actionFlag = 1;

                mapRotate(calculateAngle(mouse.x, mouse.y));

            } else if (distance < INNER_RADIUS){

                actionFlag = -1;

                if(Math.abs(tx)!=Math.abs(ty)) {
                    if (tx>ty && -tx>ty) {
                        moveUp();
                    } else if (tx<ty && -tx<ty) {
                        moveDown();
                    } else if (tx<ty && -tx>ty) {
                        moveLeft();
                    } else if (tx>ty && -tx<ty) {
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
                final int oldOver = overButton;

                final double tx =   mouse.x - CENTER[0];
                final double ty =   mouse.y - CENTER[1];

                if(resetCercle.contains(mouse)){
                    overButton = 4;
                } else if (tx>ty && -tx>ty) {
                    overButton = 0;
                } else if (tx<ty && -tx<ty) {
                    overButton = 2;
                } else if (tx<ty && -tx>ty) {
                    overButton = 3;
                } else if (tx>ty && -tx<ty) {
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

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(buffer.getWidth(),buffer.getHeight());
    }

    @Override
    public Dimension getSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getSize(final Dimension rv) {
        if(rv != null){
            rv.height = buffer.getHeight();
            rv.width = buffer.getWidth();
            return rv;
        }else{
            return getSize();
        }
    }

    @Override
    public int getWidth() {
        return this.getSize().width;
    }

    @Override
    public int getHeight() {
        return this.getSize().height;
    }
}
