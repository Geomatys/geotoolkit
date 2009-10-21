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

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import junit.framework.TestCase;
import junit.framework.TestResult;

import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.data.DataStore;


/**
 * Test support class for jdbc test cases.
 * <p>
 * This test class fires up a live instance of an h2 database to provide a
 * live database to work with.
 * </p>
 *
 * @author Justin Deoliveira, The Open Planning Project, jdeolive@openplans.org
 *
 * @module pending
 */
public abstract class JDBCTestSupport extends TestCase {
    /**
     * map of test setup class to boolean which tracks which 
     * setups can obtain a connection and which cannot
     */
    static Map dataSourceAvailable = new HashMap();
    
    static {
        // uncomment to turn up logging
                
//        java.util.logging.ConsoleHandler handler = new java.util.logging.ConsoleHandler();
//        handler.setLevel(java.util.logging.Level.FINE);
//        
//        org.geotoolkit.util.logging.Logging.getLogger("org.geotools.data.jdbc").setLevel(java.util.logging.Level.FINE);
//        org.geotoolkit.util.logging.Logging.getLogger("org.geotools.data.jdbc").addHandler(handler);
//        
//        org.geotoolkit.util.logging.Logging.getLogger("org.geotools.jdbc").setLevel(java.util.logging.Level.FINE);
//        org.geotoolkit.util.logging.Logging.getLogger("org.geotools.jdbc").addHandler(handler);
    }

    protected JDBCTestSetup setup;
    protected JDBCDataStore dataStore;
    protected SQLDialect dialect;
    
    /**
     * Override to check if a database connection can be obtained, if not
     * tests are ignored.
     */
    @Override
    public void run(TestResult result) {
        JDBCTestSetup setup = createTestSetup();
        
        //check if the data source is available for this setup
        Boolean available = 
            (Boolean) dataSourceAvailable.get( setup.getClass() );
        if ( available == null || available.booleanValue() ) {
            //test the connection
            try {
                DataSource dataSource = setup.getDataSource();
                Connection cx = dataSource.getConnection();
                cx.close();
                dataSourceAvailable.put( setup.getClass(), Boolean.TRUE );
            } catch (Throwable t) {
                System.out.println("Skipping tests " + getClass().getName() + " since data souce is not available: " + t.getMessage());
                dataSourceAvailable.put( setup.getClass(), Boolean.FALSE );
                return;
            } finally {
                try {
                    setup.tearDown();
                } catch(Exception e) {
                    System.out.println("Error occurred tearing down the test setup");
                }
            }
            
            super.run(result);
        }
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //create the test harness
        if (setup == null) {
            setup = createTestSetup();
        }

        setup.setUp();

        //initialize the database
        setup.initializeDatabase();

        //initialize the data
        setup.setUpData();

        //create the dataStore
        //TODO: replace this with call to datastore factory
        HashMap params = new HashMap();
        params.put( JDBCDataStoreFactory.NAMESPACE.getName().toString(), "http://www.geotools.org/test" );
        params.put( JDBCDataStoreFactory.SCHEMA.getName().toString(), "geotools" );
        params.put( JDBCDataStoreFactory.DATASOURCE.getName().toString(), setup.getDataSource() );
        
        JDBCDataStoreFactory factory = setup.createDataStoreFactory();
        dataStore = (JDBCDataStore) factory.createDataStore( params );
        
        setup.setUpDataStore(dataStore);
        dialect = dataStore.getSQLDialect();
    }

    protected abstract JDBCTestSetup createTestSetup();

    @Override
    protected void tearDown() throws Exception {
        setup.tearDown();
        dataStore.dispose();
        super.tearDown();
    }
    
    /**
     * Returns the table name as the datastore understands it (some datastore are incapable of supporting
     * mixed case names for example)
     */
    protected String tname( String raw ) {
        return setup.typeName( raw );
    }
    
    /**
     * Returns the attribute name as the datastore understands it (some datastore are incapable of supporting
     * mixed case names for example)
     */
    protected String aname( String raw ) {
        return setup.attributeName( raw );
    }
    
    /**
     * Returns the attribute name as the datastore understands it (some datastore are incapable of supporting
     * mixed case names for example)
     */
    protected Name aname( Name raw ) {
        return new DefaultName( raw.getNamespaceURI(), aname( raw.getLocalPart() ) );
    }
    
    /**
     * Checkes the two feature types are equal, taking into consideration the eventual modification
     * the datastore had to perform in order to actually manage the type (change in names case, for example)
     */
    protected void assertFeatureTypesEqual(SimpleFeatureType expected, SimpleFeatureType actual) {
        for (int i = 0; i < expected.getAttributeCount(); i++) {
            AttributeDescriptor expectedAttribute = expected.getDescriptor(i);
            AttributeDescriptor actualAttribute = actual.getDescriptor(i);

            assertAttributesEqual(expectedAttribute,actualAttribute);
        }

        // make sure the geometry is nillable and has minOccurrs to 0
        if(expected.getGeometryDescriptor() != null) {
            AttributeDescriptor dg = actual.getGeometryDescriptor();
            assertTrue(dg.isNillable());
            assertEquals(0, dg.getMinOccurs());
        }
    }

    /**
     * Checkes the two feature types are equal, taking into consideration the eventual modification
     * the datastore had to perform in order to actually manage the type (change in names case, for example)
     */
    protected void assertAttributesEqual(AttributeDescriptor expected, AttributeDescriptor actual) {
        assertEquals(aname(expected.getName()), actual.getName());
        assertEquals(expected.getMinOccurs(), actual.getMinOccurs());
        assertEquals(expected.getMaxOccurs(), actual.getMaxOccurs());
        assertEquals(expected.isNillable(), actual.isNillable());
        assertEquals(expected.getDefaultValue(), actual.getDefaultValue());

        AttributeType texpected = expected.getType();
        AttributeType tactual = actual.getType();

        if ( Number.class.isAssignableFrom( texpected.getBinding() ) ) {
            assertTrue( Number.class.isAssignableFrom( tactual.getBinding() ) );
        }
        else if ( Geometry.class.isAssignableFrom( texpected.getBinding())) {
            assertTrue( Geometry.class.isAssignableFrom( tactual.getBinding()));
        }
        else {
            assertTrue(texpected.getBinding().isAssignableFrom(tactual.getBinding()));    
        }
        
    }
    
    protected boolean areCRSEqual(CoordinateReferenceSystem crs1, CoordinateReferenceSystem crs2) {
    	
    	if (crs1==null && crs2==null)
    		return true;
    	
    	if (crs1==null ) return false;
    		
    	return crs1.equals(crs2); 
   	}

	protected boolean areReferencedEnvelopesEuqal(JTSEnvelope2D e1, JTSEnvelope2D e2) {
		
		if (e1==null && e2 ==null) return true;
		if (e1==null || e2 == null) return false;
		
		boolean equal = 
			Math.round(e1.getMinX())==Math.round(e2.getMinX()) &&
			Math.round(e1.getMinY())==Math.round(e2.getMinY()) &&
			Math.round(e1.getMaxX())==Math.round(e2.getMaxX()) &&
			Math.round(e1.getMaxY())==Math.round(e2.getMaxY());
		
		if (!equal) return false;
		return areCRSEqual(e1.getCoordinateReferenceSystem(), e2.getCoordinateReferenceSystem());
	}

}
