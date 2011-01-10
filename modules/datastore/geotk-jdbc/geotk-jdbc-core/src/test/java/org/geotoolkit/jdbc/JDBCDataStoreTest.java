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
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;

import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.FeatureCollection;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;


public abstract class JDBCDataStoreTest extends JDBCTestSupport {
    public void testGetNames() throws DataStoreException {
        String[] typeNames = dataStore.getTypeNames();
        assertTrue(new HashSet(Arrays.asList(typeNames)).contains(tname("ft1")));
    }

    public void testGetSchema() throws Exception {
        SimpleFeatureType ft1 = (SimpleFeatureType) dataStore.getFeatureType(tname("ft1"));
        assertNotNull(ft1);

        assertNotNull(ft1.getDescriptor(aname("geometry")));
        assertNotNull(ft1.getDescriptor(aname("intProperty")));
        assertNotNull(ft1.getDescriptor(aname("doubleProperty")));
        assertNotNull(ft1.getDescriptor(aname("stringProperty")));

        assertTrue(Geometry.class.isAssignableFrom(ft1.getDescriptor(aname("geometry")).getType()
                                                      .getBinding()));
        assertTrue(Number.class.isAssignableFrom( ft1.getDescriptor(aname("intProperty")).getType().getBinding()) );
        assertEquals(Double.class, ft1.getDescriptor(aname("doubleProperty")).getType().getBinding());
        assertEquals(String.class, ft1.getDescriptor(aname("stringProperty")).getType().getBinding());
    }

