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
package org.geotoolkit.thw.xml;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Guilhem Legal
 * @since 2.4
 */
@XmlRootElement(name = "GetLinkedCsw", namespace = "http://ws.geotk.org/")
public class GetLinkedCsw {
    
    private String outputFormat;
    
    public GetLinkedCsw() {
        
    }
    
    public GetLinkedCsw(final String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * @return the outputFormat
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @param outputFormat the outputFormat to set
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
}
