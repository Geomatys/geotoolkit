/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.feature.xml;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Rohan FERRE (Geomatys)
 */
@XmlRootElement(name = "Collections")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class collections implements FeatureResponse {

    @XmlElement(name = "link")
    private List<Link> links;
    @XmlElement(name = "Collection")
    private List<Collection> collections;

    public collections() {
        links = new ArrayList<>();
        collections = new ArrayList<>();
    }

    public collections(List<Link> links, List<Collection> collections) {
        this.links = links;
        this.collections = collections;
    }

    /**
     *
     * @return the array list of links
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     *
     * @param links the array list of link to set
     */
    public void setLinks(List<Link> links) {
        this.links = links;
    }

    /**
     *
     * @return the array list of collections
     */
    public List<Collection> getCollections() {
        return collections;
    }

    /**
     *
     * @param collections the array list of collection to set
     */
    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

}
