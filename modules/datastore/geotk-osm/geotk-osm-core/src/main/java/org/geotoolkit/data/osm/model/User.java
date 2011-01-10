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
package org.geotoolkit.data.osm.model;

import java.util.ArrayList;
import java.util.Collection;

import org.geotoolkit.feature.AbstractComplexAttribute;

import org.opengis.feature.Property;
import org.opengis.filter.identity.Identifier;

import static org.geotoolkit.data.osm.model.OSMModelConstants.*;

/**
 * Open Street Map user.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class User extends AbstractComplexAttribute<Collection<Property>,Identifier> {

    /**
     * No user id.
     */
    public static final int USER_ID_NONE = -1;

    /**
     * The user instance representing no user available or no user applicable.
     */
    public static final User NONE = new User(USER_ID_NONE, "");

    private final String name;
    private final int id;

    /**
     * Creates a new instance.
     *
     * @param id The userId associated with the user name.
     * @param userName The name of the user that this object represents.
     */
    private User(final int id, final String userName) {
        super(OSMModelConstants.ATT_USER, new SimpleId(id));
        if (userName == null) {
            throw new NullPointerException("The user name cannot be null.");
        }

        // Disallow a user to be created with the "NONE" id.
        if (NONE != null && id == USER_ID_NONE) {
            throw new IllegalArgumentException("A user id of " + USER_ID_NONE + " is not permitted.");
        }

        this.name = userName;
        this.id = id;
    }

    /**
     * @return The userId.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The name of the user.
     */
    public String getUserName() {
        return name;
    }

    public static User create(final int userId, String userName){
        if(userId <=0){
            return NONE;
        }

        if(userName == null) userName = "";
        return new User(userId, userName);
    }

    @Override
    public Collection<Property> getValue() {
        final Collection<Property> props = new ArrayList<Property>();
        props.add(FF.createAttribute(id, ATT_USER_ID, null));
        props.add(FF.createAttribute(name, ATT_USER_NAME, null));
        return props;
    }

    // feature/attribut model --------------------------------------------------

    @Override
    public String toString() {
        return new StringBuilder().append(id).append(" - ").append(name).toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 59 * hash + this.id;
        return hash;
    }
    
}
