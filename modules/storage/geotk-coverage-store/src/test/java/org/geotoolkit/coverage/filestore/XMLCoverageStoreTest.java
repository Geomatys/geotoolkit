/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.coverage.filestore;

import org.geotoolkit.coverage.xmlstore.XMLCoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.coverage.PyramidalModelStoreTest;
import org.geotoolkit.parameter.Parameters;
import org.opengis.parameter.ParameterValueGroup;

import java.io.File;

/**
 * @author Johann Sorel (Geomatys)
 */
public class XMLCoverageStoreTest extends PyramidalModelStoreTest {

    @Override
    protected CoverageStore createStore() throws Exception{

        final File tempFolder = File.createTempFile("mosaic", "");
        tempFolder.delete();
        tempFolder.mkdirs();

        final ParameterValueGroup params = XMLCoverageStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(XMLCoverageStoreFactory.PATH, params).setValue(tempFolder.toURI().toURL());
        final CoverageStore store = CoverageStoreFinder.open(params);

        return store;
    }

}
