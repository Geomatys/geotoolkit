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
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 * Storage management event.
 *
 * @todo work in progress
 * @author Johann Sorel (Geomatys)
 */
public class StorageManagementEvent extends EventObject{

    public static enum Type{
        ADD,
        UPDATE,
        DELETE
    };

    private final Type type;
    private final Name name;
    private final FeatureType oldType;
    private final FeatureType newType;

    private StorageManagementEvent(final Object source, final Type type, final Name name, final FeatureType oldtype, final FeatureType newtype){
        super(source);

        ensureNonNull("type", type);
        ensureNonNull("name", name);
        if(oldtype == null && newtype == null){
            throw new NullPointerException("Old and new feature type can not be both null.");
        }
        this.type = type;
        this.name = name;
        this.oldType = oldtype;
        this.newType = newtype;
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

    /**
     * Retrieve the newly created feature type or
     * the updated feature type.
     * @return FeatureType or null if event is a Delete
     */
    public FeatureType getNewFeatureType() {
        return newType;
    }

    /**
     * Retrieve the deleted feature type or
     * the old updated feature type.
     *
     * @return FeatureType or null if event is an Add
     */
    public FeatureType getOldFeatureType() {
        return oldType;
    }

    public static StorageManagementEvent createAddEvent(final Object source, final Name name, final FeatureType type){
        return new StorageManagementEvent(source, Type.ADD, name, null, type);
    }

    public static StorageManagementEvent createUpdateEvent(final Object source, final Name name, final FeatureType oldType, final FeatureType newType){
        return new StorageManagementEvent(source, Type.UPDATE, name, oldType, newType);
    }

    public static StorageManagementEvent createDeleteEvent(final Object source, final Name name, final FeatureType type){
        return new StorageManagementEvent(source, Type.DELETE, name, type, null);
    }

    public static StorageManagementEvent resetSource(final Object source, final StorageManagementEvent event){
        return new StorageManagementEvent(source, event.type, event.name, event.oldType, event.newType);
    }

}
