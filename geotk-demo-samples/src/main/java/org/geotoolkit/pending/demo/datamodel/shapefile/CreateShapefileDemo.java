
package org.geotoolkit.pending.demo.datamodel.shapefile;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.storage.shapefile.ShapefileProvider;
import org.apache.sis.storage.shapefile.ShapefileStore;


public class CreateShapefileDemo {

    public static void main(String[] args) throws Exception {

        //create a featurestore toward the wanted path
        final ShapefileStore store = new ShapefileStore(ShapefileProvider.provider(), new StorageConnector(URI.create("file:/tmp/test.shp")));

        //create the feature type needed
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("River");
        ftb.addAttribute(Point.class).setName("the_geom").setCRS(CommonCRS.WGS84.geographic());
        ftb.addAttribute(String.class).setName("name");
        final FeatureType type = ftb.build();

        //add this model in the datastore
        store.updateType(type);

        //create and store a feature
        final List<Feature> features = new ArrayList<>();
        final Feature f = type.newInstance();
        f.setPropertyValue(AttributeConvention.GEOMETRY, org.geotoolkit.geometry.jts.JTS.getFactory().createPoint(new Coordinate(15, 20)));
        f.setPropertyValue("name", "long river");
        features.add(f);
        store.add(features.iterator());

    }

}
