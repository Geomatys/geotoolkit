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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v200.ContentsBaseType;
import org.geotoolkit.wcs.xml.Content;
import org.geotoolkit.wcs.xml.CoverageInfo;


/**
 * <p>Java class for ContentsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContentsType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/2.0}ContentsBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs/2.0}CoverageSummary" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wcs/2.0}Extension" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentsType", propOrder = {
    "coverageSummary",
    "extension"
})
public class ContentsType extends ContentsBaseType implements Content {

    @XmlElement(name = "CoverageSummary")
    private List<CoverageSummaryType> coverageSummary;
    @XmlElement(name = "Extension")
    private ExtensionType extension;

    /**
     * Gets the value of the coverageSummary property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link CoverageSummaryType }
     * 
     * 
     */
    public List<CoverageSummaryType> getCoverageSummary() {
        if (coverageSummary == null) {
            coverageSummary = new ArrayList<CoverageSummaryType>();
        }
        return this.coverageSummary;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link ExtensionType }
     *     
     */
    public ExtensionType getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtensionType }
     *     
     */
    public void setExtension(ExtensionType value) {
        this.extension = value;
    }

    @Override
    public List<? extends CoverageInfo> getCoverageInfos() {
        return coverageSummary;
    }
}
