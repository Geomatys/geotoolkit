/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display2d.canvas;

import java.awt.geom.Rectangle2D;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import javax.measure.converter.ConversionException;
import javax.measure.unit.NonSI;

import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.display.canvas.GraphicVisitor;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.logging.Logging;

import org.opengis.coverage.CannotEvaluateException;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.operation.TransformException;

/**
 * A visitor which can be applied to the
 * {@link org.opengis.display.primitive.Graphic} objects of a scene and through
 * the {@code Graphic} objects, to the underlying
 * {@link org.opengis.feature.Feature} or
 * {@link org.opengis.coverage.grid.GridCoverage}.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractGraphicVisitor implements GraphicVisitor {

    public abstract void visit(ProjectedFeature feature, RenderingContext2D context, SearchAreaJ2D area);

    public abstract void visit(ProjectedCoverage coverage, RenderingContext2D context, SearchAreaJ2D area);

    /**
     * {@inheritDoc }
     */
    @Override
    public void startVisit() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void endVisit() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void visit(final Graphic graphic, final RenderingContext context, final SearchArea area) {

        if(graphic == null ) return;

        if(graphic instanceof ProjectedFeature){
            visit((ProjectedFeature)graphic, (RenderingContext2D)context, (SearchAreaJ2D)area);
        }else if(graphic instanceof ProjectedCoverage){
            visit((ProjectedCoverage)graphic, (RenderingContext2D)context, (SearchAreaJ2D)area);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isStopRequested() {
        return false;
    }

    /**
     * Returns the data values of the given coverage, or {@code null} if the
     * values can not be obtained.
     *
     * @return list : each entry contain a gridsampledimension and value associated.
     */
    protected static List<Entry<GridSampleDimension,Object>> getCoverageValues(final ProjectedCoverage gra, final RenderingContext2D context, final SearchAreaJ2D queryArea){

        final CoverageMapLayer layer = gra.getLayer();
        Envelope objBounds = context.getCanvasObjectiveBounds();
        CoordinateReferenceSystem objCRS = objBounds.getCoordinateReferenceSystem();
        TemporalCRS temporalCRS = CRS.getTemporalCRS(objCRS);
        if (temporalCRS == null) {
            /*
             * If there is no temporal range, arbitrarily select the latest date.
             * This is necessary otherwise the call to reader.read(...) will scan
             * every records in the GridCoverages table for the layer.
             */
            Envelope timeRange = layer.getBounds();
            if (timeRange != null) {
                temporalCRS = CRS.getTemporalCRS(timeRange.getCoordinateReferenceSystem());
                if (temporalCRS != null) {
                    try {
                        timeRange = CRS.transform(timeRange, temporalCRS);
                    } catch (TransformException e) {
                        // Should never happen since temporalCRS is a component of layer CRS.
                        Logging.unexpectedException(AbstractGraphicVisitor.class, "getCoverageValues", e);
                        return null;
                    }
                    final double lastTime = timeRange.getMaximum(0);
                    double day;
                    try {
                        // Arbitrarily use a time range of 1 day, to be converted in units of the temporal CRS.
                        day = NonSI.DAY.getConverterToAny(temporalCRS.getCoordinateSystem().getAxis(0).getUnit()).convert(1);
                    } catch (ConversionException e) {
                        // Should never happen since TemporalCRS use time units. But if it happen
                        // anyway, use a time range of 1 of whatever units the temporal CRS use.
                        Logging.unexpectedException(AbstractGraphicVisitor.class, "getCoverageValues", e);
                        day = 1;
                    }
                    objCRS = new DefaultCompoundCRS(objCRS.getName().getCode() + " + time", objCRS, temporalCRS);
                    final GeneralEnvelope merged = new GeneralEnvelope(objCRS);
                    merged.setSubEnvelope(objBounds, 0);
                    merged.setRange(objBounds.getDimension(), lastTime - day, lastTime);
                    objBounds = merged;
                }
            }
        }
        double[] resolution = context.getResolution();
        resolution = XArrays.resize(resolution, objCRS.getCoordinateSystem().getDimension());

        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(objBounds);
        param.setResolution(resolution);

        final GridCoverageReader reader = layer.getCoverageReader();
        final GridCoverage2D coverage;
        try {
            coverage = (GridCoverage2D) reader.read(0,param);
        } catch (CoverageStoreException ex) {
            context.getMonitor().exceptionOccured(ex, Level.INFO);
            return null;
        }

        if (coverage == null) {
            //no coverage for this BBOX
            return null;
        }

        final GeneralDirectPosition dp = new GeneralDirectPosition(objCRS);
        final Rectangle2D bounds2D = queryArea.getObjectiveShape().getBounds2D();
        dp.setOrdinate(0, bounds2D.getCenterX());
        dp.setOrdinate(1, bounds2D.getCenterY());

        float[] values = null;
        
        try{
            values = coverage.evaluate(dp, values);
        }catch(CannotEvaluateException ex){
            context.getMonitor().exceptionOccured(ex, Level.INFO);
            values = new float[coverage.getSampleDimensions().length];
            Arrays.fill(values, Float.NaN);
        }

        final List<Entry<GridSampleDimension,Object>> results = new ArrayList<Entry<GridSampleDimension, Object>>();
        for (int i=0; i<values.length; i++){
            final GridSampleDimension sample = coverage.getSampleDimension(i);
            results.add(new SimpleImmutableEntry<GridSampleDimension, Object>(sample, values[i]));
        }
        return results;
    }
}
