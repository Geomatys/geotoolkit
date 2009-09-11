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

package org.geotoolkit.display2d.ext.isoline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.MapLayer;

import org.opengis.display.canvas.Canvas;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class IsolineGraphicBuilder implements GraphicBuilder<GraphicJ2D>{

    public static final String STEP_PROPERTY = "step";

    private final ValueExtractor extractor;

    public IsolineGraphicBuilder(ValueExtractor extractor){
        this.extractor = extractor;
    }

    @Override
    public Collection<GraphicJ2D> createGraphics(MapLayer layer, Canvas canvas) {

        if(canvas instanceof ReferencedCanvas2D && layer instanceof FeatureMapLayer){
            final ReferencedCanvas2D refCanvas = (ReferencedCanvas2D) canvas;
            final Collection<GraphicJ2D> graphics = new ArrayList<GraphicJ2D>();
            graphics.add(new IsolineGraphicJ2D(refCanvas,(FeatureMapLayer)layer, extractor));
            return graphics;
        }

        return Collections.EMPTY_LIST;
    }

    @Override
    public Class<GraphicJ2D> getGraphicType() {
        return GraphicJ2D.class;
    }

}
