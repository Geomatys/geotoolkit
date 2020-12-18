/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2015, Geomatys
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
package org.geotoolkit.display2d.style.renderer;

import org.geotoolkit.display2d.presentation.ShapePresentation;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.renderer.Presentation;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.style.CachedPolygonSymbolizer;
import org.geotoolkit.display2d.style.CachedStroke;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.renderer.ExceptionPresentation;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.referencing.operation.TransformException;

/**
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class PolygonSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedPolygonSymbolizer>{

    private final boolean mosaic;

    public PolygonSymbolizerRenderer(final SymbolizerRendererService service,final CachedPolygonSymbolizer symbol, final RenderingContext2D context){
        super(service,symbol,context);
        mosaic = symbol.isMosaic();
    }

    @Override
    public Stream<Presentation> presentations(MapLayer layer, Feature feature) {

        final float offset = symbol.getOffset(feature, coeff);
        final Shape[] shapes;

        //calculate displacement
        final float[] disps = symbol.getDisplacement(feature);
        Point2D dispStep = null;
        if (disps[0] != 0 || disps[1] != 0) {
            dispStep = new Point2D.Float(disps[0], -disps[1]);
        }

        final ProjectedGeometry projectedGeometry = new ProjectedGeometry(renderingContext);
        projectedGeometry.setDataGeometry(GO2Utilities.getGeometry(feature, symbol.getSource().getGeometry()), null);

        float sizeCorrection = 1f;
        try {
            if (dispGeom) {
                shapes = (offset != 0) ? bufferDisplayGeometry(projectedGeometry, offset)
                                      : projectedGeometry.getDisplayShape();
            } else {
                //NOTE : Java2d has issues when rendering shapes with large strokes when
                //there is a given transform, we cheat by converting the geometry in
                //display unit.
                //
                //renderingContext.switchToObjectiveCRS();
                //shapes = (offset != 0) ? bufferObjectiveGeometry(renderingContext, projectedGeometry, symbolUnit, offset)
                //                      : projectedGeometry.getObjectiveShape();
                //adjust displacement, displacement is expressed in pixel units
                //final AffineTransform inverse = renderingContext.getDisplayToObjective();
                //if(dispStep!=null) dispStep = inverse.deltaTransform(dispStep, dispStep);

                sizeCorrection = (float)AffineTransforms2D.getScale(renderingContext.getObjectiveToDisplay());

                if (offset != 0) {
                    shapes = bufferDisplayGeometry(projectedGeometry, offset*sizeCorrection);
                } else {
                    shapes = projectedGeometry.getDisplayShape();
                }
            }
        } catch (TransformException ex) {
            return Stream.of(new ExceptionPresentation(layer, layer.getResource(), null, ex));
        }

        if (shapes == null) {
            //no geometry, end here
            return Stream.empty();
        }

        final float coeff = this.coeff * sizeCorrection;

        final List<Presentation> presentations = new ArrayList<>();

        for (Shape shape : shapes) {

            //we apply the displacement ---------------------------------------
            if (dispStep != null) {
                final AffineTransform trs = new AffineTransform();
                trs.setToTranslation(dispStep.getX(), dispStep.getY());
                shape = trs.createTransformedShape(shape);
            }

            final int x;
            final int y;
            if (mosaic) {
                //we need the upperleft point to properly paint the polygon
                final float margin = symbol.getMargin(feature, coeff) /2f;
                final Rectangle2D bounds = shape.getBounds2D();
                if (bounds == null) return Stream.empty();
                x = (int) (bounds.getMinX() - margin);
                y = (int) (bounds.getMinY() - margin);
            } else {
                x = 0;
                y = 0;
            }

            if (symbol.isFillVisible(feature)) {
                final ShapePresentation presentation = new ShapePresentation(layer, feature);
                presentation.forGrid(renderingContext);
                presentation.fillComposite = symbol.getJ2DFillComposite(feature);
                presentation.fillPaint = symbol.getJ2DFillPaint(feature, x, y,coeff, hints);
                presentation.shape = shape;
                presentations.add(presentation);
            }

            if (symbol.isStrokeVisible(feature)) {
                final CachedStroke cachedStroke = symbol.getCachedStroke();
                presentations.addAll(LineSymbolizerRenderer.portray(layer, symbol, shape, cachedStroke, feature, coeff, hints, renderingContext));
            }
        }

        return presentations.stream();
    }

    /**
     * Recalculate display geometry with the given offset,
     * for polygon this act like a buffer
     */
    private static Shape[] bufferDisplayGeometry(final ProjectedGeometry projectedFeature, final float offset) throws TransformException{

        final Geometry[] geoms = projectedFeature.getDisplayGeometryJTS();
        final Shape[] shapes = new Shape[geoms.length];
        for(int i=0;i<geoms.length;i++){
            try{
                geoms[i] = geoms[i].buffer(offset);
            }catch(IllegalArgumentException ex){
                //can happen if the geometry has too few points, like a ring of 3points
                LOGGER.log(Level.FINE, ex.getLocalizedMessage(), ex);
            }
            shapes[i] = GO2Utilities.toJava2D(geoms[i]);
        }

        return shapes;
    }

}
