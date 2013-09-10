/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.s52.symbolizer;

import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.lookuptable.LookupRecord;
import org.opengis.feature.Feature;

/**
 * Description of an S-52 element to render.
 */
public final class S52Graphic implements Comparable<S52Graphic> {
    
    public int priority;
    public ProjectedObject graphic;
    public Feature feature;
    public int viewingGroup;
    public LookupRecord record;
    public S52Context.GeoType geoType;

    @Override
    public int compareTo(S52Graphic other) {
        return priority - other.priority;
    }

}
