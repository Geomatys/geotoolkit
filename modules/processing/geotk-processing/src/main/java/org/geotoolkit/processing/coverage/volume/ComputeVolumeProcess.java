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

import java.awt.image.RenderedImage;
import javax.measure.IncommensurableException;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Length;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.IncompleteGridGeometryException;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.measure.Units;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.ResampleBorderComportement;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.referencing.GeodeticCalculator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;

/**
 * Process which compute volume from DEM (Digital Elevation Model) got
 * by {@link ComputeVolumeDescriptor#IN_GRIDCOVERAGE_READER GridCoverageReader}, on area defined by
 * a {@link ComputeVolumeDescriptor#IN_JTSGEOMETRY Geometry} and
 * between 2 elevation value define by {@link ComputeVolumeDescriptor#GEOMETRY_ALTITUDE geometry altitude}
 * and {@link ComputeVolumeDescriptor#IN_MAX_ALTITUDE_CEILING maximum ceiling}.<br/><br/>
 *
 * Note : {@link ComputeVolumeDescriptor#GEOMETRY_ALTITUDE geometry altitude} may be lesser than
 * {@link ComputeVolumeDescriptor#IN_MAX_ALTITUDE_CEILING maximum ceiling}, to compute lock volume for example.
 *
 * @author Remi Marechal (Geomatys).
 */
public class ComputeVolumeProcess extends AbstractProcess {

    /**
     * Default measure unit use to compute volume (Meter).
     */
    private final static Unit<Length> METER = Units.METRE;

    /**
     * Step move on grid X and grid Y axis.<br/>
     * To compute volume we comute sum of 1/16 pixel area.
     */
    private final static double PIXELSTEP = 0.25;

    ComputeVolumeProcess(final ParameterValueGroup input) {
        super(ComputeVolumeDescriptor.INSTANCE, input);
    }

    /**
     *
     * @param gcReader elevation model coverage reader
     * @param jtsGeom polygon area to process
     * @param geomCRS geometry crs
     * @param bIndex coverage band index
     * @param zMinCeil coverage min ceiling
     * @param zMaxCeil coverage max ceiling
     */
    public ComputeVolumeProcess(GridCoverageReader gcReader, Geometry jtsGeom,
            CoordinateReferenceSystem geomCRS, Integer bIndex, Double zMinCeil, double zMaxCeil){
        super(ComputeVolumeDescriptor.INSTANCE, asParameters(gcReader, jtsGeom, geomCRS, bIndex, zMinCeil, zMaxCeil));
    }

    private static ParameterValueGroup asParameters(GridCoverageReader gcReader, Geometry jtsGeom,
            CoordinateReferenceSystem geomCRS, Integer bIndex, Double zMinCeil, double zMaxCeil){
        final Parameters params = Parameters.castOrWrap(ComputeVolumeDescriptor.INPUT_DESC.createValue());
        params.getOrCreate(ComputeVolumeDescriptor.IN_GRIDCOVERAGE_READER).setValue(gcReader);
        params.getOrCreate(ComputeVolumeDescriptor.IN_JTSGEOMETRY).setValue(jtsGeom);
        if(geomCRS!=null) params.getOrCreate(ComputeVolumeDescriptor.IN_GEOMETRY_CRS).setValue(geomCRS);
        if(bIndex!=null) params.getOrCreate(ComputeVolumeDescriptor.IN_INDEX_BAND).setValue(bIndex);
        if(zMinCeil!=null) params.getOrCreate(ComputeVolumeDescriptor.IN_GEOMETRY_ALTITUDE).setValue(zMinCeil);
        params.getOrCreate(ComputeVolumeDescriptor.IN_MAX_ALTITUDE_CEILING).setValue(zMaxCeil);
        return params;
    }

