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
package org.geotoolkit.filter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.ValidatingFeatureFactory;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.logging.Logging;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class FilterTestConstants {

    private static final Logger LOGGER = Logging.getLogger(FilterTestConstants.class);

    public static final FilterFactory2 FF = (FilterFactory2)
            FactoryFinder.getFilterFactory(new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));
    public static final GeometryFactory GF = new GeometryFactory();
    public static final FeatureFactory FEAF = new ValidatingFeatureFactory();

    public static Geometry RIGHT_GEOMETRY;
    public static Geometry WRONG_GEOMETRY;
    public static Date DATE;
    public static final Feature FEATURE_1;

    static{
        CoordinateReferenceSystem crs = null;
        try {
            crs = CRS.decode("EPSG:4326");
        } catch (NoSuchAuthorityCodeException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (FactoryException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("testFeatureType");
        ftb.add("testGeometry", Polygon.class, crs);
        ftb.add("testBoolean", Boolean.class);
        ftb.add("testCharacter", Character.class);
        ftb.add("testByte", Byte.class);
        ftb.add("testShort", Short.class);
        ftb.add("testInteger", Integer.class);
        ftb.add("testLong", Long.class);
        ftb.add("testFloat", Float.class);
        ftb.add("testDouble", Double.class);
        ftb.add("testString", String.class);
        ftb.add("testString2", String.class);
        ftb.add("date", java.sql.Date.class);
        ftb.add("time", java.sql.Time.class);
        ftb.add("datetime1", java.util.Date.class);
        ftb.add("datetime2", java.sql.Timestamp.class);
        ftb.add("testNull", String.class);
        ftb.add("attribut.Géométrie", String.class);
        final FeatureType ft = ftb.buildFeatureType();

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

        // Builds the test feature
        final Collection<Property> properties = new ArrayList<Property>();
        properties.add(FEAF.createAttribute(RIGHT_GEOMETRY, (AttributeDescriptor) ft.getDescriptor("testGeometry"), null));
        properties.add(FEAF.createAttribute(Boolean.TRUE, (AttributeDescriptor) ft.getDescriptor("testBoolean"), null));
        properties.add(FEAF.createAttribute('t', (AttributeDescriptor) ft.getDescriptor("testCharacter"), null));
        properties.add(FEAF.createAttribute((byte)101, (AttributeDescriptor) ft.getDescriptor("testByte"), null));
        properties.add(FEAF.createAttribute((short)101, (AttributeDescriptor) ft.getDescriptor("testShort"), null));
        properties.add(FEAF.createAttribute(101, (AttributeDescriptor) ft.getDescriptor("testInteger"), null));
        properties.add(FEAF.createAttribute(101l, (AttributeDescriptor) ft.getDescriptor("testLong"), null));
        properties.add(FEAF.createAttribute(101f, (AttributeDescriptor) ft.getDescriptor("testFloat"), null));
        properties.add(FEAF.createAttribute(101d, (AttributeDescriptor) ft.getDescriptor("testDouble"), null));
        properties.add(FEAF.createAttribute("test string data", (AttributeDescriptor) ft.getDescriptor("testString"), null));
        properties.add(FEAF.createAttribute("cow $10", (AttributeDescriptor) ft.getDescriptor("testString2"), null));
        DATE = new Date();
        properties.add(FEAF.createAttribute(new java.sql.Date(DATE.getTime()), (AttributeDescriptor) ft.getDescriptor("date"), null));
        properties.add(FEAF.createAttribute(new java.sql.Time(DATE.getTime()), (AttributeDescriptor) ft.getDescriptor("time"), null));
        properties.add(FEAF.createAttribute(DATE, (AttributeDescriptor) ft.getDescriptor("datetime1"), null));
        Timestamp stamp = new java.sql.Timestamp(DATE.getTime());
        properties.add(FEAF.createAttribute(stamp, (AttributeDescriptor) ft.getDescriptor("datetime2"), null));
        properties.add(FEAF.createAttribute(null, (AttributeDescriptor) ft.getDescriptor("testNull"), null));
        properties.add(FEAF.createAttribute("POINT(45,32)", (AttributeDescriptor) ft.getDescriptor("attribut.Géométrie"), null));
        
        FEATURE_1 = FEAF.createFeature(properties, ft, "testFeatureType.1");
    }

}
