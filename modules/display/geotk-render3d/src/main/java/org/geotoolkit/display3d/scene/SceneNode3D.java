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
package org.geotoolkit.display3d.scene;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLException;
import org.geotoolkit.display.primitive.SceneNode;
import org.geotoolkit.display3d.Map3D;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SceneNode3D extends SceneNode{

    private boolean initialized = false;
    private Updater updater = null;

    public SceneNode3D(Map3D map) {
        super(map);
    }

    @Override
    public Map3D getCanvas() {
        return (Map3D)super.getCanvas();
    }

    public Updater getUpdater() {
        return updater;
    }

    public void setUpdater(Updater updater) {
        this.updater = updater;
    }

    public synchronized final boolean isInitialized() {
        return initialized;
    }

    public final synchronized void init(GLAutoDrawable glDrawable) throws GLException{
        if(initialized) return;
        initInternal(glDrawable);
        initialized = true;
    }

    protected void initInternal(GLAutoDrawable glDrawable) throws GLException{}

    /**
     * Check that the node has been initialized properly before drawing.
     *
     * @param glDrawable
     * @throws GLException
     */
    public final void draw(GLAutoDrawable glDrawable) throws GLException{
        //ensure object has been initialized
        if(!isInitialized()){
                init(glDrawable);
        }
        drawInternal(glDrawable);
    }

    protected void drawInternal(GLAutoDrawable glDrawable) throws GLException{}

    public void dispose(GLAutoDrawable glDrawable) {
        for(SceneNode sn : getChildren()){
            if(sn instanceof SceneNode3D){
                ((SceneNode3D)sn).dispose(glDrawable);
            }
        }
    }

}
