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
package org.geotoolkit.display3d;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import javax.vecmath.Vector3d;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3f;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display.canvas.AbstractCanvas;
import org.geotoolkit.display.primitive.SceneNode;
import org.geotoolkit.display3d.phase.Phase;
import org.geotoolkit.display3d.scene.ContextContainer3D;
import org.geotoolkit.display3d.scene.Scene3D;
import org.geotoolkit.display3d.scene.SceneNode3D;
import org.geotoolkit.display3d.scene.Updater;
import org.geotoolkit.display3d.scene.camera.TrackBallCamera;
import org.geotoolkit.display3d.scene.light.Light;
import org.geotoolkit.display3d.scene.light.LightsManager;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.math.XMath;

/**
 * Custom GLEventListener
 *
 * @author Thomas Rouby (Geomatys)
 */
public class Map3D extends AbstractCanvas<Scene3D> implements GLEventListener {

    public static final Logger LOGGER = Logging.getLogger("org.geotoolkit.display3d");

    /**
     * Keep it, we might need it for dispose.
     */
    private GLAutoDrawable drawable;

    private final List<Phase> phases = new ArrayList<>();
    private final TrackBallCamera camera;
    private final LightsManager lights = new LightsManager();

    private float rotation = 0.0f;
    private float azimut = 45.0f;

    //COUNTER to reduce number of OpenGL actions made.
    private int action = 0;
    private final int MAX_ACTION = 2;

    public Map3D(){
        super(new Hints());
        setContainer(new ContextContainer3D(this));
        this.camera = new TrackBallCamera(this, 0.0f, 0.0f, 1000.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        final Light light0 = lights.addLight(0, null);
        if (light0 != null) {
            light0.setPosition(new Vector3f(0.0f, 0.0f, 1.0f), true);
            light0.setAmbient(new float[]{0.2f, 0.2f, 0.2f, 1.0f});
            light0.setDiffuse(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
            light0.setSpecular(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        }
    }

    public boolean doAction(){
        if (action<MAX_ACTION){
            return true;
        }
        return false;
    }

    public void addAction(){
        action++;
    }

    /**
     * TODO, should not be here.
     */
    public GLAutoDrawable getDrawable() {
        return drawable;
    }

    public LightsManager getLightManager() {
        return this.lights;
    }

    @Override
    public void init(GLAutoDrawable gLDrawable){
        drawable = gLDrawable;

        try {
            final GL2 gl2 = gLDrawable.getGL().getGL2();

            gl2.glEnable(GL.GL_DEPTH_TEST);
            gl2.glEnable(GL.GL_CULL_FACE);
            gl2.glEnable(GL2.GL_NORMALIZE);

            gl2.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);

            gl2.glDepthFunc(GL.GL_LEQUAL);
            gl2.glClearDepth(Float.MAX_VALUE);

            gl2.glCullFace(GL.GL_BACK);

        } catch (Exception ex) {
            getLogger().log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    @Override
    public void display(GLAutoDrawable glDrawable) {
        this.action = 0;

        final GL2 gl2 = glDrawable.getGL().getGL2();

        //update camera
        gl2.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl2.glLoadIdentity();
        final GLU glu = new GLU();
        final Vector3f eye = camera.getEye();
        final Vector3f center = camera.getCenter();
        glu.gluPerspective(camera.getFovy(), camera.getAspect(),
                camera.getNear(), (camera.getLength() + 6378000.0f));
        glu.gluLookAt(eye.x, eye.y, eye.z,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f);
        gl2.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl2.glLoadIdentity();

        //clean the canvas
        gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // Move the scene (camera management)
        gl2.glRotated((double) camera.getRotateX(), 1.0, 0.0, 0.0);
        gl2.glRotated((double) camera.getRotateZ(), 0.0, 0.0, 1.0);

        final Vector3d scale3d = camera.getScale3d();
        gl2.glScaled(scale3d.x / 100.0, scale3d.y / 100.0, scale3d.z / 100.0);
        gl2.glTranslatef(-center.x, -center.y, -center.z);

        //loop on all scene nodes for rendering
        final SceneNode root = getContainer().getRoot();
        display(root, glDrawable);

        // light management
        if (!lights.isEmpty() && lights.isEnable()) {

            gl2.glEnable(GL2.GL_LIGHTING);

            gl2.glPushMatrix();
                gl2.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
                gl2.glRotatef(90.0f-azimut, 1.0f, 0.0f, 0.0f);

                lights.updateMaterial(glDrawable);
                lights.updateLight(glDrawable);
            gl2.glPopMatrix();

        } else {
            gl2.glDisable(GL2.GL_LIGHTING);
        }

        gl2.glFlush();

        // update other phases
        for (Phase phase : phases.toArray(new Phase[0])) {
            phase.update(gl2);
        }

    }

    /**
     * Loop on scene nodes and render.
     */
    private void display(SceneNode node, GLAutoDrawable glDrawable){
        if(node instanceof SceneNode3D){
            final SceneNode3D n3d = (SceneNode3D) node;
            n3d.init(glDrawable); //won't do anythis if already initialized
            final Updater updater = n3d.getUpdater();
            if(updater != null) updater.update(glDrawable);
            n3d.draw(glDrawable);
        }

        //explore childrens, defensive copy to reduce concurency modifications
        for(Object child : node.getChildren().toArray()){
            display((SceneNode)child, glDrawable);
        }
    }

    @Override
    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
        try {
            if (height <= 0) {
                height = 1;
            }
            this.getCamera().setWidth(width);
            this.getCamera().setHeight(height);
        } catch (Exception ex) {
            getLogger().log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    @Override
    public synchronized void dispose() {
        dispose(drawable);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        getContainer().dispose();
    }

    public int addPhase(Phase phase) {
        if (phase == null){
            return -1;
        }
        int index = phases.indexOf(phase);
        if(index < 0){
            phase.setMap(this);
            index = this.phases.size();
            this.phases.add(phase);
        }
        return index;
    }

    public boolean removePhase(Phase phase) {
        if (phase == null) {
            return false;
        }
        phase.setMap(null);
        return this.phases.remove(phase);
    }

    public float getLightRotation() {
        return rotation;
    }

    public void setLightRotation(float rotation) {
        if (rotation < 0.0f) {
            this.rotation = 360.0f - rotation%360.0f;
        } else {
            this.rotation = rotation%360.0f;
        }
    }

    public float getLightAzimut() {
        return azimut;
    }

    public void setLightAzimut(float azimut) {
        this.azimut = XMath.clamp(azimut, 0.0f, 90.0f);
    }

    public TrackBallCamera getCamera() {
        return this.camera;
    }

    public double getDistForScale(double scale) {
        final double a = scale*camera.getWidth()*(camera.getScale3d().x/100.0);
        final double fov = camera.getFovy()*camera.getAspect();
        final double dist = (a/2.0) / Math.toRadians(fov / 2.0);
        return dist;
    }

}
