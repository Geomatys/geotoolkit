/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.storage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.StringUtilities;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.Metadata;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractResource implements Resource {

    protected final Set<StoreListener> listeners = new HashSet<>();

    protected NamedIdentifier identifier;
    private DefaultMetadata metadata;

    protected AbstractResource() {
    }

    public AbstractResource(GenericName name) {
        ArgumentChecks.ensureNonNull("identifier", name);
        this.identifier = (name instanceof NamedIdentifier) ?
                (NamedIdentifier)name : new NamedIdentifier(name);
    }

    public AbstractResource(Identifier identifier) {
        ArgumentChecks.ensureNonNull("identifier", identifier);
        this.identifier = (identifier instanceof NamedIdentifier) ?
                (NamedIdentifier)identifier : new NamedIdentifier(identifier);
    }

    public AbstractResource(NamedIdentifier identifier) {
        ArgumentChecks.ensureNonNull("identifier", identifier);
        this.identifier = identifier;
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        return Optional.ofNullable(identifier);
    }

    @Override
    public final synchronized Metadata getMetadata() throws DataStoreException {
        if (metadata == null) {
            metadata = createMetadata();
            metadata.transitionTo(DefaultMetadata.State.FINAL);
        }
        return metadata;
    }

    protected DefaultMetadata createMetadata() throws DataStoreException {
        final DefaultMetadata metadata = new DefaultMetadata();
        final DefaultDataIdentification idf = new DefaultDataIdentification();
        final DefaultCitation citation = new DefaultCitation();
        if (identifier != null) citation.getIdentifiers().add(identifier);
        idf.setCitation(citation);
        metadata.setIdentificationInfo(Arrays.asList(idf));
        return metadata;
    }

    @Override
    public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Forward a structure event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendEvent(final StoreEvent event){
        final StoreListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StoreListener[listeners.size()]);
        }
        for(final StoreListener listener : lst){
            listener.eventOccured(event);
        }
    }

    /**
     * Forward given event, changing the source by this object.
     * For implementation use only.
     * @param event
     */
    public void forwardEvent(StorageEvent event){
        sendEvent(event.copy(this));
    }

    @Override
    public String toString() {
        CharSequence name = "";
        try {
            name = getMetadata().getIdentificationInfo().iterator().next().getCitation().getIdentifiers().iterator().next().getCode();
        } catch (Exception ex) {
            //do nothing : various errors can happen here : null pointer, no such element, etc.
        }
        if (this instanceof Aggregate) {
            try {
                return StringUtilities.toStringTree(name.toString(), ((Aggregate)this).components());
            } catch (DataStoreException ex) {
                return name.toString() +" (Failed to list resource components)";
            }
        } else {
            return name.toString();
        }
    }
}
