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

import java.util.HashSet;
import java.util.Set;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.gui.swing.tree.Trees;
import org.opengis.metadata.Identifier;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractResource implements Resource{

    protected final Set<StorageListener> listeners = new HashSet<>();


    protected Identifier identifier;

    protected AbstractResource() {
    }

    public AbstractResource(GenericName name) {
        ArgumentChecks.ensureNonNull("identifier", name);
        this.identifier = new NamedIdentifier(name);
    }

    public AbstractResource(Identifier identifier) {
        ArgumentChecks.ensureNonNull("identifier", identifier);
        this.identifier = identifier;
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public void addStorageListener(final StorageListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeStorageListener(final StorageListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Forward a structure event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendStructureEvent(final StorageEvent event){
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for(final StorageListener listener : lst){
            listener.structureChanged(event);
        }
    }

    /**
     * Forward a data event to all listeners.
     * @param event , event to send to listeners.
     */
    protected void sendContentEvent(final StorageEvent event){
        final StorageListener[] lst;
        synchronized (listeners) {
            lst = listeners.toArray(new StorageListener[listeners.size()]);
        }
        for(final StorageListener listener : lst){
            listener.contentChanged(event);
        }
    }

    /**
     * Forward given event, changing the source by this object.
     * For implementation use only.
     * @param event
     */
    public void forwardStructureEvent(StorageEvent event){
        sendStructureEvent(event.copy(this));
    }

    /**
     * Forward given event, changing the source by this object.
     * For implementation use only.
     * @param event
     */
    public void forwardContentEvent(StorageEvent event){
        sendContentEvent(event.copy(this));
    }

    @Override
    public String toString() {
        CharSequence name = "";
        try {
            name = getMetadata().getIdentificationInfo().iterator().next().getCitation().getIdentifiers().iterator().next().getCode();
        } catch (DataStoreException ex) {
            //do nothing
        }
        if (this instanceof DataSet) {
            return Trees.toString(name.toString(), ((DataSet)this).getResources());
        } else {
            return name.toString();
        }
    }

}
