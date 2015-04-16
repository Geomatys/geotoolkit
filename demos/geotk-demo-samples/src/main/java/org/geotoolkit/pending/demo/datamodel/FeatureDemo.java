

package org.geotoolkit.pending.demo.datamodel;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.Date;

import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.FeatureBuilder;

import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.feature.ComplexAttribute;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.FeatureFactory;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

public class FeatureDemo {

    /**
     * This factory will produce features without validtion process.
     */
    private static final FeatureFactory LFF = FeatureFactory.LENIENT;
    /**
     * This factory will produce features with validate.
     * Validation may be expensive depending on the feature type and it's constraints.
     */
    private static final FeatureFactory VFF = FeatureFactory.VALIDATING;

    private static final GeometryFactory GF = new GeometryFactory();

    public static void main(String[] args) throws NoSuchAuthorityCodeException, FactoryException {
        Demos.init();

        final FeatureType type = FeatureTypeBuilderDemo.createSimpleType();
        final FeatureType cpxtype = FeatureTypeBuilderDemo.createComplexType();

        System.out.println(usingSimpleFeatureBuilder(type));
        System.out.println(usingFeatureUtilities(type));
        System.out.println(withComplexFeatureType(cpxtype));

    }

    private static Feature usingSimpleFeatureBuilder(FeatureType type){
        final FeatureBuilder sfb = new FeatureBuilder(type);
        sfb.setPropertyValue("name", "placide");
        sfb.setPropertyValue("length", 12);
        sfb.setPropertyValue("lastPosition", GF.createPoint(new Coordinate(-10, 23)));
        sfb.setPropertyValue("lastPositionDate", new Date());
        sfb.setPropertyValue("direction", 56.498f);
        return sfb.buildFeature("id-0");
    }

    private static Feature usingFeatureUtilities(FeatureType type){
        final Feature feature = FeatureUtilities.defaultFeature(type, "id-0");
        feature.getProperty("name").setValue("placide");
        feature.getProperty("length").setValue(12);
        feature.getProperty("lastPosition").setValue(GF.createPoint(new Coordinate(-10, 23)));
        feature.getProperty("lastPositionDate").setValue(new Date());
        feature.getProperty("direction").setValue(56.498f);
        return feature;
    }


    private static Feature withComplexFeatureType(FeatureType type){
        final Feature feature = FeatureUtilities.defaultFeature(type, "id-0");
        feature.getProperty("trackNumber").setValue(new Long(1));

        final ComplexAttribute fish = (ComplexAttribute)feature.getProperty("fish");
        fish.getProperty("name").setValue("placide");
        fish.getProperty("code").setValue("01");

        final ComplexAttribute track = (ComplexAttribute) FeatureUtilities.defaultProperty(
                feature.getType().getDescriptor("trackpoints"));
        track.getProperty("location").setValue(GF.createPoint(new Coordinate(-10, 23)));
        track.getProperty("time").setValue(new Date());

        feature.getProperties().add(track);
        return feature;
    }

}
