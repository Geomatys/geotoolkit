/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.thw.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlRootElement(name = "Thesaurus")
public class XmlThesaurus {

    /**
     * The URI of the thesaurus. Mandatory
     */
    public String uri;
    /**
     * The name of the thesaurus. Mandatory
     */
    public String name;
    /**
     * A text string providing the version. Can be date or number (or both) Mandatory
     */
    public String version;
    /**
     *  A description of the thesaurus
     */
    public String description;

    public XmlThesaurus() {
    }

    public XmlThesaurus(final String uri, final String name, final String version, final String description) {
        this.uri = uri;
        this.name = name;
        this.version = version;
        this.description = description;
    }

    public XmlThesaurus(final Thesaurus t) {
        this.uri = t.getURI();
        this.name = t.getName();
        this.version = t.getVersion();
        this.description = t.getDescription();
    }
}
