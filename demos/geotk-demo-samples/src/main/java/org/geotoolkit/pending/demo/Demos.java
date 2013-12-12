
package org.geotoolkit.pending.demo;

import com.sun.java.swing.plaf.gtk.GTKLookAndFeel;
import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.lang.Setup;
import org.openide.util.Exceptions;

/**
 * Common geotoolkit hints for all demos
 */
public final class Demos {

    private Demos(){}

    public static void init(){
        try {
            UIManager.setLookAndFeel(new GTKLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        //allow reprojection even if grid or bursawolf parameters are missing
        Hints.putSystemDefault(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);

        //global initialization
        Setup.initialize(null);

        //force loading all image readers/writers
        ImageIO.scanForPlugins();

    }

}
