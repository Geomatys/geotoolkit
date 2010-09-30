/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Geomatys
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

import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedLineSymbolizer;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display.shape.TransformedShape;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.display2d.style.CachedGraphicStroke;
import org.geotoolkit.display2d.style.CachedStroke;
import org.geotoolkit.display2d.style.CachedStrokeGraphic;
import org.geotoolkit.display2d.style.CachedStrokeSimple;
import org.geotoolkit.display2d.style.j2d.DefaultPathWalker;
import org.geotoolkit.display2d.style.j2d.PathWalker;

import org.opengis.feature.Feature;
import org.opengis.geometry.Geometry;
import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultLineSymbolizerRenderer extends AbstractSymbolizerRenderer<CachedLineSymbolizer>{

    public DefaultLineSymbolizerRenderer(CachedLineSymbolizer symbol, RenderingContext2D context){
        super(symbol,context);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(ProjectedFeature projectedFeature) throws PortrayalException{
        final Feature feature = projectedFeature.getFeature();
        final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(symbol.getSource().getGeometryPropertyName());

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        if(projectedGeometry == null) return;

        //test if the symbol is visible on this feature
        if(symbol.isVisible(feature)){
            portray(projectedGeometry, feature);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void portray(final ProjectedCoverage projectedCoverage) throws PortrayalException{
        //portray the border of the coverage
        final ProjectedGeometry projectedGeometry = projectedCoverage.getEnvelopeGeometry();

        //could not find the border geometry
        if(projectedGeometry == null) return;

        portray(projectedGeometry, null);
    }

    private void portray(ProjectedGeometry projectedGeometry, Feature feature) throws PortrayalException{

        final Unit symbolUnit = symbol.getSource().getUnitOfMeasure();
        final float coeff = renderingContext.getUnitCoefficient(symbolUnit);
        final Shape j2dShape;

        if(NonSI.PIXEL == symbolUnit){
            renderingContext.switchToDisplayCRS();
            try {
                j2dShape = projectedGeometry.getDisplayShape();
            } catch (TransformException ex) {
                throw new PortrayalException("Could not calculate display projected geometry",ex);
            }
        }else{
            renderingContext.switchToObjectiveCRS();
            try {
                j2dShape = projectedGeometry.getObjectiveShape();
            } catch (TransformException ex) {
                throw new PortrayalException("Could not calculate objective projected geometry",ex);
            }
        }

        final float margin = symbol.getMargin(feature, coeff) /2f;
        final Rectangle2D bounds = j2dShape.getBounds2D();
        final int x = (int) (bounds.getMinX() - margin);
        final int y = (int) (bounds.getMinY() - margin);

        final float offset = symbol.getOffset(feature, coeff);
        if(offset != 0){
            g2d.translate(offset, 0);
        }

        final CachedStroke cachedStroke = symbol.getCachedStroke();
        if(cachedStroke instanceof CachedStrokeSimple){
            final CachedStrokeSimple cs = (CachedStrokeSimple)cachedStroke;
            g2d.setComposite(cs.getJ2DComposite(feature));
            g2d.setPaint(cs.getJ2DPaint(feature, x, y, coeff, hints));
            g2d.setStroke(cs.getJ2DStroke(feature,coeff));
            g2d.draw(j2dShape);
        }else if(cachedStroke instanceof CachedStrokeGraphic){
            final CachedStrokeGraphic gc = (CachedStrokeGraphic)cachedStroke;
            g2d.setComposite(GO2Utilities.ALPHA_COMPOSITE_1F);
            final float initGap = gc.getInitialGap(feature);
            final Point2D pt = new Point2D.Double();
            final CachedGraphicStroke cgs = gc.getCachedGraphic();
            final Image img = cgs.getImage(feature, 1, hints);
            final float imgWidth = img.getWidth(null);
            final float imgHeight = img.getHeight(null);
            final float gap = gc.getGap(feature)+ imgWidth;
            final AffineTransform trs = new AffineTransform();

            final PathIterator ite = j2dShape.getPathIterator(null);
            final PathWalker walker = new DefaultPathWalker(ite);
            walker.walk(initGap);
            while(!walker.isFinished()){
                //paint the motif --------------------------------------------------
                walker.getPosition(pt);
                final float angle = walker.getRotation();
                trs.setToTranslation(pt.getX(), pt.getY());
                trs.rotate(angle);
                final float[] anchor = cgs.getAnchor(feature, null);
                final float[] disp = cgs.getDisplacement(feature, null);
                trs.translate(-imgWidth*anchor[0], -imgHeight*anchor[1]);
                trs.translate(disp[0], -disp[1]);

                g2d.drawImage(img, trs, null);

                //walk over the gap ------------------------------------------------
                walker.walk(gap);
            }
        }

        if(offset != 0){
            g2d.translate(-offset, 0);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(final ProjectedFeature projectedFeature, SearchAreaJ2D search, VisitFilter filter) {

        //TODO optimize test using JTS geometries, Java2D Area cost to much cpu

        final Geometry mask = search.getDisplayGeometry();

        final Feature feature = projectedFeature.getFeature();

        //test if the symbol is visible on this feature
        if(!(symbol.isVisible(feature))) return false;

        final ProjectedGeometry projectedGeometry = projectedFeature.getGeometry(symbol.getSource().getGeometryPropertyName());

        //symbolizer doesnt match the featuretype, no geometry found with this name.
        if(projectedGeometry == null) return false;

        //Test composites ------------------------------------------------------
        if(symbol.getCachedStroke() instanceof CachedStrokeSimple){
            CachedStrokeSimple cs = (CachedStrokeSimple) symbol.getCachedStroke();
            final float strokeAlpha = cs.getJ2DComposite(feature).getAlpha();
            if(strokeAlpha < GO2Utilities.SELECTION_LOWER_ALPHA){
                //feature graphic is translucide, not selectable
                return false;
            }
        }

        final Unit symbolUnit = symbol.getSource().getUnitOfMeasure();
        final float coeff = renderingContext.getUnitCoefficient(symbolUnit);

        
        if(NonSI.PIXEL.equals(symbolUnit)){

            final Geometry j2dShape;

            try {
                j2dShape = projectedGeometry.getDisplayGeometry();
            } catch (TransformException ex) {
                ex.printStackTrace();
                return false;
            }

            final int bufferWidth = (int) symbol.getMargin(feature,1);

            //test envelopes first
//            Geometry CRSShape = mask.getEnvelope();
//            CRSShape = mask.buffer(bufferWidth,1);
//            boolean hit = testHit(filter,CRSShape,j2dShape.getEnvelope());

//            if(!hit) return false;

            //test real shape
            Geometry CRSShape = mask.getBuffer(bufferWidth);
            return GO2Utilities.testHit(filter,CRSShape,j2dShape);

        }else{            
            final Shape j2dShape;
            final Shape CRSShape;
            try {
                j2dShape = projectedGeometry.getObjectiveShape();
                CRSShape = new TransformedShape();
                ((TransformedShape)CRSShape).setTransform(
                        renderingContext.getAffineTransform(renderingContext.getDisplayCRS(), renderingContext.getObjectiveCRS()));
                ((TransformedShape)CRSShape).setOriginalShape(search.getDisplayShape());
            } catch (TransformException ex) {
                ex.printStackTrace();
                return false;
            } catch (FactoryException ex) {
                ex.printStackTrace();
                return false;
            }

            if(symbol.getCachedStroke() instanceof CachedStrokeSimple){
                final CachedStrokeSimple cs = (CachedStrokeSimple) symbol.getCachedStroke();
                final java.awt.Stroke stroke = cs.getJ2DStroke(feature,coeff);
                final Area area = new Area(stroke.createStrokedShape(j2dShape));

                switch(filter){
                    case INTERSECTS :
                        area.intersect(new Area(CRSShape));
                        return !area.isEmpty();
                    case WITHIN :
                        Area start = new Area(area);
                        area.add(new Area(CRSShape));
                        return start.equals(area);
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hit(ProjectedCoverage graphic, SearchAreaJ2D mask, VisitFilter filter) {
        return false;
    }
    
}
