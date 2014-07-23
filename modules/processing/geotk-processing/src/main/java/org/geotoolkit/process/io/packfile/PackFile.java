/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.process.io.packfile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import static org.geotoolkit.parameter.Parameters.getOrCreate;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import static org.geotoolkit.process.io.packfile.PackFileDescriptor.INSTANCE;
import static org.geotoolkit.process.io.packfile.PackFileDescriptor.RESULT_OUT;
import static org.geotoolkit.process.io.packfile.PackFileDescriptor.SOURCE_IN;
import static org.geotoolkit.process.io.packfile.PackFileDescriptor.TARGET_IN;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class PackFile extends AbstractProcess {

    static final int BUFFER = 2048;
    
    public PackFile(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }
    
    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException {
        fireProcessStarted("Start pack");
        final File[] source = (File[]) getOrCreate(SOURCE_IN, inputParameters).getValue();
        final File target   = (File) getOrCreate(TARGET_IN, inputParameters).getValue();
        
        //Prepare compression
        try {
            final FileOutputStream fileOS = new FileOutputStream(target);
            final CheckedOutputStream checksumOS = new CheckedOutputStream(fileOS, new Adler32());
            final BufferedOutputStream buffOS = new BufferedOutputStream(checksumOS);
            final ZipOutputStream zipOS = new ZipOutputStream(buffOS);
            zipOS.setMethod(ZipOutputStream.DEFLATED);
            zipOS.setLevel(Deflater.BEST_COMPRESSION);

            //Compress all files
            compressAllDirectoryFiles(zipOS, new byte[BUFFER], source, null);

            //End compression
            zipOS.close();
            buffOS.close();
            checksumOS.close();
            fileOS.close();   
        } catch (IOException ex) {
            throw new ProcessException("IO exception while packing files", this, ex);
        }
        
        getOrCreate(RESULT_OUT, outputParameters).setValue(target);
        
        fireProcessCompleted("Pack done.");
    }
    
    private static void compressAllDirectoryFiles(final ZipOutputStream zipOS, final byte data[], 
            final File[] files, final String treePath) throws FileNotFoundException, IOException {
        
        for (int i = 0; i < files.length; i++) {
            //Restore file tree
            final String fileName = files[i].getName();
            final String filePath = (treePath != null) ? treePath + File.separator + fileName : fileName;
            
            if (files[i].isDirectory()) {
                //Recursive call for directories
                compressAllDirectoryFiles(zipOS, data, files[i].listFiles(), filePath);
            } else {
                //Prepare file compression
                final FileInputStream fileIS = new FileInputStream(files[i]);
                final BufferedInputStream buffIS = new BufferedInputStream(fileIS, BUFFER);
                final ZipEntry entry = new ZipEntry(filePath);
                zipOS.putNextEntry(entry);
                
                //Compress file
                int count;
                while ((count = buffIS.read(data, 0, BUFFER)) != -1) {
                    zipOS.write(data, 0, count);
                }

                //End file compression
                zipOS.closeEntry();
                buffIS.close();
            }
        }
    }

}
