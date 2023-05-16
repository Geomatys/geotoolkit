/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2019, Geomatys
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

package org.geotoolkit.csw.xml.v300;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.FederatedSearchException;
import org.geotoolkit.ows.xml.v200.ExceptionReport;

/**
 * <p>Classe Java pour FederatedExceptionType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="FederatedExceptionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw/3.0}FederatedSearchResultBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}ExceptionReport" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FederatedExceptionType", propOrder = {
    "exceptionReport"
})
public class FederatedExceptionType extends FederatedSearchResultBaseType implements FederatedSearchException {

    @XmlElement(name = "ExceptionReport", namespace = "http://www.opengis.net/ows/2.0", required = true)
    protected List<ExceptionReport> exceptionReport;

    public FederatedExceptionType() {

    }

    public FederatedExceptionType(String catalogueURL, List<ExceptionReport> exceptionReport) {
        super(catalogueURL);
        this.exceptionReport = exceptionReport;
    }

    public FederatedExceptionType(String catalogueURL, ExceptionReport exceptionReport) {
        super(catalogueURL);
        if (exceptionReport != null) {
            this.exceptionReport = new ArrayList<>();
            this.exceptionReport.add(exceptionReport);
        }
    }

    /**
     * Gets the value of the exceptionReport property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the exceptionReport property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExceptionReport().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExceptionReport }
     *
     *
     */
    public List<ExceptionReport> getExceptionReport() {
        if (exceptionReport == null) {
            exceptionReport = new ArrayList<>();
        }
        return this.exceptionReport;
    }

    @Override
    public int getMatched() {
        return 0;
    }

    @Override
    public int getReturned() {
        return 0;
    }

}
