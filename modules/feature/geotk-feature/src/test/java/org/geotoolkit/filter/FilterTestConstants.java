/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2015, Geomatys
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
package org.geotoolkit.filter;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.apache.sis.util.logging.Logging;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.referencing.CommonCRS;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public class FilterTestConstants {


    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.filter");

    public static final FilterFactory2 FF = (FilterFactory2)
            FactoryFinder.getFilterFactory(new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));
    public static final GeometryFactory GF = new GeometryFactory();

    public static final Geometry RIGHT_GEOMETRY;
    public static final Geometry WRONG_GEOMETRY;
    public static final Date DATE;
    public static final FeatureType FEATURE_TYPE_1;
    public static final Feature FEATURE_1;
    public static final FeatureType CX_FEATURE_TYPE;
    public static final Feature CX_FEATURE;

    public static final Object CANDIDATE_1;

    static{
        CoordinateReferenceSystem crs = CommonCRS.WGS84.geographic();

        Coordinate[] coords = new Coordinate[5];
        coords[0] = new Coordinate(5, 5);
        coords[1] = new Coordinate(5, 10);
        coords[2] = new Coordinate(10,10);
        coords[3] = new Coordinate(10,5);
        coords[4] = new Coordinate(5,5);
        LinearRing ring = GF.createLinearRing(coords);
        RIGHT_GEOMETRY = GF.createPolygon(ring, new LinearRing[0]);

        coords = new Coordinate[4];
        coords[0] = new Coordinate(45, 8);
        coords[1] = new Coordinate(39, 12);
        coords[2] = new Coordinate(1, 9);
        coords[3] = new Coordinate(45, 8);
        ring = GF.createLinearRing(coords);
        WRONG_GEOMETRY = GF.createPolygon(ring, new LinearRing[0]);

        final Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 0, 13, 10, 30); // Needs to be in local time zone for this test.
        DATE = calendar.getTime();

        // Builds the test candidate
        final Map<String,Object> candidate = new HashMap<>();
        candidate.put(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "testFeatureType.1");
        candidate.put("testGeometry", RIGHT_GEOMETRY);
        candidate.put("testBoolean", Boolean.TRUE);
        candidate.put("testCharacter", 't');
        candidate.put("testByte", Byte.valueOf((byte)101));
        candidate.put("testShort", Short.valueOf((short)101));
        candidate.put("testInteger", Integer.valueOf(101));
        candidate.put("testLong", Long.valueOf(101l));
        candidate.put("testFloat", Float.valueOf(101f));
        candidate.put("testDouble", Double.valueOf(101d));
        candidate.put("testString", "test string data");
        candidate.put("testString2", "cow $10");
        candidate.put("date", new java.sql.Date(DATE.getTime()));
        candidate.put("time", new java.sql.Time(DATE.getTime()));
        candidate.put("datetime1", DATE);
        candidate.put("datetime2", new java.sql.Timestamp(DATE.getTime()));
        candidate.put("testNull", null);
        candidate.put("attribut.Géométrie", "POINT(45,32)");

        // assign the candidate
        CANDIDATE_1 = candidate;

        FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("testFeatureType");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(Polygon.class).setName("testGeometry").setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Boolean.class).setName("testBoolean");
        ftb.addAttribute(Character.class).setName("testCharacter");
        ftb.addAttribute(Byte.class).setName("testByte");
        ftb.addAttribute(Short.class).setName("testShort");
        ftb.addAttribute(Integer.class).setName("testInteger");
        ftb.addAttribute(Long.class).setName("testLong");
        ftb.addAttribute(Float.class).setName("testFloat");
        ftb.addAttribute(Double.class).setName("testDouble");
        ftb.addAttribute(String.class).setName("testString");
        ftb.addAttribute(String.class).setName("testString2");
        ftb.addAttribute(java.sql.Date.class).setName("date");
        ftb.addAttribute(java.sql.Time.class).setName("time");
        ftb.addAttribute(java.util.Date.class).setName("datetime1");
        ftb.addAttribute(java.sql.Timestamp.class).setName("datetime2");
        ftb.addAttribute(String.class).setName("testNull");
        ftb.addAttribute(String.class).setName("attribut.Géométrie");
        final FeatureType ft = ftb.build();

        // Builds the test feature
        FEATURE_TYPE_1 = ft;
        FEATURE_1 = ft.newInstance();
        FEATURE_1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "testFeatureType.1");
        FEATURE_1.setPropertyValue("testGeometry", RIGHT_GEOMETRY);
        FEATURE_1.setPropertyValue("testBoolean", Boolean.TRUE);
        FEATURE_1.setPropertyValue("testCharacter", 't');
        FEATURE_1.setPropertyValue("testByte",(byte)101);
        FEATURE_1.setPropertyValue("testShort",(short)101);
        FEATURE_1.setPropertyValue("testInteger",101);
        FEATURE_1.setPropertyValue("testLong",101l);
        FEATURE_1.setPropertyValue("testFloat",101f);
        FEATURE_1.setPropertyValue("testDouble",101d);
        FEATURE_1.setPropertyValue("testString","test string data");
        FEATURE_1.setPropertyValue("testString2","cow $10");
        FEATURE_1.setPropertyValue("date",new java.sql.Date(DATE.getTime()));
        FEATURE_1.setPropertyValue("time",new java.sql.Time(DATE.getTime()));
        FEATURE_1.setPropertyValue("datetime1",DATE);
        Timestamp stamp = new java.sql.Timestamp(DATE.getTime());
        FEATURE_1.setPropertyValue("datetime2",stamp);
        FEATURE_1.setPropertyValue("testNull",null);
        FEATURE_1.setPropertyValue("attribut.Géométrie","POINT(45,32)");



        ///////////// COMPLEX TYPE //////////////////////////

        ftb = new FeatureTypeBuilder();
        ftb.setName("http://test.com","cpxatt");
        ftb.addAttribute(String.class).setName("http://test.com","attString").setMinimumOccurs(0).setMaximumOccurs(12);
        ftb.addAttribute(String.class).setName("http://test2.com","attString").setMinimumOccurs(0).setMaximumOccurs(12);
        ftb.addAttribute(Double.class).setName("http://test.com","attDouble").setMinimumOccurs(0).setMaximumOccurs(12);
        ftb.addAttribute(Date.class).setName("http://test.com","attDate").setMinimumOccurs(0).setMaximumOccurs(12);
        final FeatureType ct = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName("http://test.com","test");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("http://test.com","attString").setMinimumOccurs(0).setMaximumOccurs(12);
        ftb.addAttribute(String.class).setName("http://test2.com","attString").setMinimumOccurs(0).setMaximumOccurs(12);
        ftb.addAttribute(Double.class).setName("http://test.com","attDouble").setMinimumOccurs(0).setMaximumOccurs(12);
        ftb.addAttribute(Date.class).setName("http://test.com","attDate").setMinimumOccurs(0).setMaximumOccurs(12);
        ftb.addAssociation(ct).setName("http://test.com","attCpx").setMinimumOccurs(0).setMaximumOccurs(10);

        CX_FEATURE_TYPE = ftb.build();


        final Feature ce1 = ct.newInstance();
        ce1.setPropertyValue("http://test.com:attString","toto19");
        ce1.setPropertyValue("http://test2.com:attString",Arrays.asList("marcel1","marcel5"));
        ce1.setPropertyValue("http://test.com:attDouble",45d);
        ce1.setPropertyValue("http://test.com:attDate",new Date());

        final Feature ce2 = ct.newInstance();
        ce2.setPropertyValue("http://test.com:attString","toto41");
        ce2.setPropertyValue("http://test2.com:attString",Arrays.asList("marcel2","marcel3","marcel5"));
        ce2.setPropertyValue("http://test.com:attDouble",45d);
        ce2.setPropertyValue("http://test.com:attDate",new Date());


        CX_FEATURE = CX_FEATURE_TYPE.newInstance();
        CX_FEATURE.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id");
        CX_FEATURE.setPropertyValue("http://test.com:attString",Arrays.asList("toto1","toto2"));
        CX_FEATURE.setPropertyValue("http://test2.com:attString","toto3");
        CX_FEATURE.setPropertyValue("http://test.com:attCpx",Arrays.asList(ce1,ce2));
    }



}
