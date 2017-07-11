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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.data.AbstractModelTests;
import org.geotoolkit.data.FeatureStore;
import org.apache.sis.storage.DataStoreException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class CSVModelTest extends AbstractModelTests{

    private final CSVFeatureStore store;
    private final List<Class> geometries = new ArrayList<>();
    private final List<Class> attributs = new ArrayList<>();

    public CSVModelTest() throws IOException, MalformedURLException, DataStoreException{
        geometries.add(Geometry.class);
        attributs.add(String.class);
        attributs.add(Integer.class);
        attributs.add(Double.class);

        File f = File.createTempFile("temp", "csv");
        f.deleteOnExit();
        store = new CSVFeatureStore(f, ';');
    }

    @Override
    protected FeatureStore getDataStore() {
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
