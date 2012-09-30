
package org.geotoolkit.pending.demo.coverage;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.coverage.CoverageStack;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.geotoolkit.style.random.RandomStyleBuilder;
import org.opengis.coverage.Coverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class CustomCoverageNDDemo {
    
    private static final MutableStyleFactory SF = new DefaultStyleFactory();    
    private static final GridCoverageFactory GCF = new GridCoverageFactory();
    
    public static void main(String[] args) throws Exception {
        
        CoordinateReferenceSystem crs = new DefaultCompoundCRS("4D crs",
                    CRS.decode("EPSG:4326"),
                    DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT,
                    DefaultTemporalCRS.JAVA);

        List<Coverage> temps = new ArrayList<Coverage>();
        for(int i=0; i<10; i++){
            final List<Coverage> eles = new ArrayList<Coverage>();
            for(int k=0;k<10;k++){
                GeneralEnvelope env = new GeneralEnvelope(crs);
                env.setRange(0,  0,  10);
                env.setRange(1, 0, 10);
                env.setRange(2, k, k+1);
                env.setRange(3, i, i+1);
                BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
                final Graphics2D g = img.createGraphics();
                g.setColor(new RandomStyleBuilder().randomColor() );
                g.fillRect(0, 0, 100, 100);
                GridCoverage2D coverage = GCF.create("2D", img, env);
                eles.add(coverage);
            }
            temps.add(new CoverageStack("3D", eles));
        }
        CoverageStack coverage4D = new CoverageStack("4D", temps);
        
        System.out.println(coverage4D.getEnvelope());
                
    }
    
}
