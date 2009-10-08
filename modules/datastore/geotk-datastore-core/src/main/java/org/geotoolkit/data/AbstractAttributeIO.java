/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data;

import org.opengis.feature.type.AttributeDescriptor;

/**
 * Provides support for creating AttributeReaders.
 * @since 2.0
 * @source $URL$
 * @version $Id$
 * @author  Ian Schneider
 */
public abstract class AbstractAttributeIO {

    protected final AttributeDescriptor[] metaData;

    /**
     * Copy the meta-data from this reader, but doesn't use the reader.
     */
    protected AbstractAttributeIO(AttributeReader defs) {
        this(defs.getAttributeDescriptors());
    }

    protected AbstractAttributeIO(AttributeDescriptor[] metaData) {
        this.metaData = metaData;
    }

    public final int getAttributeCount() {
        return metaData.length;
    }

    public final AttributeDescriptor getAttributeDescriptor(int position) {
        return metaData[position];
    }

    public final AttributeDescriptor[] getAttributeDescriptors(){
        return metaData;
    }

}
