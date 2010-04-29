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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.opengis.geometry.Envelope;

/**
 * Metadatas of GPX files.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MetaData {

    private final String name;
    private final String description;
    private final Person person;
    private final CopyRight copyRight;
    private final List<URI> links;
    private final Date time;
    private final String keywords;
    private final Envelope bounds;

    public MetaData(String name, String description, Person person, CopyRight copyRight,
            List<URI> links, Date time, String keywords, Envelope bounds) {
        this.name = name;
        this.description = description;
        this.person = person;
        this.copyRight = copyRight;
        this.time = time;
        this.keywords = keywords;
        this.bounds = bounds;

        if(links != null && !links.isEmpty()){
            this.links = links;
        }else{
            this.links = Collections.emptyList();
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Person getPerson() {
        return person;
    }

    public CopyRight getCopyRight() {
        return copyRight;
    }

    public List<URI> getLinks() {
        return links;
    }

    public Date getTime() {
        return time;
    }

    public String getKeywords() {
        return keywords;
    }

    public Envelope getBounds() {
        return bounds;
    }

}
