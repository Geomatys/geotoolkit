/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.wcs.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gmlcov.xml.v100.AbstractCoverageType;


/**
 * <p>Java class for OfferedCoverageType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OfferedCoverageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gmlcov/1.0}AbstractCoverage"/>
 *         &lt;element ref="{http://www.opengis.net/wcs/2.0}ServiceParameters"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OfferedCoverageType", propOrder = {
    "abstractCoverage",
    "serviceParameters"
})
public class OfferedCoverageType {

    @XmlElementRef(name = "AbstractCoverage", namespace = "http://www.opengis.net/gmlcov/1.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractCoverageType> abstractCoverage;
    @XmlElement(name = "ServiceParameters", required = true)
    private ServiceParametersType serviceParameters;

    /**
     * Gets the value of the abstractCoverage property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *
     */
    public JAXBElement<? extends AbstractCoverageType> getAbstractCoverage() {
        return abstractCoverage;
    }

    /**
     * Sets the value of the abstractCoverage property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDiscreteCoverageType }{@code >}
     *
     */
    public void setAbstractCoverage(JAXBElement<? extends AbstractCoverageType> value) {
        this.abstractCoverage = ((JAXBElement<? extends AbstractCoverageType> ) value);
    }

    /**
     * Gets the value of the serviceParameters property.
     *
     * @return
     *     possible object is
     *     {@link ServiceParametersType }
     *
     */
    public ServiceParametersType getServiceParameters() {
        return serviceParameters;
    }

    /**
     * Sets the value of the serviceParameters property.
     *
     * @param value
     *     allowed object is
     *     {@link ServiceParametersType }
     *
     */
    public void setServiceParameters(ServiceParametersType value) {
        this.serviceParameters = value;
    }

}
