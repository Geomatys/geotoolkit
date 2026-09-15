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
import jakarta.xml.bind.annotation.XmlType;

/**
 * Unordered list entry describing one output produced (or to be produced) by the JoinData operation.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JoinDataOutputType", propOrder = {"mechanism", "resource", "exceptionReport"})
public class JoinDataOutputType {

    @XmlElement(name = "Mechanism", required = true)
    private MechanismType mechanism;

    @XmlElement(name = "Resource")
    private ResourceType resource;

    @XmlElement(name = "ExceptionReport")
    private JoinDataExceptionReportType exceptionReport;

    public MechanismType getMechanism() {
        return mechanism;
    }

    public void setMechanism(MechanismType mechanism) {
        this.mechanism = mechanism;
    }

    public ResourceType getResource() {
        return resource;
    }

    public void setResource(ResourceType resource) {
        this.resource = resource;
    }

    public JoinDataExceptionReportType getExceptionReport() {
        return exceptionReport;
    }

    public void setExceptionReport(JoinDataExceptionReportType exceptionReport) {
        this.exceptionReport = exceptionReport;
    }
}
