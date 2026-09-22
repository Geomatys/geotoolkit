/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.wcts.xml.v030;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Response to a valid {@code GetCapabilities} operation request ({@code wcts:Capabilities}).
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapabilitiesType", propOrder = {"serviceTitle", "serviceAbstract", "contents"})
@XmlRootElement(name = "Capabilities")
public class CapabilitiesType {

    @XmlElement(name = "ServiceTitle")
    private String serviceTitle;
    @XmlElement(name = "ServiceAbstract")
    private String serviceAbstract;
    @XmlElement(name = "Contents")
    private ContentsType contents;
    @XmlAttribute
    private String updateSequence;
    @XmlAttribute(required = true)
    private String version = "0.3.0";

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public String getServiceAbstract() {
        return serviceAbstract;
    }

    public void setServiceAbstract(String serviceAbstract) {
        this.serviceAbstract = serviceAbstract;
    }

    public ContentsType getContents() {
        return contents;
    }

    public void setContents(ContentsType contents) {
        this.contents = contents;
    }

    public String getUpdateSequence() {
        return updateSequence;
    }

    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
