/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.display2d.container.stateless;

import java.beans.PropertyChangeEvent;
import java.util.EventObject;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.primitive.AbstractGraphicJ2D;
import org.geotoolkit.map.LayerListener;
import org.geotoolkit.map.MapLayer;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractLayerJ2D<T extends MapLayer> extends AbstractGraphicJ2D{

    private final LayerListener listener = new LayerListener() {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if(MapLayer.STYLE_PROPERTY.equals(event.getPropertyName())){
                //TODO should call a repaint only on this graphic
                getCanvas().getController().repaint();
            }
        }

        @Override
        public void styleChange(MapLayer source, EventObject event) {
            //TODO should call a repaint only on this graphic
            getCanvas().getController().repaint();
        }
    };

    protected final T layer;

    public AbstractLayerJ2D(final ReferencedCanvas2D canvas, final T layer){
        super(canvas, layer.getBounds().getCoordinateReferenceSystem());
        this.layer = layer;

        layer.addLayerListener(listener);

        try{
            setEnvelope(layer.getBounds());
        }catch(TransformException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public T getUserObject() {
        return layer;
    }

}
