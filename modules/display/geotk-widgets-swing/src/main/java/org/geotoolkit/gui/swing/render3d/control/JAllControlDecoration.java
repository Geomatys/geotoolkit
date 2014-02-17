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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.logging.Level;
import org.geotoolkit.display3d.Map3D;
import org.geotoolkit.display3d.scene.camera.TrackBallCamera;

/**
 *
 * @author Thomas Rouby (Geomatys)
 */
public class JAllControlDecoration extends JComponent implements PropertyChangeListener {

    private static final int ACTION_UNKNOW = -1;
    private static final int ACTION_RESET = 0;
    private static final int ACTION_MOVE_LEFT = 1;
    private static final int ACTION_MOVE_BOTTOM = 2;
    private static final int ACTION_MOVE_RIGHT = 3;
    private static final int ACTION_MOVE_TOP = 4;
    private static final int ACTION_ROTATE_LEFT = 5;
    private static final int ACTION_ROTATE_BOTTOM = 6;
    private static final int ACTION_ROTATE_RIGHT = 7;
    private static final int ACTION_ROTATE_TOP = 8;
    private static final int ACTION_ROTATION = 9;

    private static final int[] MARGIN = new int[]{5,5,5,5};
    private static final int RADIUS_RESET_BUTTON = 4;
    private static final int RADIUS_CERCLE_INNER = 16 + RADIUS_RESET_BUTTON;
    private static final int RADIUS_CERCLE_OUTER = 18 + RADIUS_CERCLE_INNER;
    private static final int RADIUS_CERCLE_ROTATION = 14 + RADIUS_CERCLE_OUTER;
    private static final int[] CENTER = new int[]{MARGIN[0] + RADIUS_CERCLE_ROTATION, MARGIN[3] + RADIUS_CERCLE_ROTATION};
    private static final int[] SIZE = new int[]{MARGIN[0] + RADIUS_CERCLE_ROTATION*2 + MARGIN[2], MARGIN[3] + RADIUS_CERCLE_ROTATION*2 + MARGIN[1]};

    private final BufferedImage buffer = new BufferedImage(SIZE[0], SIZE[1], BufferedImage.TYPE_INT_ARGB);

    private final Shape content = new java.awt.geom.Ellipse2D.Float(CENTER[0]-RADIUS_CERCLE_ROTATION, CENTER[1]-RADIUS_CERCLE_ROTATION, RADIUS_CERCLE_ROTATION*2, RADIUS_CERCLE_ROTATION*2);

    private final Color white;
    private final Color gray;
    private final Color black;
    private final Color red;
    private final Color over;

    private final BufferedImage[] ARROWS_MOVE;
    private final BufferedImage[] ARROWS_ROTATION;

    private boolean mustUpdate = true;
    private double rotate = 0.0; // Angle (radian) of the rotation
    private final TrackBallCamera camera;

    /**
     * -1 = no action
     * 9 = rotation
     */
    private int actionFlag = ACTION_UNKNOW;

    /**
     * -1 = no button
     * 0 = reset button
     * 1,2,3,4 = left,bottom,right,top button on inner cercle
     * 5,6,7,8 = left,bottom,right,top button on outer cercle
     * 9 = rotate button
     */
    private int overButton = ACTION_UNKNOW;

