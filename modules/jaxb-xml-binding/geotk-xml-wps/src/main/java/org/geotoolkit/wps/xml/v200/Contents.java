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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wps.xml.ProcessOffering;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProcessSummary" type="{http://www.opengis.net/wps/2.0}ProcessSummaryType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "processSummary"
})
@XmlRootElement(name = "Contents")
public class Contents implements org.geotoolkit.wps.xml.ProcessOfferings {

    @XmlElement(name = "ProcessSummary", required = true)
    protected List<ProcessSummaryType> processSummary;


    public Contents() {

    }

    public Contents(List<ProcessSummaryType> processSummary) {
        this.processSummary = processSummary;
    }

    /**
     * Gets the value of the processSummary property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processSummary property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessSummary().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProcessSummaryType }
     *
     *
     */
    public List<ProcessSummaryType> getProcessSummary() {
        if (processSummary == null) {
            processSummary = new ArrayList<>();
        }
        return this.processSummary;
    }

    @Override
    public List<? extends ProcessOffering> getProcesses() {
        return getProcessSummary();
    }

}
