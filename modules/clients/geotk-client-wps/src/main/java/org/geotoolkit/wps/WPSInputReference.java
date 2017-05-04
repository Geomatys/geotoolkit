/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wps;

/**
 * Reference Input for WPS
 * @author Quentin Boileau
 * @module
 */
public class WPSInputReference extends AbstractWPSInput{

    private String href;
    private String encoding;
    private String schema;
    private String mime;
    private String method;


    /**
     * Minimal constructor with only identifier and href Input Reference parameters
     * @param identifier
     * @param href
     */
    public WPSInputReference(final String identifier, final String href) {
        super(identifier);
        this.href = href;
        this.encoding = null;
        this.schema = null;
        this.mime = null;
        this.method = null;
    }

    /**
     * Constructor with all Input Reference parameters
     * @param identifier
     * @param href
     * @param encoding
     * @param schema
     * @param mime
     * @param method
     */
    public WPSInputReference(final String identifier, final String href, final String encoding,
            final String schema, final String mime, final String method) {
        super(identifier);
        this.href = href;
        this.encoding = encoding;
        this.schema = schema;
        this.mime = mime;
        this.method = method;
    }

    /**
     * Return Output encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Return Output href
     */
    public String getHref() {
        return href;
    }

    /**
     * Return Output method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Return Output mime
     */
    public String getMime() {
        return mime;
    }

    /**
     * Return Output schema
     */
    public String getSchema() {
        return schema;
    }

}
