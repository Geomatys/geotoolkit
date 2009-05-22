

package org.geotoolkit.display3d.style.renderer;

import java.util.Collection;
import org.geotoolkit.display3d.primitive.A3DGraphic;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;
import org.opengis.display.canvas.Canvas;

/**
 *
 * @author eclesia
 */
public class ExtrudedRenderer3D implements GraphicBuilder<A3DGraphic>{

    @Override
    public Collection<A3DGraphic> createGraphics(MapLayer layer, Canvas canvas) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Class<A3DGraphic> getGraphicType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
