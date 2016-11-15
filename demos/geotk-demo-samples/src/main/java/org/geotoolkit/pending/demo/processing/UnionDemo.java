
package org.geotoolkit.pending.demo.processing;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.feature.builder.AttributeRole;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.apache.sis.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;


public class UnionDemo {

    private static final GeometryFactory geometryFactory = new GeometryFactory();
    private static FeatureType type;

    public static void main(String[] args) throws ProcessException, NoSuchIdentifierException{
        Demos.init();


        // Inputs
        final FeatureCollection featureList = buildFeatureList();
        System.out.println("Input FeatureCollection 1 : "+featureList);
        System.out.println("----------------------------------------------------------------------------------------");

        final FeatureCollection featureUnionList = buildFeatureUnionList();
        System.out.println("Input FeatureCollection 2 : "+featureUnionList);


        //get the description of the process we want
        ProcessDescriptor descriptor = ProcessFinder.getProcessDescriptor("vector", "union");

        //fill process input from process descriptor
        ParameterValueGroup in = descriptor.getInputDescriptor().createValue();
        in.parameter("feature_in").setValue(featureList);
        in.parameter("feature_union").setValue(featureUnionList);

        //the name of the geometry used for the union.
        in.parameter("input_geometry_name").setValue("geom1");

        //create a process with input
        org.geotoolkit.process.Process process = descriptor.createProcess(in);

        //get the result
        final FeatureCollection featuresOut = (FeatureCollection) process.call().parameter("feature_out").getValue();

        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("Resulting FeatureCollection : "+featuresOut);
    }


    /**
     * Create the FeatureType used for the first input FeatureCollection
     * @returna SimpleFeatureType
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    private static FeatureType createSimpleType() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("UnionTest");
        ftb.addAttribute(String.class).setName("id").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Geometry.class).setName("geom1").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Geometry.class).setName("geom2").setCRS(CRS.forCode("EPSG:3395"));
        return ftb.build();
    }

    /**
     * Create the FeatureType used for the second input FeatureCollection
     * @return a SimpleFeatureType
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    private static FeatureType createSimpleType2() throws NoSuchAuthorityCodeException, FactoryException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("UnionTest");
        ftb.addAttribute(String.class).setName("id").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(String.class).setName("color");
        ftb.addAttribute(Geometry.class).setName("geom3").setCRS(CRS.forCode("EPSG:3395")).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Integer.class).setName("att");
        return ftb.build();
    }

    /**
     * Build the first FeatureCollection usind createSimpleType() as FeatureType.
     * It define 4 features with basic geometry and property.
     * @return FeatureCollection
     */
    private static FeatureCollection buildFeatureList() {

        try {
            type = createSimpleType();
        } catch (FactoryException ex) {
            Logger.getLogger("org.geotoolkit.pending.demo.processing").log(Level.WARNING, null, ex);
        }

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
        myFeature1.setPropertyValue("id", "id-01");
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
        myFeature2.setPropertyValue("id", "id-02");
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
        myFeature3.setPropertyValue("id", "id-03");
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
        myFeature4.setPropertyValue("id", "id-04");
        myFeature4.setPropertyValue("name", "feature4");
        myFeature4.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        featureList.add(myFeature4);

        return featureList;
    }

    /**
     * Build the second FeatureCollection usind createSimpleType2() as FeatureType.
     * It define 4 features with basic geometry and property.
     * @return FeatureCollection
     */
    private static FeatureCollection buildFeatureUnionList() {

        try {
            type = createSimpleType2();
        } catch (FactoryException ex) {
            Logger.getLogger("org.geotoolkit.pending.demo.processing").log(Level.SEVERE, null, ex);
        }

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
        myFeature1.setPropertyValue("id", "id-11");
        myFeature1.setPropertyValue("name", "feature11");
        myFeature1.setPropertyValue("color", "red");
        myFeature1.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        myFeature1.setPropertyValue("att",20);
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
        myFeature2.setPropertyValue("id", "id-12");
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
        myFeature3.setPropertyValue("id", "id-13");
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
        myFeature4.setPropertyValue("id", "id-14");
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
        myFeature5.setPropertyValue("id", "id-15");
        myFeature5.setPropertyValue("name", "feature15");
        myFeature5.setPropertyValue("color", "grey");
        myFeature5.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        myFeature5.setPropertyValue("att", 12);
        featureList.add(myFeature5);

        return featureList;
    }

}
