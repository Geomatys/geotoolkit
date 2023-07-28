/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.display2d;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.portrayal.MapLayers;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.lang.Setup;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.referencing.crs.PredefinedCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.opengis.coverage.grid.SequenceType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Test between output image from renderer and images from some differents storing source.
 *
 * @author Remi Marechal (Geomatys).
 */
public class CoverageImageTest {

    public static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static final double EPSILON = 1E-9;

    final CanvasDef cdef = new CanvasDef();
    final SceneDef sdef  = new SceneDef();
    final Dimension outputImgDim  = new Dimension();

    int proportionalityCoefficient;
    Envelope resEnv;
    Hints hints;
    int srcWidth;
    int srcHeight;

    /**
     * Create an appropriate test image.
     *
     * @param width output image width.
     * @param height output image height.
     * @return an appropriate test image.
     */
    private BufferedImage createImage(int width, int height) {
        final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.red);
        g2d.fillRect(0, 0, width, height);
        return img;
    }

    /**
     * Create and return a {@link GridCoverage2D} from image, {@link CoordinateReferenceSystem}
     * and double table values which represent geographic envelope in {@link CoordinateReferenceSystem} units.
     *
     * @param image coverage image.
     * @param crs coverage {@link CoordinateReferenceSystem}
     * @param coordinates coverage envelope ordinate values.(xmin, ymin, ... xmax, ymax ...)
     * @return {@link GridCoverage2D} from image, {@link CoordinateReferenceSystem}
     * and double table values which represent geographic envelope in {@link CoordinateReferenceSystem} units.
     */
    private GridCoverage createCoverage(RenderedImage image, CoordinateReferenceSystem crs, double...coordinates) {
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setEnvelope(coordinates);
        gcb.setValues(image);
        gcb.setDomain(env);
        return gcb.build();
    }

    /**
     * Compare {@link RenderedImage} from expected {@link RenderedImage} and a proportionality coefficient.
     *
     * @param sourceImage tested Image.
     * @param resultImage expected image.
     * @param proportionalityCoefficient resample coefficient.
     */
    private void checkImage(RenderedImage sourceImage, RenderedImage resultImage, int proportionalityCoefficient) {
        final int numband = sourceImage.getSampleModel().getNumBands();
        final int srcMinX = sourceImage.getMinX();
        final int srcMinY = sourceImage.getMinY();
        assertTrue(numband == resultImage.getSampleModel().getNumBands());
        final PixelIterator srcPix  = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).create(sourceImage);
        final PixelIterator destPix = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).create(resultImage);
        assertTrue(Math.abs(resultImage.getWidth()  - sourceImage.getWidth()  * proportionalityCoefficient) <= EPSILON);
        assertTrue(Math.abs(resultImage.getHeight() - sourceImage.getHeight() * proportionalityCoefficient) <= EPSILON);
        while (srcPix.next()) {
            final int srcX        = srcPix.getPosition().x - srcMinX;
            final int srcY        = srcPix.getPosition().y - srcMinY;
            final int destX       = proportionalityCoefficient * srcX;
            final int destY       = proportionalityCoefficient * srcY;

            for (int b = 0; b < numband; b++) {
                final double srcValue = srcPix.getSampleDouble(b);
                for(int dy = destY; dy < destY + proportionalityCoefficient; dy++) {
                    for(int dx = destX; dx < destX + proportionalityCoefficient; dx++) {
                        destPix.moveTo(dx, dy);
                        assertTrue("At pixel "+dx+","+dy+","+b,Math.abs(srcValue-destPix.getSampleDouble(b)) <= EPSILON);
                    }
                }
            }
        }
    }

    /**
     * Compute and compare result image from {@link MapContext} build with {@link CoverageMapLayer},
     * and sourceImage.
     *
     * @param sourceImage expected image will be tested.
     * @param cml {@link CoverageMapLayer} use to build {@link MapContext}.
     * @throws PortrayalException
     */
    private void testImageLayer(RenderedImage sourceImage, MapLayer cml) throws PortrayalException{
        //create a mapcontext
        final MapLayers context  = MapBuilder.createContext();
        context.getComponents().add(cml);

        outputImgDim.setSize(proportionalityCoefficient * srcWidth, proportionalityCoefficient * srcHeight);

        hints = new Hints(GO2Hints.KEY_COLOR_MODEL, sourceImage.getColorModel(),
                         RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        cdef.setDimension(outputImgDim);
        sdef.setContext(context);
        sdef.setHints(hints);
        cdef.setEnvelope(resEnv);

        final RenderedImage imgResult = DefaultPortrayalService.portray(cdef, sdef);
        checkImage(sourceImage, imgResult, proportionalityCoefficient);
    }

    /**
     * Test between output image from renderer and source image within {@link GridCoverage2D}.
     *
     * @throws PortrayalException
     */
    @Test
    public void coverage2DTest() throws PortrayalException {
        final BufferedImage img = createImage(180, 90);
        final CoordinateReferenceSystem crs = PredefinedCRS.CARTESIAN_2D;
        final double[] envelope = new double[]{-180, -90, 180, 90};
        final GridCoverage gc2D = createCoverage(img, crs, envelope);

        final MapLayer cl = MapBuilder.createCoverageLayer(gc2D, SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), "raster");

        //Envelope result
        GeneralEnvelope resuEnv = new GeneralEnvelope(crs);
        resuEnv.setEnvelope(envelope);
        resEnv = resuEnv;
        proportionalityCoefficient = 2;
        srcWidth = 180;
        srcHeight = 90;

        testImageLayer(img, cl);
    }

    /**
     * Test between output image from renderer and source image within {@link GridCoverageReader}.
     *
     * @throws PortrayalException
     * @throws DataStoreException
     * @throws IOException
     */
    @Test
    @org.junit.Ignore("We will remove the class tested by this method.")
    public void coverageReaderTest() throws PortrayalException, DataStoreException, IOException {

        ImageIO.scanForPlugins();
        Setup.initialize(null);

        final File input = new File("src/test/resources/org/geotoolkit/display2d/clouds.jpg");
        final ImageCoverageReader reader = CoverageIO.createSimpleReader(input);

        final BufferedImage img = ImageIO.read(input);
        final GridCoverage gridcov = reader.read(null);

        proportionalityCoefficient = 2;

        final MapLayer cl = MapBuilder.createCoverageLayer(input);

        //Envelope result
        resEnv = gridcov.getGridGeometry().getEnvelope();

        srcWidth  = img.getWidth();
        srcHeight = img.getHeight();

        testImageLayer(img, cl);
    }
}
