/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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

import java.io.Serializable;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.geotoolkit.data.ServiceInfo;


/**
 * Implementation of DefaultServiceInfo as a java bean.
 * 
 * @author Jody Garnett (Refractions Research)
 */
public class DefaultServiceInfo implements ServiceInfo, Serializable {

    private static final long serialVersionUID = 7975308744804800859L;
    protected String description;
    protected Set<String> keywords;
    protected URI publisher;
    protected URI schema;
    protected String title;
    private URI source;

    public DefaultServiceInfo() {
    }

    public DefaultServiceInfo(final ServiceInfo copy) {
        this.description = copy.getDescription();
        this.keywords = new HashSet<String>();
        if (copy.getKeywords() != null) {
            this.keywords.addAll(copy.getKeywords());
        }
        this.publisher = copy.getPublisher();
        this.schema = copy.getSchema();
        this.title = copy.getTitle();
        this.source = copy.getSource();
    }

    /**
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the keywords
     */
    @Override
    public Set<String> getKeywords() {
        return keywords;
    }

    /**
     * @param keywords the keywords to set
     */
    public void setKeywords(final Set<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * @return the publisher
     */
    @Override
    public URI getPublisher() {
        return publisher;
    }

    /**
     * @param publisher the publisher to set
     */
    public void setPublisher(final URI publisher) {
        this.publisher = publisher;
    }

    /**
     * @return the schema
     */
    @Override
    public URI getSchema() {
        return schema;
    }

    /**
     * @param schema the schema to set
     */
    public void setSchema(final URI schema) {
        this.schema = schema;
    }

    /**
     * @return the title
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * @return the source
     */
    @Override
    public URI getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(final URI source) {
        this.source = source;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("ServiceInfo ");
        if (source != null) {
            buf.append(source);
        }
        if (this.title != null) {
            buf.append("\n title=");
            buf.append(title);
        }
        if (this.publisher != null) {
            buf.append("\n publisher=");
            buf.append(publisher);
        }
        if (this.publisher != null) {
            buf.append("\n schema=");
            buf.append(schema);
        }
        if (keywords != null) {
            buf.append("\n keywords=");
            buf.append(keywords);
        }
        if (description != null) {
            buf.append("\n description=");
            buf.append(description);
        }
        return buf.toString();
    }
}
