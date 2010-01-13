/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

import java.util.EventObject;
import org.opengis.feature.type.Name;

/**
 * Storage content event.
 *
 * @todo work in progress , what kind of information should we return?
 * list of ids for add ?
 * filter and modified attributs on update ?
 * list of ids for delete ?
 * @author Johann Sorel (Geomatys)
 */
public class StorageContentEvent extends EventObject{

    public static enum Type{
        ADD,
        UPDATE,
        DELETE
    };

    private final Type type;
    private final Name name;

    public StorageContentEvent(Object source, Type type, Name name){
        super(source);

        this.type = type;
        this.name = name;
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
    public Name getFeatureTypeName() {
        return name;
    }

    public static StorageContentEvent createAddEvent(Object source, Name name){
        return new StorageContentEvent(source, Type.ADD, name);
    }

    public static StorageContentEvent createUpdateEvent(Object source, Name name){
        return new StorageContentEvent(source, Type.UPDATE, name);
    }

    public static StorageContentEvent createDeleteEvent(Object source, Name name){
        return new StorageContentEvent(source, Type.DELETE, name);
    }

    public static StorageContentEvent resetSource(Object source, StorageContentEvent event){
        return new StorageContentEvent(source, event.type, event.name);
    }

}
