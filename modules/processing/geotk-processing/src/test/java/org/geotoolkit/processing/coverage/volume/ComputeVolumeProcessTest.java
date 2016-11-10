/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009 - 2012, Geomatys
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
package org.geotoolkit.processing.coverage.volume;

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
import org.apache.sis.measure.Units;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.apache.sis.geometry.Envelopes;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.process.ProcessException;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.referencing.crs.PredefinedCRS;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.util.GenericName;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.apache.sis.referencing.CommonCRS;

/**
 * Test {@link ComputeVolumeProcess process} to compute volume from DEM.
 *
 * @author Remi Marechal (Geomatys).
 */
public strictfp class ComputeVolumeProcessTest extends org.geotoolkit.test.TestBase {

    /**
     * Test tolerance.
     */
    private final static double TOLERANCE = 1E-9;

    /**
     * {@link CoordinateReferenceSystem} to test compute volume process with data
     * from {@link PredefinedCRS} with cartesian {@link CoordinateSystem}.
     */
    private static CoordinateReferenceSystem CARTESIAN_CRS = PredefinedCRS.CARTESIAN_2D;

    /**
     * {@link GeometryFactory} to create geometry to test process in differents way.
     */
    private final static GeometryFactory GF = new GeometryFactory();

    /**
     * Test volume computing with bilinear interpolation in a cartesian space.
     */
    @Test
    public void testBilinearCartesian() throws ProcessException {
        basicTest(4, 4, 4, CARTESIAN_CRS,/*envelope coords -> */ 0, 0, 4, 4,
                                         /*geometry coords -> */ 1, 1, 1, 3, 3, 3, 3, 1, 1, 1);

    }

    /**
     * Test volume computing with bilinear interpolation in a geographical context.<br/>
     * Anti regression test.
     */
    @Test
    public void testBilinearGeographic() throws ProcessException {
        basicTest(4, 4, 1.969068511407543E11, CommonCRS.defaultGeographic(),
                /*envelope coords -> */ -4, -4, 4, 4,
                /*geometry coords -> */ -2, -2, -2, 2, 2, 2, 2, -2, -2, -2);
    }

    /**
     * Test volume computing with bicubic interpolation in a cartesian space.
     */
    @Test
    public void testBicubicCartesian() throws ProcessException {
        basicTest(7, 7, 25, CARTESIAN_CRS,/* envelope coords -> */ 0, 0, 7, 7,
                                          /* geometry coords -> */ 1, 1, 1, 6, 6, 6, 6, 1, 1, 1);
    }

    /**
     * Test volume computing with bicubic interpolation in a geographical context.<br/>
     * Anti regression test.
     */
    @Test
    public void testBicubicGeographic() throws ProcessException {
        basicTest(7, 7, 3.070735084708064E11, CommonCRS.defaultGeographic(),
                /* envelope coords -> */ 0, 0, 7, 7,
                /* geometry coords -> */ 1, 1, 1, 6, 6, 6, 6, 1, 1, 1);
    }

    /**
     * Test process comportement with image filled by value 1.
     *
     * @param imageWidth tested grid coverage image width.
     * @param imageHeight tested grid coverage image height.
     * @param expectedValue expected volume result.
     * @param crs coverage space.
     * @param envelopeAndGeomCoordinates estate value which define coverage coordinate and geometry coordinate.
     * In this case coverage envelope coordinates are the four first value and geometry coordinates the others.
     * @throws ProcessException
     */
    private void basicTest(final int imageWidth, final int imageHeight, final double expectedValue,
                          final CoordinateReferenceSystem crs, final double ...envelopeAndGeomCoordinates) throws ProcessException {

        // create image adapted to test.
        final BufferedImage buff = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_GRAY);
        PixelIterator pixIter = PixelIteratorFactory.createRowMajorWriteableIterator(buff, buff);
        // fill image with value 1.
        while (pixIter.next()) {
            pixIter.setSample(1);
        }

        // create coverage reader adapted for test.
        final double[] envCoords = new double[4];
        System.arraycopy(envelopeAndGeomCoordinates, 0, envCoords, 0, 4);
        GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setEnvelope(envCoords);
        GridCoverageReader gcrTest = new GridCovReaderTest(buff, env);

        // define area where volume is computed.
        final double[] geomCoords = new double[envelopeAndGeomCoordinates.length - 4];
        System.arraycopy(envelopeAndGeomCoordinates, 4, geomCoords, 0, geomCoords.length);
        Geometry geomTest = getGeometry(geomCoords);
        double altiCeiling = 1;

        // create volume builder
        ComputeVolumeBuilder cvb = new ComputeVolumeBuilder(gcrTest, geomTest, altiCeiling);
        final double volume = cvb.getVolume();

        // test if volume computed is conform.
        assertEquals(expectedValue, volume, 1E-9);
    }

    /**
     * Test differents altitudes in a cartesian space.
     */
    @Test
    public void testAltitudesInCartesianSpace() throws ProcessException {
        altitudesTest(CARTESIAN_CRS, 6.5, 6.5, 3.25);
    }



    /**
     * Test different altitudes in geographical space.
     */
    @Test
    public void testAltitudesInGeographicSpace() throws ProcessException {
        altitudesTest(CommonCRS.defaultGeographic(), 7.985004325473668E10, 7.985004325473668E10, 3.992502162736839E10);
    }

    /**
     * Test differents altitudes.
     *
     * @param crs coverage space.
     * @param expectedResults expected results.
     * @throws ProcessException
     */
    private void altitudesTest(final CoordinateReferenceSystem crs, final double ...expectedResults) throws ProcessException {
        final BufferedImage buff = new BufferedImage(7, 7, BufferedImage.TYPE_BYTE_GRAY);
        PixelIterator pixIter = PixelIteratorFactory.createRowMajorWriteableIterator(buff, buff);
        while (pixIter.next()) {
            pixIter.setSample(1);
        }
        GeneralEnvelope env = new GeneralEnvelope(crs);
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
        assertEquals(expectedResults[0], volume, 1E-9);

        // change ceilings
        cvb.setAnotherCeiling(0.75);
        cvb.setGeometryAltitude(0.25);
        volume = cvb.getVolume();
        assertEquals(expectedResults[1], volume, 1E-9);

        // change ceilings
        // negative sens
        cvb.setAnotherCeiling(0.75);
        cvb.setGeometryAltitude(1.25);
        volume = cvb.getVolume();
        assertEquals(expectedResults[2], volume, 1E-9);
    }

    /**
     * Test results from pike volume computing in cartesian space.
     */
    @Test
    public void testPikeCartesian() throws ProcessException {
        pikeOrHoleTest(new double[]{1.5, 3.5}, 9, 9, 1, 1, new double[]{1, 0.5, 3}, CARTESIAN_CRS,
                       /*resolution = 1 -> */  53.72712182998657,  108.27287817001343,
                       /*resolution = 0.5 -> */13.431780457496643, 27.068219542503357,
                       /*resolution = 3 -> */  483.54409646987915,  974.4559035301208);
    }

    /**
     * Test results from pike volume computing in geographic space.
     */
    @Test
    public void testPikeGeographic() throws ProcessException {
        pikeOrHoleTest(new double[]{1.5, 3.5}, 9, 9, 1, 1, new double[]{1, 0.5, 3}, CommonCRS.defaultGeographic(),
                       /*resolution = 1 -> */  6.590783448178236E11,  1.327019633558373E12,
                       /*resolution = 0.5 -> */1.6519189509155865E11, 3.3282694459549146E11,
                       /*resolution = 3 -> */  5.770442307446779E12,  1.1535336449348941E13);
    }

    /**
     * Test results from hole volume computing in cartesian space.
     */
    @Test
    public void testHoleCartesian() throws ProcessException {
        pikeOrHoleTest(new double[]{2.5, 4.5}, 9, 9, 5, -1, new double[]{1, 0.5, 3}, CARTESIAN_CRS,
                       /*resolution = 1 -> */  108.27287817001343,  53.72712182998657,
                       /*resolution = 0.5 -> */27.068219542503357, 13.431780457496643,
                       /*resolution = 3 -> */  974.4559035301208,  483.54409646987915);
    }

    /**
     * Test results from hole volume computing in geographic space.
     */
    @Test
    public void testHoleGeographic() throws ProcessException {
        pikeOrHoleTest(new double[]{2.5, 4.5}, 9, 9, 5, -1, new double[]{1, 0.5, 3}, CommonCRS.defaultGeographic(),
                       /*resolution = 1 -> */  1.3270196335583735E12,  6.590783448178241E11,
                       /*resolution = 0.5 -> */3.328269445954914E11,   1.6519189509155847E11,
                       /*resolution = 3 -> */  1.1535336449348945E13,  5.770442307446776E12);
    }


    /**
     * Test process.
     *
     * @param altitudes altitude[0] = geometry altitude, altitude[1] = ceiling value.
     * @param imageWidth coverage DEM width.
     * @param imageHeight coverage DEM height.
     * @param basicImageValue value use to create pike or hole see fillImageWithPikeOrHole() method.
     * @param imageStep value use to create pike or hole see fillImageWithPikeOrHole() method.
     * @param resolution coverage resolution
     * @param crs space test
     * @param expectedResults test results. 2 results for each resolution. If n = resolution number. expectedResult lenght = 2 * n.
     * @throws ProcessException
     */
    private void pikeOrHoleTest(double[] altitudes, final int imageWidth, final int imageHeight, final int basicImageValue, final int imageStep,
                                final double[] resolution, final CoordinateReferenceSystem crs, final double ...expectedResults ) throws ProcessException {

        // index for resolution table and expected results.
        int expResult = 0;
        // create appropriate image for test.
        final BufferedImage buff = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_GRAY);
        // fill image by value to create a pikes or a hole at the image center.
        fillImageWithPikeOrHole(buff, basicImageValue, imageStep);

        // default coverage envelope.
        GeneralEnvelope env = new GeneralEnvelope(crs);

        for (int resId = 0; resId < resolution.length; resId++) {
            final double res  = resolution[resId];
            final double maxx = imageWidth  * res;
            final double maxy = imageHeight * res;

            // initialize coverage envelope coordinate.
            env.setEnvelope(0 ,0, maxx, maxy);

            // create appropriate gridcoverage reader.
            GridCoverageReader gcrTest = new GridCovReaderTest(buff, env);

            // create geometry.
            Geometry geomTest = getGeometry(0, 0, 0, maxy, maxx, maxy, maxx, 0, 0, 0);

            // create builder to test process
            ComputeVolumeBuilder cvb = new ComputeVolumeBuilder(gcrTest, geomTest, 0);

            int geomAltiId = 0;
            int ceilAltId  = 1;

            while (ceilAltId >= 0) {
                cvb.setAnotherCeiling(altitudes[ceilAltId]);
                cvb.setGeometryAltitude(altitudes[geomAltiId]);
                double volume = cvb.getVolume();
                assertEquals(expectedResults[expResult++], volume, TOLERANCE);

                // geometry altitude becomme ceil altitude and vice versa.
                ceilAltId--;
                geomAltiId++;
            }
        }
    }

    /**
     * Create a pike or hole DEM.<br/><br/>
     * for example : if we have an image 5x5 pixels and we choose basic value equal 1 and step equal 2.<br/>
     * The produced image will be : <br/><br/>
     *
     * &nbsp;&nbsp;_  _  _  _  _<br/>
     * |1|1|1|1|1|<br/>
     * |1|3|3|3|1|<br/>
     * |1|3|5|3|1|<br/>
     * |1|3|3|3|1|<br/>
     * |1|1|1|1|1|<br/>
     * Image define as a pike.<br/><br/>
     *
     * in contrary, if we have an image 6x6 pixels and we choose basic value equal 3 and step equal -1.<br/>
     * The produced image will be : <br/><br/>
     *
     * &nbsp;&nbsp;_  _  _  _  _  _<br/>
     * |3|3|3|3|3|3|<br/>
     * |3|2|2|2|2|3|<br/>
     * |3|2|1|1|2|3|<br/>
     * |3|2|1|1|2|3|<br/>
     * |3|2|2|2|2|3|<br/>
     * |3|3|3|3|3|3|<br/>
     * Image define as a hole.<br/><br/>
     *
     * @param img DEM. image which will be fill.
     * @param basicValue value of ground elevation.
     * @param step increase or decrease value of ground elevaton by this step.
     */
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
    /**
     * Create jts geometry with the given coordinates.
     *
     * @param coords geometry coordinates.
     * @return jts geometry with the given coordinates.
     */
    private Geometry getGeometry(double ...coords) {
        assert coords.length % 2 == 0;
        final int coordinateLength = coords.length / 2;
        final Coordinate[] polyPoint = new Coordinate[coordinateLength];
        for (int c = 0; c < coordinateLength; c++) {
            final int coordID = c << 1;
            polyPoint[c] = new Coordinate(coords[coordID], coords[coordID + 1]);
        }
        return GF.createPolygon(polyPoint);
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
                MathTransform paramToCoverageCrs = CRS.findOperation(param.getCoordinateReferenceSystem(), coverage.getCoordinateReferenceSystem(), null).getMathTransform();
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
                GridSampleDimension gsd = new GridSampleDimension("dim0", new Category[]{cat}, Units.METRE);

                gcb.setSampleDimensions(gsd);
                return gcb.getGridCoverage2D();
            } catch (Exception ex) {
                throw new CoverageStoreException(ex);
            }
        }
    }
}
