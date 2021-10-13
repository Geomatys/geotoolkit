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

package org.geotoolkit.gmlcov.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.CoverageFunctionType;
import org.geotoolkit.gml.xml.v321.RangeSetType;
import org.geotoolkit.swe.xml.v200.DataRecordPropertyType;
import org.geotoolkit.wcs.xml.v200.CoverageDescriptionType;


/**
 * The gml:coverageFunction property is shifted "up" to this place in the inheritance hierarchy because it is included in both discrete and continuous coverages (i.e., all subtypes of AbstractCoverageType) and, hence, does not change syntax nor semantic in any way. It permits, however, coverages in the gmlcov:AbstractCoverage substitutionGroup to be used for either discrete and continuous coverages, in preparation for expected future elimination of this distinction.
 *
 * <p>Java class for AbstractCoverageType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AbstractCoverageType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractCoverageType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}coverageFunction" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gmlcov/1.0}rangeType"/>
 *         &lt;element ref="{http://www.opengis.net/gmlcov/1.0}metadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractCoverageType", propOrder = {
    "coverageFunction",
    "rangeType",
    "metadata"
})
@XmlSeeAlso({
    AbstractDiscreteCoverageType.class,
    AbstractContinuousCoverageType.class
})
public class AbstractCoverageType extends org.geotoolkit.gml.xml.v321.AbstractCoverageType {

    @XmlElement(namespace = "http://www.opengis.net/gml/3.2")
    private CoverageFunctionType coverageFunction;
    @XmlElement(required = true)
    private DataRecordPropertyType rangeType;
    private List<Metadata> metadata;

    public AbstractCoverageType() {

    }

    public AbstractCoverageType(CoverageDescriptionType covDesc, RangeSetType rangeSet) {
        super(covDesc, rangeSet, covDesc.getDomainSet());
        this.coverageFunction = covDesc.getCoverageFunction();
        this.rangeType        = covDesc.getRangeType();
        this.metadata         = covDesc.getMetadata();
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
     * Gets the value of the metadata property.
     *
     */
    public List<Metadata> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<Metadata>();
        }
        return this.metadata;
    }

}
