/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.go3.control.navigation;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.geotoolkit.display3d.canvas.A3DCanvas;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class CameraSpeedSlider extends JSlider{

    private A3DCanvas map = null;

    public CameraSpeedSlider() {
        super(1, 1000);

        addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent arg0) {
                if(map != null){
                    map.getController().setCameraSpeed(CameraSpeedSlider.this.getValue());
                }
            }
        });

    }

    public void setMap(final A3DCanvas map) {
        this.map = map;
    }

    public A3DCanvas getMap() {
        return map;
    }


}
