/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.data.shapefile.cpg;

import org.geotoolkit.nio.IOUtilities;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * CPG files utilities.
 * *.cpg files contains a single word for the name of the dbf character encoding.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CpgFiles {

    private CpgFiles(){}

    /**
     * Read charset from given stream.
     *
     * @param in input channel
     * @return CharSet
     * @throws IOException
     */
    public static Charset read(ReadableByteChannel in) throws IOException{
        final String str = IOUtilities.toString(Channels.newInputStream(in)).trim();
        return Charset.forName(str);
    }

    /**
     * Write charset to given file.
     *
     * @param cs charset to write.
     * @param file output file.
     * @throws IOException
     */
    public static void write(Charset cs, Path file) throws IOException{
        try (BufferedWriter cpgWriter = Files.newBufferedWriter(file, Charset.defaultCharset())) {
            cpgWriter.write(cs.name());
        }
    }

}
