/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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

import java.util.Arrays;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.WritableAggregate;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.opengis.feature.FeatureType;
import org.opengis.metadata.Metadata;

/**
 * Define the structure and properties of a FeatureSet to be created.
 *
 * <p>
 * A DefiningFeatureSet is meant to be passed to {@link WritableAggregate#add(org.apache.sis.storage.Resource) }.
 * It allows the target Aggregate to create and prepare space for a new type of features
 * without providing any features yet.
 * </p>
 * <p>
 * Special implementations, such as JDBC are encouraged to provider a custom
 * sub-class of DefiningFeatureSet to store additional creation informations.
 * </p>
 * Example of possible informations are :
 * <ul>
 * <li>index</li>
 * <li>comment</li>
 * <li>encoding</li>
 * <li>compression</li>
 * </ul>
 *
 * <p>
 * Note : this class is experimental and should be moved to SIS when ready.
 * </p>
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefiningFeatureSet implements Resource {

    private final FeatureType type;
    private final Metadata metadata;

    /**
     *
     * @param type mandatory resource type
     * @param metadata can be null, a default one will be created.
     */
    public DefiningFeatureSet(FeatureType type, Metadata metadata) {
        ArgumentChecks.ensureNonNull("type", type);
        this.type = type;
        if (metadata == null) {
            //create a basic one with identifier
            final DefaultMetadata md = new DefaultMetadata();
            final DefaultDataIdentification ident = new DefaultDataIdentification();
            final DefaultCitation citation = new DefaultCitation();
            citation.setTitle(new SimpleInternationalString(type.getName().toString()));
            citation.setIdentifiers(Arrays.asList(new NamedIdentifier(type.getName())));
            ident.setCitation(citation);
            md.setIdentificationInfo(Arrays.asList(ident));
            metadata = md;
        }
        this.metadata = metadata;
    }

    /**
     *
     * @return defined type
     * @throws DataStoreException
     */
    public FeatureType getType() throws DataStoreException {
        return type;
    }

    /**
     * The returned metadata contains only general informations about this type.
     * Numeric and statistic informations should not be available.
     *
     * @return Metadata
     * @throws DataStoreException
     */
    @Override
    public Metadata getMetadata() throws DataStoreException {
        return metadata;
    }

    @Override
    public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }

    @Override
    public <T extends ChangeEvent> void removeListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }

}
