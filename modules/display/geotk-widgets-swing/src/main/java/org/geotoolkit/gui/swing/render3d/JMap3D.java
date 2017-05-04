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
package org.geotoolkit.gui.swing.render3d;

import com.jogamp.opengl.FPSCounter;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import org.geotoolkit.gui.swing.util.BufferLayout;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.EventListener;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import org.geotoolkit.display3d.Map3D;
import org.geotoolkit.gui.swing.render3d.control.JAllControlDecoration;
import org.geotoolkit.gui.swing.render3d.control.JZoomControlDecoration;
import org.geotoolkit.gui.swing.render3d.control.MouseController;
import org.geotoolkit.display3d.scene.ContextContainer3D;
import org.geotoolkit.display3d.scene.Terrain;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Thomas Rouby (Geomatys)
 */
public class JMap3D extends JPanel {

    private static final int FPS = 35; // Animator's target frames per second

    //OpenGL objects
    private GLJPanel glCanvas;
    private final Map3D map3d = new Map3D();
    private FPSAnimator animator;

    private final List<JComponent> userDecorations = new ArrayList<>();
    private final JLayeredPane mapDecorationPane = new JLayeredPane();
    private final JLayeredPane userDecorationPane = new JLayeredPane();
    private final JLayeredPane mainDecorationPane = new JLayeredPane();

