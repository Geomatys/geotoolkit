package org.geotoolkit.pending.demo.datamodel.geojson;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.storage.geojson.GeoJSONStreamWriter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class GeoJSONStreamWritingDemo {

    public static void main(String[] args) throws DataStoreException {
        Demos.init();

        final GeometryFactory gf = org.geotoolkit.geometry.jts.JTS.getFactory();

        //start by creating a memory featurestore for this test -----------------------------
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("Fish");
        ftb.addAttribute(String.class).setName("name");
        ftb.addAttribute(Integer.class).setName("length");
        ftb.addAttribute(Point.class).setName("position").setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType type = ftb.build();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final GeoJSONStreamWriter writer = new GeoJSONStreamWriter(baos, type, 7);
        Feature feature = writer.next();
        feature.setPropertyValue("name","sam");
        feature.setPropertyValue("length",30);
        feature.setPropertyValue("position",gf.createPoint(new Coordinate(20, 30)));
        writer.write();

        feature = writer.next();
        feature.setPropertyValue("name","tomy");
        feature.setPropertyValue("length",5);
        feature.setPropertyValue("position",gf.createPoint(new Coordinate(41, 56)));
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
