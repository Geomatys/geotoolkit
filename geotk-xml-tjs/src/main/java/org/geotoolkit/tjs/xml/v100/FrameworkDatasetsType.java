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

import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Spatial framework for which attribute datasets are housed on this server.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FrameworkDatasetsType", propOrder = {
    "frameworkURI", "organization", "title", "abstractText", "referenceDate", "version",
    "documentation", "frameworkKey", "boundingCoordinates", "describeDatasetsRequest", "dataset"})
public class FrameworkDatasetsType {

    @XmlElement(name = "FrameworkURI", required = true)
    private String frameworkURI;

    @XmlElement(name = "Organization", required = true)
    private String organization;

    @XmlElement(name = "Title", required = true)
    private String title;

    @XmlElement(name = "Abstract", required = true)
    private String abstractText;

    @XmlElement(name = "ReferenceDate", required = true)
    private ReferenceDateType referenceDate;

    @XmlElement(name = "Version", required = true)
    private String version;

    @XmlElement(name = "Documentation")
    private String documentation;

    @XmlElement(name = "FrameworkKey", required = true)
    private FrameworkKeyType frameworkKey;

    @XmlElement(name = "BoundingCoordinates", required = true)
    private BoundingCoordinatesType boundingCoordinates;

    @XmlElement(name = "DescribeDatasetsRequest", required = true)
    private RequestReferenceType describeDatasetsRequest;

    @XmlElement(name = "Dataset", required = true)
    private List<DatasetDescriptionType> dataset;

    public String getFrameworkURI() {
        return frameworkURI;
    }

    public void setFrameworkURI(String frameworkURI) {
        this.frameworkURI = frameworkURI;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public ReferenceDateType getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(ReferenceDateType referenceDate) {
        this.referenceDate = referenceDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public FrameworkKeyType getFrameworkKey() {
        return frameworkKey;
    }

    public void setFrameworkKey(FrameworkKeyType frameworkKey) {
        this.frameworkKey = frameworkKey;
    }

    public BoundingCoordinatesType getBoundingCoordinates() {
        return boundingCoordinates;
    }

    public void setBoundingCoordinates(BoundingCoordinatesType boundingCoordinates) {
        this.boundingCoordinates = boundingCoordinates;
    }

    public RequestReferenceType getDescribeDatasetsRequest() {
        return describeDatasetsRequest;
    }

    public void setDescribeDatasetsRequest(RequestReferenceType describeDatasetsRequest) {
        this.describeDatasetsRequest = describeDatasetsRequest;
    }

    public List<DatasetDescriptionType> getDataset() {
        return dataset;
    }

    public void setDataset(List<DatasetDescriptionType> dataset) {
        this.dataset = dataset;
    }
}
