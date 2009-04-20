
package org.geotools.display3d.container;

import java.util.ArrayList;
import java.util.Collection;
import org.geotools.display3d.canvas.A3DCanvas;
import org.geotools.display3d.primitive.A3DGraphic;
import org.geotools.map.CoverageMapLayer;
import org.geotools.map.DynamicMapLayer;
import org.geotools.map.FeatureMapLayer;
import org.geotools.map.GraphicBuilder;
import org.geotools.map.MapLayer;
import org.opengis.display.canvas.Canvas;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class A3DGraphicBuilder implements GraphicBuilder<A3DGraphic>{

    @Override
    public Collection<A3DGraphic> createGraphics(MapLayer layer, Canvas canvas) {

        if(canvas == null || !(canvas instanceof A3DCanvas)){
            throw new IllegalArgumentException("Canvas must be an A3DCanvas");
        }

        final A3DCanvas a3dcanvas = (A3DCanvas) canvas;

        final Collection<A3DGraphic> graphics = new ArrayList<A3DGraphic>();

        if(layer instanceof FeatureMapLayer){
            graphics.add(new FeatureLayerNode(a3dcanvas, (FeatureMapLayer)layer));
        }else if(layer instanceof CoverageMapLayer){
            //TODO, not handle yet
        }else if(layer instanceof DynamicMapLayer){
            //TODO not handle yet
        }

        return graphics;
    }

    @Override
    public Class<A3DGraphic> getGraphicType() {
        return A3DGraphic.class;
    }

}
