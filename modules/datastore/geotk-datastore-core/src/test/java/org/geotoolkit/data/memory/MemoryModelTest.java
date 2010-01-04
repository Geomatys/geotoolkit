/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data.memory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.geotoolkit.data.AbstractModelTests;
import org.geotoolkit.data.DataStore;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MemoryModelTest extends AbstractModelTests{

    private final MemoryDataStore store = new MemoryDataStore();
    private final List<Class> geometries = new ArrayList<Class>();
    private final List<Class> attributs = new ArrayList<Class>();

    public MemoryModelTest(){
        geometries.add(Geometry.class);
        geometries.add(Point.class);
        geometries.add(LineString.class);
        geometries.add(Polygon.class);
        geometries.add(MultiPoint.class);
        geometries.add(MultiLineString.class);
        geometries.add(MultiPolygon.class);

        attributs.add(String.class);
        attributs.add(Byte.class);
        attributs.add(Short.class);
        attributs.add(Integer.class);
        attributs.add(Long.class);
        attributs.add(Float.class);
        attributs.add(Double.class);
        attributs.add(BigDecimal.class);
        attributs.add(Date.class);
        attributs.add(java.sql.Date.class);
        attributs.add(Timestamp.class);
        attributs.add(Object.class);
    }

    @Override
    protected DataStore getDataStore() {
        return store;
    }

    @Override
    protected List<Class> getSupportedGeometryTypes() {
        return geometries;
    }

    @Override
    protected List<Class> getSupportedAttributTypes() {
        return attributs;
    }

}
