
package org.geotoolkit.pending.demo.datamodel.shapefile;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.data.shapefile.ShapefileFeatureStore;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;


public class CreateShapefileDemo {
    
    public static void main(String[] args) throws Exception {
        
        //create a featurestore toward the wanted path
        final ShapefileFeatureStore store = new ShapefileFeatureStore(new URL("file:/tmp/test.shp"));
        
        //create the feature type needed
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("River");
        ftb.add("the_geom",Point.class, CRS.decode("EPSG:4326"));
        ftb.add("name", String.class);
        final FeatureType type = ftb.buildFeatureType();
        
        //add this model in the datastore
        store.createFeatureType(type.getName(), type);
        
        //create and store a feature
        final List<Feature> features = new ArrayList<Feature>();
        final Feature f = FeatureUtilities.defaultFeature(type, "id-0");
        f.getProperty("the_geom").setValue(new GeometryFactory().createPoint(new Coordinate(15, 20)));
        f.getProperty("name").setValue("long river");
        features.add(f);
        store.addFeatures(type.getName(), features);
        
    }
    
}
