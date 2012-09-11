/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.display2d.style;

import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import java.awt.Graphics2D;
import java.awt.image.Raster;
import java.io.File;
import java.awt.Color;
import org.opengis.filter.FilterFactory;
import org.geotoolkit.factory.FactoryFinder;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test that raster symbolizer are properly rendered.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class RasterSymbolizerTest {
    
    private static final GeometryFactory GF = new GeometryFactory();
    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    protected static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    
    /**
     * Check proper image reprojection in UTM
     */
    @Ignore
    @Test
    public void UTM32632Test() throws Exception{
        
        final BufferedImage img = new BufferedImage(120, 90, BufferedImage.TYPE_INT_ARGB);        
        final Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.GREEN);
        g2d.fillRect(0, 0, 120, 90);
        
         //set it's envelope
        final GeneralEnvelope gridEnv = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        gridEnv.setRange(0, 0, 120);
        gridEnv.setRange(1, 0, 90);
        
        //create the coverage
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setEnvelope(gridEnv);
        gcb.setName("myCoverage");
        gcb.setRenderedImage(img);
        final GridCoverage2D coverage = gcb.getGridCoverage2D();
                        
        
        
        
        final MapContext context = MapBuilder.createContext();
        final CoverageMapLayer cl = MapBuilder.createCoverageLayer(coverage, SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), "coverage");
        context.layers().add(cl);
        
        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:32632"));
        env.setRange(0, -2574823.6832217844, 5487970.783439655);
        env.setRange(1, 4289777.45228916, 1.0491927042028729E7);
        
        final Hints hints = new Hints();        
        final SceneDef scenedef = new SceneDef(context,hints);
        final ViewDef viewdef = new ViewDef(env);
        final CanvasDef canvasdef = new CanvasDef(new Dimension(800, 800), Color.WHITE);
        
        final BufferedImage buffer = DefaultPortrayalService.portray(canvasdef, scenedef, viewdef);
        ImageIO.write(buffer, "PNG", new File("test.png"));
        
        //We should obtain a green triangle crossing the image looking like this :
        //
        // |\
        // |_\
        //we can't test the shape so we test we found more and more green pixels on each line
        
        //we expect to have a blue label at the center of the image
        final int[] pixel = new int[4];
        final int[] green = new int[]{0,255,0,255};
        
        int nbGreen = 0;
        
        final Raster raster = buffer.getData();
        for(int y=0; y<800;y++){
            int nb = 0;
            
            for(int x=0;x<800;x++){
                raster.getPixel(x, y, pixel);
                if(Arrays.equals(green, pixel)){
                    nb++;
                }
            }
            
            assertTrue("expected at least one green pixel", nb>0);
            assertTrue(nb >= nbGreen);
            nbGreen = nb;
        }   
        
    }
    
    
}
