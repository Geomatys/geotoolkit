/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.style;


import javax.measure.unit.Unit;

import org.opengis.style.Description;
import org.opengis.style.Symbolizer;

import static org.geotoolkit.style.StyleConstants.*;

/**
 * Abstract implementation of GeoAPI symbolizer.
 * 
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractSymbolizer implements Symbolizer{
    
    protected final Unit uom;
    
    protected final String geom;
    
    protected final String name;
    
    protected final Description desc;
    
    /**
     * Create an abstract symbolizer.
     * 
     * @param uom : if null will be replaced by default value.
     * @param geom : can be null
     * @param name : can be null
     * @param desc : if null will be replaced by default description.
     */
    protected AbstractSymbolizer(Unit uom, String geom, String name, Description desc){
        this.uom = (uom == null) ? DEFAULT_UOM : uom ;
        this.geom = geom;
        this.name = name;
        this.desc = (desc == null) ? StyleConstants.DEFAULT_DESCRIPTION : desc;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Unit getUnitOfMeasure() {
        return uom;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getGeometryPropertyName() {
        return geom;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Description getDescription() {
        return desc;
    }

}