    // Constructor to create profile, caps, drawable, animator, and initialize Frame
    public JMap3D() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(150,150));
        setOpaque(false);
        setBackground(Color.WHITE);

        mapDecorationPane.setOpaque(false);
        userDecorationPane.setOpaque(false);
        mainDecorationPane.setOpaque(false);

        mapDecorationPane.setFocusable(false);
        userDecorationPane.setFocusable(false);
        mainDecorationPane.setFocusable(false);

        mapDecorationPane.setLayout(new BufferLayout());
        userDecorationPane.setLayout(new GridBagLayout());
        mainDecorationPane.setLayout(new BufferLayout());

        final JPanel userDecorationWest = new JPanel();
        userDecorationWest.setLayout(new BorderLayout());
        userDecorationWest.setOpaque(false);
        userDecorationWest.setFocusable(false);

        final JPanel userDecorationNorth = new JPanel();
        userDecorationNorth.setLayout(new BorderLayout());
        userDecorationNorth.setOpaque(false);
        userDecorationNorth.setFocusable(false);

        mainDecorationPane.add(userDecorationWest, Integer.valueOf(1));
        userDecorationWest.add(BorderLayout.WEST, userDecorationNorth);
        userDecorationNorth.add(BorderLayout.NORTH, userDecorationPane);

        mainDecorationPane.add(mapDecorationPane, Integer.valueOf(0));

        this.add(BorderLayout.CENTER, mainDecorationPane);


        //listen to frame change, we must rebuild the GL context
        //The graphic device has changed for opengl, it's an unstable state.
        addAncestorListener(new AncestorListener() {
            private Window lastWindow = null;

            @Override
            public void ancestorAdded(AncestorEvent event) {
                final Window window = SwingUtilities.getWindowAncestor(JMap3D.this);
                if(lastWindow==null){
                    lastWindow = window;
                }else if(lastWindow != window){
                    lastWindow = window;
                    buildGLPanel();
                }
            }
            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }
            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });

        buildGLPanel();

        //attach navigation controls
        final MouseController mouseControl = new MouseController(getMap3D().getCamera());
        addEventListener(mouseControl);
        addDecoration(new JAllControlDecoration(getMap3D().getCamera()));
        addDecoration(new JZoomControlDecoration(getMap3D()));
    }

    /**
     * Build the GLJPanel.
     * Dispose any previous one.
     */
    private synchronized void buildGLPanel(){

        Terrain terrain = null;

        //dispose previous panel
        if(glCanvas != null){
            mapDecorationPane.removeAll();
            mapDecorationPane.revalidate();
            mapDecorationPane.repaint();

            animator.stop();
            glCanvas.removeGLEventListener(map3d);
            terrain = ((ContextContainer3D)map3d.getContainer()).getTerrain();
            map3d.dispose();
            final GLContext context = glCanvas.getContext();
            if(context != null){
                context.release();
                context.destroy();
            }
            glCanvas.destroy();

        }

         // Get the default OpenGL profile that best reflect your running
        // platform.
        final GLProfile glp = GLProfile.getDefault();
        // Specifies a set of OpenGL capabilities, based on your profile.
        final GLCapabilities caps = new GLCapabilities(glp);
    caps.setDoubleBuffered(true);
        // Allocate a GLDrawable, based on your OpenGL capabilities.
        glCanvas = new GLJPanel(caps);
        glCanvas.setIgnoreRepaint(true);
        //canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        glCanvas.addGLEventListener(map3d);

        // Create a animator that drives canvas' display() at 60 fps.
        animator = new FPSAnimator(glCanvas, FPS){
//            @Override
//            protected void display() {
//                super.display();
//                if(map3d.getMonitor().stopRequested()){
//                    System.err.println("Stop display Map3D");
//                    this.stop();
//                }
//            }
        };
        animator.setUpdateFPSFrames(FPSCounter.DEFAULT_FRAMES_PER_INTERVAL, null);
        animator.start(); //active animator
        animator.pause(); //wait for wake up

        mapDecorationPane.add(glCanvas, Integer.valueOf(0));
        mapDecorationPane.revalidate();

        if(terrain != null){
            final Envelope env = terrain.getEnvelope();
//            final int numMosaic = terrain.getMaxTreeDepth();
            try {
                terrain = ((ContextContainer3D)map3d.getContainer()).createTerrain(env, 10);
                terrain.getUpdater().forceUpdate();
            } catch (TransformException | FactoryException ex) {
                Map3D.LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

    }

    /**
     * add a Decoration between the map and the information top decoration
     * @param deco : MapDecoration to add
     */
    public void addDecoration(final JComponent deco) {

        if (deco != null && !userDecorations.contains(deco)) {
            userDecorations.add(deco);

            final GridBagConstraints cst = new GridBagConstraints();
            cst.gridx = 0;
            cst.gridy = GridBagConstraints.RELATIVE;
            cst.gridwidth = deco.getWidth();
            cst.gridheight = deco.getHeight();
            cst.anchor = GridBagConstraints.NORTHWEST;

            userDecorationPane.add(deco, cst);
            userDecorationPane.setSize(userDecorationPane.getWidth() + deco.getWidth(), userDecorationPane.getHeight() + deco.getHeight());

            userDecorationPane.revalidate();
            userDecorationPane.repaint();
        }
    }

    /**
     * insert a MapDecoration at a specific index
     * @param index : index where to insert the decoration
     * @param deco : MapDecoration to add
     */
    public void addDecoration(final int index, final JComponent deco) {

        if (deco != null && !userDecorations.contains(deco)) {
            userDecorations.add(index, deco);

            final GridBagConstraints cst = new GridBagConstraints();
            cst.gridx = 0;
            cst.gridy = GridBagConstraints.RELATIVE;
            cst.gridwidth = deco.getWidth();
            cst.gridheight = deco.getHeight();
            cst.anchor = GridBagConstraints.NORTHWEST;

            userDecorationPane.add(deco, cst);
            userDecorationPane.setSize(userDecorationPane.getWidth() + deco.getWidth(), userDecorationPane.getHeight() + deco.getHeight());

            userDecorationPane.revalidate();
            userDecorationPane.repaint();
        }
    }

    /**
     * get the index of a MapDecoration
     * @param deco : MapDecoration to find
     * @return index of the MapDecoration
     * @throw ClassCastException or NullPointerException
     */
    public int getDecorationIndex(final JComponent deco) {
        return userDecorations.indexOf(deco);
    }

    /**
     * remove a MapDecoration
     * @param deco : MapDecoration to remove
     */
    public void removeDecoration(final JComponent deco) {
        if (deco != null && userDecorations.contains(deco)) {
            userDecorations.remove(deco);
            userDecorationPane.remove(deco);
            userDecorationPane.setSize(userDecorationPane.getWidth()-deco.getWidth(),userDecorationPane.getHeight()-deco.getHeight());

            userDecorationPane.revalidate();
            userDecorationPane.repaint();
        }
    }

    /**
     * Add an event listener to the map pane
     *
     * possible event listener are : MouseListener, MouseWheelListener, MouseMotionListener and KeyListener
     *
     * @param event en EventListener to add
     */
    public void addEventListener(EventListener event){
        if (event instanceof MouseListener) {
            mapDecorationPane.addMouseListener((MouseListener)event);
        }
        if (event instanceof MouseWheelListener){
            mapDecorationPane.addMouseWheelListener((MouseWheelListener)event);
        }
        if (event instanceof MouseMotionListener){
            mapDecorationPane.addMouseMotionListener((MouseMotionListener)event);
        }
        if (event instanceof KeyListener){
            mainDecorationPane.addKeyListener((KeyListener)event);
        }

    }

    public Map3D getMap3D(){
        return this.map3d;
    }

    /**
     * Start the GL animator.
     */
    public void startGLRendering(){
//        if (!this.animator.isAnimating()){
            this.animator.resume();
//        }
    }

    /**
     * Stop GL animator.
     */
    public void stopGLRendering(){
        if (this.animator.isAnimating()) {
            this.animator.pause();
        }
    }

}
