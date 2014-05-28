package org.geotoolkit.pending.demo.datamodel.geojson;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.geojson.GeoJSONStreamWriter;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.Feature;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class GeoJSONStreamWritingDemo {

    public static void main(String[] args) throws DataStoreException {
        Demos.init();

        final GeometryFactory gf = new GeometryFactory();

        //start by creating a memory featurestore for this test -----------------------------
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Fish");
        ftb.add("name", String.class);
        ftb.add("length", Integer.class);
        ftb.add("position", Point.class, DefaultGeographicCRS.WGS84);
        ftb.setDefaultGeometry("position");
        final SimpleFeatureType type = ftb.buildSimpleFeatureType();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final FeatureWriter writer = new GeoJSONStreamWriter(baos, type, 7);
        Feature feature = writer.next();
        feature.getProperty("name").setValue("sam");
        feature.getProperty("length").setValue(30);
        feature.getProperty("position").setValue(gf.createPoint(new Coordinate(20, 30)));
        writer.write();

        feature = writer.next();
        feature.getProperty("name").setValue("tomy");
        feature.getProperty("length").setValue(5);
        feature.getProperty("position").setValue(gf.createPoint(new Coordinate(41, 56)));
        writer.write();

        //and so on write features ...

        writer.close();

        try {
            //print output JSON
            System.out.println(baos.toString("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
