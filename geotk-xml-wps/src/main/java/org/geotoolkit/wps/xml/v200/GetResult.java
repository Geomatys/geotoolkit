/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.HashMap;
import java.util.Map;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wps/2.0}RequestBase">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wps/2.0}JobID"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "", propOrder = {
    "jobID"
})
@XmlRootElement(name = "GetResult")
public class GetResult extends RequestBase {

    @XmlElement(name = "JobID", required = true)
    protected String jobID;

    public GetResult() {

    }

    public GetResult(final String service, final String version, final String jobId) {
        super(service, version);
        this.jobID = jobId;
    }

    /**
     * Gets the value of the jobID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getJobID() {
        return jobID;
    }

    /**
     * Sets the value of the jobID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setJobID(String value) {
        this.jobID = value;
    }

    @Override
    public Map<String, String> toKVP() throws UnsupportedOperationException {
        final Map<String, String> kvp = new HashMap<>();
        kvp.put("SERVICE",getService());
        kvp.put("REQUEST","GetResult");
        kvp.put("VERSION",getVersion().toString());
        kvp.put("JOBID",jobID);
        return kvp;
    }

}
