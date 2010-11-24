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
import javax.measure.unit.Unit;

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
import org.geotoolkit.map.CoverageMapLayer;

import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;

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
    public void visit(Graphic graphic, RenderingContext context, SearchArea area) {

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
     * TODO: flesh out this explanation. Is the area clipped? what's with the 
     * columns?
     *
     * first column is the value : Float
     * second column is the unit : Unit
     */
    protected static Object[][] getCoverageValues(final ProjectedCoverage gra,  RenderingContext2D context, SearchAreaJ2D queryArea){

        final CoverageMapLayer layer = gra.getCoverageLayer();

        //find center of the selected area
        final Rectangle2D bounds2D = queryArea.getObjectiveShape().getBounds2D();
        final double centerX = bounds2D.getCenterX();
        final double centerY = bounds2D.getCenterY();
        final Envelope objBounds = context.getCanvasObjectiveBounds();
        final double[] resolution = new double[objBounds.getCoordinateReferenceSystem().getCoordinateSystem().getDimension()];
        resolution[0] = context.getResolution()[0];
        resolution[1] = context.getResolution()[1];

        final GridCoverageReader reader = layer.getCoverageReader();
        final GridCoverage2D coverage;

        final GridCoverageReadParam param = new GridCoverageReadParam();
        param.setEnvelope(objBounds);
        param.setResolution(resolution);

        try{
            coverage = (GridCoverage2D) reader.read(0,param);
        }catch(CoverageStoreException ex){
            ex.printStackTrace();
            return null;
        }

        if(coverage == null){
            //no coverage for this BBOX
            return null;
        }

        final GeneralDirectPosition dp = new GeneralDirectPosition(objBounds.getCoordinateReferenceSystem());
        dp.setOrdinate(0, centerX);
        dp.setOrdinate(1, centerY);

        final float[] values = new float[coverage.getNumSampleDimensions()];
        coverage.evaluate(dp,values);

        final Object[][] results = new Object[values.length][2];
        for(int i=0; i<values.length; i++){
            final float value = values[i];
            final GridSampleDimension sample = coverage.getSampleDimension(i);
            final Unit<?> unit = sample.getUnits();
            results[i][0] = value;
            results[i][1] = unit;
            return results;
        }

        return null;
    }
    
}
