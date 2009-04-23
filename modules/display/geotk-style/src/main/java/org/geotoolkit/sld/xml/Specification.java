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

package org.geotoolkit.sld.xml;

/**
 * Enumerations of all specifications versions.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class Specification {

    public enum Filter{
        V_1_0_0,
        V_1_1_0
    }
    
    public enum SymbologyEncoding{
        SLD_1_0_0,
        V_1_1_0
    }
    
    public enum StyledLayerDescriptor{
        V_1_0_0,
        V_1_1_0
    }
    
}
