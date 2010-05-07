/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data;

import java.net.URL;
import org.geotoolkit.storage.DataStoreException;

/**
 * FileDataStoreFactory for working with formats based on a single URL.
 * <p>
 * This interface provides a mechanism of discovery for DataStoreFactories
 * which support singular files.
 * </p>
 *
 * @author dzwiers
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface FileDataStoreFactory extends DataStoreFactory {

    /**
     * The list of filename extentions handled by this factory.
     *
     * @return List of file extensions which can be read by this
     *         dataStore.
     */
    public String[] getFileExtensions();

    /**
     * Tests if the provided url can be handled by this factory.
     *
     * @param url URL to a real file (may not be local)
     *
     * @return <code>true</code> if this url can when this dataStore can resolve and read the data specified
     */
    public boolean canProcess(URL url);

    /**
     * A DataStore attached to the provided url, may be created if needed.
     * <p>
     * Please note that additional configuration options may be available
     * via the traditional createDataStore( Map ) method provided by the
     * superclass.
     * <p>
     * @param url The data location for the
     *
     * @return Returns a DataStore created from the data source provided.
     *
     * @throws DataStoreException
     */
    public DataStore createDataStore(URL url) throws DataStoreException;

}
