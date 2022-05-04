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

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.util.List;
import java.util.concurrent.CancellationException;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.referencing.crs.PredefinedCRS;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.coverage.grid.SequenceType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;

/**
 * Test {@link ComputeVolumeProcess process} to compute volume from DEM.
 *
 * @author Remi Marechal (Geomatys).
 */
public strictfp class ComputeVolumeProcessTest extends org.geotoolkit.test.TestBase {

    /**
     * Test tolerance relative to the expected value.
     */
    private final static double TOLERANCE = 0.005;

    /**
     * {@link CoordinateReferenceSystem} to test compute volume process with data
     * from {@link PredefinedCRS} with cartesian {@link CoordinateSystem}.
     */
    private static final CoordinateReferenceSystem CARTESIAN_CRS = PredefinedCRS.CARTESIAN_2D;

    /**
     * {@link GeometryFactory} to create geometry to test process in differents way.
     */
    private final static GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();

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
     */
    private void basicTest(final int imageWidth, final int imageHeight, final double expectedValue,
                          final CoordinateReferenceSystem crs, final double ...envelopeAndGeomCoordinates) throws ProcessException {

        // create image adapted to test.
        final BufferedImage buff = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_GRAY);
        WritablePixelIterator pixIter = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).createWritable(buff);
        // fill image with value 1.
        while (pixIter.next()) {
            pixIter.setSample(0, 1);
        }

        // create coverage reader adapted for test.
        final double[] envCoords = new double[4];
        System.arraycopy(envelopeAndGeomCoordinates, 0, envCoords, 0, 4);
        GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setEnvelope(envCoords);
        TestResource gcrTest = new TestResource(buff, env);

        // define area where volume is computed.
        final double[] geomCoords = new double[envelopeAndGeomCoordinates.length - 4];
        System.arraycopy(envelopeAndGeomCoordinates, 4, geomCoords, 0, geomCoords.length);
        Geometry geomTest = getGeometry(geomCoords);
        double altiCeiling = 1;

        // create volume builder
        ComputeVolumeBuilder cvb = new ComputeVolumeBuilder(gcrTest, geomTest, altiCeiling);
        final double volume = cvb.getVolume();

        // test if volume computed is conform.
        assertEquals(expectedValue, volume, expectedValue * TOLERANCE);
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
     */
    private void altitudesTest(final CoordinateReferenceSystem crs, final double ...expectedResults) throws ProcessException {
        final BufferedImage buff = new BufferedImage(7, 7, BufferedImage.TYPE_BYTE_GRAY);
        WritablePixelIterator pixIter = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).createWritable(buff);
        while (pixIter.next()) {
            pixIter.setSample(0, 1);
        }
        GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setEnvelope(0 ,0, 7, 7);
        TestResource gcrTest = new TestResource(buff, env);
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
        double expected = expectedResults[0];
        assertEquals(expected, volume, expected * TOLERANCE);

        // change ceilings
        cvb.setAnotherCeiling(0.75);
        cvb.setGeometryAltitude(0.25);
        volume = cvb.getVolume();
        expected = expectedResults[1];
        assertEquals(expected, volume, expected * TOLERANCE);

        // change ceilings
        // negative sens
        cvb.setAnotherCeiling(0.75);
        cvb.setGeometryAltitude(1.25);
        volume = cvb.getVolume();
        expected = expectedResults[2];
        assertEquals(expected, volume, expected * TOLERANCE);
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
     * @param expectedResults test results. 2 results for each resolution. If n = resolution number. expectedResult length = 2 * n.
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
            TestResource gcrTest = new TestResource(buff, env);

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
                double expected = expectedResults[expResult++];
                assertEquals(expected, volume, expected * TOLERANCE);

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
        final WritablePixelIterator pixIter = new PixelIterator.Builder().setIteratorOrder(SequenceType.LINEAR).createWritable(img);
        while (idMinX < idMaxX && idMinY < idMaxY) {
            for (int y = idMinY; y < idMaxY; y++) {
                for (int x = idMinX; x < idMaxX; x++) {
                    pixIter.moveTo(x, y);
                    pixIter.setSample(0, value);
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
     * {@link GridCoverageResource} need to test {@link ComputeVolumeProcess} class.
     */
    private class TestResource extends AbstractGridCoverageResource {

        final GridCoverage coverage;

        TestResource(final RenderedImage image, final Envelope envelope){
            super(null, false);


            final SampleDimension.Builder builder = new SampleDimension.Builder();
            builder.setName(Names.createLocalName(null, null, "dim0"));
            builder.addQuantitative("val", -128, +128, null);
            final SampleDimension gsd = builder.build();

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setDomain(envelope);
            gcb.setValues(image);
            gcb.setRanges(gsd);
            coverage = gcb.build();
        }

        @Override
        public GridGeometry getGridGeometry() throws DataStoreException, CancellationException {
            return coverage.getGridGeometry();
        }

        @Override
        public List<SampleDimension> getSampleDimensions() throws DataStoreException, CancellationException {
            return coverage.getSampleDimensions();
        }

        @Override
        public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
            return coverage;
        }
    }
}
