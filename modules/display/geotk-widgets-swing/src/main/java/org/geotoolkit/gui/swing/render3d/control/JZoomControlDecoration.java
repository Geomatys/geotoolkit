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

import org.geotoolkit.math.XMath;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.logging.Level;
import org.geotoolkit.display3d.Map3D;
import org.geotoolkit.display3d.scene.ContextContainer3D;
import org.geotoolkit.display3d.scene.Terrain;
import org.geotoolkit.display3d.scene.camera.TrackBallCamera;

/**
 *
 * @author Thomas Rouby (Geomatys)
 */
public class JZoomControlDecoration extends JComponent implements PropertyChangeListener {

    private static final int[] MARGIN = new int[]{50,5,50,5};
    private static final int ANCHOR_RADIUS = 6;
    private static final int PADDING = ANCHOR_RADIUS/2;
    private static final int BUTTON_HEIGHT = ANCHOR_RADIUS*2+2;
    private static final int HEIGHT = 80;
    private static final int[] CONTENT_SIZE = new int[]{ANCHOR_RADIUS*2+2, BUTTON_HEIGHT*2 + PADDING*2 + HEIGHT};

    private final Map3D map;
    private final TrackBallCamera camera;

    private final Color teinteDark;
    private final Color teinteLight;
    private final Color text1;
    private final Color text2;

    private Shape content;
    private Shape anchor;

    private BufferedImage imgPlus;
    private BufferedImage imgMinus;

    private double zoom = 0.0; // 1.0 = zoomMax (top bar) && 0.0 = zoomMin (bottom bar)

    private final BufferedImage buffer = new BufferedImage(MARGIN[0]+MARGIN[2]+CONTENT_SIZE[0], MARGIN[3]+MARGIN[1]+CONTENT_SIZE[1], BufferedImage.TYPE_INT_ARGB);
    private boolean mustUpdate = true;

    /**
     * -1 = no button
     * 0 = top
     * 1 = bottom
     * 2 = anchor
     */
    private short overButton = -1;

    /**
     * -1 = no action
     * 1 = zoomAction
     */
    private int actionFlag;


