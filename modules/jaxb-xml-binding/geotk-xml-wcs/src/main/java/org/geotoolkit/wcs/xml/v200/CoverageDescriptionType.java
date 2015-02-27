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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.gml.xml.v321.AbstractFeatureType;
import org.geotoolkit.gml.xml.v321.BoundingShapeType;
import org.geotoolkit.gml.xml.v321.EnvelopeType;
import org.geotoolkit.gml.xml.v321.CoverageFunctionType;
import org.geotoolkit.gml.xml.v321.DomainSetType;
import org.geotoolkit.gmlcov.xml.v100.Metadata;
import org.geotoolkit.swe.xml.v200.DataRecordPropertyType;
import org.geotoolkit.wcs.xml.CoverageInfo;
import org.opengis.geometry.Envelope;

/**
 * <p>Java class for CoverageDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CoverageDescriptionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs/2.0}CoverageId"/>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}coverageFunction" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gmlcov/1.0}metadata" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}domainSet"/>
 *         &lt;element ref="{http://www.opengis.net/gmlcov/1.0}rangeType"/>
 *         &lt;element ref="{http://www.opengis.net/wcs/2.0}ServiceParameters"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoverageDescriptionType", propOrder = {
    "coverageId",
    "coverageFunction",
    "metadata",
    "domainSet",
    "rangeType",
    "serviceParameters"
})
public class CoverageDescriptionType extends AbstractFeatureType implements CoverageInfo {

    @XmlElement(name = "CoverageId", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    private String coverageId;
    @XmlElement(namespace = "http://www.opengis.net/gml/3.2")
    private CoverageFunctionType coverageFunction;
    @XmlElement(namespace = "http://www.opengis.net/gmlcov/1.0")
    private List<Metadata> metadata;
    @XmlElementRef(name = "domainSet", namespace = "http://www.opengis.net/gml/3.2", type = JAXBElement.class)
    private JAXBElement<DomainSetType> domainSet;
    @XmlElement(namespace = "http://www.opengis.net/gmlcov/1.0", required = true)
    private DataRecordPropertyType rangeType;
    @XmlElement(name = "ServiceParameters", required = true)
    private ServiceParametersType serviceParameters;

    public CoverageDescriptionType() {
        
    }
    
    public CoverageDescriptionType(final String coverageId, final EnvelopeType env, final DomainSetType domainSet, 
            final DataRecordPropertyType rangeType, final ServiceParametersType serviceParameters) {
        this.coverageId = coverageId;
        if (env != null) {
            setBoundedBy(new BoundingShapeType(env));
        }
        final org.geotoolkit.gml.xml.v321.ObjectFactory factory = new org.geotoolkit.gml.xml.v321.ObjectFactory();
        if (domainSet != null) {
            this.domainSet = factory.createGridDomain(domainSet);
        }
        this.rangeType = rangeType;
        this.serviceParameters = serviceParameters;
    }
    
    /**
     * Gets the value of the coverageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCoverageId() {
        return coverageId;
    }

    /**
     * Sets the value of the coverageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCoverageId(String value) {
        this.coverageId = value;
    }

    /**
     * Gets the value of the coverageFunction property.
     * 
     * @return
     *     possible object is
     *     {@link CoverageFunctionType }
     *     
     */
    public CoverageFunctionType getCoverageFunction() {
        return coverageFunction;
    }

    /**
     * Sets the value of the coverageFunction property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoverageFunctionType }
     *     
     */
    public void setCoverageFunction(CoverageFunctionType value) {
        this.coverageFunction = value;
    }

    /**
     * Gets the value of the metadata property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link Metadata }
     * 
     * 
     */
    public List<Metadata> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
        return this.metadata;
    }

    @Override
    public void setMetadata(final String href) {
        if (href != null) {
            this.metadata = new ArrayList<>();
            this.metadata.add(new Metadata(href));
        }
    }
    
    /**
     * Gets the value of the domainSet property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DomainSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DomainSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DomainSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DomainSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DomainSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DomainSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DomainSetType }{@code >}
     *     
     */
    public JAXBElement<DomainSetType> getDomainSet() {
        return domainSet;
    }

    /**
     * Sets the value of the domainSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DomainSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DomainSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DomainSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DomainSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DomainSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DomainSetType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DomainSetType }{@code >}
     *     
     */
    public void setDomainSet(JAXBElement<DomainSetType> value) {
        this.domainSet = ((JAXBElement<DomainSetType> ) value);
    }

    /**
     * Gets the value of the rangeType property.
     * 
     * @return
     *     possible object is
     *     {@link DataRecordPropertyType }
     *     
     */
    public DataRecordPropertyType getRangeType() {
        return rangeType;
    }

    /**
     * Sets the value of the rangeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataRecordPropertyType }
     *     
     */
    public void setRangeType(DataRecordPropertyType value) {
        this.rangeType = value;
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

    @Override
    public Envelope getLonLatEnvelope() {
        return null;
    }

    @Override
    public List<?> getRest() {
        return new ArrayList<>();
    }

    @Override
    public void setTitle(String title) {
        //do nothing
    }

    @Override
    public void setAbstract(String abs) {
        //do nothing
    }

    @Override
    public void setKeywordValues(List<String> values) {
        //do nothing
    }

}
