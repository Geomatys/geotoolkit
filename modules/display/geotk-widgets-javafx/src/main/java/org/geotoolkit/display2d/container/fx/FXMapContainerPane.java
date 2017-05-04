/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.display2d.container.fx;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.render2d.FXMapDecoration;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXMapContainerPane extends Pane implements FXMapDecoration{

    private final FXRenderingContext context = new FXRenderingContext();
    private FXMap map;

    public FXMapContainerPane() {
        setCache(false);
    }

    @Override
    public void refresh() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void setMap2D(final FXMap map) {
        this.map = map;
        if(this.map!=null){
            map.getCanvas().addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if(J2DCanvas.TRANSFORM_KEY.equals(evt.getPropertyName())){
                        context.objToDisp.set(map.getCanvas().getObjectiveToDisplay());
                    }
                }
            });
            context.objToDisp.set(map.getCanvas().getObjectiveToDisplay());
        }
    }

    @Override
    public FXMap getMap2D() {
        return map;
    }

    @Override
    public Node getComponent() {
        return this;
    }

    public FXRenderingContext getContext() {
        return context;
    }

}
