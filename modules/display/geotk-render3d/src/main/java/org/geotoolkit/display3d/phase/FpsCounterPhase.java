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
package org.geotoolkit.display3d.phase;

import com.jogamp.opengl.GL;
import jogamp.opengl.FPSCounterImpl;

import org.geotoolkit.display3d.Map3D;

/**
 *
 * @author Thomas Rouby (Geomatys)
 */
public class FpsCounterPhase implements Phase {

    private final FPSCounterImpl fpsCounter = new FPSCounterImpl();
    private Map3D map;

    public FpsCounterPhase(){
        this.fpsCounter.setUpdateFPSFrames(35, null);
    }

    @Override
    public void setMap(Map3D map) {
        if (this.map != null) {
            this.map.removePhase(this);
        }
        this.map = map;
        this.map.addPhase(this);
    }

    @Override
    public Map3D getMap() {
        return this.map;
    }

    @Override
    public void update(GL gl) {
        this.fpsCounter.tickFPS();
    }

    public double getLastFPS(){
        return this.fpsCounter.getLastFPS();
    }

    public double getTotalFPS(){
        return this.fpsCounter.getTotalFPS();
    }
}
