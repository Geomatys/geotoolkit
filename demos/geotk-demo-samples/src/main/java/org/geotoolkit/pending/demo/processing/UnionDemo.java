
package org.geotoolkit.pending.demo.processing;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureBuilder;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.process.ProcessFinder;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;


public class UnionDemo {

    private static FeatureBuilder sfb;
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
        ftb.add("name", String.class);
        ftb.add("geom1", Geometry.class, CRS.forCode("EPSG:3395"));
        ftb.add("geom2", Geometry.class, CRS.forCode("EPSG:3395"));

        ftb.setDefaultGeometry("geom1");
        final FeatureType sft = ftb.buildFeatureType();
        return sft;
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
        ftb.add("name", String.class);
        ftb.add("color", String.class);
        ftb.add("geom3", Geometry.class, CRS.forCode("EPSG:3395"));
        ftb.add("att", Integer.class);

        ftb.setDefaultGeometry("geom3");
        final FeatureType sft = ftb.buildFeatureType();
        return sft;
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


        Feature myFeature1;
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(3.0, 5.0),
                    new Coordinate(3.0, 7.0),
                    new Coordinate(6.0, 7.0),
                    new Coordinate(6.0, 5.0),
                    new Coordinate(3.0, 5.0)
                });
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "feature1");
        sfb.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature1 = sfb.buildFeature("id-01");
        featureList.add(myFeature1);

        Feature myFeature2;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 5.0),
                    new Coordinate(6.0, 7.0),
                    new Coordinate(8.0, 7.0),
                    new Coordinate(8.0, 5.0),
                    new Coordinate(6.0, 5.0)
                });
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "feature2");
        sfb.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature2 = sfb.buildFeature("id-02");
        featureList.add(myFeature2);

        Feature myFeature3;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 2.0),
                    new Coordinate(6.0, 5.0),
                    new Coordinate(8.0, 5.0),
                    new Coordinate(8.0, 2.0),
                    new Coordinate(6.0, 2.0)
                });
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "feature3");
        sfb.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        //sfb.set("geom2", line);
        myFeature3 = sfb.buildFeature("id-03");
        featureList.add(myFeature3);

        Feature myFeature4;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2.0, 3.0),
                    new Coordinate(2.0, 4.0),
                    new Coordinate(3.0, 4.0),
                    new Coordinate(3.0, 3.0),
                    new Coordinate(2.0, 3.0)
                });
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "feature4");
        sfb.setPropertyValue("geom1", geometryFactory.createPolygon(ring, null));
        myFeature4 = sfb.buildFeature("id-04");
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


        Feature myFeature1;
        LinearRing ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4.0, 4.0),
                    new Coordinate(4.0, 8.0),
                    new Coordinate(7.0, 8.0),
                    new Coordinate(7.0, 4.0),
                    new Coordinate(4.0, 4.0)
                });
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "feature11");
        sfb.setPropertyValue("color", "red");
        sfb.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        sfb.setPropertyValue("att",20);
        myFeature1 = sfb.buildFeature("id-11");
        featureList.add(myFeature1);

        Feature myFeature2;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(7.0, 4.0),
                    new Coordinate(7.0, 8.0),
                    new Coordinate(9.0, 8.0),
                    new Coordinate(9.0, 4.0),
                    new Coordinate(7.0, 4.0)
                });
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "feature12");
        sfb.setPropertyValue("color", "blue");
        sfb.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        sfb.setPropertyValue("att", 20);
        myFeature2 = sfb.buildFeature("id-12");
        featureList.add(myFeature2);

        Feature myFeature3;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(6.0, 2.0),
                    new Coordinate(6.0, 4.0),
                    new Coordinate(9.0, 4.0),
                    new Coordinate(9.0, 2.0),
                    new Coordinate(6.0, 2.0)
                });
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "feature13");
        sfb.setPropertyValue("color", "grey");
        sfb.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        sfb.setPropertyValue("att", 10);
        myFeature3 = sfb.buildFeature("id-13");
        featureList.add(myFeature3);

        Feature myFeature4;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(4.0, 2.0),
                    new Coordinate(4.0, 3.0),
                    new Coordinate(5.0, 3.0),
                    new Coordinate(5.0, 2.0),
                    new Coordinate(4.0, 2.0)
                });
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "feature14");
        sfb.setPropertyValue("color", "grey");
        sfb.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        sfb.setPropertyValue("att", 12);
        myFeature4 = sfb.buildFeature("id-14");
        featureList.add(myFeature4);

        Feature myFeature5;
        ring = geometryFactory.createLinearRing(
                new Coordinate[]{
                    new Coordinate(2.0, 5.0),
                    new Coordinate(2.0, 6.0),
                    new Coordinate(3.0, 6.0),
                    new Coordinate(3.0, 5.0),
                    new Coordinate(2.0, 5.0)
                });
        sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "feature15");
        sfb.setPropertyValue("color", "grey");
        sfb.setPropertyValue("geom3", geometryFactory.createPolygon(ring, null));
        sfb.setPropertyValue("att", 12);
        myFeature5 = sfb.buildFeature("id-15");
        featureList.add(myFeature5);

        return featureList;
    }

}
