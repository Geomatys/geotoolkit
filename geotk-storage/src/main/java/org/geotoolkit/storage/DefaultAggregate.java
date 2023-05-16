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

package org.geotoolkit.storage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import jakarta.xml.bind.annotation.XmlTransient;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.apache.sis.storage.event.StoreListeners;
import org.geotoolkit.storage.event.AggregationEvent;
import org.opengis.metadata.Identifier;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlTransient
public class DefaultAggregate extends AbstractResource implements Aggregate {

    private final StoreListeners listeners = new StoreListeners(null, this);
    protected final List<Resource> resources = new CopyOnWriteArrayList<Resource>();

    public DefaultAggregate(GenericName name) {
        super(name);
    }

    public DefaultAggregate(Identifier identifier) {
        super(identifier);
    }

    public void addResource(Resource res) {
        resources.add(res);
        listeners.fire(AggregationEvent.class, new AggregationEvent(this, AggregationEvent.TYPE_ADD, res));
    }

    public void removeResource(Resource res) {
        if (resources.remove(res)) {
            listeners.fire(AggregationEvent.class, new AggregationEvent(this, AggregationEvent.TYPE_REMOVE, res));
        }
    }

    @Override
    public Collection<Resource> components() {
        return Collections.unmodifiableList(resources);
    }

    @Override
    public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
        listeners.addListener(eventType, listener);
    }

    @Override
    public synchronized <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
        listeners.removeListener(eventType, listener);
    }
}
