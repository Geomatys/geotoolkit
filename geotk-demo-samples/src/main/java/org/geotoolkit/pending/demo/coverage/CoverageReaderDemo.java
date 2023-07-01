
package org.geotoolkit.pending.demo.coverage;

import java.awt.image.RenderedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.portrayal.MapLayers;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;


public class CoverageReaderDemo {

    public static final MutableStyleFactory SF = DefaultStyleFactory.provider();

    public static void main(String[] args) throws Exception {
        Demos.init();

        // Create a temp file to extract data from jar file.
        final Path tempData = Files.createTempFile("tempCvg", ".grb");
        try (InputStream stream = CoverageReaderDemo.class.getResourceAsStream("/data/grib/Atlantic.wave.grb")) {
            IOUtilities.writeStream(stream, tempData);
        }

        /*
         * DEFERRED READING
         */
        final GridCoverageReadParam readParam = new GridCoverageReadParam();
        // Here is the parameter which tells the reader to perform lazy loading.
        readParam.setDeferred(true);

        final ImageCoverageReader reader = CoverageIO.createSimpleReader(tempData);
        final GridCoverage coverage = reader.read(readParam);

        // Ok, so how to use it now ?

        // You can get pixel values directly.
        coverage.evaluator().apply(new GeneralDirectPosition(-100, 10, 0));

        // But in most cases ...
        // ... You will acquire iterator for fast and safe browsing.
        final RenderedImage cvgData = coverage.render(null);
        final PixelIterator pxIterator = PixelIterator.create(cvgData);

        // What should you avoid to do with deferred reading ?

        //Don't asked for the entire Raster of the image, it load all image data in memory.
        cvgData.getData();

        // Do not close your coverage reader before you've ended using your coverage, it would close connexion to the source,
        // and tile loading will return you an error.
        reader.dispose();
        try {
            cvgData.getTile(cvgData.getMinTileX(), cvgData.getMinTileY());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        final File input = new File(CoverageReaderDemo.class.getResource("/data/coverage/clouds.jpg").toURI());
//        final GridCoverageReader reader = CoverageIO.createSimpleReader(input);

//        //print the iso 19115 metadata
//        final Metadata metadata = reader.getMetadata();
//        System.out.println(metadata);
//
//        //read a piece of coverage
//        final GridCoverageReadParam param = new GridCoverageReadParam();
//        param.setResolution(1,1);
//        param.setEnvelope(new Rectangle2D.Double(0, 0, 100, 100), CommonCRS.WGS84.normalizedGeographic());
//
//        final GridCoverage2D coverage = (GridCoverage2D) reader.read(0, param);
//        coverage.show();


        /*
         * USING THE RENDERER.
         */
        //create a mapcontext
        final MapLayers context = MapBuilder.createContext();
        final MapLayer cl = MapBuilder.createCoverageLayer(tempData);
        context.getComponents().add(cl);

        //display it
//        FXMapFrame.show(context);

    }

}
