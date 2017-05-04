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

import org.w3c.dom.Node;

import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface MetadataWriter {

    /**
     * Record an object in the metadata datasource.
     *
     * @param obj The object to store in the datasource.
     * @return true if the storage succeed, false else.
     *
     * @throws org.geotoolkit.metadata.MetadataIoException
     */
    boolean storeMetadata(final Node obj) throws MetadataIoException;

    /**
     * Delete an object in the metadata database.
     * @param metadataID The identifier of the metadata to delete.
     * @return true if the delete succeed, false else.
     *
     * @throws org.geotoolkit.metadata.MetadataIoException
     */
    boolean deleteMetadata(final String metadataID) throws MetadataIoException;

    /**
     * Return true if the specified id is already used in the database.
     * @param metadataID
     * @return
     *
     * @throws org.geotoolkit.metadata.MetadataIoException
     */
    boolean isAlreadyUsedIdentifier(final String metadataID) throws MetadataIoException;

    /**
     * Replace an object in the metadata datasource.
     *
     * @param metadataID The identifier of the metadata to Replace.
     * @param any The object to replace the matching metadata.
     * @return
     *
     * @throws org.geotoolkit.metadata.MetadataIoException
     */
     boolean replaceMetadata(String metadataID, Node any) throws MetadataIoException;

    /**
     * Return true if the Writer supports the delete mecanism.
     * @return
     */
    boolean deleteSupported();

    /**
     * Return true if the Writer supports the update mecanism.
     * @return
     */
    boolean updateSupported();

    /**
     * Destoy all the resource and close connection.
     */
    void destroy();

    /**
     * Update an object in the metadata database.
     *
     * @param metadataID The identifier of the metadata to Replace.
     * @param properties A List of property-value to replace in the specified metadata.
     * @return
     * @throws org.geotoolkit.metadata.MetadataIoException
     */
    boolean updateMetadata(String metadataID, Map<String, Object> properties) throws MetadataIoException;

    /**
     * Set the global level of log.
     *
     * @param logLevel
     */
    void setLogLevel(Level logLevel);

    boolean canImportInternalData();
}
