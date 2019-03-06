/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
import javax.measure.IncommensurableException;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.GeodeticObjectBuilder;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.GraphicVisitor;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.storage.coverage.CoverageExtractor;
import org.geotoolkit.storage.coverage.GridCoverageResource;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * A visitor which can be applied to the
 * {@link org.opengis.display.primitive.Graphic} objects of a scene and through
 * the {@code Graphic} objects, to the underlying
 * {@link org.opengis.feature.Feature} or
 * {@link org.geotoolkit.coverage.grid.GridCoverage}.
 *
 * @author Johann Sorel (Geomatys)
 * @module
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
    protected static List<Entry<SampleDimension,Object>> getCoverageValues(final ProjectedCoverage gra, final RenderingContext2D context, final SearchAreaJ2D queryArea) {

        final MapLayer layer = gra.getLayer();
        Envelope objBounds = context.getCanvasObjectiveBounds();
        CoordinateReferenceSystem objCRS = objBounds.getCoordinateReferenceSystem();
        TemporalCRS temporalCRS = CRS.getTemporalComponent(objCRS);
        if (temporalCRS == null) {
            /*
             * If there is no temporal range, arbitrarily select the latest date.
             * This is necessary otherwise the call to reader.features(...) will scan
             * every records in the GridCoverages table for the layer.
             */
            Envelope timeRange = layer.getBounds();
            if (timeRange != null) {
                temporalCRS = CRS.getTemporalComponent(timeRange.getCoordinateReferenceSystem());
                if (temporalCRS != null) {
                    try {
                        timeRange = Envelopes.transform(timeRange, temporalCRS);
                    } catch (TransformException e) {
                        // Should never happen since temporalCRS is a component of layer CRS.
                        Logging.unexpectedException(null, AbstractGraphicVisitor.class, "getCoverageValues", e);
                        return null;
                    }
                    final double lastTime = timeRange.getMaximum(0);
                    double day;
                    try {
                        // Arbitrarily use a time range of 1 day, to be converted in units of the temporal CRS.
                        day = Units.DAY.getConverterToAny(temporalCRS.getCoordinateSystem().getAxis(0).getUnit()).convert(1);
                    } catch (IncommensurableException e) {
                        // Should never happen since TemporalCRS use time units. But if it happen
                        // anyway, use a time range of 1 of whatever units the temporal CRS use.
                        Logging.unexpectedException(null, AbstractGraphicVisitor.class, "getCoverageValues", e);
                        day = 1;
                    }
                    try {
                        objCRS = new GeodeticObjectBuilder().addName(objCRS.getName().getCode() + " + time")
                                .createCompoundCRS(objCRS, temporalCRS);
                    } catch (FactoryException ex) {
                        throw new IllegalStateException(ex);
                    }
                    final GeneralEnvelope merged = new GeneralEnvelope(objCRS);
                    merged.subEnvelope(0, objBounds.getDimension()).setEnvelope(objBounds);
                    merged.setRange(objBounds.getDimension(), lastTime - day, lastTime);
                    objBounds = merged;
                }
            }
        }
        double[] resolution = context.getResolution();
        resolution = ArraysExt.resize(resolution, objCRS.getCoordinateSystem().getDimension());

        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(objBounds);
        param.setResolution(resolution);

        GridCoverage2D coverage = null;
        try {
            final Resource resource = layer.getResource();
            if (resource instanceof GridCoverageResource) {
                final GridCoverageResource ref = (GridCoverageResource) resource;
                final GridCoverageReader reader = ref.acquireReader();
                coverage = (GridCoverage2D) reader.read(param);
                ref.recycle(reader);
            }
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

        final List<SampleDimension> dims = coverage.getSampleDimensions();
        float[] values = null;

        try{
            values = coverage.evaluate(dp, values);
        }catch(CannotEvaluateException ex){
            context.getMonitor().exceptionOccured(ex, Level.INFO);
            values = new float[dims.size()];
            Arrays.fill(values, Float.NaN);
        }

        final List<Entry<SampleDimension,Object>> results = new ArrayList<>();
        for (int i=0; i<values.length; i++){
            final SampleDimension sample = dims.get(i);
            results.add(new SimpleImmutableEntry<SampleDimension, Object>(sample, values[i]));
        }
        return results;
    }


    /**
     *
     * @param projectedCoverage
     * @param context
     * @param area
     * @return
     * @throws CoverageStoreException
     * @throws TransformException
     */
    protected static CoverageExtractor.Ray rayExtraction(ProjectedCoverage projectedCoverage, RenderingContext2D context, SearchAreaJ2D area)
            throws CoverageStoreException, TransformException {

        //point in objective CRS
        final GeneralDirectPosition dp = new GeneralDirectPosition(context.getObjectiveCRS2D());
        final Rectangle2D bounds2D = area.getObjectiveShape().getBounds2D();
        dp.setOrdinate(0, bounds2D.getCenterX());
        dp.setOrdinate(1, bounds2D.getCenterY());

        final MapLayer layer = projectedCoverage.getLayer();
        final Resource resource = layer.getResource();
        if (resource instanceof GridCoverageResource) {
            final GridCoverageResource covRef = (GridCoverageResource) resource;
            GridCoverageReader reader = null;
            try {
                reader = covRef.acquireReader();
                final Envelope canvasObjective = context.getCanvasObjectiveBounds();
                final int canvasNbDim = canvasObjective.getDimension();

                //fix resolution array
                double[] resolution = new double[canvasNbDim];
                resolution[0] = context.getResolution()[0];
                resolution[1] = context.getResolution()[1];
                for (int i = 2; i < canvasNbDim; i++) {
                    resolution[i] = 1.0;
                }

                final GridCoverageReadParam param = new GridCoverageReadParam();
                param.setDeferred(true);
                param.setEnvelope(canvasObjective);
                param.setResolution(resolution);
                return CoverageExtractor.rayExtraction(dp, reader, param);
            } finally {
                if (reader != null) {
                    covRef.recycle(reader);
                }
            }
        } else {
            throw new CoverageStoreException("Resource is not a coverage.");
        }
    }
}
