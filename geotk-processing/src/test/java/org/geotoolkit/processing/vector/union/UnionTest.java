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

import java.util.List;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.processing.vector.AbstractProcessTest;
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;

/**
 * JUnit test of Union process
 *
 * @author Quentin Boileau
 * @module
 */
public class UnionTest extends AbstractProcessTest {

    private static final GeometryFactory geometryFactory = org.geotoolkit.geometry.jts.JTS.getFactory();
    private static FeatureType type;

    public UnionTest() {
        super("vector:union");
    }

    @Test
    public void testUnion() throws ProcessException, NoSuchIdentifierException, FactoryException {

        // Inputs
        final FeatureSet featureList = buildFeatureList();
        final FeatureSet featureUnionList = buildFeatureUnionList();

        // Process
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(GeotkProcessingRegistry.NAME,"vector:union");

        ParameterValueGroup in = desc.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("feature_union").setValue(featureUnionList);
        in.parameter("input_geometry_name").setValue("geom1");
        org.geotoolkit.process.Process proc = desc.createProcess(in);

        //Features out
        final FeatureSet featureListOut = (FeatureSet) proc.call().parameter("feature_out").getValue();

        //Expected Features out
        final FeatureSet featureListResult = buildResultList();

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

    private static FeatureSet buildFeatureList() throws FactoryException {

        type = createSimpleType();
        final InMemoryFeatureSet featureList = new InMemoryFeatureSet("noname", type);

        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 5.0),
                    new Coordinate(3.0, 7.0),
                    new Coordinate(6.0, 7.0),
                    new Coordinate(6.0, 5.0),
                    new Coordinate(3.0, 5.0)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER, "id-01");
        myFeature1.setPropertyValue("name", "feature1");
        myFeature1.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));

        Feature myFeature2 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 5.0),
                    new Coordinate(6.0, 7.0),
                    new Coordinate(8.0, 7.0),
                    new Coordinate(8.0, 5.0),
                    new Coordinate(6.0, 5.0)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER, "id-02");
        myFeature2.setPropertyValue("name", "feature2");
        myFeature2.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));

        Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 2.0),
                    new Coordinate(6.0, 5.0),
                    new Coordinate(8.0, 5.0),
                    new Coordinate(8.0, 2.0),
                    new Coordinate(6.0, 2.0)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER, "id-03");
        myFeature3.setPropertyValue("name", "feature3");
        myFeature3.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        //sfb.set("geom2", line);

        Feature myFeature4 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2.0, 3.0),
                    new Coordinate(2.0, 4.0),
                    new Coordinate(3.0, 4.0),
                    new Coordinate(3.0, 3.0),
                    new Coordinate(2.0, 3.0)
                });
        myFeature4.setPropertyValue(AttributeConvention.IDENTIFIER, "id-04");
        myFeature4.setPropertyValue("name", "feature4");
        myFeature4.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));

        featureList.add(List.of(myFeature1, myFeature2, myFeature3, myFeature4).iterator());

        return featureList;
    }

    private static FeatureSet buildFeatureUnionList() throws FactoryException {

        type = createSimpleType2();

        final InMemoryFeatureSet featureList = new InMemoryFeatureSet("noname", type);


        Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4.0, 4.0),
                    new Coordinate(4.0, 8.0),
                    new Coordinate(7.0, 8.0),
                    new Coordinate(7.0, 4.0),
                    new Coordinate(4.0, 4.0)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER, "id-11");
        myFeature1.setPropertyValue("name", "feature11");
        myFeature1.setPropertyValue("color", "red");
        myFeature1.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        myFeature1.setPropertyValue("att", 20);

        Feature myFeature2 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(7.0, 4.0),
                    new Coordinate(7.0, 8.0),
                    new Coordinate(9.0, 8.0),
                    new Coordinate(9.0, 4.0),
                    new Coordinate(7.0, 4.0)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER, "id-12");
        myFeature2.setPropertyValue("name", "feature12");
        myFeature2.setPropertyValue("color", "blue");
        myFeature2.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        myFeature2.setPropertyValue("att", 20);

        Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 2.0),
                    new Coordinate(6.0, 4.0),
                    new Coordinate(9.0, 4.0),
                    new Coordinate(9.0, 2.0),
                    new Coordinate(6.0, 2.0)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER, "id-13");
        myFeature3.setPropertyValue("name", "feature13");
        myFeature3.setPropertyValue("color", "grey");
        myFeature3.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        myFeature3.setPropertyValue("att", 10);

        Feature myFeature4 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4.0, 2.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(5.0, 3.0),
                    new Coordinate(5.0, 2.0),
                    new Coordinate(4.0, 2.0)
                });
        myFeature4.setPropertyValue(AttributeConvention.IDENTIFIER, "id-14");
        myFeature4.setPropertyValue("name", "feature14");
        myFeature4.setPropertyValue("color", "grey");
        myFeature4.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        myFeature4.setPropertyValue("att", 12);

        Feature myFeature5 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2.0, 5.0),
                    new Coordinate(2.0, 6.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(3.0, 5.0),
                    new Coordinate(2.0, 5.0)
                });
        myFeature5.setPropertyValue(AttributeConvention.IDENTIFIER, "id-15");
        myFeature5.setPropertyValue("name", "feature15");
        myFeature5.setPropertyValue("color", "grey");
        myFeature5.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        myFeature5.setPropertyValue("att", 12);

        featureList.add(List.of(myFeature1, myFeature2, myFeature3, myFeature4, myFeature5).iterator());

        return featureList;
    }

    private static FeatureSet buildResultList() throws FactoryException {

        type = createSimpleResultType();

        final InMemoryFeatureSet featureList = new InMemoryFeatureSet("noname", type);


        final Feature myFeature1 = type.newInstance();
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4, 7),
                    new Coordinate(6, 7),
                    new Coordinate(6, 5),
                    new Coordinate(4, 5),
                    new Coordinate(4, 7)
                });
        myFeature1.setPropertyValue(AttributeConvention.IDENTIFIER, "id-01-id-11");
        myFeature1.setPropertyValue("name", "feature1");
        myFeature1.setPropertyValue("color", "red");
        myFeature1.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature1.setPropertyValue("att", 20);

        final Feature myFeature2 = type.newInstance();
        LineString str = geometryFactory.createLineString(
                new Coordinate[]{
                    new Coordinate(3, 5),
                    new Coordinate(3, 6)
                });
        myFeature2.setPropertyValue(AttributeConvention.IDENTIFIER, "id-01-id-15");
        myFeature2.setPropertyValue("name", "feature1");
        myFeature2.setPropertyValue("color", "grey");
        myFeature2.setPropertyValue("geom1", str);
        myFeature2.setPropertyValue("att", 12);

        final Feature myFeature3 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3, 5),
                    new Coordinate(3, 6),
                    new Coordinate(3, 7),
                    new Coordinate(4, 7),
                    new Coordinate(4, 5),
                    new Coordinate(3, 5)
                });
        myFeature3.setPropertyValue(AttributeConvention.IDENTIFIER, "id-01");
        myFeature3.setPropertyValue("name", "feature1");
        myFeature3.setPropertyValue("color", null);
        myFeature3.setPropertyValue("att", null);
        myFeature3.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));

        final Feature myFeature4 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(7, 5),
                    new Coordinate(8, 5),
                    new Coordinate(8, 4),
                    new Coordinate(7, 4),
                    new Coordinate(7, 5)
                });
        myFeature4.setPropertyValue(AttributeConvention.IDENTIFIER, "id-03-id-12");
        myFeature4.setPropertyValue("name", "feature3");
        myFeature4.setPropertyValue("color", "blue");
        myFeature4.setPropertyValue("att", 20);
        myFeature4.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));

        final Feature myFeature5 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6, 4),
                    new Coordinate(6, 5),
                    new Coordinate(7, 5),
                    new Coordinate(7, 4),
                    new Coordinate(6, 4)
                });
        myFeature5.setPropertyValue(AttributeConvention.IDENTIFIER, "id-03-id-11");
        myFeature5.setPropertyValue("name", "feature3");
        myFeature5.setPropertyValue("color", "red");
        myFeature5.setPropertyValue("att", 20);
        myFeature5.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));

        final Feature myFeature6 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6, 2),
                    new Coordinate(6, 4),
                    new Coordinate(8, 4),
                    new Coordinate(8, 2),
                    new Coordinate(6, 2)
                });
        myFeature6.setPropertyValue(AttributeConvention.IDENTIFIER, "id-03-id-13");
        myFeature6.setPropertyValue("name", "feature3");
        myFeature6.setPropertyValue("color", "grey");
        myFeature6.setPropertyValue("att", 10);
        myFeature6.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));

        final Feature myFeature7 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(7, 7),
                    new Coordinate(8, 7),
                    new Coordinate(8, 5),
                    new Coordinate(7, 5),
                    new Coordinate(7, 7)
                });
        myFeature7.setPropertyValue(AttributeConvention.IDENTIFIER, "id-02-id-12");
        myFeature7.setPropertyValue("name", "feature2");
        myFeature7.setPropertyValue("color", "blue");
        myFeature7.setPropertyValue("att", 20);
        myFeature7.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));

        final Feature myFeature8 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6, 5),
                    new Coordinate(6, 7),
                    new Coordinate(7, 7),
                    new Coordinate(7, 5),
                    new Coordinate(6, 5)
                });
        myFeature8.setPropertyValue(AttributeConvention.IDENTIFIER, "id-02-id-11");
        myFeature8.setPropertyValue("name", "feature2");
        myFeature8.setPropertyValue("color", "red");
        myFeature8.setPropertyValue("att", 20);
        myFeature8.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));

        final Feature myFeature9 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2, 3),
                    new Coordinate(2, 4),
                    new Coordinate(3, 4),
                    new Coordinate(3, 3),
                    new Coordinate(2, 3)
                });
        myFeature9.setPropertyValue(AttributeConvention.IDENTIFIER, "id-04");
        myFeature9.setPropertyValue("name", "feature4");
        myFeature9.setPropertyValue("color", null);
        myFeature9.setPropertyValue("att", null);
        myFeature9.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));

        final Feature myFeature10 = type.newInstance();
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
        myFeature10.setPropertyValue(AttributeConvention.IDENTIFIER, "id-12");
        myFeature10.setPropertyValue("name", "feature12");
        myFeature10.setPropertyValue("color", "blue");
        myFeature10.setPropertyValue("att", 20);
        myFeature10.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));

        final Feature myFeature11 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(8, 4),
                    new Coordinate(9, 4),
                    new Coordinate(9, 2),
                    new Coordinate(8, 2),
                    new Coordinate(8, 4)
                });
        myFeature11.setPropertyValue(AttributeConvention.IDENTIFIER, "id-13");
        myFeature11.setPropertyValue("name", "feature13");
        myFeature11.setPropertyValue("color", "grey");
        myFeature11.setPropertyValue("att", 10);
        myFeature11.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));

        final Feature myFeature12 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4, 2),
                    new Coordinate(4, 3),
                    new Coordinate(5, 3),
                    new Coordinate(5, 2),
                    new Coordinate(4, 2)
                });
        myFeature12.setPropertyValue(AttributeConvention.IDENTIFIER, "id-14");
        myFeature12.setPropertyValue("name", "feature14");
        myFeature12.setPropertyValue("color", "grey");
        myFeature12.setPropertyValue("att", 12);
        myFeature12.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));

        final Feature myFeature13 = type.newInstance();
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
        myFeature13.setPropertyValue(AttributeConvention.IDENTIFIER, "id-11");
        myFeature13.setPropertyValue("name", "feature11");
        myFeature13.setPropertyValue("color", "red");
        myFeature13.setPropertyValue("att", 20);
        myFeature13.setPropertyValue("geom1", geometryFactory.createMultiPolygon(new Polygon[]{poly1, poly2}));

        final Feature myFeature14 = type.newInstance();
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2.0, 5.0),
                    new Coordinate(2.0, 6.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(3.0, 5.0),
                    new Coordinate(2.0, 5.0)
                });
        myFeature14.setPropertyValue(AttributeConvention.IDENTIFIER, "id-15");
        myFeature14.setPropertyValue("name", "feature15");
        myFeature14.setPropertyValue("color", "grey");
        myFeature14.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature14.setPropertyValue("att", 12);

        featureList.add(List.of(myFeature1, myFeature2, myFeature3, myFeature4, myFeature5,
                myFeature6, myFeature7, myFeature8, myFeature9, myFeature10, myFeature11,
                myFeature12, myFeature13, myFeature14).iterator());

        return featureList;
    }
}
