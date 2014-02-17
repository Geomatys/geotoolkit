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
 * Literal Input for WPS
 * @author Quentin Boileau
 * @module pending
 */
public class WPSInputLiteral extends AbstractWPSInput{
    
    private String data;
    private String dataType;
    private String uom;

    
    /**
     * Minimal constructor with only identifier and href Input Literal parameters
     * @param identifier
     * @param href
     */
    public WPSInputLiteral(final String identifier, final String obj) {
        super(identifier);
        this.data = obj;
        this.dataType = null;
        this.uom = null;
    }
    
    /**
     * Constructor with all Input Literal parameters
     * @param identifier
     * @param href
     * @param encoding
     * @param schema
     * @param mime
     */
    public WPSInputLiteral(final String identifier, final String data, final String dataType, 
            final String uom) {
        super(identifier);
        this.data = data;
        this.dataType = dataType;
        this.uom = uom;
    }

    /**
     * Return Output dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Return Output href
     */
    public String getData() {
        return data;
    }

    /**
     * Return Output uom
     */
    public String getUom() {
        return uom;
    }

    
}
