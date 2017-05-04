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

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface MetadataReader {

    /**
     * Return a metadata object from the specified identifier.
     *
     * @param identifier The metadata identifier.
     * @param mode An output schema mode: EBRIM, ISO_19115, DUBLINCORE and SENSORML supported.
     *
     * @return A marshallable metadata object.
     * @throws MetadataIoException
     */
    Node getMetadata(final String identifier, final MetadataType mode) throws MetadataIoException;

    /**
     * Return true if the metadata exist.
     *
     * @param identifier The metadata identifier.
     *
     * @return true if the metadata exist
     * @throws MetadataIoException
     */
    boolean existMetadata(final String identifier) throws MetadataIoException;

    /**
     * Return the number of metadata in the datasource.
     *
     * @return the number of metadata in the datasource.
     * @throws MetadataIoException
     */
    int getEntryCount() throws MetadataIoException;

    /**
     * @return all the entries from the database
     * @throws MetadataIoException
     */
    List<? extends Object> getAllEntries() throws MetadataIoException;

     /**
     * @return all the entries identifiers from the database
     *
     * @throws org.geotoolkit.metadata.MetadataIoException
     */
    List<String> getAllIdentifiers() throws MetadataIoException;

    Iterator<String> getIdentifierIterator() throws MetadataIoException;

    Iterator<? extends Object> getEntryIterator() throws MetadataIoException;

    boolean useEntryIterator();

    /**
     * Destroy all the resource used by this reader.
     */
    void destroy();

    /**
     * Remove a metadata from the cache.
     *
     * @param identifier The metadata identifier.
     */
    void removeFromCache(final String identifier);

    /**
     * Remove all metadata from the cache.
     */
    void clearCache();


    /**
     * @return true is the cache mecanism is enabled.
     */
    boolean isCacheEnabled();

    /**
     * @return true is the cache mecanism is enabled.
     */
    boolean isThreadEnabled();

    /**
     * @return the list of supported data types.
     */
    List<MetadataType> getSupportedDataTypes();

    /**
     * @return A map of label / Skos concept URI.
     */
    Map<String, URI> getConceptMap();

    /**
     * Set the global level of log for information message of this reader.
     * @param logLevel
     */
    void setLogLevel(Level logLevel);
}
