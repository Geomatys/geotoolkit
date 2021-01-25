
package org.geotoolkit.pending.demo.coverage;

import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.portrayal.MapLayers;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.StyleConstants;


public class CustomCoverage1SDemo {

    public static final MutableStyleFactory SF = new DefaultStyleFactory();

    public static void main(String[] args) throws Exception {
        Demos.init();

        UIManager.setLookAndFeel(new MetalLookAndFeel());

        //first create a matrix table
        final float[][] matrix = new float[100][100];
        for(int x=0;x<100;x++){
            for(int y=0;y<100;y++){
                matrix[x][y] = x+y;
            }
        }

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setValues(BufferedImages.toDataBuffer(matrix), null);

        //set it's envelope
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 100);
        env.setRange(1, 0, 100);
        gcb.setDomain(env);

        //create the coverage
        final GridCoverage coverage = gcb.build();

        //display it
        final MapLayers context = MapBuilder.createContext();
        final MapLayer cl = MapBuilder.createCoverageLayer(coverage, SF.style(StyleConstants.DEFAULT_RASTER_SYMBOLIZER), "coverage");
        context.getComponents().add(cl);
//        FXMapFrame.show(context);
    }

}
