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
package org.geotoolkit.storage.coverage;

import java.util.Arrays;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.util.NamesExt;
import org.opengis.metadata.Metadata;
import org.opengis.util.GenericName;

/**
 * Define the structure and properties of a CoverageResource to be created.
 *
 * <p>
 * A DefiningCoverageResource is meant to be passed to {@link WritableAggregate#add(org.apache.sis.storage.Resource) }.
 * It allows the target Aggregate to create and prepare space for a new type of coverages
 * without providing any coverage yet.
 * </p>
 * <p>
 * Special implementations, such as tiled formats are encouraged to provider a custom
 * sub-class of DefiningCoverageResource to store additional creation informations.
 * </p>
 * Example of possible informations are :
 * <ul>
 * <li>CoordinateReferenceSystem</li>
 * <li>Pyramid depth</li>
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
public class DefiningCoverageResource implements Resource {

    private final GenericName name;
    private final Metadata metadata;

    /**
     *
     * @param name mandatory new resource name
     */
    public DefiningCoverageResource(String name) {
        this(NamesExt.valueOf(name),null);
    }
    /**
     *
     * @param name mandatory new resource name
     */
    public DefiningCoverageResource(GenericName name) {
        this(name,null);
    }

    /**
     *
     * @param name mandatory new resource name
     * @param metadata can be null, a default one will be created.
     */
    public DefiningCoverageResource(GenericName name, Metadata metadata) {
        ArgumentChecks.ensureNonNull("name", name);
        this.name = name;
        if (metadata == null) {
            //create a basic one with identifier
            final DefaultMetadata md = new DefaultMetadata();
            final DefaultDataIdentification ident = new DefaultDataIdentification();
            final DefaultCitation citation = new DefaultCitation();
            citation.setTitle(new SimpleInternationalString(name.toString()));
            citation.setIdentifiers(Arrays.asList(new NamedIdentifier(name)));
            ident.setCitation(citation);
            md.setIdentificationInfo(Arrays.asList(ident));
            metadata = md;
        }
        this.metadata = metadata;
    }

    @Override
    public GenericName getIdentifier() {
        return name;
    }

    /**
     * New resource wanted name.
     *
     * @return new resource name.
     */
    public GenericName getName() {
        return name;
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
