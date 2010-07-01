package org.geotoolkit.data.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 *
 * @author Samuel Andrés
 */
public class FileUtilities {

    private FileUtilities(){
    }

    public static void copy( File source, File destination){
        FileInputStream sourceFile=null;
        FileOutputStream destinationFile=null;

        try {

            sourceFile = new FileInputStream(source);
            destinationFile = new FileOutputStream(destination);

            // Lecture par segment de 0.5Mo
            byte buffer[] = new byte[512*1024];
            int nbLecture;
            while( (nbLecture = sourceFile.read(buffer)) != -1 )
                destinationFile.write(buffer, 0, nbLecture);

        } catch( java.io.FileNotFoundException f ) {
        } catch( java.io.IOException e ) {
        } finally {

            try {
                sourceFile.close();
            } catch(Exception e) { }
            try {
                destinationFile.close();
            } catch(Exception e) { }
        }

    }
}
