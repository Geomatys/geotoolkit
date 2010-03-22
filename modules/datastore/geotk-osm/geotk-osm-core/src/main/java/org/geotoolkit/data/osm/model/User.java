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

/**
 * Open Street Map user.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class User {

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
    private User(int id, String userName) {
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
    public String getName() {
        return name;
    }

    public static User create(int userId, String userName){
        if(userId <=0){
            return NONE;
        }

        if(userName == null) userName = "";
        return new User(userId, userName);
    }

    @Override
    public String toString() {
        return new StringBuilder().append(id).append(" - ").append(name).toString();
    }



}