    /**
     * Execute process now.
     *
     * @return result volume
     * @throws ProcessException
     */
    public Geometry[] executeNow() throws ProcessException {
        execute();
        return (Geometry[]) outputParameters.parameter(ComputeVolumeDescriptor.OUT_VOLUME_RESULT.getName().getCode()).getValue();
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameters", inputParameters);

        final GridCoverageReader gcReader = inputParameters.getValue(ComputeVolumeDescriptor.IN_GRIDCOVERAGE_READER);
        final Geometry jtsGeom            = inputParameters.getValue(ComputeVolumeDescriptor.IN_JTSGEOMETRY        );
        CoordinateReferenceSystem geomCRS = inputParameters.getValue(ComputeVolumeDescriptor.IN_GEOMETRY_CRS       );
        final Integer bIndex              = inputParameters.getValue(ComputeVolumeDescriptor.IN_INDEX_BAND         );
        final Double zMinCeil             = inputParameters.getValue(ComputeVolumeDescriptor.IN_GEOMETRY_ALTITUDE  );
        final double zMaxCeiling          = inputParameters.getValue(ComputeVolumeDescriptor.IN_MAX_ALTITUDE_CEILING);

        final int bandIndex               = (bIndex   == null) ? 0 : (int) bIndex;
        final double zGroundCeiling       = (zMinCeil == null) ? 0 : (double) zMinCeil;
        final boolean positiveSens        = zGroundCeiling < zMaxCeiling;

        if (zGroundCeiling == zMaxCeiling) {
            outputParameters.getOrCreate(ComputeVolumeDescriptor.OUT_VOLUME_RESULT).setValue(0);
            return;
        }

        try {

            /*
             * geomCRS attribut should be null, we looking for find another way to define geometry CoordinateReferenceSystem.
             * It may be already stipulate in JTS geometry.
             */
            if (geomCRS == null) {
                geomCRS = JTS.findCoordinateReferenceSystem(jtsGeom);
            }

            final GridGeometry covGridGeom = gcReader.getGridGeometry();

            /*
             * If we have no CRS informations from geometry we consider that geometry is defined in same crs as Coverage.
             */
            final CoordinateReferenceSystem covCrs = covGridGeom.getCoordinateReferenceSystem();
            if (geomCRS == null) {
                geomCRS = covCrs;
            }

            final MathTransform covToGeomCRS = CRS.findOperation(covCrs, geomCRS, null).getMathTransform();

            //-- next read only interest area.
            final Envelope envGeom     = jtsGeom.getEnvelopeInternal();
            final Envelope2D envGeom2D = new Envelope2D(geomCRS, envGeom.getMinX(), envGeom.getMinY(), envGeom.getWidth(), envGeom.getHeight());

            final GridCoverageReadParam gcrp = new GridCoverageReadParam();
            gcrp.setEnvelope(envGeom2D, geomCRS);
            /*******************************************/

            final GridCoverage2D dem      = (GridCoverage2D) gcReader.read(gcrp);
            final GridSampleDimension gsd = dem.getSampleDimensions().get(bandIndex);

            final MathTransform1D zmt     = gsd.getSampleToGeophysics();
            if (zmt == null) {
                throw new ProcessException("you should stipulate MathTransform1D from sampleDimension to geophysic.", this, null);
            }

            final GridGeometry2D gg2d = dem.getGridGeometry();

            InterpolationCase interpolationChoice;
            //-- adapt interpolation in function of grid extend
            final GridExtent gridEnv2D = gg2d.getExtent2D();
            final long gWidth = gridEnv2D.getSize(0);
            final long gHeight = gridEnv2D.getSize(1);

            if (gWidth < 1 || gHeight < 1) {
                outputParameters.getOrCreate(ComputeVolumeDescriptor.OUT_VOLUME_RESULT).setValue(0);
                return;
            } else if (gWidth < 2 || gHeight < 2) {
                interpolationChoice = InterpolationCase.NEIGHBOR;
            } else if (gWidth < 4 || gHeight < 4) {
                interpolationChoice = InterpolationCase.BILINEAR;
            } else {
                assert gWidth >= 4 && gHeight >= 4; //-- paranoiac assert
                interpolationChoice = InterpolationCase.BICUBIC;
            }

            final MathTransform gridToCrs  = gg2d.getGridToCRS(PixelInCell.CELL_CENTER);
            final CoordinateSystem destCS  = covCrs.getCoordinateSystem();
            final RenderedImage mnt        = dem.getRenderedImage();

            final Interpolation interpol   = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(mnt), interpolationChoice, 0, ResampleBorderComportement.EXTRAPOLATION, null);

            final MathTransform gridToGeom = MathTransforms.concatenate(gridToCrs, covToGeomCRS);
            final StepPixelAreaCalculator stePixCalculator;

            if (covCrs instanceof GeographicCRS) {
                stePixCalculator = new GeographicStepPixelAreaCalculator(PIXELSTEP, covCrs, gridToCrs);
            } else {
                if (destCS instanceof CartesianCS) {

                    //-- resolution
                    double[] resolution = null;
                    try {
                        resolution = gg2d.getResolution(false);
                    } catch (IncompleteGridGeometryException ex){}

                    final int dimDestCS                  = destCS.getDimension();
                    final int destDim                    = destCS.getDimension();
                    final UnitConverter[] unitConverters = new UnitConverter[dimDestCS];
                    for (int d = 0; d < destDim; d++) {
                        final CoordinateSystemAxis csA   = destCS.getAxis(d);
                        unitConverters[d]                = csA.getUnit().getConverterToAny(METER);
                    }

                    //-- pixel step computing in m²
                    stePixCalculator          = new CartesianStepPixelAreaCalculator(PIXELSTEP, unitConverters, resolution);

                } else {
                    throw new ProcessException("Coordinate reference system configuration not supported. CRS should be instance of geographic crs or has a cartesian coordinate system.", this, null);
                }
            }

            //-- geometry factory to create point at n step to test if it is within geometry
            final GeometryFactory gf = new GeometryFactory();

            //-- coordinate to test if point is within geom
            final Coordinate coords = new Coordinate();

            //-- image attributs
            final double minx        = mnt.getMinX() - 0.5;
            final double miny        = mnt.getMinY() - 0.5;
            final double maxx        = minx + mnt.getWidth();
            final double maxy        = miny + mnt.getHeight();
            final double debx        = minx + PIXELSTEP / 2.0;
            final double[] pixPoint  = new double[]{debx, miny + PIXELSTEP / 2.0};
            final double[] geomPoint = new double[2];

            double volume = 0;

            final UnitConverter hconverter;
            if(gsd.getUnits() == null || Units.UNITY.equals(gsd.getUnits())){
                //-- unit unknowed, assume it's meters already
                hconverter = METER.getConverterTo(METER);
            }else{
                hconverter = gsd.getUnits().getConverterToAny(METER);
            }

            while (pixPoint[1] < maxy) {
                if(isCanceled()) break;
                pixPoint[0] = debx;
                while (pixPoint[0] < maxx) {
                    if(isCanceled()) break;
                    //-- project point in geomtry CRS
                    gridToGeom.transform(pixPoint, 0, geomPoint, 0, 1);

                    //-- test if point is within geometry.
                    coords.setOrdinate(0, geomPoint[0]);
                    coords.setOrdinate(1, geomPoint[1]);

                    if (jtsGeom.contains(gf.createPoint(coords))) {

                        //-- get interpolate value
                        double h = interpol.interpolate(pixPoint[0], pixPoint[1], bandIndex);

                        //-- projet h in geophysic value
                        h = zmt.transform(h);

                        //-- convert in meter
                        h = hconverter.convert(h);

                        //-- Verify that h value found is in appropriate interval.
                        if ((positiveSens && h > zGroundCeiling) || (!positiveSens && h < zGroundCeiling)) {
                            //-- add in volum
                            volume += (Math.min(Math.abs(h - zGroundCeiling), Math.abs(zMaxCeiling - zGroundCeiling))) * stePixCalculator.computeStepPixelArea(pixPoint);
                        }
                    }
                    pixPoint[0] += PIXELSTEP;
                }
                pixPoint[1] += PIXELSTEP;
            }
            outputParameters.getOrCreate(ComputeVolumeDescriptor.OUT_VOLUME_RESULT).setValue(volume);
        } catch (Exception ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

    /**
     * Compute area of pixel step.
     */
    private abstract class StepPixelAreaCalculator {

        /**
         * Pixel fraction.
         */
        protected final double pixelWidth;

        protected StepPixelAreaCalculator(final double pixelWidth) {
            this.pixelWidth      = pixelWidth;
        }

        /**
         * Compute area of pixel step in meters² from its grid position.
         *
         * @param pixelPosition position in grid space.
         * @return area in m².
         */
        protected abstract double computeStepPixelArea(final double ...pixelPosition) throws TransformException;
    }

    /**
     * Compute area of a pixel step in a Cartesian space.
     */
    private final class CartesianStepPixelAreaCalculator extends StepPixelAreaCalculator {

        /**
         * In cartesian space all pixel step area have same area.<br/>
         * Compute is define one time at calculator building.
         */
        private final double stepPixelArea;

        /**
         * Create a calculator to cartesian space.
         *
         * @param pixelWidth pixel fraction value which is the deplacement in x and y grid axis direction.
         * @param unitConverters table of {@link UnitConverter} for each axis from CRS.
         * @param resolution resolution from {@link GridGeometry2D#getResolution() }.
         */
        CartesianStepPixelAreaCalculator(final double pixelWidth, final UnitConverter[] unitConverters, final double[] resolution) {
            super(pixelWidth);
            stepPixelArea = unitConverters[0].convert(resolution[0] * pixelWidth) * unitConverters[1].convert(resolution[1] * pixelWidth);
            assert stepPixelArea > 0 : "pixel area should be positive.";
        }

        /**
         * {@inheritDoc }.
         * In this implementation position parameter has no impact on area computing  in cartesian space
         * because all pixel step have same area.
         */
        @Override
        protected double computeStepPixelArea(double ...pixelPosition) {
            return stepPixelArea;
        }
    }

    /**
     * Compute area of a pixel step in a Geographic space.
     */
    private final class GeographicStepPixelAreaCalculator extends StepPixelAreaCalculator {

        /**
         * Calculator need to compute distance between to point on ellipsoid.
         */
        private final GeodeticCalculator geoCalc;

        /**
         * lower and upper position on a grid axis.
         */
        private final double[] lowGridPosition;
        private final double[] upGridPosition;

        /**
         * projected lower and upper position on a grid axis in {@link CoordinateReferenceSystem}.
         */
        private final double[] lowCRSPosition;
        private final double[] upCRSPosition;

        /**
         * {@link MathTransform} to project grid coordinate to crs coordinate.
         */
        private final MathTransform gridToCrs;

        /**
         * {@link UnitConverter} to convert unit from ellipsoid system axis.
         */
        private final UnitConverter ellConverter;


        /**
         * Create a calculator to cartesian space.
         *
         * @param pixelWidth pixel fraction value which is the deplacement in x and y grid axis direction.
         * @param unitConverters table of {@link UnitConverter} for each axis from CRS.
         * @param resolution resolution from {@link GridGeometry2D#getResolution() }.
         */
        GeographicStepPixelAreaCalculator(final double pixelWidth, final CoordinateReferenceSystem crs, final MathTransform gridToCrs) throws IncommensurableException {
            super(pixelWidth);
            this.gridToCrs       = gridToCrs;
            this.lowGridPosition = new double[2];
            this.upGridPosition  = new double[2];
            this.lowCRSPosition  = new double[2];
            this.upCRSPosition   = new double[2];
            this.geoCalc         = new GeodeticCalculator(crs);
            ellConverter         = geoCalc.getEllipsoid().getAxisUnit().getConverterToAny(METER);
        }

        /**
         * {@inheritDoc }.
         */
        @Override
        protected double computeStepPixelArea(double ...pixelPosition) throws TransformException {
            // compute on grid x axis
            lowGridPosition[0] = pixelPosition[0] - pixelWidth / 2;
            lowGridPosition[1] = pixelPosition[1];
            upGridPosition[0]  = pixelPosition[0] + pixelWidth / 2;
            upGridPosition[1]  = pixelPosition[1];
            gridToCrs.transform(lowGridPosition, 0, lowCRSPosition, 0, 1);
            gridToCrs.transform(upGridPosition, 0, upCRSPosition, 0, 1);

            // compute distance on grid x projected axis
            geoCalc.setStartingGeographicPoint(lowCRSPosition[0], lowCRSPosition[1]);
            geoCalc.setDestinationGeographicPoint(upCRSPosition[0], upCRSPosition[1]);
            final double distX = ellConverter.convert(geoCalc.getOrthodromicDistance());

            // compute on y grid axis
            lowGridPosition[0] = pixelPosition[0];
            lowGridPosition[1] = pixelPosition[1] - pixelWidth / 2;
            upGridPosition[0]  = pixelPosition[0];
            upGridPosition[1]  = pixelPosition[1] + pixelWidth / 2;
            gridToCrs.transform(lowGridPosition, 0, lowCRSPosition, 0, 1);
            gridToCrs.transform(upGridPosition, 0, upCRSPosition, 0, 1);

            // compute distance on grid y projected axis
            geoCalc.setStartingGeographicPoint(lowCRSPosition[0], lowCRSPosition[1]);
            geoCalc.setDestinationGeographicPoint(upCRSPosition[0], upCRSPosition[1]);
            final double distY = ellConverter.convert(geoCalc.getOrthodromicDistance());
            return distX * distY;
        }
    }
}
