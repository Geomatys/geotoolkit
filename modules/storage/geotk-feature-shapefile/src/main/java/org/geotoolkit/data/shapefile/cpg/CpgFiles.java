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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import org.geotoolkit.util.FileUtilities;

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
     * Given stream will be closed.
     *
     * @param in input channel
     * @return CharSet
     * @throws IOException
     */
    public static Charset read(ReadableByteChannel in) throws IOException{
        final InputStream is = Channels.newInputStream(in);
        final String str = FileUtilities.getStringFromStream(is).trim();
        in.close();
        return Charset.forName(str);
    }

    /**
     * Write charset to given file.
     *
     * @param cs charset to write.
     * @param file output file.
     * @throws IOException
     */
    public static void write(Charset cs, File file) throws IOException{
        try (FileWriter cpgWriter = new FileWriter(file)) {
            cpgWriter.write(cs.name());
        }
    }

}
