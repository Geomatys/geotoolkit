
package org.geotoolkit.pending.demo.coverage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.gui.swing.go2.JMap2DFrame;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;


public class CustomCoverageDemo {
    
    public static final MutableStyleFactory SF = new DefaultStyleFactory();
    
    public static void main(String[] args) {
        
        //first create an image        
        final BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);        
        final Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.GREEN);
        g2d.fillRect(0, 0, 500, 500);
        
        //set it's envelope
        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, 0, 100);
        env.setRange(1, 0, 100);
        
        //create the coverage
        final GridCoverageFactory GF = new GridCoverageFactory();
        final GridCoverage2D coverage = GF.create("myCoverage", img, env);
                        
        //display it
        final MapContext context = MapBuilder.createContext();
        final CoverageMapLayer cl = MapBuilder.createCoverageLayer(coverage, SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), "coverage");
        context.layers().add(cl);
        JMap2DFrame.show(context);
        
    }
    
}
