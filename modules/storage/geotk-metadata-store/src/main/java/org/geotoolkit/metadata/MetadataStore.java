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

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.DomainValues;
import org.geotoolkit.storage.DataStore;
import org.opengis.parameter.ParameterValueGroup;
import org.w3c.dom.Node;

/**
 * Metadata store.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class MetadataStore extends DataStore{

    protected ParameterValueGroup params;

    public MetadataStore(ParameterValueGroup params) {
        this.params = params;
    }

    @Override
    public ParameterValueGroup getConfiguration() {
        return params;
    }

    public abstract MetadataReader getReader();

    public abstract MetadataWriter getWriter();

    public abstract void setLogLevel(Level level);

    public abstract List<MetadataType> getSupportedDataTypes();

    public abstract Map<String, URI> getConceptMap();

    public abstract List<QName> getAdditionalQueryableQName();

    public abstract String[] executeEbrimSQLQuery(final String sqlQuery) throws MetadataIoException;

    public abstract Node getMetadata(final String identifier, final MetadataType mode) throws MetadataIoException;

    public abstract Node getMetadata(final String identifier, final MetadataType mode, final ElementSetType type, final List<QName> elementName) throws MetadataIoException;

    public abstract List<DomainValues> getFieldDomainofValues(final String propertyNames) throws MetadataIoException;

    public abstract boolean storeMetadata(final Node obj) throws MetadataIoException;

    public abstract boolean deleteMetadata(final String metadataID) throws MetadataIoException;

    public abstract boolean replaceMetadata(String metadataID, Node any) throws MetadataIoException;

    public abstract boolean updateMetadata(String metadataID, Map<String, Object> properties) throws MetadataIoException;

    public abstract boolean updateSupported();

    public abstract boolean deleteSupported();

    public abstract boolean existMetadata(final String identifier) throws MetadataIoException;

    public abstract List<? extends Object> getAllEntries() throws MetadataIoException;

    public abstract List<String> getAllIdentifiers() throws MetadataIoException;

    public abstract Iterator<String> getIdentifierIterator() throws MetadataIoException;

    public abstract int getEntryCount() throws MetadataIoException;

}
