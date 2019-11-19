/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Geomatys
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

package org.geotoolkit.storage.event;

import org.apache.sis.storage.Resource;
import org.opengis.filter.Id;
import org.opengis.util.GenericName;

/**
 * Storage content event.
 *
 * @todo work in progress , what kind of information should we return?
 * list of ids for add ?
 * filter and modified attributs on update ?
 * list of ids for delete ?
 * @author Johann Sorel (Geomatys)
 */
public class FeatureStoreContentEvent extends StorageEvent {

    public static enum Type{
        ADD,
        UPDATE,
        DELETE,
        /**
         * When a modification on the session has been made, new pending changes.
         */
        SESSION
    };

    private final Type type;
    private final GenericName name;
    private Id ids;

    public FeatureStoreContentEvent(final Resource source, final Type type, final GenericName name, final Id candidates){
        super(source);

        this.type = type;
        this.name = name;
        this.ids = candidates;
    }

    /**
     * get the event type, can be Add, Update or Delete.
     * @return Type of the event , never null.
     */
    public Type getType() {
        return type;
    }

    /**
     * Get the affected type name by this event.
     * @return Name , never null.
     */
    public GenericName getFeatureTypeName() {
        return name;
    }

    /**
     * Get the modified feature ids related to this event.
     * This object may be null if the ids could not be retrieved.
     * @return Id or null
     */
    public Id getIds() {
        return ids;
    }

    public FeatureStoreContentEvent copy(final Resource source){
        return new FeatureStoreContentEvent(source, type, name, ids);
    }

    public static FeatureStoreContentEvent createAddEvent(final Resource source, final GenericName name, final Id ids){
        return new FeatureStoreContentEvent(source, Type.ADD, name, ids);
    }

    public static FeatureStoreContentEvent createUpdateEvent(final Resource source, final GenericName name, final Id ids){
        return new FeatureStoreContentEvent(source, Type.UPDATE, name, ids);
    }

    public static FeatureStoreContentEvent createDeleteEvent(final Resource source, final GenericName name, final Id ids){
        return new FeatureStoreContentEvent(source, Type.DELETE, name, ids);
    }

    public static FeatureStoreContentEvent createSessionEvent(final Resource source){
        return new FeatureStoreContentEvent(source, Type.SESSION, null, null);
    }

}
