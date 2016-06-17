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
package org.geotoolkit.metadata;

import org.apache.sis.util.logging.Logging;
import org.w3c.dom.Node;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class AbstractMetadataWriter implements MetadataWriter {

    /**
     * A debugging logger.
     */
    protected static final Logger LOGGER = Logging.getLogger("org.constellation.metadata.io");
    
    public static final int INSERTED = 0;

    public static final int REPLACED = 1;

    /**
     * The default level for logging non essential informations (ToSee => finer)
     */
    protected Level logLevel = Level.INFO;
    
    /**
     * Build a new metadata writer.
     * 
     * @throws org.geotoolkit.metadata.MetadataIoException
     */
    public AbstractMetadataWriter() throws MetadataIoException {
    }

    /**
     * Record an object in the metadata dataSource.
     * 
     * @param obj The object to store in the dataSource.
     * @return true if the storage succeed, false else.
     * 
     * @throws org.geotoolkit.metadata.MetadataIoException
     */
    @Override
    public abstract boolean storeMetadata(final Node obj) throws MetadataIoException;

    /**
     * Delete an object in the metadata database.
     * @param metadataID The identifier of the metadata to delete.
     * @return true if the delete succeed, false else.
     * 
     * @throws org.geotoolkit.metadata.MetadataIoException
     */
    @Override
    public abstract boolean deleteMetadata(final String metadataID) throws MetadataIoException;


    /**
     * Replace an object in the metadata dataSource.
     *
     * @param metadataID The identifier of the metadata to Replace.
     * @param any The object to replace the matching metadata.
     * @return true if the replacing succeed.
     * 
     * @throws org.geotoolkit.metadata.MetadataIoException
     */
    @Override
    public abstract boolean replaceMetadata(final String metadataID, final Node any) throws MetadataIoException;

    /**
     * Update an object in the metadata database.
     *
     * @param metadataID The identifier of the metadata to Replace.
     * @param properties A List of property-value to replace in the specified metadata.
     * @return true if the update succeed.
     * 
     * @throws org.geotoolkit.metadata.MetadataIoException
     */
    @Override
    public abstract boolean updateMetadata(final String metadataID, final Map<String , Object> properties) throws MetadataIoException;
    
    /**
     * @return true if the Writer supports the delete mecanism.
     */
    @Override
    public abstract boolean deleteSupported();

    /**
     * @return true if the Writer supports the update mecanism.
     */
    @Override
    public abstract boolean updateSupported();

    /**
     * Destroy all the resource and close connection.
     */
    @Override
    public abstract void destroy();

    /**
     * @param logLevel the LogLevel to set
     */
    @Override
    public void setLogLevel(final Level logLevel) {
        this.logLevel = logLevel;
    }
}
