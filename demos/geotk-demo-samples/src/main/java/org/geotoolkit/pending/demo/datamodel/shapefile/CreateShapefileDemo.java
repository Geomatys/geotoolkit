
package org.geotoolkit.pending.demo.datamodel.shapefile;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.data.shapefile.ShapefileFeatureStore;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.apache.sis.referencing.CommonCRS;


public class CreateShapefileDemo {

    public static void main(String[] args) throws Exception {

        //create a featurestore toward the wanted path
        final ShapefileFeatureStore store = new ShapefileFeatureStore(URI.create("file:/tmp/test.shp"));

        //create the feature type needed
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("River");
        ftb.addAttribute(Point.class).setName("the_geom").setCRS(CommonCRS.WGS84.geographic());
        ftb.addAttribute(String.class).setName("name");
        final FeatureType type = ftb.build();

        //add this model in the datastore
        store.createFeatureType(type);

        //create and store a feature
        final List<Feature> features = new ArrayList<>();
        final Feature f = type.newInstance();
        f.setPropertyValue("the_geom",new GeometryFactory().createPoint(new Coordinate(15, 20)));
        f.setPropertyValue("name","long river");
        features.add(f);
        store.addFeatures(type.getName().toString(), features);

    }

}
