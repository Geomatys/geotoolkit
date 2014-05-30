/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.display2d.primitive;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import static java.awt.geom.PathIterator.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.display2d.canvas.J2DCanvasBuffered;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.stateless.StatelessContextParams;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.filter.DefaultFilterFactory2;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.StyleConstants;
import static org.junit.Assert.*;
import org.junit.Test;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.PointSymbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ProjectedGeometryTest {
    
    private static final double DELTA = 0.0000000001;
    
    private static final GeometryFactory GF = new GeometryFactory();
    private static final DefaultStyleFactory SF = new DefaultStyleFactory();
    private static final FilterFactory2 FF = new DefaultFilterFactory2();
    
    /**
     * Sanity test.
     * If this test fail, don't even bother looking at the others.
     */
    @Test
    public void testSanity() throws Exception {
        
        final Polygon poly = GF.createPolygon(new Coordinate[]{
            new Coordinate( 0,  0),
            new Coordinate( 0, 10),
            new Coordinate(20, 10),
            new Coordinate(20,  0),
            new Coordinate( 0,  0)
        });
        
        final ProjectedGeometry pg = createProjectedGeometry(poly, 
                new Dimension(360, 180),
                new AffineTransform(1, 0, 0, -1, +180, 90));
        
        testArray(pg.getObjectiveGeometryJTS(), 
                GF.createPolygon(new Coordinate[]{
                    new Coordinate( 0,  0),
                    new Coordinate( 0, 10),
                    new Coordinate(20, 10),
                    new Coordinate(20,  0),
                    new Coordinate( 0,  0)
                }));
        testArray(pg.getObjectiveShape(),
                createPath(new int[][]{
                    {SEG_MOVETO,  0,  0},
                    {SEG_LINETO,  0, 10},
                    {SEG_LINETO, 20, 10},
                    {SEG_LINETO, 20,  0},
                    {SEG_CLOSE}
                }));
        testArray(pg.getDisplayGeometryJTS(), 
                GF.createPolygon(new Coordinate[]{
                    new Coordinate(180, 90),
                    new Coordinate(180, 80),
                    new Coordinate(200, 80),
                    new Coordinate(200, 90),
                    new Coordinate(180, 90)
                }));
        testArray(pg.getDisplayShape(),
                createPath(new int[][]{
                    {SEG_MOVETO, 180, 90},
                    {SEG_LINETO, 180, 80},
                    {SEG_LINETO, 200, 80},
                    {SEG_LINETO, 200, 90},
                    {SEG_LINETO, 180, 90},
                    {SEG_CLOSE}
                }));
    }
    
    /**
     * Test display shape clipped
     */
    @Test
    public void testClipping() throws Exception {
        
        final Polygon poly = GF.createPolygon(new Coordinate[]{
            new Coordinate( 0,  0),
            new Coordinate( 0, 10),
            new Coordinate(20, 10),
            new Coordinate(20,  0),
            new Coordinate( 0,  0)
        });
        
        
        //we make the geometry cross the left canvas bounds
        //we reduce image width to avoid repetition
        final ProjectedGeometry pg = createProjectedGeometry(poly, 
                new Dimension(100, 180),
                new AffineTransform(1, 0, 0, -1, -10, +90));
        
        testArray(pg.getObjectiveGeometryJTS(), 
                GF.createPolygon(new Coordinate[]{
                    new Coordinate( 0,  0),
                    new Coordinate( 0, 10),
                    new Coordinate(20, 10),
                    new Coordinate(20,  0),
                    new Coordinate( 0,  0)
                }));
        testArray(pg.getObjectiveShape(),
                createPath(new int[][]{
                    {SEG_MOVETO,  0,  0},
                    {SEG_LINETO,  0, 10},
                    {SEG_LINETO, 20, 10},
                    {SEG_LINETO, 20,  0},
                    {SEG_CLOSE}
                }));
        testArray(pg.getDisplayGeometryJTS(), 
                GF.createPolygon(new Coordinate[]{
                    new Coordinate(-10, 90),
                    new Coordinate(-10, 80),
                    new Coordinate( 10, 80),
                    new Coordinate( 10, 90),
                    new Coordinate(-10, 90)
                }));
        //the display shape should have been clipped on x=0
        testArray(pg.getDisplayShape(),
                createPath(new int[][]{
                    {SEG_MOVETO,  0-StatelessContextParams.CLIP_PIXEL_MARGIN, 90},
                    {SEG_LINETO,  0-StatelessContextParams.CLIP_PIXEL_MARGIN, 80},
                    {SEG_LINETO, 10, 80},
                    {SEG_LINETO, 10, 90},
                    {SEG_LINETO,  0-StatelessContextParams.CLIP_PIXEL_MARGIN, 90},
                    {SEG_CLOSE}
                }));
    }
    
    
    
    
    private void testArray(Geometry[] candidate, Geometry ... expected){
        assertEquals(expected.length, candidate.length);
        
        for(int i=0;i<candidate.length;i++){
            assertTrue(candidate[i].equalsExact(expected[i]));
        }
        
    }
    
    private void testArray(Shape[] candidate, Shape ... expected){
        assertEquals(expected.length, candidate.length);
        
        for(int i=0;i<candidate.length;i++){
            PathIterator candidateIte = candidate[i].getPathIterator(new AffineTransform());
            PathIterator expectedIte = expected[i].getPathIterator(new AffineTransform());
            testPathIterator(candidateIte, expectedIte);
        }
        
    }
    
    private void testPathIterator(PathIterator candidate, PathIterator expected){
        
        while(true){
            boolean done1 = candidate.isDone();
            boolean done2 = expected.isDone();
            if(done1 && done2){
                //ok same path
                return;
            }else if(!done1 && !done2){
                
                assertEquals(candidate.getWindingRule(), expected.getWindingRule());
                
                final double[] candidateValues =  new double[6];
                final double[] expectedValues =  new double[6];
                final int candidateType = candidate.currentSegment(candidateValues);
                final int expectedType = expected.currentSegment(expectedValues);
                
                assertEquals(candidateType, expectedType);
                assertArrayEquals(expectedValues, candidateValues, DELTA);
            }else{
                fail("Path iterator do not have the same number of iteration.");
            }
            
            candidate.next();
            expected.next();
        }
        
    }
    
    private static ProjectedGeometry createProjectedGeometry(Geometry geometry, Dimension canvasBounds, AffineTransform objToDisp) throws NoninvertibleTransformException, TransformException {
        
        final int canvasWidth = canvasBounds.width;
        final int canvasHeight = canvasBounds.height;
        
        //build a maplayer
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("geom", Geometry.class, DefaultGeographicCRS.WGS84);
        final FeatureType type = ftb.buildFeatureType();
        
        final Feature feature = FeatureUtilities.defaultFeature(type, "0");
        JTS.setCRS(geometry, DefaultGeographicCRS.WGS84);
        feature.getProperty("geom").setValue(geometry);
        final FeatureCollection col = FeatureStoreUtilities.collection(feature);
        
        final List<GraphicalSymbol> symbols = new ArrayList<>();
        symbols.add(SF.mark(StyleConstants.MARK_SQUARE, SF.fill(Color.BLACK), SF.stroke(Color.BLACK, 0)));
        final Graphic graphic = SF.graphic(symbols, StyleConstants.LITERAL_ONE_FLOAT, FF.literal(2), StyleConstants.LITERAL_ZERO_FLOAT, null, null);
        final PointSymbolizer ps = SF.pointSymbolizer(graphic, null);
        
        final MutableStyle style = SF.style(ps);
        final MapLayer layer = MapBuilder.createFeatureLayer(col, style);
        
        //build a rendering canvas
        final J2DCanvasBuffered canvas = new J2DCanvasBuffered(DefaultGeographicCRS.WGS84, new Dimension(canvasWidth, canvasHeight));
        canvas.applyTransform(objToDisp);
        
        final StatelessContextParams params = new StatelessContextParams(canvas, layer);
        final RenderingContext2D context = new RenderingContext2D(canvas);
        canvas.prepareContext(context, new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB).createGraphics(), 
                new Rectangle(0, 0, canvasWidth, canvasHeight));
        params.update(context);
        
        final ProjectedGeometry pg = new ProjectedGeometry(params);
        pg.setDataGeometry(geometry, DefaultGeographicCRS.WGS84);
                
        Envelope env = canvas.getVisibleEnvelope();
        System.out.println(env.getMinimum(0)+" "+env.getMaximum(0));
        System.out.println(env.getMinimum(1)+" "+env.getMaximum(1));
        
        return pg;
    }
    
    private static GeneralPath createPath(int[][] steps){
        final GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        
        for(int i=0;i<steps.length;i++){
            int[] step = steps[i];
            if(step[0]==SEG_MOVETO){
                path.moveTo(step[1], step[2]);
            }else if(step[0]==SEG_LINETO){
                path.lineTo(step[1], step[2]);
            }else if(step[0]==SEG_CLOSE){
                path.closePath();
            }else{
                throw new IllegalArgumentException("Unsupported step type : "+step[0]);
            }
        }
        
        return path;
    }
}
