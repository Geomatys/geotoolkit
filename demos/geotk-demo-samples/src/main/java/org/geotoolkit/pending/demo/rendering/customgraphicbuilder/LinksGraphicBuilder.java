

package org.geotoolkit.pending.demo.rendering.customgraphicbuilder;

import java.awt.Image;
import java.util.Collection;
import java.util.Collections;

import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;

import org.opengis.display.canvas.Canvas;

public class LinksGraphicBuilder implements GraphicBuilder<GraphicJ2D>{

    @Override
    public Collection<GraphicJ2D> createGraphics(MapLayer layer, Canvas canvas) {

        if(layer instanceof FeatureMapLayer && canvas instanceof J2DCanvas){
            final J2DCanvas rc = (J2DCanvas) canvas;
            final FeatureMapLayer fl = (FeatureMapLayer) layer;
            return Collections.singletonList((GraphicJ2D)new LinksGraphic(rc, fl));
        }

        return Collections.emptyList();
    }

    @Override
    public Class<GraphicJ2D> getGraphicType() {
        return GraphicJ2D.class;
    }

    @Override
    public Image getLegend(MapLayer layer) throws PortrayalException {
        return null;
    }

}
