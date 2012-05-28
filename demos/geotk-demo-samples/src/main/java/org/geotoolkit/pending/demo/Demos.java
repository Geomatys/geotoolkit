
package org.geotoolkit.pending.demo;

import javax.imageio.ImageIO;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.lang.Setup;

/**
 * Common geotoolkit hints for all demos
 */
public final class Demos {
    
    private Demos(){}
    
    public static void init(){
        
        //allow reprojection even if grid or bursawolf parameters are missing
        Hints.putSystemDefault(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);
        
        //global initialization
        Setup.initialize(null);
        
        //force loading all image readers/writers
        ImageIO.scanForPlugins();
        
    }
    
}
