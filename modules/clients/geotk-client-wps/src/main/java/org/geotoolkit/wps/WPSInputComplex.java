/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
 * Complex Input for WPS
 * @author Quentin Boileau
 * @module pending
 */
public class WPSInputComplex extends AbstractWPSInput{
    
    private Object data;
    private String encoding;
    private String schema;
    private String mime;
    private Class clazz;
    
    /**
     * Minimal constructor with only identifier and href Input Complex parameters
     * @param identifier
     * @param href
     */
    public WPSInputComplex(final String identifier, final Object obj, final Class complexClass) {
        super(identifier);
        this.data = obj;
        this.encoding = null;
        this.schema = null;
        this.mime = null;
        this.clazz = complexClass;
    }
    
    /**
     * Constructor with all Input Complex parameters
     * @param identifier
     * @param href
     * @param encoding
     * @param schema
     * @param mime
     */
    public WPSInputComplex(final String identifier, final Object data, final Class complexClass, final String encoding, 
            final String schema, final String mime) {
        this(identifier, data, complexClass);
        this.data = data;
        this.encoding = encoding;
        this.schema = schema;
        this.mime = mime;
    }

    /**
     * Return Output encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Return Output data
     */
    public Object getData() {
        return data;
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

    /**
     * Return Output class
     */
    public Class getComplexClass() {
        return clazz;
    }
    
}
