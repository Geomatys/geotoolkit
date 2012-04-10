/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.lucene;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.geotoolkit.util.logging.Logging;

/**
 *
 * @author Guilhem Legal
 */
public class LuceneUtils {
    private static final Logger LOGGER = Logging.getLogger(LuceneUtils.class);
    
    public static Directory getAppropriateDirectory(final File indexDirectory) throws IOException {
        
        // for windows
        if (System.getProperty("os.name", "").startsWith("Windows")) {
             return new SimpleFSDirectory(indexDirectory);
             
        // for unix     
        } else {
            final String archModel = System.getProperty("sun.arch.data.model");
            LOGGER.log(Level.FINER, "archmodel:{0}", archModel);
            if ("64".equals(archModel)) {
                return new MMapDirectory(indexDirectory);
            } else {
                return new NIOFSDirectory(indexDirectory);
            }
        }
    }
    
}