    public void testCreateSchema() throws Exception {
        FeatureTypeBuilder builder = new FeatureTypeBuilder();
        builder.setName(dataStore.getNamespaceURI(), tname("ft2"));
        builder.add(aname("id"), Integer.class,1,1,false,FeatureTypeBuilder.PRIMARY_KEY);
        builder.add(aname("geometry"), Geometry.class, CRS.decode("EPSG:4326"));
        builder.add(aname("intProperty"), Integer.class);
        builder.add(aname("dateProperty"), Date.class);

        SimpleFeatureType featureType = builder.buildSimpleFeatureType();
        dataStore.createSchema(featureType.getName(),featureType);

        SimpleFeatureType ft2 = (SimpleFeatureType) dataStore.getFeatureType(tname("ft2"));
        
        //JD: making the comparison a bit more lax
        //asertEquals(ft2,featureType);
        assertEqualsLax(ft2,featureType);
        
        // GEOT-2031
        assertNotSame(ft2, featureType);

        Connection cx = dataStore.createConnection();
        Statement st = cx.createStatement();
        ResultSet rs = null;

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ");

            if (dataStore.getDatabaseSchema() != null) {
                dataStore.getDialect().encodeSchemaName(dataStore.getDatabaseSchema(), sql);
                sql.append(".");
            }

            dataStore.getDialect().encodeTableName("ft2", sql);
            rs = st.executeQuery(sql.toString());
        } catch (SQLException e) {
            throw e;
        } finally {
            if(rs != null)
                rs.close();
            st.close();
            cx.close();
        }
    }
    
    public void testCreateSchemaWithConstraints() throws Exception {

        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();

        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        builder.setName(dataStore.getNamespaceURI(), tname("ft2"));
        builder.add(aname("geometry"), Geometry.class, DefaultGeographicCRS.WGS84);

        atb.reset();
        atb.setBinding(Integer.class);
        adb.reset();
        adb.setName(aname("intProperty"));
        adb.setType(atb.buildType());
        adb.setNillable(false);
        builder.add(adb.buildDescriptor());

        atb.reset();
        atb.setName(aname("stringProperty"));
        atb.setBinding(String.class);
        atb.setLength(5);
        adb.reset();
        adb.setName(aname("stringProperty"));
        adb.setType(atb.buildType());
        builder.add(adb.buildDescriptor());

        
        SimpleFeatureType featureType = builder.buildSimpleFeatureType();
        dataStore.createSchema(featureType.getName(), featureType);
        
        SimpleFeatureType ft2 = (SimpleFeatureType) dataStore.getFeatureType(tname("ft2"));
        //assertEquals(ft2, featureType);
        
        //grab a writer
        FeatureWriter w = dataStore.getFeatureWriter( nsname("ft2"), Filter.INCLUDE);
        w.hasNext();

        //a generic id should have been added, offset index by one
        SimpleFeature f = (SimpleFeature) w.next();
        f.setAttribute( 2, new Integer(0));
        f.setAttribute( 3, "hello");
        w.write();
        
        w.hasNext();
        f = (SimpleFeature) w.next();
        f.setAttribute( 2, null );
        try {
            w.write();
            fail( "null value for intProperty should have failed");
        }
        catch( Exception e ) {
        }
        
        f.setAttribute( 2, new Integer(1) );
        f.setAttribute( 3, "hello!");
        try {
            w.write();
            fail( "string greather than 5 chars should have failed");
        }
        catch( Exception e ) {
        }
        
        w.close();
    }

    void assertEqualsLax( final SimpleFeatureType e, final SimpleFeatureType a ) {
        if ( e.equals( a ) ) {
            return;  
        }
        
        //do a lax check
        assertEquals( e.getAttributeCount(), a.getAttributeCount() );
        for ( int i = 0; i < e.getAttributeCount(); i++ ) {
            AttributeDescriptor att1 = e.getDescriptor( i );
            AttributeDescriptor att2 = a.getDescriptor( i );
            
            assertEquals( att1.getName().getLocalPart(), att2.getName().getLocalPart() ); //test only local part

            //skip this test, dependding on database schema can handle differently min occurs and nillable relation
//            assertEquals( att1.getMinOccurs(), att2.getMinOccurs() );
            assertEquals( att1.getMaxOccurs(), att2.getMaxOccurs() );
            assertEquals( att1.isNillable(), att2.isNillable() );
            assertEquals( att1.getDefaultValue(), att2.getDefaultValue() );
        
            AttributeType t1 = att1.getType();
            AttributeType t2 = att2.getType();
            
            assertEquals( t1.getName().getLocalPart(), t2.getName().getLocalPart() ); //test only local part
            assertEquals( t1.getDescription(), t2.getDescription() );
            assertEquals( t1.getRestrictions(), t2.getRestrictions() );
            
            //be a bit lax on type mappings
            if (!t1.getBinding().equals( t2.getBinding() ) ) {
                if ( Number.class.isAssignableFrom( t1.getBinding() ) ) {
                    assertTrue( Number.class.isAssignableFrom( t2.getBinding() ) );
                }
                if ( Date.class.isAssignableFrom( t2.getBinding() ) ) {
                    assertTrue( Date.class.isAssignableFrom( t2.getBinding() ) );
                }
            }
        }
    }
    
    public void testGetFeatureSource() throws Exception {
        FeatureCollection<SimpleFeature> featureSource = dataStore.createSession(false).getFeatureCollection(QueryBuilder.all(nsname("ft1")));
        assertNotNull(featureSource);
    }

    public void testGetFeatureReader() throws Exception {
        GeometryFactory gf = dataStore.getGeometryFactory();

        Query query = QueryBuilder.all(dataStore.getFeatureType(tname("ft1")).getName());
        FeatureReader<SimpleFeatureType, SimpleFeature> reader = dataStore.getFeatureReader(query);

        for (int i = 0; i < 3; i++) {
            assertTrue(reader.hasNext());

            SimpleFeature feature = reader.next();
            assertNotNull(feature);
            assertEquals(5, feature.getAttributeCount());

            Point p = gf.createPoint(new Coordinate(i, i));
            assertTrue(p.equals((Geometry) feature.getAttribute(aname("geometry"))));

            Number ip = (Number) feature.getAttribute(aname("intProperty"));
            assertEquals(i, ip.intValue());
        }

        assertFalse(reader.hasNext());
        reader.close();

        final QueryBuilder builder = new QueryBuilder(query);
        builder.setProperties(new String[] { aname("intProperty") });
        query = builder.buildQuery();
        reader = dataStore.getFeatureReader(query);

        for (int i = 0; i < 3; i++) {
            assertTrue(reader.hasNext());

            SimpleFeature feature = reader.next();
            assertEquals(1, feature.getAttributeCount());
        }

        assertFalse(reader.hasNext());
        reader.close();

        FilterFactory ff = dataStore.getFilterFactory();
        Filter f = ff.equals(ff.property(aname("intProperty")), ff.literal(1));
        builder.setFilter(f);
        query = builder.buildQuery();

        reader = dataStore.getFeatureReader(query);

        for (int i = 0; i < 1; i++) {
            assertTrue(reader.hasNext());

            SimpleFeature feature = reader.next();
        }

        assertFalse(reader.hasNext());
        reader.close();
    }

    public void testGetFeatureWriter() throws Exception {
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = dataStore.getFeatureWriter(nsname("ft1"),Filter.INCLUDE);

        while (writer.hasNext()) {
            SimpleFeature feature = writer.next();
            feature.setAttribute(aname("stringProperty"), "foo");
            writer.write();
        }

        writer.close();

        Query query = QueryBuilder.all(dataStore.getFeatureType(tname("ft1")).getName());
        FeatureReader<SimpleFeatureType, SimpleFeature> reader = dataStore.getFeatureReader(query);
        assertTrue(reader.hasNext());

        while (reader.hasNext()) {
            SimpleFeature feature = reader.next();
            assertEquals("foo", feature.getAttribute(aname("stringProperty")));
        }

        reader.close();
    }

    public void testGetFeatureWriterWithFilter() throws Exception {
        FilterFactory ff = dataStore.getFilterFactory();

        Filter f = ff.equals(ff.property(aname("intProperty")), ff.literal(100));
        FeatureCollection<SimpleFeature> features = dataStore.createSession(false).getFeatureCollection(
                QueryBuilder.filtered(nsname("ft1"), f));
        assertEquals(0, features.size());

        f = ff.equals(ff.property(aname("intProperty")), ff.literal(1));

        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = dataStore.getFeatureWriter(nsname("ft1"), f);

        while (writer.hasNext()) {
            SimpleFeature feature = writer.next();
            feature.setAttribute(aname("intProperty"), new Integer(100));
            writer.write();
        }

        writer.close();

        f = ff.equals(ff.property(aname("intProperty")), ff.literal(100));
        features = dataStore.createSession(false).getFeatureCollection(QueryBuilder.filtered(nsname("ft1"), f));
        assertEquals(1, features.size());
    }

    public void testGetFeatureWriterAppend() throws Exception {
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = dataStore.getFeatureWriterAppend(nsname("ft1"));

        for (int i = 3; i < 6; i++) {
            SimpleFeature feature = writer.next();
            feature.setAttribute(aname("intProperty"), new Integer(i));
            writer.write();
        }

        writer.close();

        FeatureCollection<SimpleFeature> features = dataStore.
                createSession(false).getFeatureCollection(QueryBuilder.all(nsname("ft1")));
        assertEquals(6, features.size());
    }
}
