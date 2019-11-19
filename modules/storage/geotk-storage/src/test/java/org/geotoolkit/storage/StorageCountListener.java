/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

import java.util.ArrayList;
import java.util.List;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;

/**
 * Test storage listener, count the number of events and store the last event objects.
 * @author Johann Sorel (Puzzle-GIS)
 */
public final class StorageCountListener implements StoreListener<StoreEvent> {

    private final List<StoreEvent> events = new ArrayList<>();

    @Override
    public void eventOccured(StoreEvent event) {
        events.add(event);
    }

    public <T extends StoreEvent> T last(Class<T> clazz) {
        for (StoreEvent event : events) {
            if (clazz.isInstance(event)) {
                return (T) event;
            }
        }
        return null;
    }

    public <T extends StoreEvent> int count(Class<T> clazz) {
        int nb = 0;
        for (StoreEvent event : events) {
            if (clazz.isInstance(event)) {
                nb++;
            }
        }
        return nb;
    }

}
