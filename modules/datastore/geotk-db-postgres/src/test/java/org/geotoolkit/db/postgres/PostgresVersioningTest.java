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
package org.geotoolkit.db.postgres;

import com.vividsolutions.jts.geom.Point;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.junit.Test;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

import static org.geotoolkit.db.postgres.PostgresFeatureStoreFactory.*;
import org.geotoolkit.version.VersioningException;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PostgresVersioningTest {
    
    private static final FeatureType FTYPE_SIMPLE;
    private static final CoordinateReferenceSystem CRS_4326;
    
    static{
        try {
            CRS_4326 = CRS.decode("EPSG:4326",true);
        } catch (NoSuchAuthorityCodeException ex) {
            throw new RuntimeException("Failed to load CRS");
        } catch (FactoryException ex) {
            throw new RuntimeException("Failed to load CRS");
        }
        
        FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("testTable");
        ftb.add("point",    Point.class, CRS_4326);
        ftb.add("boolean",  Boolean.class);
        ftb.add("integer",  Integer.class);
        ftb.add("string",   String.class);
        FTYPE_SIMPLE = ftb.buildFeatureType();
        
    }
    
    private PostgresFeatureStore store;
    
    public PostgresVersioningTest(){
    }
    
    private void reload() throws DataStoreException, VersioningException {
        if(store != null){
            store.dispose();
        }
        
        final ParameterValueGroup params = PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(DATABASE, params).setValue("junit");
        Parameters.getOrCreate(PORT, params).setValue(5432);
        Parameters.getOrCreate(SCHEMA, params).setValue("public");
        Parameters.getOrCreate(USER, params).setValue("postgres");
        Parameters.getOrCreate(PASSWORD, params).setValue("postgres");
        Parameters.getOrCreate(SIMPLETYPE, params).setValue(false);
        Parameters.getOrCreate(NAMESPACE, params).setValue("no namespace");
        store = (PostgresFeatureStore) FeatureStoreFinder.open(params);
        
        for(Name n : store.getNames()){
            store.deleteSchema(n);
        }
        assertTrue(store.getNames().isEmpty());
        
        //delete historisation functions, he must create them himself
        store.dropHSFunctions();
        
    }
    
    @Test
    public void testSimpleTypeVersioning() throws DataStoreException, VersioningException {
        reload();
        
        final FeatureType refType = FTYPE_SIMPLE;        
        store.createSchema(refType.getName(), refType);        
        assertEquals(1, store.getNames().size());
        
        
        
        
    }
    
}
