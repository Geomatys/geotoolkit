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
package org.geotoolkit.process.io.unpackfile;


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xeustechnologies.jtar.TarEntry;
import org.xeustechnologies.jtar.TarInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.zip.GZIPInputStream;
import java.net.URL;
import java.util.List;
import org.geotoolkit.util.FileUtilities;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.process.io.unpackfile.UnpackFileDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;

/**
 * Uncompress an archive.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class UnpackFile extends AbstractProcess {

    public UnpackFile(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException {

        fireProcessStarted("Start unpack");

        Object source = getOrCreate(SOURCE_IN, inputParameters).getValue();
        Object target = getOrCreate(TARGET_IN, inputParameters).getValue();

        try {
            if (!(source instanceof File)) {
                source = IOUtilities.tryToFile(source);
            }
            if (!(target instanceof File)) {
                target = IOUtilities.tryToFile(target);
            }
        } catch (IOException ex) {
            fireProcessFailed("Failed to unpack, source or target is not a valid file : " + source + "  " + target, ex);
            return;
        }

        final File src = (File) source;
        final File trg = (File) target;

        final String name = src.getName().toLowerCase();
        final List<URL> urls = new ArrayList<URL>();

        if (name.endsWith(".zip") || name.endsWith(".jar")) {
            try {
                final List<File> files = FileUtilities.unzip(src, trg, null);                
                for(File f : files){
                    urls.add(f.toURI().toURL());
                }
            } catch (IOException ex) {
                fireProcessFailed(ex.getMessage(), ex);
                return;
            }
        } else if (name.endsWith(".tar")) {
            TarInputStream tis = null;
            try{
                tis = new TarInputStream(new BufferedInputStream(new FileInputStream(src)));
                TarEntry entry;
                while ((entry = tis.getNextEntry()) != null) {
                    int count;
                    final byte data[] = new byte[2048];

                    final File targetFile = new File(trg,entry.getName());
                    urls.add(targetFile.toURI().toURL());
                    final FileOutputStream fos = new FileOutputStream(targetFile);
                    final BufferedOutputStream dest = new BufferedOutputStream(fos);

                    while ((count = tis.read(data)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }catch(IOException ex){
                fireProcessFailed(ex.getMessage(), ex);
                return;
            }finally{
                try {
                    tis.close();
                } catch (IOException ex) {
                    //we tryed
                }
            }
        } else if (name.endsWith(".tar.gz")) {
            GZIPInputStream gin = null;
            TarInputStream tis = null;
            try{
                gin = new GZIPInputStream(new FileInputStream(src));
                tis = new TarInputStream(new BufferedInputStream(gin));
                TarEntry entry;
                while ((entry = tis.getNextEntry()) != null) {
                    int count;
                    final byte data[] = new byte[2048];

                    final File targetFile = new File(trg,entry.getName());
                    urls.add(targetFile.toURI().toURL());
                    final FileOutputStream fos = new FileOutputStream(targetFile);
                    final BufferedOutputStream dest = new BufferedOutputStream(fos);

                    while ((count = tis.read(data)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                }
            }catch(IOException ex){
                fireProcessFailed(ex.getMessage(), ex);
                return;
            }finally{
                try {
                    if(tis!=null){
                        tis.close();
                    }
                    if(gin != null){
                        gin.close();
                    }
                } catch (IOException ex) {
                    //we tryed
                }
            }
            
        } else {
            fireProcessFailed("Failed to unpack, compression unknowned : " + source, null);
        }
        
        getOrCreate(RESULT_OUT, outputParameters).setValue(urls.toArray(new URL[urls.size()]));
        
        fireProcessCompleted("Unpack done.");
    }

}
