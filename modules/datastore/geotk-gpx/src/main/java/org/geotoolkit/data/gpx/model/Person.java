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

package org.geotoolkit.data.gpx.model;

import java.net.URI;

/**
 * GPX person model
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Person {

    private final String name;
    private final String email;
    private final URI link;

    public Person(String name, String email, URI link){
        this.name = name;
        this.email = email;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public URI getLink() {
        return link;
    }

}
