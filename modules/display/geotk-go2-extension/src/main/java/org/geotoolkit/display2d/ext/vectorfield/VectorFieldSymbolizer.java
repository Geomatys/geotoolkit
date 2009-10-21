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

import javax.measure.unit.NonSI;

import org.geotoolkit.style.AbstractExtensionSymbolizer;
import org.geotoolkit.style.StyleConstants;

/**
 * VectorField symbolizer, to render wind arrows
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class VectorFieldSymbolizer extends AbstractExtensionSymbolizer{

    public static final String NAME = "VectorField";

    public VectorFieldSymbolizer(){
        super(NonSI.PIXEL,"","vectorField",StyleConstants.DEFAULT_DESCRIPTION);
    }

    @Override
    public String getExtensionName() {
        return NAME;
    }

}
