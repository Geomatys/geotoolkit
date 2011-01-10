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
package org.geotoolkit.display2d.ext.vectorfield;

import org.geotoolkit.display2d.style.CachedSymbolizer;
import org.geotoolkit.display2d.style.renderer.SymbolizerRendererService;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CachedVectorFieldSymbolizer extends CachedSymbolizer<VectorFieldSymbolizer>{

    public CachedVectorFieldSymbolizer(final VectorFieldSymbolizer symbol,
            final SymbolizerRendererService<VectorFieldSymbolizer,? extends CachedSymbolizer<VectorFieldSymbolizer>> renderer){
        super(symbol,renderer);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getMargin(final Feature feature, final float coeff) {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void evaluate() {

    }

    @Override
    public boolean isVisible(final Feature feature) {
        return true;
    }

}
