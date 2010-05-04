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
 * GPX copyright model
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CopyRight {

    private final String author;
    private final Integer year;
    private final URI license;

    public CopyRight(String author, Integer year, URI license){
        this.author = author;
        this.year = year;
        this.license = license;
    }

    public String getAuthor() {
        return author;
    }

    public Integer getYear() {
        return year;
    }

    public URI getLicense() {
        return license;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CopyRight(");
        sb.append(author).append(',').append(year).append(',').append(license);
        sb.append(')');
        return sb.toString();
    }

}
