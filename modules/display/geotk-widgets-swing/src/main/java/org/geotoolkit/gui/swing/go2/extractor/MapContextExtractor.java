/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.go2.extractor;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Component;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.measure.unit.Unit;

import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.coverage.io.CoverageReadParam;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.display2d.canvas.AbstractGraphicVisitor;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.referencing.CRS;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MapContextExtractor extends AbstractGraphicVisitor {

    private final List<String> descriptions = new ArrayList<String>();

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
    public boolean isStopRequested() {
        return false;
    }

    public boolean valid(Graphic graphic) {
        if(graphic instanceof GraphicJ2D){
            GraphicJ2D gra = (GraphicJ2D) graphic;
            Object userObj = gra.getUserObject();

            if(userObj == null) return false;
            return (userObj instanceof Feature) || (userObj instanceof CoverageMapLayer) ;
        }else{
            return false;
        }

    }

    public List<String> getDescriptions() {
        final List<String> copy = new ArrayList<String>(descriptions);
        descriptions.clear();
        return copy;
    }

    public Component getComponent(Graphic graphic) {
        return null;
    }

    @Override
    public void visit(ProjectedFeature projectedFeature, Shape queryArea) {
        final Feature feature = (Feature) projectedFeature.getFeature();
        final StringBuilder builder = new StringBuilder();

        for(final Property prop : feature.getProperties()){
            if( Geometry.class.isAssignableFrom( prop.getType().getBinding() )){
                builder.append("<b>").append(prop.getName()).append(" : </b>").append(prop.getType().getBinding().getSimpleName()).append("<br>");
            }else{
                builder.append("<b>").append(prop.getName()).append(" : </b>").append(prop.getValue()).append("<br>");
            }
        }

        descriptions.add(builder.toString());
    }

    @Override
    public void visit(ProjectedCoverage projectedCoverage, Shape queryArea) {
        final StringBuilder builder  = new StringBuilder();
        final CoverageMapLayer layer = projectedCoverage.getCoverageLayer();

        //find center of the selected area
        final Rectangle2D bounds2D   = queryArea.getBounds2D();
        final double centerX         = bounds2D.getCenterX();
        final double centerY         = bounds2D.getCenterY();

        //find grid coverage
        final ReferencedCanvas2D canvas = projectedCoverage.getCanvas();
        final GridCoverage2D coverage;

        final AffineTransform dispToObj;

        try{
            dispToObj = canvas.getController().getTransform().createInverse();
        }catch(NoninvertibleTransformException ex){
            ex.printStackTrace();
            return;
        }

        CoverageReader reader = layer.getCoverageReader();
        final Rectangle2D displayRect = canvas.getDisplayBounds().getBounds2D();
        final Rectangle2D objectiveRect;
        final double[] resolution = new double[2];

        try{
            objectiveRect = canvas.getObjectiveBounds().getBounds2D();
        }catch(TransformException ex){
            ex.printStackTrace();
            return;
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
            return;
        }catch(TransformException ex){
            ex.printStackTrace();
            return;
        }catch(IOException ex){
            ex.printStackTrace();
            return;
        }



        try {
            final GridCoverage2D geocoverage = coverage.view(ViewType.GEOPHYSICS);
//                org.geotoolkit.gui.swing.image.OperationTreeBrowser.show(geocoverage.getRenderedImage());

            final CoordinateReferenceSystem dataCRS = geocoverage.getCoordinateReferenceSystem();
            final MathTransform objToData           = CRS.findMathTransform(canvas.getObjectiveCRS(), dataCRS,true);

            final Point2D p2d = new Point2D.Double(centerX, centerY);

            //transform to objective CRS
            dispToObj.transform(p2d, p2d);

            final GeneralDirectPosition dp = new GeneralDirectPosition(p2d);
            dp.setCoordinateReferenceSystem(canvas.getObjectiveCRS());

            //transform to coverage CRS
            objToData.transform(dp, dp);

            float[] values = new float[geocoverage.getNumSampleDimensions()];
            p2d.setLocation(dp.getOrdinate(0), dp.getOrdinate(1));
            values = geocoverage.evaluate(p2d,values);

            for(int i=0; i<values.length; i++){
                final float value = values[i];
                final GridSampleDimension sample = geocoverage.getSampleDimension(i);
                final Unit unit = sample.getUnits();
                builder.append("<b>").append(i).append(" = </b> ").append(value).append((unit == null) ? "" : unit.toString()).append("<br>");
            }

        } catch (FactoryException ex) {
            ex.printStackTrace();
        } catch (TransformException ex) {
            ex.printStackTrace();
        }

        descriptions.add(builder.toString());
    }

}
