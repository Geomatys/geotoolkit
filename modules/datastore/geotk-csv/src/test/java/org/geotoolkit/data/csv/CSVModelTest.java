/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.csv;

import com.vividsolutions.jts.geom.Geometry;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.data.AbstractModelTests;
import org.geotoolkit.data.DataStore;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CSVModelTest extends AbstractModelTests{

    private final CSVDataStore store;
    private final List<Class> geometries = new ArrayList<Class>();
    private final List<Class> attributs = new ArrayList<Class>();

    public CSVModelTest() throws IOException{
        geometries.add(Geometry.class);
        attributs.add(String.class);
        attributs.add(Integer.class);
        attributs.add(Double.class);

        File f = File.createTempFile("temp", "csv");
        f.deleteOnExit();
        store = new CSVDataStore(f, "http://geotoolkit.org", "csvstore", ';');
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
