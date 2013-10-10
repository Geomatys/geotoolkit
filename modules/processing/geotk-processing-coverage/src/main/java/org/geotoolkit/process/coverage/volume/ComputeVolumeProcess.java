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
package org.geotoolkit.process.coverage.volume;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.image.RenderedImage;
import javax.measure.converter.ConversionException;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.unit.BaseUnit;
import javax.measure.unit.SI;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;
import static org.geotoolkit.process.coverage.volume.ComputeVolumeDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.GeodeticCalculator;
import org.geotoolkit.referencing.operation.MathTransforms;
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
    private final static BaseUnit<Length> METER = SI.METRE;
    
    /**
     * Step move on grid X and grid Y axis.<br/>
     * To compute volume we comute sum of 1/16 pixel area.
     */
    private final static double PIXELSTEP = 0.25;
    
    ComputeVolumeProcess(final ParameterValueGroup input) {
        super(INSTANCE, input);
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    protected void execute() throws ProcessException {
        ArgumentChecks.ensureNonNull("inputParameters", inputParameters);

        final GridCoverageReader gcReader = value(IN_GRIDCOVERAGE_READER  , inputParameters);
        final Geometry jtsGeom            = value(IN_JTSGEOMETRY          , inputParameters);
        CoordinateReferenceSystem geomCRS = value(IN_GEOMETRY_CRS         , inputParameters);
        final Integer bIndex              = value(IN_INDEX_BAND           , inputParameters);
        final Double zMinCeil             = value(IN_GEOMETRY_ALTITUDE       , inputParameters);
        final double zMaxCeiling          = value(IN_MAX_ALTITUDE_CEILING , inputParameters);
        
        final int bandIndex               = (bIndex   == null) ? 0 : (int) bIndex;
        final double zGroundCeiling          = (zMinCeil == null) ? 0 : (double) zMinCeil;
        final boolean positiveSens        = zGroundCeiling < zMaxCeiling;
        
        if (zGroundCeiling == zMaxCeiling) {
            getOrCreate(OUT_VOLUME_RESULT, outputParameters).setValue(0);
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

            final GeneralGridGeometry covGridGeom = gcReader.getGridGeometry(bandIndex);

            /*
             * If we have no CRS informations from geometry we consider that geometry is defined in same crs as Coverage.
             */
            final CoordinateReferenceSystem covCrs = covGridGeom.getCoordinateReferenceSystem();
            if (geomCRS == null) {
                geomCRS = covCrs;
            }
            
            final MathTransform covToGeomCRS = CRS.findMathTransform(covCrs, geomCRS);
            
            // next read only interest area.
            final Envelope envGeom     = jtsGeom.getEnvelopeInternal();
            final Envelope2D envGeom2D = new Envelope2D(geomCRS, envGeom.getMinX(), envGeom.getMinY(), envGeom.getWidth(), envGeom.getHeight());

            final GridCoverageReadParam gcrp = new GridCoverageReadParam();
            gcrp.setEnvelope(envGeom2D, geomCRS);
            /*******************************************/
            
            final GridCoverage2D dem      = (GridCoverage2D) gcReader.read(bandIndex, gcrp);
            final GridSampleDimension gsd = dem.getSampleDimension(bandIndex);
            
            final MathTransform1D zmt     = gsd.getSampleToGeophysics();
            if (zmt == null) {
                throw new ProcessException("you should stipulate MathTransform1D from sampleDimension to geophysic.", this, null);
            }
            
            final GridGeometry2D gg2d = dem.getGridGeometry();
            
            InterpolationCase interpolationChoice;
            // adapt interpolation in function of grid extend
            final GridEnvelope2D gridEnv2D = gg2d.getExtent2D();
            final int gWidth               = gridEnv2D.getSpan(0);
            final int gHeight              = gridEnv2D.getSpan(1);
            
            if (gWidth < 1 || gHeight < 1) {
                getOrCreate(OUT_VOLUME_RESULT, outputParameters).setValue(0);
                return;
            } else if (gWidth < 2 || gHeight < 2) { 
                interpolationChoice = InterpolationCase.NEIGHBOR;
            } else if (gWidth < 4 || gHeight < 4) {
                interpolationChoice = InterpolationCase.BILINEAR;
            } else {
                assert gWidth >= 4 && gHeight >= 4; // paranoiac assert
                interpolationChoice = InterpolationCase.BICUBIC;
            }
            
            final MathTransform gridToCrs  = gg2d.getGridToCRS(PixelInCell.CELL_CORNER);
            final CoordinateSystem destCS  = covCrs.getCoordinateSystem();
            final RenderedImage mnt        = dem.getRenderedImage();
            
            final Interpolation interpol   = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(mnt), interpolationChoice, 0);
            
            final MathTransform gridToGeom = MathTransforms.concatenate(gridToCrs, covToGeomCRS);
            final StepPixelAreaCalculator stePixCalculator;
            
            if (covCrs instanceof GeographicCRS) {
                stePixCalculator = new GeographicStepPixelAreaCalculator(PIXELSTEP, covCrs, gridToCrs);
            } else {
                if (destCS instanceof CartesianCS) {
                    
                    // resolution
                    final double[] resolution = gg2d.getResolution();
                    
                    final int dimDestCS                  = destCS.getDimension();
                    final int destDim                    = destCS.getDimension();
                    final UnitConverter[] unitConverters = new UnitConverter[dimDestCS];
                    for (int d = 0; d < destDim; d++) {
                        final CoordinateSystemAxis csA   = destCS.getAxis(d);
                        unitConverters[d]                = csA.getUnit().getConverterToAny(METER);
                    }
                    
                    // pixel step computing in m²
                    stePixCalculator          = new CartesianStepPixelAreaCalculator(PIXELSTEP, unitConverters, resolution);
                    
                } else {
                    throw new ProcessException("Coordinate reference system configuration not supported. CRS should be instance of geographic crs or has a cartesian coordinate system.", this, null);
                }
            }
            
            //geometry factory to create point at n step to test if it is within geometry
            final GeometryFactory gf = new GeometryFactory();
            
            // coordinate to test if point is within geom
            final Coordinate coords = new Coordinate();
            
            // image attributs
            final int minx           = mnt.getMinX();
            final int miny           = mnt.getMinY();
            final int maxx           = minx + mnt.getWidth();
            final int maxy           = miny + mnt.getHeight();
            final double debx        = minx + PIXELSTEP / 2.0;
            final double[] pixPoint  = new double[]{debx, miny + PIXELSTEP / 2.0};
            final double[] geomPoint = new double[2];
            
            double volume = 0;
            
            final UnitConverter hconverter = gsd.getUnits().getConverterToAny(METER);

            while (pixPoint[1] < maxy) {
                pixPoint[0] = debx;
                while (pixPoint[0] < maxx) {
                    // project point in geomtry CRS
                    gridToGeom.transform(pixPoint, 0, geomPoint, 0, 1);
                    
                    // test if point is within geometry.
                    coords.setOrdinate(0, geomPoint[0]);
                    coords.setOrdinate(1, geomPoint[1]);
                    
                    if (jtsGeom.contains(gf.createPoint(coords))) {
                        
                        // get interpolate value
                        double h = interpol.interpolate(pixPoint[0], pixPoint[1], bandIndex);
                        
                        // projet h in geophysic value
                        h = zmt.transform(h);
                        
                        // convert in meter
                        h = hconverter.convert(h);
                        
                        // Verify that h value found is in appropriate interval.
                        if ((positiveSens && h > zGroundCeiling) || (!positiveSens && h < zGroundCeiling)) {
                            // add in volum
                            volume += (Math.min(Math.abs(h - zGroundCeiling), Math.abs(zMaxCeiling - zGroundCeiling))) * stePixCalculator.computeStepPixelArea(pixPoint);
                        }
                    }
                    pixPoint[0] += PIXELSTEP;
                }
                pixPoint[1] += PIXELSTEP;
            }
            getOrCreate(OUT_VOLUME_RESULT, outputParameters).setValue(volume);
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
        GeographicStepPixelAreaCalculator(final double pixelWidth, final CoordinateReferenceSystem crs, final MathTransform gridToCrs) throws ConversionException {
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
