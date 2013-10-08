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
import com.vividsolutions.jts.geom.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.RenderedImage;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.unit.BaseUnit;
import javax.measure.unit.SI;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.measure.Units;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.geometry.Envelopes;
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
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;

/**
 * 
 *
 * @author Remi Marechal (Geomatys).
 */
public class ComputeVolumeProcess extends AbstractProcess {
    /**
     * Default measure unit use to compute volume.
     */
    private final static BaseUnit<Length> METER = SI.METRE;
    
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
        final Double zMinCeil             = value(GEOMETRY_ALTITUDE       , inputParameters);
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
            final Envelope envGeom = jtsGeom.getEnvelopeInternal();
            final Envelope2D envGeom2D = new Envelope2D(geomCRS, envGeom.getMinX(), envGeom.getMinY(), envGeom.getWidth(), envGeom.getHeight());
//            // check if read param envelop is a 4x4 pixels size in minimum to interpol values.
//            final MathTransform readerCrsToGrid = covGridGeom.getGridToCRS(PixelInCell.CELL_CORNER).inverse();
//            
//            final MathTransform geomCRSToCovGrid = MathTransforms.concatenate(geomCRSToCov, readerCrsToGrid);
//            
//            final Envelope2D envGeom2D;
//            // geom envelope in grid space
//            Envelope readGridEnv = JTS.transform(envGeom, geomCRSToCovGrid);
//            if (readGridEnv.getWidth() < 4 || readGridEnv.getHeight() < 4) {
//                // impossible to interpolate value from area with width or height lower than 4 pixels.
//                final GridEnvelope extend = covGridGeom.getExtent();
//                
//                // intersect with coverage extend.
//                final int minGridX = (int) Math.max(readGridEnv.getMinX() - 4, extend.getLow(0));
//                final int minGridY = (int) Math.max(readGridEnv.getMinY() - 4, extend.getLow(1));
//                final int maxGridX = (int) Math.min(minGridX + readGridEnv.getWidth() + 8, (extend.getHigh(0) + 1));// +1 because gethight() method return inclusive upper index. 
//                final int maxGridY = (int) Math.min(minGridY + readGridEnv.getHeight() + 8, extend.getHigh(1) + 1);
//                final Envelope2D rGE = new Envelope2D(null, minGridX, minGridY, 
//                                                      maxGridX - minGridX, maxGridY - minGridY);
//                
//                final GeneralEnvelope ge = Envelopes.transform(geomCRSToCovGrid.inverse(), rGE);
//                envGeom2D = new Envelope2D(geomCRS, ge.getLower(0), ge.getLower(1), ge.getSpan(0), ge.getSpan(1));
//            } else {
////                envGeom2D = JTS.getEnvelope2D(envGeom, geomCRS);
//                envGeom2D = new Envelope2D(geomCRS, envGeom.getMinX(), envGeom.getMinY(), envGeom.getWidth(), envGeom.getHeight());
//            }
            
            
            final GridCoverageReadParam gcrp = new GridCoverageReadParam();
            gcrp.setEnvelope(envGeom2D, geomCRS);
            /*******************************************/
            final GridCoverage2D dem      = (GridCoverage2D) gcReader.read(bandIndex, gcrp);
            final GridSampleDimension gsd = dem.getSampleDimension(bandIndex);
            
            final MathTransform1D zmt = gsd.getSampleToGeophysics();
            if (zmt == null) {
                throw new ProcessException("you should stipulate MathTransform1D from sampleDimension to geophysic.", this, null);
            }
            
            final GridGeometry2D gg2d = dem.getGridGeometry();
            
            final InterpolationCase interpolationChoice;
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
            
            final MathTransform gridToCrs = gg2d.getGridToCRS(PixelInCell.CELL_CORNER);
            final CoordinateSystem destCS = covCrs.getCoordinateSystem();
            final int destDim             = destCS.getDimension();
            final RenderedImage mnt       = dem.getRenderedImage();
            
//            // debug
//            interpolationChoice = InterpolationCase.NEIGHBOR;
            