    public JAllControlDecoration(TrackBallCamera camera){

        this.camera = camera;
        this.camera.addPropertyChangeListener(this);

        white = Color.WHITE;
        gray = Color.LIGHT_GRAY;
        black = Color.GRAY;
        red = Color.RED;
        over = Color.DARK_GRAY;

        addMouseListener(mouseListener);
        addMouseMotionListener(mouseMotionListener);
        setOpaque(false);

        ARROWS_MOVE = new BufferedImage[4];
        ARROWS_ROTATION = new BufferedImage[4];
        try{
            ARROWS_MOVE[0] = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("img/icon/arrow_left.png"));
            ARROWS_MOVE[1] = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("img/icon/arrow_bottom.png"));
            ARROWS_MOVE[2] = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("img/icon/arrow_right.png"));
            ARROWS_MOVE[3] = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("img/icon/arrow_top.png"));

            ARROWS_ROTATION[0] = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("img/icon/rotate_left.png"));
            ARROWS_ROTATION[1] = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("img/icon/rotate_bottom.png"));
            ARROWS_ROTATION[2] = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("img/icon/rotate_right.png"));
            ARROWS_ROTATION[3] = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("img/icon/rotate_top.png"));
        } catch (IOException ex) {
            Map3D.LOGGER.log(Level.WARNING, "icon not found", ex);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //event from the camera
        this.setRotate(Math.toRadians(camera.getRotateZ()));
    }

    public void rotateMap(double rotate){
        camera.setRotateZ((float)Math.toDegrees(rotate));
    }

    public void setRotate(double rotation){
        if (this.rotate != rotation) {
            this.rotate = rotation;
            this.mustUpdate = true;
            this.repaint(this.content.getBounds());
        }
    }

    public double getRotate(){
        return this.rotate;
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

        if(mouseX > CENTER[1]  ){
            angleB = 2* Math.PI - angleB;
        }

        return angleB;
    }

    public void actionPerformed(int action, double value){

        switch (action){
            case ACTION_RESET :
                rotateMap(0.0);
                break;
            case ACTION_MOVE_LEFT :
                camera.moveLeft((float)(value*camera.getViewScale(this.camera.getLength())));
                camera.updateCameraElevation();
                break;
            case ACTION_MOVE_BOTTOM :
                camera.moveBack((float)(value*camera.getViewScale(this.camera.getLength())));
                camera.updateCameraElevation();
                break;
            case ACTION_MOVE_RIGHT :
                camera.moveRight((float)(value*camera.getViewScale(this.camera.getLength())));
                camera.updateCameraElevation();
                break;
            case ACTION_MOVE_TOP :
                camera.moveFront((float)(value*camera.getViewScale(this.camera.getLength())));
                camera.updateCameraElevation();
                break;
            case ACTION_ROTATE_LEFT :
                rotateMap(this.rotate + Math.toRadians(value));
                break;
            case ACTION_ROTATE_BOTTOM :
                camera.rotateDown((float)value);
                break;
            case ACTION_ROTATE_RIGHT :
                rotateMap(this.rotate - Math.toRadians(value));
                break;
            case ACTION_ROTATE_TOP :
                camera.rotateUp((float)value);
                break;
            case ACTION_ROTATION :
                rotateMap(value);
                break;
            case ACTION_UNKNOW :
            default :
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        final Rectangle clip = g2d.getClipBounds();

        if(!clip.intersects(0,0,buffer.getWidth(),buffer.getHeight())) return;

        if(mustUpdate){
            final Graphics2D g2 = buffer.createGraphics();
            final Composite origComposite = g2.getComposite();
            final Composite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);

            g2.setStroke(new BasicStroke(1));
            g2.setFont(g2d.getFont().deriveFont(Font.BOLD));
            g2.setBackground(new Color(0f,0f,0f,0f));
            g2.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
            g2.setRenderingHints(g2d.getRenderingHints());
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int justRotationRadius = RADIUS_CERCLE_ROTATION - RADIUS_CERCLE_OUTER;

            // DRAW ROTATION CERCLE
            g2.setPaint(this.black);
            g2.drawOval(CENTER[0]-RADIUS_CERCLE_ROTATION+justRotationRadius/2, CENTER[1]-RADIUS_CERCLE_ROTATION+justRotationRadius/2, RADIUS_CERCLE_ROTATION*2-justRotationRadius-1, RADIUS_CERCLE_ROTATION*2-justRotationRadius-1);

            // DRAW OUTER CERCLE
            g2.setPaint(this.gray);
            g2.fillOval(CENTER[0]-RADIUS_CERCLE_OUTER, CENTER[1]-RADIUS_CERCLE_OUTER, RADIUS_CERCLE_OUTER*2, RADIUS_CERCLE_OUTER*2);

            // LEFT ROTATION ARROW
            g2.setComposite((overButton == ACTION_ROTATE_LEFT)?alphaComposite:origComposite);
            g2.drawImage(this.ARROWS_ROTATION[0], CENTER[0]-RADIUS_CERCLE_OUTER+2, CENTER[1]-this.ARROWS_ROTATION[0].getHeight()/2, null);
            // BOTTOM ROTATION ARROW
            g2.setComposite((overButton == ACTION_ROTATE_BOTTOM)?alphaComposite:origComposite);
            g2.drawImage(this.ARROWS_ROTATION[1], CENTER[0]-this.ARROWS_ROTATION[1].getWidth()/2, CENTER[1]+RADIUS_CERCLE_OUTER-2-this.ARROWS_ROTATION[1].getHeight(), null);
            // RIGHT ROTATION ARROW
            g2.setComposite((overButton == ACTION_ROTATE_RIGHT)?alphaComposite:origComposite);
            g2.drawImage(this.ARROWS_ROTATION[2], CENTER[0]+RADIUS_CERCLE_OUTER-2-this.ARROWS_ROTATION[2].getWidth(), CENTER[1]-this.ARROWS_ROTATION[2].getHeight()/2, null);
            // TOP ROTATION ARROW
            g2.setComposite((overButton == ACTION_ROTATE_TOP)?alphaComposite:origComposite);
            g2.drawImage(this.ARROWS_ROTATION[3], CENTER[0]-this.ARROWS_ROTATION[3].getWidth()/2, CENTER[1]-RADIUS_CERCLE_OUTER+2, null);

            // DRAW INNER CERCLE
            g2.setComposite(origComposite);
            g2.setPaint(this.white);
            g2.fillOval(CENTER[0]-RADIUS_CERCLE_INNER, CENTER[1]-RADIUS_CERCLE_INNER, RADIUS_CERCLE_INNER*2, RADIUS_CERCLE_INNER*2);

            // LEFT ROTATION ARROW
            g2.setComposite((overButton == ACTION_MOVE_LEFT)?alphaComposite:origComposite);
            g2.drawImage(this.ARROWS_MOVE[0], CENTER[0]-RADIUS_CERCLE_INNER+2, CENTER[1]-this.ARROWS_MOVE[0].getHeight()/2, null);
            // BOTTOM ROTATION ARROW
            g2.setComposite((overButton == ACTION_MOVE_BOTTOM)?alphaComposite:origComposite);
            g2.drawImage(this.ARROWS_MOVE[1], CENTER[0]-this.ARROWS_MOVE[1].getWidth()/2, CENTER[1]+RADIUS_CERCLE_INNER-2-this.ARROWS_MOVE[1].getHeight(), null);
            // RIGHT ROTATION ARROW
            g2.setComposite((overButton == ACTION_MOVE_RIGHT)?alphaComposite:origComposite);
            g2.drawImage(this.ARROWS_MOVE[2], CENTER[0]+RADIUS_CERCLE_INNER-2-this.ARROWS_MOVE[2].getWidth(), CENTER[1]-this.ARROWS_MOVE[2].getHeight()/2, null);
            // TOP ROTATION ARROW
            g2.setComposite((overButton == ACTION_MOVE_TOP)?alphaComposite:origComposite);
            g2.drawImage(this.ARROWS_MOVE[3], CENTER[0]-this.ARROWS_MOVE[3].getWidth()/2, CENTER[1]-RADIUS_CERCLE_INNER+2, null);

            // DRAW RESET CERCLE
            g2.setComposite((overButton == ACTION_RESET)?alphaComposite:origComposite);
            g2.setPaint(this.black);
            g2.fillOval(CENTER[0]-RADIUS_RESET_BUTTON, CENTER[1]-RADIUS_RESET_BUTTON, RADIUS_RESET_BUTTON*2, RADIUS_RESET_BUTTON*2);

            // DRAW ROTATION BUTTON
            g2.setComposite(origComposite);
            g2.rotate(-this.rotate, CENTER[0], CENTER[1]);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            final int posX = CENTER[0] - justRotationRadius/4;
            final int posY = CENTER[1] - RADIUS_CERCLE_ROTATION + justRotationRadius/4 ;
            g2.setColor(red);
            g2.fillOval(posX, posY, justRotationRadius / 2, justRotationRadius / 2);
            g2.rotate(this.rotate, CENTER[0], CENTER[1]);

            mustUpdate = false;
        }

        g2d.drawImage(buffer, 0, 0, this);

        g2d.dispose();
    }

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

    @Override
    public boolean contains(final int x, final int y) {
        return content.contains(x, y);
    }


    private final MouseListener mouseListener = new MouseListener() {

        boolean mousePressed = false;

        @Override
        public void mouseClicked(final MouseEvent e) {
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            final Point mouse = e.getPoint();
            if(!content.contains(mouse)) return;

            Thread actionThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    mousePressed = true;
                    while (mousePressed) {
                        final double tx =   mouse.x - CENTER[0] ;
                        final double ty =   mouse.y - CENTER[1] ;
                        final double distance = Math.hypot(tx, ty);

                        if (distance <= RADIUS_RESET_BUTTON){
                            actionPerformed(ACTION_RESET, 0.0);
                            mousePressed = false;
                        } else if (distance <= RADIUS_CERCLE_INNER){
                            if (tx>ty && -tx>ty) {
                                actionPerformed(ACTION_MOVE_TOP, 5.0);
                            } else if (tx<ty && -tx<ty) {
                                actionPerformed(ACTION_MOVE_BOTTOM, 5.0);
                            } else if (tx<ty && -tx>ty) {
                                actionPerformed(ACTION_MOVE_LEFT, 5.0);
                            } else if (tx>ty && -tx<ty) {
                                actionPerformed(ACTION_MOVE_RIGHT, 5.0);
                            }
                        } else if (distance <= RADIUS_CERCLE_OUTER){
                            if (tx>ty && -tx>ty) {
                                actionPerformed(ACTION_ROTATE_TOP, 1.0);
                            } else if (tx<ty && -tx<ty) {
                                actionPerformed(ACTION_ROTATE_BOTTOM, 1.0);
                            } else if (tx<ty && -tx>ty) {
                                actionPerformed(ACTION_ROTATE_LEFT, 1.0);
                            } else if (tx>ty && -tx<ty) {
                                actionPerformed(ACTION_ROTATE_RIGHT, 1.0);
                            }
                        } else if (distance <= RADIUS_CERCLE_ROTATION){
                            actionFlag = ACTION_ROTATION;
                            actionPerformed(ACTION_ROTATION, calculateAngle(mouse.x, mouse.y));
                            mousePressed = false;
                        } else {
                            actionFlag = ACTION_UNKNOW;
                            mousePressed = false;
                        }
                        if (mousePressed){
                            try {
                                Thread.currentThread().sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            });
            actionThread.setDaemon(true);
            actionThread.start();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mousePressed = false;
            actionFlag = ACTION_UNKNOW;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
            mousePressed = false;
            if(overButton != ACTION_UNKNOW){
                overButton = ACTION_UNKNOW;
                mustUpdate = true;
                repaint(content.getBounds());
            }
        }
    };

    private final MouseMotionListener mouseMotionListener = new MouseMotionListener() {

        @Override
        public void mouseDragged(final MouseEvent e) {
            if (actionFlag == ACTION_ROTATION) {
                actionPerformed(ACTION_ROTATION, calculateAngle(e.getX(),e.getY()));
            }
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            final Point mouse = e.getPoint();

            //we repaint inner buttons
            if(content.contains(mouse)){
                final int oldOver = overButton;

                final double tx =   mouse.x - CENTER[0];
                final double ty =   mouse.y - CENTER[1];

                final double distance = Math.hypot(tx, ty);

                if (distance <= RADIUS_RESET_BUTTON){
                    overButton = ACTION_RESET;
                } else if (distance <= RADIUS_CERCLE_INNER){
                    if (tx>ty && -tx>ty) {
                        overButton = ACTION_MOVE_TOP;
                    } else if (tx<ty && -tx<ty) {
                        overButton = ACTION_MOVE_BOTTOM;
                    } else if (tx<ty && -tx>ty) {
                        overButton = ACTION_MOVE_LEFT;
                    } else if (tx>ty && -tx<ty) {
                        overButton = ACTION_MOVE_RIGHT;
                    }
                } else if (distance <= RADIUS_CERCLE_OUTER){
                    if (tx>ty && -tx>ty) {
                        overButton = ACTION_ROTATE_TOP;
                    } else if (tx<ty && -tx<ty) {
                        overButton = ACTION_ROTATE_BOTTOM;
                    } else if (tx<ty && -tx>ty) {
                        overButton = ACTION_ROTATE_LEFT;
                    } else if (tx>ty && -tx<ty) {
                        overButton = ACTION_ROTATE_RIGHT;
                    }
                } else {
                    overButton = ACTION_UNKNOW;
                }

                if(oldOver != overButton){
                    mustUpdate = true;
                    repaint(content.getBounds());
                }

            } else if(overButton != ACTION_UNKNOW) {
                overButton = ACTION_UNKNOW;
                mustUpdate = true;
                repaint(content.getBounds());
            }
        }
    };

}
