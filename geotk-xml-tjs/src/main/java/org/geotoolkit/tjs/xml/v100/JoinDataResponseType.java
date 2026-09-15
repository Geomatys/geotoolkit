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
 * Response to a TJS JoinData request.
 *
 * @author Quentin Bialota (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JoinDataResponseType", propOrder = {"status", "dataInputs", "joinedOutputs"})
@XmlRootElement(name = "JoinDataResponse", namespace = "http://www.opengis.net/tjs/1.0")
public class JoinDataResponseType extends TJSResponseType {

    @XmlElement(name = "Status", required = true)
    private JoinDataStatusType status;

    @XmlElement(name = "DataInputs", required = true)
    private DataInputsType dataInputs;

    @XmlElement(name = "JoinedOutputs", required = true)
    private JoinedOutputsType joinedOutputs;

    public JoinDataStatusType getStatus() {
        return status;
    }

    public void setStatus(JoinDataStatusType status) {
        this.status = status;
    }

    public DataInputsType getDataInputs() {
        return dataInputs;
    }

    public void setDataInputs(DataInputsType dataInputs) {
        this.dataInputs = dataInputs;
    }

    public JoinedOutputsType getJoinedOutputs() {
        return joinedOutputs;
    }

    public void setJoinedOutputs(JoinedOutputsType joinedOutputs) {
        this.joinedOutputs = joinedOutputs;
    }
}
