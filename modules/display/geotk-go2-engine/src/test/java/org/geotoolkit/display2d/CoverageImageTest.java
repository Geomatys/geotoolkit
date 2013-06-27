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
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.PyramidCoverageBuilder;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.memory.MPCoverageStore;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.lang.Setup;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import static org.junit.Assert.assertTrue;
import org.opengis.feature.type.Name;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Test between output image from renderer and images from some differents storing source.
 *
 * @author Remi Marechal (Geomatys).
 */
public class CoverageImageTest {
    
    public static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static final double EPSILON = 1E-9;
    
    final CanvasDef cdef = new CanvasDef();
    final ViewDef vdef   = new ViewDef();
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
     * @param ordinates coverage envelope ordinate values.(xmin, ymin, ... xmax, ymax ...)
     * @return {@link GridCoverage2D} from image, {@link CoordinateReferenceSystem}
     * and double table values which represent geographic envelope in {@link CoordinateReferenceSystem} units.
     */
    private GridCoverage2D createCoverage(RenderedImage image, CoordinateReferenceSystem crs, double...ordinates) {
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setCoordinateReferenceSystem(crs);
        gcb.setRenderedImage(image);
        gcb.setEnvelope(ordinates);
        return gcb.getGridCoverage2D();
    }
    
    /**
     * Compare {@link RenderedImage} from expected {@link RenderedImage} and a proportionality coefficient.
     * 
     * @param sourceImage tested Image.
     * @param resultImage expected image.
     * @param proportionalityCoefficient resample coefficient.
     */
    private void checkImage(RenderedImage sourceImage, BufferedImage resultImage, int proportionalityCoefficient) {
        final int numband = sourceImage.getSampleModel().getNumBands();
        final int srcMinX = sourceImage.getMinX();
        final int srcMinY = sourceImage.getMinY();
        assertTrue(numband == resultImage.getSampleModel().getNumBands());
        final PixelIterator srcPix  = PixelIteratorFactory.createRowMajorIterator(sourceImage);
        final PixelIterator destPix = PixelIteratorFactory.createRowMajorIterator(resultImage);
        assertTrue(Math.abs(resultImage.getWidth()  - sourceImage.getWidth()  * proportionalityCoefficient) <= EPSILON);
        assertTrue(Math.abs(resultImage.getHeight() - sourceImage.getHeight() * proportionalityCoefficient) <= EPSILON);
        int b = 0;
        while (srcPix.next()) {
            final double srcValue = srcPix.getSampleDouble();
            final int srcX        = srcPix.getX() - srcMinX;
            final int srcY        = srcPix.getY() - srcMinY;
            final int destX       = proportionalityCoefficient * srcX;
            final int destY       = proportionalityCoefficient * srcY;
            for(int dy = destY; dy < destY + proportionalityCoefficient; dy++) {
                for(int dx = destX; dx < destX + proportionalityCoefficient; dx++) {
                    destPix.moveTo(dx, dy, b);
                    assertTrue(Math.abs(srcValue-destPix.getSampleDouble()) <= EPSILON);
                }
            }
            if (++b == numband) b = 0;
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
    private void testImageLayer(RenderedImage sourceImage, CoverageMapLayer cml) throws PortrayalException{
        //create a mapcontext
        final MapContext context  = MapBuilder.createContext();   
        context.layers().add(cml);
        
        outputImgDim.setSize(proportionalityCoefficient * srcWidth, proportionalityCoefficient * srcHeight);
        
        hints = new Hints(GO2Hints.KEY_COLOR_MODEL, sourceImage.getColorModel());
        
        cdef.setDimension(outputImgDim);
        sdef.setContext(context);
        sdef.setHints(hints);
        vdef.setEnvelope(resEnv);
        
        final BufferedImage imgResult = DefaultPortrayalService.portray(cdef, sdef, vdef);
        checkImage(sourceImage, imgResult, proportionalityCoefficient);
    }
    
    /**
     * <p>Test between output image from renderer and source image within pyramidal model.<br/>
     * Note : PyramidalModel use for this test is a "MemoryCoverageStore" which store all raster in memory.</p>
     * 
     * @throws PortrayalException
     * @throws DataStoreException
     * @throws TransformException
     * @throws FactoryException 
     */
    @Test
    public void pyramidtest() throws PortrayalException, DataStoreException, TransformException, FactoryException {
        
        ImageIO.scanForPlugins();
        Setup.initialize(null);
        
        final File input = new File("src/test/resources/org/geotoolkit/display2d/clouds.jpg");
        final GridCoverageReader reader = CoverageIO.createSimpleReader(input);
        
        final MPCoverageStore mpCovStore = new MPCoverageStore();
        final PyramidCoverageBuilder pcb = new PyramidCoverageBuilder(new Dimension(25, 25), InterpolationCase.NEIGHBOR, 2);
        final double[] fillValue = new double[3];
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setEnvelope(-180, -90, 180, 90);
        final double[] scales = new double[]{1.40625, 2.8125};
        final Map<Envelope, double[]> map = new HashMap<Envelope, double[]>();
        map.put(env, scales);
        final Name name = new DefaultName("memory_store_test");
        pcb.create(reader, mpCovStore, name, map, fillValue);
        
        final GridCoverage2D gridcov = (GridCoverage2D) reader.read(0, null);
        
        final RenderedImage img = gridcov.getRenderedImage();
        srcWidth  = img.getWidth();
        srcHeight = img.getHeight();
        
        //Envelope result
        resEnv = gridcov.getEnvelope();
        proportionalityCoefficient = 2;
        final CoverageMapLayer cl = MapBuilder.createCoverageLayer(mpCovStore.getCoverageReference(name), SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), "raster");
        testImageLayer(img, cl);
        
        proportionalityCoefficient = 1;
        testImageLayer(img, cl);
    }
    
    /**
     * Test between output image from renderer and source image within {@link GridCoverage2D}.
     * 
     * @throws PortrayalException 
     */
    @Test
    public void coverage2DTest() throws PortrayalException {
        final BufferedImage img = createImage(180, 90);
        final CoordinateReferenceSystem crs = DefaultEngineeringCRS.CARTESIAN_2D;
        final double[] envelope = new double[]{-180, -90, 180, 90};
        final GridCoverage2D gc2D = createCoverage(img, crs, envelope);
             
        final CoverageMapLayer cl = MapBuilder.createCoverageLayer(gc2D, SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), "raster");
        
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
     * @throws CoverageStoreException
     * @throws IOException 
     */
    @Test
    public void coverageReaderTest() throws PortrayalException, CoverageStoreException, IOException {
        
        ImageIO.scanForPlugins();
        Setup.initialize(null);
        
        final File input = new File("src/test/resources/org/geotoolkit/display2d/clouds.jpg");
        final GridCoverageReader reader = CoverageIO.createSimpleReader(input);
        
        final BufferedImage img = ImageIO.read(input);
        final GridCoverage2D gridcov = (GridCoverage2D) reader.read(0, null);
        
        proportionalityCoefficient = 2;
        
        final CoverageMapLayer cl = MapBuilder.createCoverageLayer(reader, 0, SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), "raster");
        
        //Envelope result
        resEnv = gridcov.getEnvelope();
        
        srcWidth  = img.getWidth();
        srcHeight = img.getHeight();
        
        testImageLayer(img, cl);
    }
}
