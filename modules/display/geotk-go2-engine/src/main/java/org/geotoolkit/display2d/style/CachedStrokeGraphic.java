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

import org.opengis.style.Stroke;


/**
 * The cached stroke work for graphic strokes.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CachedStrokeGraphic extends CachedStroke{

    private final CachedGraphicStroke cachedGraphic;

    CachedStrokeGraphic(final Stroke stroke){
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

    public float getGap(final Object candidate){
        return cachedGraphic.getGap(candidate);
    }

    public float getInitialGap(final Object candidate){
        return cachedGraphic.getInitialGap(candidate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible(final Object candidate) {
        return cachedGraphic.isVisible(candidate);
    }

    @Override
    public float getMargin(final Object candidate, final float coeff) {
        return cachedGraphic.getMargin(candidate, coeff);
    }

}
