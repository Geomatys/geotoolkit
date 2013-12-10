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
package org.geotoolkit.display3d.control;

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
 * @author Thomas Rouby (Geomatys)
 */
public class JMoveControlDecoration extends JComponent implements PropertyChangeListener {

    private final TrackBallCamera camera;

    private static final int ARROW_SIZE = 8;
    private static final int RADIUS = 25;
    private static final int[] MARGIN = new int[]{17,5,17,5};    // left, bottom, right, top
    private static final int[] CENTER = new int[]{MARGIN[0]+RADIUS, MARGIN[3]+RADIUS}; // CENTER on X, on Y
    private static final int[] SIZE = new int[]{MARGIN[0]+MARGIN[2]+RADIUS*2, MARGIN[1]+MARGIN[3]+RADIUS*2}; // WIDTH, HEIGHT

    private final Color teinteDark;
    private final Color teinteLight;
    private final Color text1;
    private final Color text2;
    private final Shape cercle;
    private final Shape arrow;

    private final BufferedImage buffer = new BufferedImage(SIZE[0], SIZE[1], BufferedImage.TYPE_INT_ARGB);
    private boolean mustUpdate = true;

    /**
     * -1 = no button
     * 0 = top
     * 1 = right
     * 2 = down
     * 3 = left
     */
    private short overButton = -1;

    public JMoveControlDecoration(TrackBallCamera camera) {
        this.camera = camera;
        this.camera.addPropertyChangeListener(this);

        teinteDark = Color.LIGHT_GRAY;
        teinteLight = Color.WHITE;
        text1 = Color.DARK_GRAY;
        text2 = Color.GRAY;

        addMouseListener(mouseListener);
        addMouseMotionListener(mouseMotionListener);
        setOpaque(false);

        cercle = new java.awt.geom.Ellipse2D.Float(CENTER[0]-RADIUS, CENTER[1]-RADIUS, RADIUS*2, RADIUS*2);
        arrow = new Polygon(new int[]{0, ARROW_SIZE, ARROW_SIZE / 2 }, new int[]{ARROW_SIZE , ARROW_SIZE , 0}, 3);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //camera event
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

            final float[] dist = {0.5f, 1.0f};
            final Color[] colors = {teinteLight, teinteDark};
            final RadialGradientPaint paint = new RadialGradientPaint(new Point2D.Float(CENTER[0], CENTER[1]), RADIUS, dist, colors);
            g2.setPaint(paint);
            g2.fillOval(CENTER[0]-RADIUS, CENTER[1]-RADIUS, RADIUS*2, RADIUS*2);

            //draw arrows
            final int topX = CENTER[0] - ARROW_SIZE/2;
            final int topY = CENTER[1] - (int)(0.8*RADIUS);

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

            mustUpdate = false;
        }

        g2d.drawImage(buffer, 0, 0, this);

        g2d.dispose();

    }

    private void moveUp(){
        camera.moveFront(10.0f*(float)camera.getViewScale(camera.getLength()));
    }

    private void moveDown(){
        camera.moveBack(10.0f*(float)camera.getViewScale(camera.getLength()));
    }

    private void moveLeft(){
        camera.moveLeft(10.0f*(float)camera.getViewScale(camera.getLength()));
    }

    private void moveRight(){
        camera.moveRight(10.0f*(float)camera.getViewScale(camera.getLength()));
    }

    @Override
    public boolean contains(final int x, final int y) {
        return cercle.contains(x,y);
    }


    private final MouseListener mouseListener = new MouseListener() {

        @Override
        public void mouseClicked(final MouseEvent e) {
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            final Point mouse = e.getPoint();

            if(!cercle.contains(mouse)) return;

            final double tx =   mouse.x - CENTER[0] ;
            final double ty =   mouse.y - CENTER[1] ;

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
                camera.updateCameraElevation();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if(overButton !=-1){
                overButton = -1;
                mustUpdate = true;
                repaint(cercle.getBounds());
            }
        }
    };

    private final MouseMotionListener mouseMotionListener = new MouseMotionListener() {

        @Override
        public void mouseDragged(final MouseEvent e) {
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            final Point mouse = e.getPoint();

            if(cercle.contains(mouse)){
                final double tx =   mouse.x - CENTER[0] ;
                final double ty =   mouse.y - CENTER[1] ;

                final int oldOver = overButton;

                if (tx>ty && -tx>ty) {
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
                    repaint(cercle.getBounds());
                }

            } else if (overButton != -1){
                overButton = -1;
                mustUpdate = true;
                repaint(cercle.getBounds());
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
