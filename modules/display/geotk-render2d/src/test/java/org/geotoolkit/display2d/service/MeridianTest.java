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
package org.geotoolkit.display2d.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.style.PolygonSymbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MeridianTest {
    
    private static final GeometryFactory GF = new GeometryFactory();
    private static final DefaultStyleFactory SF = new DefaultStyleFactory();
    
    /**
     * Test crossing the +180 meridian.
     * @throws Exception 
     */
    @Test
    public void testP169toP191() throws Exception{
        
        final Polygon poly = GF.createPolygon(new Coordinate[]{
            new Coordinate(+169, +10),
            new Coordinate(+191, +10),
            new Coordinate(+191, -10),
            new Coordinate(+169, -10),
            new Coordinate(+169, +10)
        });
        
        final MapContext context = createLayer(poly);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(349, 80, 11, 20), 
                          new Rectangle(0, 80, 11, 20));
    }
    
    /**
     * Test crossing the -180 meridian.
     * @throws Exception 
     */
    @Test
    public void testN169toN191() throws Exception{
        
        final Polygon poly = GF.createPolygon(new Coordinate[]{
            new Coordinate(-169, +10),
            new Coordinate(-191, +10),
            new Coordinate(-191, -10),
            new Coordinate(-169, -10),
            new Coordinate(-169, +10)
        });
        
        final MapContext context = createLayer(poly);
        final SceneDef sceneDef = new SceneDef(context);
        
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        checkImage(image, new Rectangle(349, 80, 11, 20), 
                          new Rectangle(0, 80, 11, 20));
    }
    
    /**
     * Test the image content.
     * The image must be completely white and pixels inside the fille areas must be black.
     * @param image
     * @param fillAreas 
     */
    private void checkImage(BufferedImage image, Rectangle ... fillAreas){
        
        final int white = Color.WHITE.getRGB();
        final int black = Color.BLACK.getRGB();
        
        final int width = image.getWidth();
        final int height = image.getHeight();
        
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                int rgb = image.getRGB(x, y);
                
                boolean inside = false;
                for(Rectangle rect : fillAreas){
                    if(rect.contains(x, y)){
                        inside = true;
                        break;
                    }
                }
                
                Assert.assertEquals("Wrong value at ("+x+","+y+")", inside?black:white, rgb);
            }
        }
        
    }
    
    
    private static MapContext createLayer(Polygon geometry){
        
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("geom", Polygon.class, DefaultGeographicCRS.WGS84);
        final FeatureType type = ftb.buildFeatureType();
        
        final Feature feature = FeatureUtilities.defaultFeature(type, "0");
        JTS.setCRS(geometry, DefaultGeographicCRS.WGS84);
        feature.getProperty("geom").setValue(geometry);
        final FeatureCollection col = FeatureStoreUtilities.collection(feature);
        
        final PolygonSymbolizer symbol = SF.polygonSymbolizer(SF.stroke(Color.BLACK, 0), SF.fill(Color.BLACK), null);
        final MutableStyle style = SF.style(symbol);
        final FeatureMapLayer layer = MapBuilder.createFeatureLayer(col, style);
        
        final MapContext context = MapBuilder.createContext();
        context.layers().add(layer);
        return context;
    }
    
}
