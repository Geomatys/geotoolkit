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
 * WPSOutput regroup all parameter about an output from a process
 * @author Quentin Boileau
 * @module pending
 */
public class WPSOutput {
    
    
    private String identifier;
    private String encoding;
    private String schema;
    private String mime;
    private String uom;
    private boolean asReference;

    
    /**
     * Constructor set all output attributs to null exept process identifier
     * @param identifier 
     */
    public WPSOutput(final String identifier){
        this.identifier = identifier;
        this.encoding = null;
        this.schema = null;
        this.mime = null;
        this.uom = null;
        this.asReference = false;
    }
  
    /**
     * 
     * @param identifier
     * @param encoding
     * @param schema
     * @param mime
     * @param uom 
     */
    public WPSOutput(final String identifier, final String encoding, final String schema, 
            final String mime, final String uom, final boolean asReference) {
        this.identifier = identifier;
        this.encoding = encoding;
        this.schema = schema;
        this.mime = mime;
        this.uom = uom;
        this.asReference = asReference;
    }
    
    /**
     * Return Output encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Return Output identifier
     */
    public String getIdentifier() {
        return identifier;
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
     * Return Output uom
     */
    public String getUom() {
        return uom;
    }
    
     /**
     * Return Output asReference booelan
     */
    public boolean getAsReference() {
        return asReference;
    }

    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder();
        buff.append("WPSOutput{identifier=");
        buff.append(identifier);
        buff.append(", encoding=");
        buff.append(encoding);
        buff.append(", schema=");
        buff.append(schema);
        buff.append(", mime=");
        buff.append(mime);
        buff.append(", uom=");
        buff.append(uom);
        buff.append(", asReference=");
        buff.append(asReference);
        buff.append("}");
        return buff.toString();
    }
    
}