            final Interpolation interpol  = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(mnt), interpolationChoice, 0);
            
            final MathTransform gridToGeom = MathTransforms.concatenate(gridToCrs, covToGeomCRS);
            
            // axis unity study
            final UnitConverter[] unitConverters = new UnitConverter[destDim + 1];
            for (int d = 0; d < destDim; d++) {
                final CoordinateSystemAxis csA = destCS.getAxis(d);
                unitConverters[d]              = csA.getUnit().getConverterToAny(METER);
            }
            // add converter from sample dimension unit.
            unitConverters[destDim] = gsd.getUnits().getConverterToAny(METER);

            // step pixel area calculator
            final StepPixelAreaCalculator stePixCalculator;
            
            if (covCrs instanceof GeographicCRS) {
                // we project point in a crs which we know system axis to compute exhaustively orthodromic distance.
                // we could cast datum in geodeticDatum because in geographicCRS the datum is only GeodeticDatum type.
                final CoordinateReferenceSystem knowCRS = new DefaultGeographicCRS((GeodeticDatum) CRS.getDatum(covCrs), DefaultEllipsoidalCS.GEODETIC_2D);
                final MathTransform gridToKnowCRS       = MathTransforms.concatenate(gridToCrs.inverse(), CRS.findMathTransform(covCrs, knowCRS));
                final GeodeticCalculator geoCalc        = new GeodeticCalculator(knowCRS);
                stePixCalculator                        = new GeographicStepPixelAreaCalculator(PIXELSTEP, unitConverters, geoCalc, gridToKnowCRS);
                
            } else {
                if (destCS instanceof CartesianCS) {
                    
                    // resolution
                    final double[] resolution = gg2d.getResolution();
                    
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

            while (pixPoint[1] < maxy) {
                pixPoint[0] = debx;
                while (pixPoint[0] < maxx) {
                    // project point in geomtry CRS
                    gridToGeom.transform(pixPoint, 0, geomPoint, 0, 1);
                    
                    // test if point is within geometry.
                    coords.setOrdinate(0, geomPoint[0]);
                    coords.setOrdinate(1, geomPoint[1]);
//                    System.out.println("point : ("+geomPoint[0]+", "+geomPoint[1]+")");
                    if (jtsGeom.contains(gf.createPoint(coords))) {
                        //get interpolate value
                        double h = interpol.interpolate(pixPoint[0], pixPoint[1], bandIndex);
//                        System.out.println("h = "+h);
                        //projet h in geophysic value
                        h = zmt.transform(h);
                        //convert in meter
                        h = unitConverters[destDim].convert(h);
                        // en une instruction et ou
                        if (positiveSens) {
                            
                            if (h > zGroundCeiling) {
                                // add in volum
                                volume += (Math.min(Math.abs(h - zGroundCeiling), Math.abs(zMaxCeiling - zGroundCeiling))) * stePixCalculator.computeStepPixelArea(pixPoint);
                            }
                            
                        } else {
                            
                            if (h < zGroundCeiling) {
                                // add in volum
                                volume += (Math.min(Math.abs(h - zGroundCeiling), Math.abs(zMaxCeiling - zGroundCeiling))) * stePixCalculator.computeStepPixelArea(pixPoint);
                            }
                        }
                        
//                        // add in volum
//                        volume += Math.min(Math.abs(h), Math.abs(zMaxCeiling)) * stePixCalculator.computeStepPixelArea(pixPoint); 
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
        protected final double pixelStep; 
        
        /**
         * Table witch contain unity converter for each {@link CoordinateSystemAxis} from destination {@link CoordinateReferenceSystem}. 
         */
        protected final UnitConverter[] unitConverters;
        
        protected StepPixelAreaCalculator(final double pixelStep, final UnitConverter[] unitConverters) {
            this.pixelStep      = pixelStep;
            this.unitConverters = unitConverters;
        }
        
        /**
         * Compute area of pixel step in meters² from its grid position.
         * 
         * @param position position in grid space.
         * @return area in m².
         */
        protected abstract double computeStepPixelArea(final double ...position) throws TransformException;
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
         * @param pixelStep pixel fraction value which is the deplacement in x and y grid axis direction.
         * @param unitConverters table of {@link UnitConverter} for each axis from CRS.
         * @param resolution resolution from {@link GridGeometry2D#getResolution() }.
         */
        CartesianStepPixelAreaCalculator(final double pixelStep, final UnitConverter[] unitConverters, final double[] resolution) {
            super(pixelStep, unitConverters);
            stepPixelArea = unitConverters[0].convert(resolution[0] * pixelStep) * unitConverters[1].convert(resolution[1] * pixelStep);
            assert stepPixelArea > 0 : "pixel area should be positive.";
        }

        /**
         * {@inheritDoc }.
         * In this implementation position parameter has no impact on area computing  in cartesian space
         * because all pixel step have same area. 
         */
        @Override
        protected double computeStepPixelArea(double ...position) {
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
         * Create a calculator to cartesian space.
         * 
         * @param pixelStep pixel fraction value which is the deplacement in x and y grid axis direction.
         * @param unitConverters table of {@link UnitConverter} for each axis from CRS.
         * @param resolution resolution from {@link GridGeometry2D#getResolution() }.
         */
        GeographicStepPixelAreaCalculator(final double pixelStep, final UnitConverter[] unitConverters, final GeodeticCalculator geodeticCalculator, final MathTransform gridToCrs) {
            super(pixelStep, unitConverters);
            this.gridToCrs       = gridToCrs;
            this.lowGridPosition = new double[2];
            this.upGridPosition  = new double[2];
            this.lowCRSPosition  = new double[2];
            this.upCRSPosition   = new double[2];
            this.geoCalc         = geodeticCalculator;
        }

        /**
         * {@inheritDoc }.
         * In this implementation position parameter has no impact on area computing 
         * because of cartesian space and all pixel step have same area. 
         */
        @Override
        protected double computeStepPixelArea(double ...position) throws TransformException {
            // compute on grid x axis
            lowGridPosition[0] = position[0] - pixelStep;
            lowGridPosition[1] = position[1];
            upGridPosition[0]  = position[0] + pixelStep;
            upGridPosition[1]  = position[1];
            gridToCrs.transform(lowGridPosition, 0, lowCRSPosition, 0, 2);
            gridToCrs.transform(upGridPosition, 0, upCRSPosition, 0, 2);
            
            // compute distance on grid x projected axis
            geoCalc.setStartingGeographicPoint(lowCRSPosition[0], lowCRSPosition[1]);
            geoCalc.setDestinationGeographicPoint(upCRSPosition[0], upCRSPosition[1]);
            final double distX = unitConverters[0].convert(geoCalc.getOrthodromicDistance());
            
            // compute on y grid axis
            lowGridPosition[0] = position[0];
            lowGridPosition[1] = position[1] - pixelStep;
            upGridPosition[0]  = position[0];
            upGridPosition[1]  = position[1] + pixelStep;
            gridToCrs.transform(lowGridPosition, 0, lowCRSPosition, 0, 2);
            gridToCrs.transform(upGridPosition, 0, upCRSPosition, 0, 2);
            
            // compute distance on grid y projected axis
            geoCalc.setStartingGeographicPoint(lowCRSPosition[0], lowCRSPosition[1]);
            geoCalc.setDestinationGeographicPoint(upCRSPosition[0], upCRSPosition[1]);
            final double distY = unitConverters[1].convert(geoCalc.getOrthodromicDistance());
            return distX * distY;
        }        
    }
}
