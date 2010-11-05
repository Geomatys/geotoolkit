
package org.geotoolkit.demo;

import java.io.File;
import java.io.InputStream;
import org.geotoolkit.io.wkt.PrjFiles;
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
        InputStream stream = PRJReadWrite.class.getResourceAsStream("/projection.prj");
        CoordinateReferenceSystem crs = PrjFiles.read(stream,true);
        System.out.println(crs);

        File output = new File("sortie.prj");
        PrjFiles.write(crs, output);
    }

}
