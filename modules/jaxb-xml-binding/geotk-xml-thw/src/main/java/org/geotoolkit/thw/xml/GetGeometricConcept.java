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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getAllGeometricTerm")
@XmlRootElement(name = "GetGeometricConcept", namespace = "http://ws.geotk.org/")
public class GetGeometricConcept {

    @XmlElement(name="uri_concept")
    private String uriConcept;

    private String outputFormat;

    public GetGeometricConcept() {

    }

    public GetGeometricConcept(final String uriConcept, final String outputFormat) {
        this.outputFormat = outputFormat;
        this.uriConcept   = uriConcept;
    }

    /**
     * @return the uri_concept
     */
    public String getUriConcept() {
        return uriConcept;
    }

    /**
     * @param uri_concept the uri_concept to set
     */
    public void setUriConcept(String uriConcept) {
        this.uriConcept = uriConcept;
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
