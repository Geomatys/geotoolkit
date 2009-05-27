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
package org.geotoolkit.display2d.style.renderer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.primitive.GraphicCoverageJ2D;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display.primitive.ReferencedGraphic.SearchArea;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.style.CachedPointSymbolizer;
import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.display2d.GO2Utilities;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Mark;
import org.opengis.style.PointSymbolizer;

/**
 * @author Johann Sorel (Geomatys)
 */
public class PointSymbolizerRenderer extends AbstractSymbolizerRenderer<PointSymbolizer, CachedPointSymbolizer>{

    @Override
    public Class<PointSymbolizer> getSymbolizerClass() {
        return PointSymbolizer.class;
    }

    @Override
    public Class<CachedPointSymbolizer> getCachedSymbolizerClass() {
        return CachedPointSymbolizer.class;
    }

    @Override
    public CachedPointSymbolizer createCachedSymbolizer(PointSymbolizer symbol) {
        return new CachedPointSymbolizer(symbol);
    }

    @Override
    public void portray(ProjectedFeature projectedFeature, CachedPointSymbolizer symbol, RenderingContext2D context) throws PortrayalException{

        final Feature feature = projectedFeature.getFeature();

        //test if the symbol is visible on this feature
        if(symbol.isVisible(feature)){
            final Graphics2D g2 = context.getGraphics();
            final RenderingHints hints = g2.getRenderingHints();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            final Unit symbolUnit = symbol.getSource().getUnitOfMeasure();

            final Geometry geom;
            try {
                geom = projectedFeature.getDisplayGeometry();
            } catch (TransformException ex) {
                throw new PortrayalException(ex);
            }

            //we switch to  more appropriate context CRS for rendering ---------
            // a point symbolis always paint in display unit -------------------
            context.switchToDisplayCRS();

            //we adjust coefficient for rendering ------------------------------
            float coeff;
            if(symbolUnit.equals(NonSI.PIXEL)){
                //symbol is in display unit
                coeff = 1;
            }else{
                //we have a special unit we must adjust the coefficient
                coeff = context.getUnitCoefficient(symbolUnit);
                // calculate scale difference between objective and display
                try{
                    final AffineTransform inverse = context.getAffineTransform(context.getObjectiveCRS(), context.getDisplayCRS());
                    coeff *= Math.abs(XAffineTransform.getScale(inverse));
                }catch(FactoryException ex){
                    throw new PortrayalException(ex);
                }
            }


            //create the image--------------------------------------------------
            final BufferedImage img = symbol.getImage(feature,coeff,hints);
            final float[] disps = symbol.getDisplacement(feature);
            disps[0] *= coeff ;
            disps[1] *= coeff ;

            final float[] anchor = symbol.getAnchor(feature);

            if(geom instanceof Point || geom instanceof MultiPoint){

                //TODO use generalisation on multipoints

                final Coordinate[] coords = geom.getCoordinates();
                for(int i=0, n = coords.length; i<n ; i++){
                    final Coordinate coord = coords[i];
                    DirectPosition pt2d = new DirectPosition2D(coord.x, coord.y);

                    final int x = (int) (-img.getWidth()*anchor[0] + pt2d.getOrdinate(0) + disps[0]);
                    final int y = (int) (-img.getHeight()*anchor[1] + pt2d.getOrdinate(1) - disps[1]);
                    g2.drawImage(img, x, y, null);
                }

            }else if( geom instanceof LineString || geom instanceof MultiLineString
                    || geom instanceof Polygon || geom instanceof MultiPolygon ){

                final Point center = geom.getCentroid();
                final Coordinate coord = center.getCoordinate();
                DirectPosition pt2d = new DirectPosition2D(coord.x, coord.y);

                final int x = (int) (-img.getWidth()*anchor[0] + pt2d.getOrdinate(0) + disps[0]);
                final int y = (int) (-img.getHeight()*anchor[1] + pt2d.getOrdinate(1) - disps[1]);
                g2.drawImage(img, x, y, null);
            }

        }
    }

    @Override
    public void portray(final GraphicCoverageJ2D graphic, CachedPointSymbolizer symbol,
            RenderingContext2D context) throws PortrayalException{
        //nothing to portray
    }

