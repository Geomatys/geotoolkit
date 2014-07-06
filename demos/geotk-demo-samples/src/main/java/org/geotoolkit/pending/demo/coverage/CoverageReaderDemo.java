
package org.geotoolkit.pending.demo.coverage;

import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.io.File;
import java.nio.file.Files;

import net.sf.jasperreports.engine.export.Grid;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.gui.swing.render2d.JMap2DFrame;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.pending.demo.Demos;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.util.FileUtilities;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.metadata.Metadata;


public class CoverageReaderDemo {

    public static final MutableStyleFactory SF = new DefaultStyleFactory();

    public static void main(String[] args) throws Exception {
        Demos.init();

        // Create a temp file to extract data from jar file.
        final File tempData = Files.createTempFile("tempCvg", ".grb").toFile();
        FileUtilities.buildFileFromStream(
                CoverageReaderDemo.class.getResourceAsStream("/data/grib/Atlantic.wave.grb"), tempData);

        /*
         * DEFERRED READING
         */
        final GridCoverageReadParam readParam = new GridCoverageReadParam();
        // Here is the parameter which tells the reader to perform lazy loading.
        readParam.setDeferred(true);

        final GridCoverageReader reader = CoverageIO.createSimpleReader(tempData);
        final GridCoverage coverage = reader.read(0, readParam);

        // Ok, so how to use it now ?

        // You can get pixel values directly.
        coverage.evaluate(new GeneralDirectPosition(-100, 10, 0));

        // But in most cases ...
        if (coverage instanceof GridCoverage2D) {
            // ... You will acquire iterator for fast and safe browsing.
            final RenderedImage cvgData = ((GridCoverage2D) coverage).getRenderedImage();
            final PixelIterator pxIterator = PixelIteratorFactory.createDefaultIterator(cvgData);

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
        }

//        final File input = new File("data/clouds.jpg");
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
        final MapContext context = MapBuilder.createContext();
        final CoverageMapLayer cl = MapBuilder.createCoverageLayer(tempData);
        context.layers().add(cl);

        //display it
        JMap2DFrame.show(context);

    }

}
