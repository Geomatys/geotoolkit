/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.internal.storage.geojson;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import org.geotoolkit.test.feature.FeatureComparator;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.internal.AttributeConvention;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.SimpleInternationalString;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.internal.geojson.FeatureTypeUtils;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.util.FactoryException;

/**
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 2.0
 * @since   2.0
 * @module
 */
public class FeatureTypeUtilsTest {

    public static void main(String[] args) throws Exception {
       new FeatureTypeUtilsTest().writeReadFTTest();
    }

    @Test
    public void writeReadFTTest() throws Exception {

        Path featureTypeFile = Files.createTempFile("complexFT", ".json");

        FeatureType featureType = createComplexType();
        FeatureTypeUtils.writeFeatureType(featureType, featureTypeFile);

        assertTrue(Files.size(featureTypeFile) > 0);

        FeatureType readFeatureType = FeatureTypeUtils.readFeatureType(featureTypeFile);

        assertNotNull(readFeatureType);
        assertTrue(hasAGeometry(readFeatureType));
        assertNotNull(FeatureExt.getCRS(readFeatureType));

        equalsIgnoreConvention(featureType, readFeatureType);
    }

    @Test
    public void writeReadNoCRSFTTest() throws Exception {

        Path featureTypeFile = Files.createTempFile("geomFTNC", ".json");

        FeatureType featureType = createGeometryNoCRSFeatureType();
        FeatureTypeUtils.writeFeatureType(featureType, featureTypeFile);

        assertTrue(Files.size(featureTypeFile) > 0);

        FeatureType readFeatureType = FeatureTypeUtils.readFeatureType(featureTypeFile);

        assertNotNull(readFeatureType);
        assertTrue(hasAGeometry(readFeatureType));
        assertNull(FeatureExt.getCRS(readFeatureType));

        equalsIgnoreConvention(featureType, readFeatureType);
    }

    @Test
    public void writeReadCRSFTTest() throws Exception {

        Path featureTypeFile = Files.createTempFile("geomFTC", ".json");

        FeatureType featureType = createGeometryCRSFeatureType();
        FeatureTypeUtils.writeFeatureType(featureType, featureTypeFile);

        assertTrue(Files.size(featureTypeFile) > 0);

        FeatureType readFeatureType = FeatureTypeUtils.readFeatureType(featureTypeFile);

        assertNotNull(readFeatureType);
        assertTrue(hasAGeometry(readFeatureType));
        assertNotNull(FeatureExt.getCRS(readFeatureType));

        equalsIgnoreConvention(featureType, readFeatureType);
    }

    public static FeatureType createComplexType() throws FactoryException {
        FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.setName("complexAtt1");
        ftb.addAttribute(Long.class).setName("longProp2");
        ftb.addAttribute(String.class).setName("stringProp2");
        final FeatureType complexAtt1 = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName("complexAtt2");
        ftb.addAttribute(Long.class).setName("longProp2");
        ftb.addAttribute(Date.class).setName("dateProp");
        final FeatureType complexAtt2 = ftb.build();

        ftb = new FeatureTypeBuilder();
        ftb.setName("complexFT");
        ftb.addAttribute(Polygon.class).setName("geometry").setCRS(CommonCRS.WGS84.geographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Long.class).setName("longProp");
        ftb.addAttribute(String.class).setName("stringProp");
        ftb.addAttribute(Integer.class).setName("integerProp");
        ftb.addAttribute(Boolean.class).setName("booleanProp");
        ftb.addAttribute(Date.class).setName("dateProp");

        ftb.addAssociation(complexAtt1).setName("complexAtt1");
        ftb.addAssociation(complexAtt2).setName("complexAtt2").setMinimumOccurs(0).setMaximumOccurs(Integer.MAX_VALUE);
        ftb.setDescription(new SimpleInternationalString("Description"));
        return ftb.build();
    }

    private FeatureType createGeometryNoCRSFeatureType() {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("FT1");
        ftb.addAttribute(Point.class).setName("geometry").addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("type");

        return ftb.build();
    }

    private FeatureType createGeometryCRSFeatureType() {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("FT2");
        ftb.addAttribute(Point.class).setName("geometry").setCRS(CommonCRS.WGS84.geographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("type");

        return ftb.build();
    }

    /**
     * Loop on properties, returns true if there is at least one geometry property.
     *
     * @param type
     * @return true if type has a geometry.
     */
    public static boolean hasAGeometry(FeatureType type) {
        for (PropertyType pt : type.getProperties(true)){
            if (AttributeConvention.isGeometryAttribute(pt)) return true;
        }
        return false;
    }


    /**
     * Test field equality ignoring convention properties.
     */
    public static void equalsIgnoreConvention(FeatureType type1, FeatureType type2) {
        final FeatureComparator comparator = new FeatureComparator(type1, type2);
        comparator.ignoredProperties.add(AttributeConvention.IDENTIFIER);
        comparator.ignoredProperties.add("identifier");
        comparator.compare();
    }

}
