/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
import org.opengis.style.Stroke;


/**
 * The cached stroke work for graphic strokes.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CachedStrokeGraphic extends CachedStroke{

    private final CachedGraphicStroke cachedGraphic;

    CachedStrokeGraphic(Stroke stroke){
        super(stroke);
        cachedGraphic = new CachedGraphicStroke(stroke.getGraphicStroke());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void evaluate(){
        if(!isNotEvaluated) return;

        this.isStatic = true;

        isNotEvaluated = false;
    }

    public CachedGraphicStroke getCachedGraphic(){
        return cachedGraphic;
    }

    public float getGap(Feature feature){
        return cachedGraphic.getGap(feature);
    }

    public float getInitialGap(Feature feature){
        return cachedGraphic.getInitialGap(feature);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible(Feature feature) {
        return cachedGraphic.isVisible(feature);
    }

    @Override
    public float getMargin(Feature feature, float coeff) {
        return cachedGraphic.getMargin(feature, coeff);
    }

}
