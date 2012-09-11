
package org.geotoolkit.pending.demo.referencing;

import java.io.File;
import java.io.InputStream;
import org.geotoolkit.io.wkt.PrjFiles;
import org.geotoolkit.pending.demo.Demos;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Johann Sorel
 */
public class PRJReadWrite {

    /**
     * *.prj files are communly used in GIS.
     * This exemple show how to read and write such files.
     */
    public static void main(String[] args) throws Exception {
        Demos.init();
        
        final InputStream stream = PRJReadWrite.class.getResourceAsStream("/projection.prj");
        final CoordinateReferenceSystem crs = PrjFiles.read(stream,true);
        System.out.println(crs);

        final File output = new File("sortie.prj");
        PrjFiles.write(crs, output);
    }

}
