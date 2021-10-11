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
package org.geotoolkit.processing.io.unpackfile;


import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.nio.ZipUtilities;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;
import org.xeustechnologies.jtar.TarEntry;
import org.xeustechnologies.jtar.TarInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static java.nio.file.StandardOpenOption.WRITE;
import static org.geotoolkit.processing.io.unpackfile.UnpackFileDescriptor.*;

/**
 * Uncompress an archive.
 *
 * @author Johann Sorel (Geomatys)
 * @module
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

        Object source = inputParameters.getValue(SOURCE_IN);
        Object target = inputParameters.getValue(TARGET_IN);

        try {
            if (!(source instanceof Path)) {
                source = IOUtilities.toPath(source);
            }
            if (!(target instanceof Path)) {
                target = IOUtilities.toPath(target);
            }
        } catch (IOException ex) {
            fireProcessFailed("Failed to unpack, source or target is not a valid file : " + source + "  " + target, ex);
            return;
        }

        final Path src = (Path) source;
        final Path trg = (Path) target;

        final String name = src.getFileName().toString().toLowerCase();
        final List<URL> urls = new ArrayList<>();

        if (name.endsWith(".zip") || name.endsWith(".jar")) {
            try {
                final List<Path> files = ZipUtilities.unzip(src, trg, null);
                for(Path f : files){
                    urls.add(f.toUri().toURL());
                }
            } catch (IOException ex) {
                fireProcessFailed(ex.getMessage(), ex);
                return;
            }
        } else if (name.endsWith(".tar")) {
            try (InputStream is = Files.newInputStream(src);
                 BufferedInputStream bis = new BufferedInputStream(is);
                 TarInputStream tis  = new TarInputStream(bis)) {

                TarEntry entry;
                while ((entry = tis.getNextEntry()) != null) {
                    int count;
                    final byte data[] = new byte[2048];

                    final Path targetFile = trg .resolve(entry.getName());
                    urls.add(targetFile.toUri().toURL());

                    try (OutputStream os = Files.newOutputStream(targetFile, StandardOpenOption.CREATE, WRITE);
                         BufferedOutputStream dest = new BufferedOutputStream(os)) {
                        while ((count = tis.read(data)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.flush();
                    }
                }
            } catch(IOException ex){
                fireProcessFailed(ex.getMessage(), ex);
                return;
            }
        } else if (name.endsWith(".tar.gz")) {

            try (InputStream is = Files.newInputStream(src);
                 GZIPInputStream gin = new GZIPInputStream(is);
                 BufferedInputStream bis = new BufferedInputStream(gin);
                 TarInputStream tis  = new TarInputStream(bis)) {

                TarEntry entry;
                while ((entry = tis.getNextEntry()) != null) {
                    int count;
                    final byte data[] = new byte[2048];

                    final Path targetFile = trg .resolve(entry.getName());
                    urls.add(targetFile.toUri().toURL());


                    try (OutputStream os = Files.newOutputStream(targetFile, StandardOpenOption.CREATE, WRITE);
                         BufferedOutputStream dest = new BufferedOutputStream(os)) {
                        while ((count = tis.read(data)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.flush();
                    }
                }
            }catch(IOException ex){
                fireProcessFailed(ex.getMessage(), ex);
                return;
            }

        } else {
            fireProcessFailed("Failed to unpack, compression unknowned : " + source, null);
        }

        outputParameters.getOrCreate(RESULT_OUT).setValue(urls.toArray(new URL[urls.size()]));

        fireProcessCompleted("Unpack done.");
    }

}
