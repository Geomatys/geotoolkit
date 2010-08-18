/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile.indexed;

import java.util.Comparator;

import org.geotoolkit.index.Data;

/**
 * A Comparator for sortin search results in ascending record number. So we can
 * read dbf & shape file forward only
 * 
 * @author Tommaso Nolli
 * @module pending
 */
public class DataComparator implements Comparator<Data> {

    public static final DataComparator INSTANCE = new DataComparator();

    private DataComparator(){}

    /**
     * @see java.util.Comparator#compare(org.geotoolkit.index.Data, org.geotoolkit.index.Data)
     */
    @Override
    public int compare(Data o1, Data o2) {
        final Integer i1 = (Integer)o1.getValue(0);
        final Integer i2 = (Integer)o2.getValue(0);
        return i1.compareTo(i2);
    }

}
