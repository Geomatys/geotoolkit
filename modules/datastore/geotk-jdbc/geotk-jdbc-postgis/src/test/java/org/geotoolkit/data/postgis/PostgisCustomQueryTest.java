/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.data.postgis;

import com.vividsolutions.jts.geom.Point;
import org.geotoolkit.data.memory.ExtendedDataStore;
import org.geotoolkit.data.query.DefaultTextStatement;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.TextStatement;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.jdbc.JDBCDataStore;
import org.geotoolkit.jdbc.JDBCTestSetup;
import org.geotoolkit.jdbc.JDBCTestSupport;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.junit.Test;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

/**
 * Test on custom sql queries
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class PostgisCustomQueryTest extends JDBCTestSupport{
    
    @Test
    public void testDefinition() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException{
        
        final ExtendedDataStore store = new ExtendedDataStore(dataStore);        
        final Name name = DefaultName.valueOf("{http://www.geotoolkit.org/test}extsql");        
        assertFalse(store.getNames().contains(name));
        
        //add a new query
        final Query query = QueryBuilder.language(JDBCDataStore.CUSTOM_SQL, "SELECT geometry ,\"intProperty\" FROM custom");    
        store.addQuery(query, name);
        assertTrue(store.getNames().contains(name));
        
        final FeatureType ft = store.getFeatureType(name);
        assertEquals(name, ft.getName());        
        assertEquals(2, ft.getDescriptors().size());
        assertTrue(ft.getDescriptor("geometry") != null);
        assertTrue(ft.getDescriptor("intProperty") != null);
        assertEquals(Point.class, ft.getDescriptor("geometry").getType().getBinding());
        assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:4326",true), ((GeometryDescriptor)ft.getDescriptor("geometry")).getCoordinateReferenceSystem() ));
        assertEquals(Integer.class, ft.getDescriptor("intProperty").getType().getBinding());
                
    }
    
    @Test
    public void testRenamedDefinition() throws DataStoreException, NoSuchAuthorityCodeException, FactoryException{
        
        final ExtendedDataStore store = new ExtendedDataStore(dataStore);        
        final Name name = DefaultName.valueOf("{http://www.geotoolkit.org/test}extsql");        
        assertFalse(store.getNames().contains(name));
        
        //add a new query
        final Query query = QueryBuilder.language(JDBCDataStore.CUSTOM_SQL, "SELECT geometry as geo ,\"intProperty\" as it FROM custom");    
        store.addQuery(query, name);
        assertTrue(store.getNames().contains(name));
        
        final FeatureType ft = store.getFeatureType(name);
        assertEquals(name, ft.getName());        
        assertEquals(2, ft.getDescriptors().size());
        assertTrue(ft.getDescriptor("geo") != null);
        assertTrue(ft.getDescriptor("it") != null);
        assertEquals(Point.class, ft.getDescriptor("geo").getType().getBinding());
        assertTrue(CRS.equalsIgnoreMetadata(CRS.decode("EPSG:4326",true), ((GeometryDescriptor)ft.getDescriptor("geo")).getCoordinateReferenceSystem() ));
        assertEquals(Integer.class, ft.getDescriptor("it").getType().getBinding());
                
    }
    

    @Override
    protected JDBCTestSetup createTestSetup() {
        return new PostGISTestSetup(){

            @Override
            public void setUpData() throws Exception {
                super.setUpData();
                runSafe("DELETE FROM GEOMETRY_COLUMNS WHERE F_TABLE_NAME = 'custom'");
                runSafe("DROP TABLE IF EXISTS \"custom\"");
                
                run("CREATE TABLE \"custom\"("
                + "\"id\" serial primary key, "
                + "\"geometry\" geometry, "
                + "\"intProperty\" int,"
                + "\"doubleProperty\" double precision, "
                + "\"stringProperty\" varchar)");
                run("INSERT INTO GEOMETRY_COLUMNS VALUES('', 'public', 'custom', 'geometry', 2, '4326', 'POINT')");
            }
        };
    }
    
}
