/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.process.coverage.volume;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.measure.unit.SI;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.GenericName;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.referencing.datum.PixelInCell;

/**
 *
 * @author Remi Marechal (Geomatys).
 */
public strictfp class ComputeVolumeProcessTest {
    private final static double TOLERANCE = 1E-9;
    private final GeometryFactory gf = new GeometryFactory();
    
    /**
     * 
     */
    @Test
    public void testBilinear() throws ProcessException {
        final BufferedImage buff = new BufferedImage(4, 4, BufferedImage.TYPE_BYTE_GRAY);
        PixelIterator pixIter = PixelIteratorFactory.createRowMajorWriteableIterator(buff, buff);
        while (pixIter.next()) {
            pixIter.setSample(1);
        }
        GeneralEnvelope env = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_2D);
        env.setEnvelope(0 ,0, 4, 4);
        GridCoverageReader gcrTest = new GridCovReaderTest(buff, env);
        Geometry geomTest = getGeometry(1, 1, 1, 3, 3, 3, 3, 1, 1, 1);
        double altiCeiling = Double.MAX_VALUE;
        
        ComputeVolumeBuilder cvb = new ComputeVolumeBuilder(gcrTest, geomTest, altiCeiling);
        final double volume = cvb.getVolume();
        assertTrue(Math.abs(volume - 4) <= 1E-9);
    }
    
    /**
     * 
     */
    @Test
    public void testBicubic() throws ProcessException {
        final BufferedImage buff = new BufferedImage(7, 7, BufferedImage.TYPE_BYTE_GRAY);
        PixelIterator pixIter = PixelIteratorFactory.createRowMajorWriteableIterator(buff, buff);
        while (pixIter.next()) {
            pixIter.setSample(1);
        }
        GeneralEnvelope env = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_2D);
        env.setEnvelope(0 ,0, 7, 7);
        GridCoverageReader gcrTest = new GridCovReaderTest(buff, env);
        Geometry geomTest = getGeometry(1, 1,
                                        1, 6, 
                                        6, 6, 
                                        6, 1, 
                                        1, 1);
        double altiCeiling = 1;
        ComputeVolumeBuilder cvb = new ComputeVolumeBuilder(gcrTest, geomTest, altiCeiling);
        final double volume = cvb.getVolume();
        assertTrue(Math.abs(volume - 25) <= 1E-9);
    }
    
    /**
     * 
     */
    @Test
    public void testDifferentResolutions() throws ProcessException {
        // resolution < 1
        BufferedImage buff = new BufferedImage(7, 7, BufferedImage.TYPE_BYTE_GRAY);
        PixelIterator pixIter = PixelIteratorFactory.createRowMajorWriteableIterator(buff, buff);
        while (pixIter.next()) {
            pixIter.setSample(1);
        }
        GeneralEnvelope env = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_2D);
        env.setEnvelope(0 ,0, 3.5, 3.5);
        GridCoverageReader gcrTest = new GridCovReaderTest(buff, env);
        Geometry geomTest = getGeometry(0.5, 0.5,
                                        0.5, 3, 
                                        3,   3, 
                                        3,   0.5, 
                                        0.5, 0.5);
        double altiCeiling = 1;
        ComputeVolumeBuilder cvb = new ComputeVolumeBuilder(gcrTest, geomTest, altiCeiling);
        double volume = cvb.getVolume();
        assertTrue(Math.abs(volume - 6.25) <= 1E-9);
        
        // resolution > 1
        env.setEnvelope(0 ,0, 21, 21);
        gcrTest = new GridCovReaderTest(buff, env);
        geomTest = getGeometry(3,  3,
                               3,  18, 
                               18, 18, 
                               18, 3, 
                               3,  3);
        altiCeiling = 0.5;
        cvb    = new ComputeVolumeBuilder(gcrTest, geomTest, altiCeiling);
        cvb.setGeometryAltitude(1.5);
        volume = cvb.getVolume();
        assertTrue(Math.abs(volume - 112.5) <= 1E-9);
    }
    
    /**
     * 
     */
    @Test
    public void testAltitudes() throws ProcessException {
        final BufferedImage buff = new BufferedImage(7, 7, BufferedImage.TYPE_BYTE_GRAY);
        PixelIterator pixIter = PixelIteratorFactory.createRowMajorWriteableIterator(buff, buff);
        while (pixIter.next()) {
            pixIter.setSample(1);
        }
        GeneralEnvelope env = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_2D);
        env.setEnvelope(0 ,0, 7, 7);
        GridCoverageReader gcrTest = new GridCovReaderTest(buff, env);
        Geometry geomTest = getGeometry(3, 1,
                                        3, 2, 
                                        2, 2, 
                                        2, 3, 
                                        1, 3,
                                        1, 4,
                                        2, 4,
                                        2, 5,
                                        3, 5,
                                        3, 6,
                                        4, 6,
                                        4, 5,
                                        5, 5,
                                        5, 4,
                                        6, 4,
                                        6, 3,
                                        5, 3,
                                        5, 2,
                                        4, 2,
                                        4, 1,
                                        3, 1);
        double altiCeiling = 0.5;
        
        ComputeVolumeBuilder cvb = new ComputeVolumeBuilder(gcrTest, geomTest, altiCeiling);
        double volume = cvb.getVolume();
        assertTrue(Math.abs(volume - 6.5) <= 1E-9);
        
        // change ceilings
        cvb.setAnotherCeiling(0.75);
        cvb.setGeometryAltitude(0.25);
        volume = cvb.getVolume();
        assertTrue(Math.abs(volume - 6.5) <= 1E-9);
        
        // change ceilings
        // negative sens
        cvb.setAnotherCeiling(0.75);
        cvb.setGeometryAltitude(1.25);
        volume = cvb.getVolume();
        assertTrue(Math.abs(volume - 3.25) <= 1E-9);
    }
    
    /**
     * 
     */
    @Test
    public void testPike() throws ProcessException {
        final BufferedImage buff = new BufferedImage(9, 9, BufferedImage.TYPE_BYTE_GRAY);
        // fill image by value to create a pikes at the image center.
        fillImageWithPikeOrHole(buff, 1, 1);
        
        // test with resolution = 1
        GeneralEnvelope env = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_2D);
        env.setEnvelope(0 ,0, 9, 9);
        GridCoverageReader gcrTest = new GridCovReaderTest(buff, env);
        Geometry geomTest = getGeometry(0, 0,
                                        0, 9, 
                                        9, 9, 
                                        9, 0, 
                                        0, 0);
        double altiCeiling = 3.5;
        ComputeVolumeBuilder cvb = new ComputeVolumeBuilder(gcrTest, geomTest, altiCeiling);
        cvb.setGeometryAltitude(1.5);
        double volume = cvb.getVolume();
        assertEquals(54.5121953487, volume, TOLERANCE);
        
        cvb.setAnotherCeiling(1.5);
        cvb.setGeometryAltitude(3.5);
        volume = cvb.getVolume();
        assertEquals(107.487804651, volume, TOLERANCE);
        
        // test with resolution = 1/2
        env = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_2D);
        env.setEnvelope(0 ,0, 4.5, 4.5);
        gcrTest = new GridCovReaderTest(buff, env);
        geomTest = getGeometry(0, 0,
                               0, 4.5, 
                               4.5, 4.5, 
                               4.5, 0, 
                               0, 0);
        
        altiCeiling = 3.5;
        cvb = new ComputeVolumeBuilder(gcrTest, geomTest, altiCeiling);
        cvb.setGeometryAltitude(1.5);
        volume = cvb.getVolume();
        assertEquals(13.628048837, volume, TOLERANCE);
        
        cvb.setAnotherCeiling(1.5);
        cvb.setGeometryAltitude(3.5);
        volume = cvb.getVolume();
        assertEquals(26.871951162, volume, TOLERANCE);
        
        
        // test with resolution = 3
        env = new GeneralEnvelope(DefaultEngineeringCRS.CARTESIAN_2D);
        env.setEnvelope(0 ,0, 27, 27);
        gcrTest = new GridCovReaderTest(buff, env);
        geomTest = getGeometry(0,  0,
                               0,  27, 
                               27, 27, 
                               27, 0, 
                               0,  0);
        altiCeiling = 3.5;
        cvb = new ComputeVolumeBuilder(gcrTest, geomTest, altiCeiling);
        cvb.setGeometryAltitude(1.5);
        volume = cvb.getVolume();
        assertEquals(490.609758138, volume, TOLERANCE);
        
        cvb.setAnotherCeiling(1.5);
        cvb.setGeometryAltitude(3.5);
        volume = cvb.getVolume();
        assertEquals(967.390241861, volume, TOLERANCE);
    }
    
    private void fillImageWithPikeOrHole(final WritableRenderedImage img, final int basicValue, final int step) {
        int idMinX = img.getMinX();
        int idMaxX = idMinX + img.getWidth();
        int idMinY = img.getMinY();
        int idMaxY = idMinY + img.getHeight();
        int value  = basicValue;
        final PixelIterator pixIter = PixelIteratorFactory.createRowMajorWriteableIterator(img, img);
        while (idMinX < idMaxX && idMinY < idMaxY) {
            for (int y = idMinY; y < idMaxY; y++) {
                for (int x = idMinX; x < idMaxX; x++) {
                    pixIter.moveTo(x, y, 0);
                    pixIter.setSample(value);
                }
            }
            idMinX++;
            idMaxX--;
            idMinY++;
            idMaxY--;
            value += step;
        }
    }
    
    private Geometry getGeometry(double ...coords) {
        assert coords.length % 2 == 0;
        final int coordinateLength = coords.length / 2;
        final Coordinate[] polyPoint = new Coordinate[coordinateLength];
        for (int c = 0; c < coordinateLength; c++) {
            final int coordID = c << 1;
            polyPoint[c] = new Coordinate(coords[coordID], coords[coordID + 1]);
        }
        return gf.createPolygon(polyPoint);
    }
    
    /**
     * {@link GridCoverageReader} need to test {@link ComputeVolumeProcess} class.
     */
    private class GridCovReaderTest extends GridCoverageReader {
        
        final GridCoverage2D coverage;
        
        GridCovReaderTest(final RenderedImage image, final Envelope envelope){
            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setCoordinateReferenceSystem(envelope.getCoordinateReferenceSystem());
            gcb.setEnvelope(envelope);
            gcb.setRenderedImage(image);
            coverage = gcb.getGridCoverage2D();
        }
        
        @Override
        public List<? extends GenericName> getCoverageNames() throws CoverageStoreException, CancellationException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public GeneralGridGeometry getGridGeometry(int index) throws CoverageStoreException, CancellationException {
            return coverage.getGridGeometry();
        }

        @Override
        public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException, CancellationException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public GridCoverage read(int index, GridCoverageReadParam param) throws CoverageStoreException, CancellationException {
            try {
                Envelope readEnvelope            = param.getEnvelope();
                MathTransform paramToCoverageCrs = CRS.findMathTransform(param.getCoordinateReferenceSystem(), coverage.getCoordinateReferenceSystem());
                readEnvelope                     = Envelopes.transform(paramToCoverageCrs, readEnvelope);
                GeneralEnvelope readGenEnvelope  = new GeneralEnvelope(readEnvelope);
                readGenEnvelope.intersects(coverage.getEnvelope(), true);
                MathTransform crsToGrid          = coverage.getGridGeometry().getGridToCRS().inverse();
                GeneralEnvelope gridEnvelope     = Envelopes.transform(crsToGrid, readGenEnvelope);
                
                final RenderedImage covImg       = coverage.getRenderedImage();
                
                // new coverage
                Rectangle rect = new Rectangle((int)gridEnvelope.getLower(0),(int) gridEnvelope.getLower(1), 
                                               (int) Math.ceil(gridEnvelope.getSpan(0)), (int) Math.ceil(gridEnvelope.getSpan(1)));
                
                final BufferedImage newImage = new BufferedImage(covImg.getColorModel(), covImg.getColorModel().createCompatibleWritableRaster(rect.width, rect.height), false, null);
                final PixelIterator pix      = PixelIteratorFactory.createRowMajorIterator(covImg, rect);
                final PixelIterator copypix  = PixelIteratorFactory.createRowMajorWriteableIterator(newImage, newImage);
                while (pix.next()) {
                    copypix.next();
                    copypix.setSampleDouble(pix.getSampleDouble());
                }
                
                final GridCoverageBuilder gcb = new GridCoverageBuilder();
                gcb.setCoordinateReferenceSystem(coverage.getCoordinateReferenceSystem());
                gcb.setEnvelope(readGenEnvelope);
                gcb.setRenderedImage(newImage);
                gcb.setPixelAnchor(PixelInCell.CELL_CORNER);
                
                Category cat = new Category("val", new Color[]{Color.WHITE,Color.BLACK}, -128, 128, 1, 0);
                GridSampleDimension gsd = new GridSampleDimension("dim0", new Category[]{cat}, SI.METRE);
                
                gcb.setSampleDimensions(gsd);
                return gcb.getGridCoverage2D();
            } catch (Exception ex) {
                throw new CoverageStoreException(ex);
            }
        }
    }
}
