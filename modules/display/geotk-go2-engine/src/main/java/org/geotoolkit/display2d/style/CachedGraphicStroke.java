/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.display2d.style;

import org.opengis.feature.Feature;
import org.opengis.style.GraphicStroke;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CachedGraphicStroke extends CachedGraphic<GraphicStroke>{

    protected CachedGraphicStroke(final GraphicStroke stroke){
        super(stroke);
    }

    public float getGap(final Feature feature){
        return styleElement.getGap().evaluate(feature,Float.class);
    }

    public float getInitialGap(final Feature feature){
        return styleElement.getInitialGap().evaluate(feature,Float.class);
    }


}
