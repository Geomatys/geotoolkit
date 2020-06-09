
package org.geotoolkit.pending.demo;

import javax.imageio.ImageIO;
import org.geotoolkit.lang.Setup;

/**
 * Common geotoolkit hints for all demos
 */
public final class Demos {

    private Demos(){}

    public static void init() {

        //force loading all image readers/writers
        ImageIO.scanForPlugins();

        //global initialization
        Setup.initialize(null);
    }

}
