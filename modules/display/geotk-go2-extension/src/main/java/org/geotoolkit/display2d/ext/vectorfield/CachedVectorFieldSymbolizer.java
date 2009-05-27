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
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CachedVectorFieldSymbolizer extends CachedSymbolizer<VectorFieldSymbolizer>{

    public CachedVectorFieldSymbolizer(VectorFieldSymbolizer symbol) {
        super(symbol);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public float getMargin(Feature feature, float coeff) {
        return 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void evaluate() {

    }

    @Override
    public boolean isVisible(Feature feature) {
        return true;
    }

}
