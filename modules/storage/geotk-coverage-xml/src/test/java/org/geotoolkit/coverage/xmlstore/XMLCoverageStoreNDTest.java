/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.coverage.xmlstore;

import org.geotoolkit.coverage.PyramidalModelStoreNDTest;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.junit.After;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johann Sorel (Geomatys)
 */
public class XMLCoverageStoreNDTest extends PyramidalModelStoreNDTest {

    private final List<File> folders = new ArrayList<File>();

    @Override
    protected CoverageStore createStore() throws Exception{

        final File tempFolder = File.createTempFile("mosaic", "");
        tempFolder.delete();
        tempFolder.mkdirs();
        folders.add(tempFolder);
        final CoverageStore store = new XMLCoverageStore(tempFolder);

        return store;
    }

    @After
    public void cleanup(){
        for(File f : folders){
            IOUtilities.deleteSilently(f.toPath());
        }
        folders.clear();
    }
}