    public JZoomControlDecoration(Map3D map) {
        this.map = map;
        this.camera = map.getCamera();
        this.camera.addPropertyChangeListener(this);

        teinteDark = Color.LIGHT_GRAY;
        teinteLight = Color.WHITE;
        text1 = Color.GRAY;
        text2 = Color.DARK_GRAY;

        try {
            imgPlus = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("img/icon/plus.png"));
            imgMinus = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("img/icon/minus.png"));
        } catch (IOException ex) {
            Map3D.LOGGER.log(Level.WARNING, "icon not found", ex);
            imgPlus = null;
            imgMinus = null;
        }

        addMouseListener(mouseListener);
        addMouseMotionListener(mouseMotionListener);
        setOpaque(false);

        content = new java.awt.geom.Rectangle2D.Float(MARGIN[0], MARGIN[3], CONTENT_SIZE[0], CONTENT_SIZE[1]);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final Terrain terrain = ((ContextContainer3D)map.getContainer()).getTerrain();
        if (terrain!= null){
            float minLength = camera.getMinLength();
            if (minLength < 0.0f){
                minLength = (float) map.getDistForScale(terrain.getMinScale());
            }
            float maxLength = camera.getMaxLength();
            if (maxLength < 0.0f){
                maxLength = (float) map.getDistForScale(terrain.getMaxScale());
            }
            final float length = camera.getLength();

            setZoom((length-minLength) / (maxLength-minLength));
        }
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        final Rectangle clip = g2d.getClipBounds();

        if(!clip.intersects(0,0,buffer.getWidth(),buffer.getHeight())) return;

        if(mustUpdate){
            final Graphics2D g2 = buffer.createGraphics();

            g2.setStroke(new BasicStroke(1));
            g2.setFont(g2d.getFont().deriveFont(Font.BOLD));
            g2.setBackground(new Color(0f,0f,0f,0f));
            g2.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
            g2.setRenderingHints(g2d.getRenderingHints());
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setPaint(teinteLight);
            g2.fillRoundRect(MARGIN[0], MARGIN[3], CONTENT_SIZE[0], CONTENT_SIZE[1], CONTENT_SIZE[0]/2, CONTENT_SIZE[0]/2);
            g2.setPaint(teinteDark);
            g2.drawRoundRect(MARGIN[0], MARGIN[3], CONTENT_SIZE[0], CONTENT_SIZE[1], CONTENT_SIZE[0]/2, CONTENT_SIZE[0]/2);


            final int iconSize = BUTTON_HEIGHT-2;

            if (imgPlus != null){

                Composite defaultComp = g2.getComposite();

                if (overButton==0) {
                    Composite comp= AlphaComposite.getInstance(AlphaComposite.XOR, 0.5f);
                    g2.setComposite(comp);
                }
                g2.drawImage(imgPlus, MARGIN[0]+CONTENT_SIZE[0]/2-iconSize/2, MARGIN[3]+BUTTON_HEIGHT/2-iconSize/2, iconSize, iconSize, null);
                g2.setComposite(defaultComp);
            }
            if (imgMinus != null){

                Composite defaultComp = g2.getComposite();

                if (overButton==1) {
                    Composite comp= AlphaComposite.getInstance(AlphaComposite.XOR, 0.5f);
                    g2.setComposite(comp);
                }
                g2.drawImage(imgMinus, MARGIN[0]+CONTENT_SIZE[0]/2-iconSize/2, MARGIN[3]+CONTENT_SIZE[1]-BUTTON_HEIGHT/2-iconSize/2, iconSize, iconSize, null);
                g2.setComposite(defaultComp);
            }

            final int paddingTop = MARGIN[3]+BUTTON_HEIGHT;
            final int paddingHeight = CONTENT_SIZE[1]-BUTTON_HEIGHT*2;

            g2.setPaint(teinteLight);
            g2.fillRect(MARGIN[0], paddingTop, CONTENT_SIZE[0], paddingHeight);
            g2.setPaint(teinteDark);
            g2.drawRect(MARGIN[0], paddingTop, CONTENT_SIZE[0], paddingHeight);

            final int barTop = paddingTop+PADDING;
            final int barHeight = paddingHeight-PADDING*2;

            g2.setPaint(teinteDark);
            g2.drawArc(MARGIN[0], barTop, CONTENT_SIZE[0], CONTENT_SIZE[0], 0, 180);
            g2.setPaint(teinteDark);
            g2.drawArc(MARGIN[0], barTop+barHeight-CONTENT_SIZE[0], CONTENT_SIZE[0], CONTENT_SIZE[0], 0, -180);

            final int barInnerTop = barTop+1;
            final int barInnerHeight = barHeight-2;

            final int centerAnchorX = MARGIN[0]+1+ANCHOR_RADIUS;
            final int centerAnchorY = barInnerTop+ANCHOR_RADIUS + (int)((1.0-zoom)*(barInnerHeight-ANCHOR_RADIUS*2));

            final float[] dist = {0.3f, 1.0f};
            final Color[] colors;
            if (overButton == 2){
                colors = new Color[]{text1, text2};
            } else {
                colors = new Color[]{teinteLight, teinteDark};
            }
            final RadialGradientPaint radialPaint = new RadialGradientPaint(new Point2D.Float(centerAnchorX, centerAnchorY), ANCHOR_RADIUS, dist, colors);

            g2.setPaint(radialPaint);
            anchor = new Ellipse2D.Float(centerAnchorX-ANCHOR_RADIUS, centerAnchorY-ANCHOR_RADIUS, ANCHOR_RADIUS*2+1, ANCHOR_RADIUS*2+1);
            g2.fill(anchor);

            mustUpdate = false;
        }

        g2d.drawImage(buffer, 0, 0, this);

        g2d.dispose();
    }

    private void zoomMap(){
        final Terrain terrain = ((ContextContainer3D)map.getContainer()).getTerrain();
        float minLength = camera.getMinLength();
        if (minLength < 0.0f){
            minLength = (float) this.map.getDistForScale(terrain.getMinScale());
        }
        float maxLength = camera.getMaxLength();
        if (maxLength < 0.0f){
            maxLength = (float) this.map.getDistForScale(terrain.getMaxScale());
        }

        camera.zoomTo((float)(minLength + (maxLength-minLength)*zoom));
    }

    private void setZoom(double zoom){
        this.zoom = XMath.clamp(zoom, 0.0, 1.0);
        mustUpdate = true;
        repaint(content.getBounds());
    }

    private void zoomMore(){
        this.setZoom(XMath.clamp(zoom+0.001, 0.0, 1.0));
        zoomMap();
    }

    private void zoomLess(){
        this.setZoom(XMath.clamp(zoom-0.001, 0.0, 1.0));
        zoomMap();
    }

    private double calculateZoom(int X, int Y){
        int top = MARGIN[3]+BUTTON_HEIGHT+PADDING+1+ANCHOR_RADIUS;
        int bottom = top + HEIGHT - ANCHOR_RADIUS*2;
        int height = bottom - top;
        int h = XMath.clamp(Y, top, bottom) - top;

        return 1.0 - (double)(h)/(double)(height);
    }

    @Override
    public boolean contains(final int x, final int y) {
        return content.contains(x,y);
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
                        if (anchor.contains(mouse.x, mouse.y)){
                            actionFlag = 1;
                            mousePressed = false;
                        } else {
                            Shape buttonNorth = new Rectangle2D.Float(MARGIN[0], MARGIN[3], CONTENT_SIZE[0], BUTTON_HEIGHT);
                            Shape buttonSouth = new Rectangle2D.Float(MARGIN[0], MARGIN[3]+CONTENT_SIZE[1]-BUTTON_HEIGHT, CONTENT_SIZE[0], BUTTON_HEIGHT);

                            if (buttonNorth.contains(mouse.x, mouse.y)){
                                zoomMore();
                            } else if (buttonSouth.contains(mouse.x, mouse.y)){
                                zoomLess();
                            }
                        }

                        if (mousePressed){
                            try {
                                Thread.sleep(100);
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
            actionFlag = -1;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
            mousePressed = false;
            if(overButton !=-1){
                overButton = -1;
                mustUpdate = true;
                repaint(content.getBounds());
            }
        }
    };

    private final MouseMotionListener mouseMotionListener = new MouseMotionListener() {

        @Override
        public void mouseDragged(final MouseEvent e) {
            if (actionFlag == 1) {
                setZoom(calculateZoom(e.getX(),e.getY()));
                zoomMap();
            }
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            final Point mouse = e.getPoint();

            if(content.contains(mouse)){
                if (anchor.contains(mouse.x, mouse.y)){
                    overButton = 2;
                } else {
                    Shape buttonNorth = new Rectangle2D.Float(MARGIN[0], MARGIN[3], CONTENT_SIZE[0], BUTTON_HEIGHT);
                    Shape buttonSouth = new Rectangle2D.Float(MARGIN[0], MARGIN[3]+CONTENT_SIZE[1]-BUTTON_HEIGHT, CONTENT_SIZE[0], BUTTON_HEIGHT);

                    if (buttonNorth.contains(mouse.x, mouse.y)){
                        overButton = 0;
                    } else if (buttonSouth.contains(mouse.x, mouse.y)){
                        overButton = 1;
                    } else {
                        overButton = -1;
                    }
                }
                mustUpdate = true;
                repaint(content.getBounds());
            } else if (overButton != -1){
                overButton = -1;
                mustUpdate = true;
                repaint(content.getBounds());
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
