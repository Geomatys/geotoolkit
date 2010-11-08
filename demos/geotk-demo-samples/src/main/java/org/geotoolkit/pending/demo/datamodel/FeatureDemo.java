

package org.geotoolkit.pending.demo.datamodel;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.Date;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.feature.ValidatingFeatureFactory;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

public class FeatureDemo {

    /**
     * This factory will produce features without validtion process.
     */
    private static final FeatureFactory LFF = FactoryFinder.getFeatureFactory(
                                              new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));
    /**
     * This factory will produce features with validate.
     * Validation may be expensive depending on the feature type and it's constraints.
     */
    private static final FeatureFactory VFF = FactoryFinder.getFeatureFactory(
                                              new Hints(Hints.FEATURE_FACTORY, ValidatingFeatureFactory.class));

    private static final GeometryFactory GF = new GeometryFactory();

    public static void main(String[] args) throws NoSuchAuthorityCodeException, FactoryException {

        final SimpleFeatureType type = FeatureTypeBuilderDemo.createSimpleType();

        System.out.println(usingFeatureFactory(type));
        System.out.println(usingSimpleFeatureBuilder(type));
        System.out.println(usingFeatureUtilities(type));

    }

    private static Feature usingFeatureFactory(SimpleFeatureType type){

        final Object[] values = new Object[]{
            "placide",
            12,
            GF.createPoint(new Coordinate(-10, 23)),
            new Date(),
            56.498f
        };

        final Feature feature = LFF.createSimpleFeature(values, type, "id-0");
        return feature;
    }

    private static Feature usingSimpleFeatureBuilder(SimpleFeatureType type){
        final SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(type);
        sfb.set("name", "placide");
        sfb.set("length", 12);
        sfb.set("lastPosition", GF.createPoint(new Coordinate(-10, 23)));
        sfb.set("lastPositionDate", new Date());
        sfb.set("direction", 56.498f);
        return sfb.buildFeature("id-0");
    }

    private static Feature usingFeatureUtilities(SimpleFeatureType type){
        final Feature feature = FeatureUtilities.defaultFeature(type, "id-0");
        feature.getProperty("name").setValue("placide");
        feature.getProperty("length").setValue(12);
        feature.getProperty("lastPosition").setValue(GF.createPoint(new Coordinate(-10, 23)));
        feature.getProperty("lastPositionDate").setValue(new Date());
        feature.getProperty("direction").setValue(56.498f);
        return feature;
    }

}
