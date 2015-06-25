/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.internal.tree;

import java.io.IOException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author rmarechal
 */
public class TreeAccessSQLByteArray extends TreeAccessByteArray {

    public TreeAccessSQLByteArray(int magicNumber, double versionNumber, int maxElements, CoordinateReferenceSystem crs) throws IOException {
        super(magicNumber, versionNumber, maxElements, crs);
    }

    public TreeAccessSQLByteArray(byte[] data, int magicNumber, double versionNumber) throws IOException, ClassNotFoundException {
        super(data, magicNumber, versionNumber);
    }

    @Override
    public strictfp void close() throws IOException {
        super.close(); //To change body of generated methods, choose Tools | Templates.
        final byte[] array = getData();
        
        //-- ecrire array dans base de donnée
    }
}
