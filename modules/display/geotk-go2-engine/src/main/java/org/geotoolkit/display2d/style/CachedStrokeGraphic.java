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

import java.awt.AlphaComposite;

import org.opengis.feature.Feature;
import org.opengis.style.Stroke;


/**
 * The cached simple stroke work for strokes that have
 * only a paint or a color defined.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CachedStrokeGraphic extends CachedStroke{

    private CachedGraphic cachedGraphic = null;
    private AlphaComposite cachedComposite = null;

    CachedStrokeGraphic(Stroke stroke){
        super(stroke);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible(Feature feature) {
        evaluate();
        return true;
    }

}
