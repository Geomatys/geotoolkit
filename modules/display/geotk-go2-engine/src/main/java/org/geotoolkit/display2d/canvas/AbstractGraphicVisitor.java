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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import javax.measure.unit.Unit;

import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.display.canvas.GraphicVisitor;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotools.coverage.io.CoverageReadParam;
import org.geotools.coverage.io.CoverageReader;
import org.geotoolkit.display2d.primitive.GraphicCoverageJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.referencing.CRS;

import org.opengis.display.primitive.Graphic;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * A visitor which can be applied to the 
 * {@link org.opengis.display.primitive.Graphic} objects of a scene and through 
 * the {@code Graphic} objects, to the underlying 
 * {@link org.opengis.feature.Feature} or 
 * {@link org.opengis.coverage.grid.GridCoverage}.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractGraphicVisitor implements GraphicVisitor {

    public abstract void visit(ProjectedFeature feature, Shape queryArea);

    public abstract void visit(GraphicCoverageJ2D coverage, Shape queryArea);

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
    public void visit(Graphic graphic, Shape area) {

        if(graphic == null ) return;

        if(graphic instanceof ProjectedFeature){
            visit((ProjectedFeature)graphic, area);
        }else if(graphic instanceof GraphicCoverageJ2D){
            visit((GraphicCoverageJ2D)graphic, area);
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
    protected static Object[][] getCoverageValues(final GraphicCoverageJ2D gra, final Shape selectedArea){

        final CoverageMapLayer layer = gra.getUserObject();

        //find center of the selected area
        final Rectangle2D bounds2D   = selectedArea.getBounds2D();
        final double centerX         = bounds2D.getCenterX();
        final double centerY         = bounds2D.getCenterY();

        //find grid coverage
        final ReferencedCanvas2D canvas = gra.getCanvas();
        final GridCoverage2D coverage;

        final AffineTransform dispToObj;

        try{
            dispToObj = canvas.getController().getTransform().createInverse();
        }catch(NoninvertibleTransformException ex){
            ex.printStackTrace();
            return null;
        }
        
        if(layer.getCoverageReader() != null){
            CoverageReader reader = layer.getCoverageReader();
            final Rectangle2D displayRect = canvas.getDisplayBounds().getBounds2D();
            final Rectangle2D objectiveRect;
            final double[] resolution = new double[2];

            try{
                objectiveRect = canvas.getObjectiveBounds().getBounds2D();
            }catch(TransformException ex){
                ex.printStackTrace();
                return null;
            }

            resolution[0] = objectiveRect.getWidth()/displayRect.getWidth();
            resolution[1] = objectiveRect.getHeight()/displayRect.getHeight();

            GeneralEnvelope env = new GeneralEnvelope(objectiveRect);
            env.setCoordinateReferenceSystem(canvas.getObjectiveCRS());

            CoverageReadParam param = new CoverageReadParam(env, resolution);

            try{
                coverage = reader.read(param);
            }catch(FactoryException ex){
                ex.printStackTrace();
                return null;
            }catch(TransformException ex){
                ex.printStackTrace();
                return null;
            }catch(IOException ex){
                ex.printStackTrace();
                return null;
            }

        }else{
            throw new IllegalArgumentException("A coverageLayer without gridcoverage2D nor coverage reader ? should not be possible.");
        }

        try {

            final CoordinateReferenceSystem dataCRS = coverage.getCoordinateReferenceSystem();
            final MathTransform objToData           = CRS.findMathTransform(CRS.getHorizontalCRS(canvas.getObjectiveCRS()),
                                                                            CRS.getHorizontalCRS(dataCRS),true);

            final Point2D p2d = new Point2D.Double(centerX, centerY);

            //transform to objective CRS
            dispToObj.transform(p2d, p2d);

            final GeneralDirectPosition dp = new GeneralDirectPosition(p2d);
            dp.setCoordinateReferenceSystem(canvas.getObjectiveCRS());

            //transform to coverage CRS
            objToData.transform(dp, dp);

            float[] values = new float[coverage.getNumSampleDimensions()];
            p2d.setLocation(dp.getOrdinate(0), dp.getOrdinate(1));
            values = coverage.evaluate(p2d,values);

            Object[][] results = new Object[values.length][2];
            for(int i=0; i<values.length; i++){
                final float value = values[i];
                final GridSampleDimension sample = coverage.getSampleDimension(i);
                final Unit<?> unit = sample.getUnits();
                results[i][0] = value;
                results[i][1] = unit;
                return results;
            }

        } catch (FactoryException ex) {
            ex.printStackTrace();
        } catch (TransformException ex) {
            ex.printStackTrace();
        }

        return null;
    }
    
}
