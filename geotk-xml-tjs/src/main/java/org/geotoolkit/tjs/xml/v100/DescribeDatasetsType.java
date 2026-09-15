/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.tjs.xml.v100;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Request to describe the attribute datasets from which data is available from this server.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescribeDatasetsType", propOrder = {"frameworkURI", "datasetURI"})
@XmlRootElement(name = "DescribeDatasets", namespace = "http://www.opengis.net/tjs/1.0")
public class DescribeDatasetsType extends RequestBaseType {

    @XmlElement(name = "FrameworkURI")
    private String frameworkURI;

    @XmlElement(name = "DatasetURI")
    private String datasetURI;

    public String getFrameworkURI() {
        return frameworkURI;
    }

    public void setFrameworkURI(String frameworkURI) {
        this.frameworkURI = frameworkURI;
    }

    public String getDatasetURI() {
        return datasetURI;
    }

    public void setDatasetURI(String datasetURI) {
        this.datasetURI = datasetURI;
    }
}
