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
package org.geotoolkit.jdbc;

import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.data.collection.FeatureIterator;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;


/**
 * Checks the datastore can operate against geometryless tables
 * @author Andrea Aime - OpenGeo
 *
 * @module pending
 */
public abstract class JDBCGeometrylessTest extends JDBCTestSupport {

    protected SimpleFeatureType personSchema;
    protected SimpleFeatureType zipCodeSchema;
    protected static final String PERSON = "person";
    protected static final String ID = "id";
    protected static final String NAME = "name";
    protected static final String AGE = "age";
    protected static final String ZIPCODE = "zipcode";
    protected static final String CODE = "code";

    @Override
    protected abstract JDBCGeometrylessTestSetup createTestSetup();
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        personSchema = FeatureTypeUtilities.createType(dataStore.getNamespaceURI() + "." + PERSON, ID + ":0," + NAME+":String," + AGE + ":0");
        zipCodeSchema = FeatureTypeUtilities.createType(dataStore.getNamespaceURI() + "." + ZIPCODE, ID + ":0," + CODE + ":String");
    }

    public void testPersonSchema() throws Exception {
        SimpleFeatureType ft =  dataStore.getSchema(tname(PERSON));
        assertFeatureTypesEqual(personSchema, ft);
    }
    
    public void testReadFeatures() throws Exception {
        FeatureCollection fc = dataStore.getFeatureSource(tname(PERSON)).getFeatures();
        assertEquals(2, fc.size());
        FeatureIterator<SimpleFeature> fr = fc.features();
        assertTrue(fr.hasNext());
        SimpleFeature f = fr.next();
        assertTrue(fr.hasNext());
        f = fr.next();
        assertFalse(fr.hasNext());
        fr.close();
    }
    
    public void testGetBounds() throws Exception {
        JTSEnvelope2D env = dataStore.getFeatureSource(tname(PERSON)).getBounds();
        assertTrue(env.isEmpty());
    }
    
    public void testCreate() throws Exception {
        dataStore.createSchema(zipCodeSchema);
        assertFeatureTypesEqual(zipCodeSchema, dataStore.getSchema(tname(ZIPCODE)));
    }
}