    @Override
    public boolean hit(final ProjectedFeature graphic, final CachedPointSymbolizer symbol,
            final RenderingContext2D context, final SearchArea search, final VisitFilter filter) {

        //TODO optimize test using JTS geometries, Java2D Area cost to much cpu

        final Shape mask = search.displayShape;

        final SimpleFeature feature = graphic.getFeature();

        //test if the symbol is visible on this feature
        if(!(symbol.isVisible(feature))) return false;

        final Unit symbolUnit = symbol.getSource().getUnitOfMeasure();

        final Geometry geom;
        try {
            geom = graphic.getDisplayGeometry();
        } catch (TransformException ex) {
            ex.printStackTrace();
            return false;
        }


        //we switch to  more appropriate context CRS ---------------------------
        // a point symbolis always paint in display unit -----------------------

        //we adjust coefficient for rendering ----------------------------------
        float coeff = 1;
        if(symbolUnit.equals(NonSI.PIXEL)){
            //symbol is in display unit
            coeff = 1;
        }else{
            //we have a special unit we must adjust the coefficient
            coeff = context.getUnitCoefficient(symbolUnit);
            // calculate scale difference between objective and display
            try{
                final AffineTransform inverse = context.getAffineTransform(context.getObjectiveCRS(), context.getDisplayCRS());
                coeff *= Math.abs(XAffineTransform.getScale(inverse));
            }catch(FactoryException ex){
                ex.printStackTrace();
                return false;
            }
        }

        //create the image------------------------------------------------------
        final BufferedImage img = symbol.getImage(feature,coeff,null);
        final float[] disps = symbol.getDisplacement(feature);
        disps[0] *= coeff ;
        disps[1] *= coeff ;

        final float[] anchor = symbol.getAnchor(feature);

        if(geom instanceof Point || geom instanceof MultiPoint){

            //TODO use generalisation on multipoints

            final Coordinate[] coords = geom.getCoordinates();
            for(int i=0, n = coords.length; i<n ; i++){
                final Coordinate coord = coords[i];
                DirectPosition pt2d = new DirectPosition2D(coord.x, coord.y);

                final int x = (int) (-img.getWidth()*anchor[0] + pt2d.getOrdinate(0) + disps[0]);
                final int y = (int) (-img.getHeight()*anchor[1] + pt2d.getOrdinate(1) - disps[1]);

                switch(filter){
                    case INTERSECTS :
                        if(mask.intersects(x,y,img.getWidth(),img.getHeight())){
                            //TODO should make a better test for the alpha pixel values in image
                            return true;
                        }
                        break;
                    case WITHIN :
                        if(mask.contains(x,y,img.getWidth(),img.getHeight())){
                            //TODO should make a better test for the alpha pixel values in image
                            return true;
                        }
                        break;
                }

            }

        }else if( geom instanceof LineString || geom instanceof MultiLineString
                || geom instanceof Polygon || geom instanceof MultiPolygon ){

            final Point center = geom.getCentroid();
            final Coordinate coord = center.getCoordinate();
            DirectPosition pt2d = new DirectPosition2D(coord.x, coord.y);

            final int x = (int) (-img.getWidth()*anchor[0] + pt2d.getOrdinate(0) + disps[0]);
            final int y = (int) (-img.getHeight()*anchor[1] + pt2d.getOrdinate(1) - disps[1]);

            switch(filter){
                case INTERSECTS :
                    if(mask.intersects(x,y,img.getWidth(),img.getHeight())){
                        //TODO should make a better test for the alpha pixel values in image
                        return true;
                    }
                    break;
                case WITHIN :
                    if(mask.contains(x,y,img.getWidth(),img.getHeight())){
                        //TODO should make a better test for the alpha pixel values in image
                        return true;
                    }
                    break;
            }

        }

        return false;
    }

    @Override
    public Rectangle2D estimate(ProjectedFeature feature, CachedPointSymbolizer symbol,
            RenderingContext2D context, Rectangle2D rect) {
        return XRectangle2D.INFINITY;
    }

    @Override
    public boolean hit(GraphicCoverageJ2D graphic, CachedPointSymbolizer symbol, 
            RenderingContext2D renderingContext, SearchArea mask, VisitFilter filter) {
        return false;
    }

    @Override
    public Rectangle2D glyphPreferredSize(CachedPointSymbolizer symbol) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void glyph(Graphics2D target, Rectangle2D rectangle, CachedPointSymbolizer symbol) {
        target.setClip(rectangle);
        
        final Expression expOpa = symbol.getSource().getGraphic().getOpacity();
        final Expression expRotation = symbol.getSource().getGraphic().getRotation();
        final Expression expSize = symbol.getSource().getGraphic().getSize();
        
        final float opacity;
        final float rotation;
        float size;
        
        if(GO2Utilities.isStatic(expOpa)){
            opacity = expOpa.evaluate(null, Number.class).floatValue();
        }else{
            opacity = 0.6f;
        }
                
        if(GO2Utilities.isStatic(expRotation)){
            rotation = expRotation.evaluate(null, Number.class).floatValue();
        }else{
            rotation = 0f;
        }
        
        if(GO2Utilities.isStatic(expSize)){
            size = expSize.evaluate(null, Number.class).floatValue();
        }else{
            size = 8f;
        }
        
        if(size> rectangle.getHeight()){
            size = (float)rectangle.getHeight();
        }
        
        target.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        target.translate(rectangle.getCenterX(), rectangle.getCenterY());
                
        target.rotate(Math.toRadians(rotation), 0,0);
        
        for(final GraphicalSymbol graphic : symbol.getSource().getGraphic().graphicalSymbols()){
            if(graphic instanceof Mark){
                renderGraphic((Mark) graphic,size,target);
            }
        }
        
        target.rotate(-Math.toRadians(rotation), 0,0);

    }

}
