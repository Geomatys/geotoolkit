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
package org.geotoolkit.processing.vector.union;

import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.opengis.util.NoSuchIdentifierException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;

import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.processing.GeotkProcessingRegistry;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * JUnit test of Union process
 *
 * @author Quentin Boileau
 * @module
 */
public class UnionTest extends AbstractProcessTest {

    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static FeatureType type;

    public UnionTest() {
        super("vector:union");
    }

    @Test
    public void testUnion() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Inputs
        final FeatureCollection featureList = buildFeatureList();
        final FeatureCollection featureUnionList = buildFeatureUnionList();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"vector:union");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("feature_union").setValue(featureUnionList);
        in.parameter("input_geometry_name").setValue("geom1");
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureCollection featureListOut = (FeatureCollection) proc.call().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureCollection featureListResult = buildResultList();

        compare(featureListResult,featureListOut);
    }

    private static FeatureType createSimpleType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("UnionTest");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Geometry.class).setName("geom1").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Geometry.class).setName("geom2").setCRS(CRS.forCode("EPSG:3395"));
        return ftb.build();
    }

    private static FeatureType createSimpleType2() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("UnionTest");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(String.class).setName("color");
        ftb.addAttribute(Geometry.class).setName("geom3").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Integer.class).setName("att");
        return ftb.build();
    }

    private static FeatureType createSimpleResultType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("UnionTest-UnionTest");
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(String.class).setName("color");
        ftb.addAttribute(Integer.class).setName("att");
        ftb.addAttribute(Geometry.class).setName("geom1").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        return ftb.build();
    }

    private static FeatureCollection buildFeatureList() throws FactoryException {

        type = createSimpleType();
        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);


        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 5.0),
                    new Coordinate(3.0, 7.0),
                    new Coordinate(6.0, 7.0),
                    new Coordinate(6.0, 5.0),
                    new Coordinate(3.0, 5.0)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-01");
        myFeature1.setPropertyValue("name", "feature1");
        myFeature1.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature1);

        Feature myFeature2 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 5.0),
                    new Coordinate(6.0, 7.0),
                    new Coordinate(8.0, 7.0),
                    new Coordinate(8.0, 5.0),
                    new Coordinate(6.0, 5.0)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-02");
        myFeature2.setPropertyValue("name", "feature2");
        myFeature2.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature2);

        Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 2.0),
                    new Coordinate(6.0, 5.0),
                    new Coordinate(8.0, 5.0),
                    new Coordinate(8.0, 2.0),
                    new Coordinate(6.0, 2.0)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-03");
        myFeature3.setPropertyValue("name", "feature3");
        myFeature3.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        //sfb.set("geom2", line);
        featureList.add(myFeature3);

        Feature myFeature4 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2.0, 3.0),
                    new Coordinate(2.0, 4.0),
                    new Coordinate(3.0, 4.0),
                    new Coordinate(3.0, 3.0),
                    new Coordinate(2.0, 3.0)
                });
        myFeature4.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-04");
        myFeature4.setPropertyValue("name", "feature4");
        myFeature4.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature4);

        return featureList;
    }

    private static FeatureCollection buildFeatureUnionList() throws FactoryException {

        type = createSimpleType2();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);


        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4.0, 4.0),
                    new Coordinate(4.0, 8.0),
                    new Coordinate(7.0, 8.0),
                    new Coordinate(7.0, 4.0),
                    new Coordinate(4.0, 4.0)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-11");
        myFeature1.setPropertyValue("name", "feature11");
        myFeature1.setPropertyValue("color", "red");
        myFeature1.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        myFeature1.setPropertyValue("att", 20);
        featureList.add(myFeature1);

        Feature myFeature2 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(7.0, 4.0),
                    new Coordinate(7.0, 8.0),
                    new Coordinate(9.0, 8.0),
                    new Coordinate(9.0, 4.0),
                    new Coordinate(7.0, 4.0)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-12");
        myFeature2.setPropertyValue("name", "feature12");
        myFeature2.setPropertyValue("color", "blue");
        myFeature2.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        myFeature2.setPropertyValue("att", 20);
        featureList.add(myFeature2);

        Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 2.0),
                    new Coordinate(6.0, 4.0),
                    new Coordinate(9.0, 4.0),
                    new Coordinate(9.0, 2.0),
                    new Coordinate(6.0, 2.0)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-13");
        myFeature3.setPropertyValue("name", "feature13");
        myFeature3.setPropertyValue("color", "grey");
        myFeature3.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        myFeature3.setPropertyValue("att", 10);
        featureList.add(myFeature3);

        Feature myFeature4 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4.0, 2.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(5.0, 3.0),
                    new Coordinate(5.0, 2.0),
                    new Coordinate(4.0, 2.0)
                });
        myFeature4.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-14");
        myFeature4.setPropertyValue("name", "feature14");
        myFeature4.setPropertyValue("color", "grey");
        myFeature4.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        myFeature4.setPropertyValue("att", 12);
        featureList.add(myFeature4);

        Feature myFeature5 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2.0, 5.0),
                    new Coordinate(2.0, 6.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(3.0, 5.0),
                    new Coordinate(2.0, 5.0)
                });
        myFeature5.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-15");
        myFeature5.setPropertyValue("name", "feature15");
        myFeature5.setPropertyValue("color", "grey");
        myFeature5.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        myFeature5.setPropertyValue("att", 12);
        featureList.add(myFeature5);

        return featureList;
    }

    private static FeatureCollection buildResultList() throws FactoryException {

        type = createSimpleResultType();

        final FeatureCollection featureList = FeatureStoreUtilities.collection("", type);


        Feature myFeature = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4, 7),
                    new Coordinate(6, 7),
                    new Coordinate(6, 5),
                    new Coordinate(4, 5),
                    new Coordinate(4, 7)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-01-id-11");
        myFeature.setPropertyValue("name", "feature1");
        myFeature.setPropertyValue("color", "red");
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature.setPropertyValue("att", 20);
        featureList.add(myFeature);

        myFeature = type.newInstance();
        LineString str = geometryFactory.createLineString(
                new Coordinate[]{
                    new Coordinate(3, 5),
                    new Coordinate(3, 6)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-01-id-15");
        myFeature.setPropertyValue("name", "feature1");
        myFeature.setPropertyValue("color", "grey");
        myFeature.setPropertyValue("geom1", str);
        myFeature.setPropertyValue("att", 12);
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3, 5),
                    new Coordinate(3, 6),
                    new Coordinate(3, 7),
                    new Coordinate(4, 7),
                    new Coordinate(4, 5),
                    new Coordinate(3, 5)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-01");
        myFeature.setPropertyValue("name", "feature1");
        myFeature.setPropertyValue("color", null);
        myFeature.setPropertyValue("att", null);
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(7, 5),
                    new Coordinate(8, 5),
                    new Coordinate(8, 4),
                    new Coordinate(7, 4),
                    new Coordinate(7, 5)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-03-id-12");
        myFeature.setPropertyValue("name", "feature3");
        myFeature.setPropertyValue("color", "blue");
        myFeature.setPropertyValue("att", 20);
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6, 4),
                    new Coordinate(6, 5),
                    new Coordinate(7, 5),
                    new Coordinate(7, 4),
                    new Coordinate(6, 4)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-03-id-11");
        myFeature.setPropertyValue("name", "feature3");
        myFeature.setPropertyValue("color", "red");
        myFeature.setPropertyValue("att", 20);
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6, 2),
                    new Coordinate(6, 4),
                    new Coordinate(8, 4),
                    new Coordinate(8, 2),
                    new Coordinate(6, 2)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-03-id-13");
        myFeature.setPropertyValue("name", "feature3");
        myFeature.setPropertyValue("color", "grey");
        myFeature.setPropertyValue("att", 10);
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(7, 7),
                    new Coordinate(8, 7),
                    new Coordinate(8, 5),
                    new Coordinate(7, 5),
                    new Coordinate(7, 7)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-02-id-12");
        myFeature.setPropertyValue("name", "feature2");
        myFeature.setPropertyValue("color", "blue");
        myFeature.setPropertyValue("att", 20);
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6, 5),
                    new Coordinate(6, 7),
                    new Coordinate(7, 7),
                    new Coordinate(7, 5),
                    new Coordinate(6, 5)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-02-id-11");
        myFeature.setPropertyValue("name", "feature2");
        myFeature.setPropertyValue("color", "red");
        myFeature.setPropertyValue("att", 20);
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2, 3),
                    new Coordinate(2, 4),
                    new Coordinate(3, 4),
                    new Coordinate(3, 3),
                    new Coordinate(2, 3)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-04");
        myFeature.setPropertyValue("name", "feature4");
        myFeature.setPropertyValue("color", null);
        myFeature.setPropertyValue("att", null);
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        //POLYGON ((8 5, 8 7, 7 7, 7 8, 9 8, 9 4, 8 4, 8 5))
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(8, 5),
                    new Coordinate(8, 7),
                    new Coordinate(7, 7),
                    new Coordinate(7, 8),
                    new Coordinate(9, 8),
                    new Coordinate(9, 4),
                    new Coordinate(8, 4),
                    new Coordinate(8, 5)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-12");
        myFeature.setPropertyValue("name", "feature12");
        myFeature.setPropertyValue("color", "blue");
        myFeature.setPropertyValue("att", 20);
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(8, 4),
                    new Coordinate(9, 4),
                    new Coordinate(9, 2),
                    new Coordinate(8, 2),
                    new Coordinate(8, 4)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-13");
        myFeature.setPropertyValue("name", "feature13");
        myFeature.setPropertyValue("color", "grey");
        myFeature.setPropertyValue("att", 10);
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4, 2),
                    new Coordinate(4, 3),
                    new Coordinate(5, 3),
                    new Coordinate(5, 2),
                    new Coordinate(4, 2)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-14");
        myFeature.setPropertyValue("name", "feature14");
        myFeature.setPropertyValue("color", "grey");
        myFeature.setPropertyValue("att", 12);
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4, 4),
                    new Coordinate(4, 5),
                    new Coordinate(6, 5),
                    new Coordinate(6, 4),
                    new Coordinate(4, 4)
                });
        LinearRing ring2 = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6, 7),
                    new Coordinate(4, 7),
                    new Coordinate(4, 8),
                    new Coordinate(7, 8),
                    new Coordinate(7, 7),
                    new Coordinate(6, 7)
                });
        Polygon poly1 = geometryFactory.createPolygon(ring, null);
        Polygon poly2 = geometryFactory.createPolygon(ring2, null);
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-11");
        myFeature.setPropertyValue("name", "feature11");
        myFeature.setPropertyValue("color", "red");
        myFeature.setPropertyValue("att", 20);
        myFeature.setPropertyValue("geom1", geometryFactory.createMultiPolygon(new Polygon[]{poly1, poly2}));
        featureList.add(myFeature);

        myFeature = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2.0, 5.0),
                    new Coordinate(2.0, 6.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(3.0, 5.0),
                    new Coordinate(2.0, 5.0)
                });
        myFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), "id-15");
        myFeature.setPropertyValue("name", "feature15");
        myFeature.setPropertyValue("color", "grey");
        myFeature.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature.setPropertyValue("att", 12);
        featureList.add(myFeature);

        return featureList;
    }
}
