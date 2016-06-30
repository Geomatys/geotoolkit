
package org.geotoolkit.pending.demo.datamodel.postgis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.db.postgres.PostgresFeatureStore;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.style.RandomStyleBuilder;
import org.opengis.coverage.Coverage;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;

/**
 * Example of creating a postgresql feature store with a raster geometry.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PostgisRasterDemo {

    public static void main(String[] args) throws Exception {

        final CoordinateReferenceSystem crs = CRS.forCode("CRS:84");

        //connect to postgres feature store
        final FeatureStore store = new PostgresFeatureStore("localhost", 5432, "table", "public", "user", "password");

        //create a feature type with a coverage attribute type
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("SpotImages");
        ftb.add("name", String.class);
        ftb.add("image", Coverage.class, crs);
        FeatureType type = ftb.buildFeatureType();
        store.createFeatureType(type.getName(), type);
        //type migh be a little different after insertion
        type = store.getFeatureType("SpotImages");

        //WARNING : if you use a existing table you must ensure that the srid
        //constraint is set in the raster_columns view or that the raster
        //column comment contains the srid as a comment


        //Create an image we will use as a coverage
        final BufferedImage image = new BufferedImage(180, 360, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 180, 360);

        //Create a coverage
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName("world");
        gcb.setRenderedImage(image);
        gcb.setCoordinateReferenceSystem(crs);
        gcb.setGridToCRS(-1, 0, 0, 1, +90, -180);
        gcb.setPixelAnchor(PixelInCell.CELL_CORNER);
        final GridCoverage2D coverage = gcb.getGridCoverage2D();

        //Create a feature
        final Feature feature = FeatureUtilities.defaultFeature(type, "id-0");
        feature.getProperty("name").setValue("world");
        feature.getProperty("image").setValue(coverage);

        //Save the feature
        store.addFeatures(type.getName(), Collections.singletonList(feature));

        //Display it
        final FeatureCollection col = store.createSession(false).getFeatureCollection(QueryBuilder.all(store.getNames().iterator().next()));
        final FeatureMapLayer layer = MapBuilder.createFeatureLayer(col, RandomStyleBuilder.createDefaultRasterStyle());
        final MapContext context = MapBuilder.createContext();
        context.layers().add(layer);
        JMap2DFrame.show(context);


    }

}
