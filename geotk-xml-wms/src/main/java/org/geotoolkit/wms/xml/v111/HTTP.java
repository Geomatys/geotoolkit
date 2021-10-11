/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wms.xml.v111;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractHTTP;


/**
 * <p>Java class for anonymous complex type.
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "get",
    "post"
})
@XmlRootElement(name = "HTTP")
public class HTTP implements AbstractHTTP {

    @XmlElement(name = "Get", required = true)
    private Get get;
    @XmlElement(name = "Post")
    private Post post;

    /**
     * An empty constructor used by JAXB.
     */
     HTTP() {
     }

    public HTTP(final HTTP http) {
        if (http != null)  {
            this.get  = new Get(http.get);
            this.post = new Post(http.post);
        }
    }
    /**
     * Build a new HTTP object.
     */
    public HTTP(final Get get, final Post post) {
        this.get  = get;
        this.post = post;
    }


    /**
     * Gets the value of the get property.
     *
     */
    public Get getGet() {
        return get;
    }

    /**
     * Gets the value of the post property.
     *
     */
    public Post getPost() {
        return post;
    }
}
