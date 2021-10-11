/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.wps.xml.v100;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v200.ExceptionReport;


/**
 * Indicator that the process has failed to execute successfully. The reason for failure is given in the exception report.
 *
 * <p>Java class for ProcessFailed complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ProcessFailed">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}ExceptionReport"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcessFailedType", propOrder = {
    "exceptionReport"
})
public class ProcessFailed {

    @XmlElement(name = "ExceptionReport", namespace = "http://www.opengis.net/ows/1.1", required = true)
    protected ExceptionReport exceptionReport;

    public ProcessFailed() {

    }

    public ProcessFailed( ExceptionReport exceptionReport) {
        this.exceptionReport = exceptionReport;
    }

    /**
     * Gets the value of the exceptionReport property.
     *
     * @return
     *     possible object is
     *     {@link ExceptionReport }
     *
     */
    public ExceptionReport getExceptionReport() {
        return exceptionReport;
    }

    /**
     * Sets the value of the exceptionReport property.
     *
     * @param value
     *     allowed object is
     *     {@link ExceptionReport }
     *
     */
    public void setExceptionReport(final ExceptionReport value) {
        this.exceptionReport = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (exceptionReport != null) {
            sb.append("exceptionReport:").append(exceptionReport).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify that this entry is identical to the specified object.
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ProcessFailed) {
            final ProcessFailed that = (ProcessFailed) object;
            return Objects.equals(this.exceptionReport, that.exceptionReport);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.exceptionReport);
        return hash;
    }
}
