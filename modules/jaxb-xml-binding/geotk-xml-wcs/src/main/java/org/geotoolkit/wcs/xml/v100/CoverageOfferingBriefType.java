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
package org.geotoolkit.wcs.xml.v100;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Brief description of one coverage avaialble from a WCS. 
 * 
 * WCS version 1.0.0
 * 
 * <p>Java class for CoverageOfferingBriefType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CoverageOfferingBriefType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wcs}AbstractDescriptionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs}lonLatEnvelope"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}keywords" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoverageOfferingBriefType")
@XmlSeeAlso({
    CoverageOfferingType.class
})
public class CoverageOfferingBriefType extends AbstractDescriptionType {
    
    private LonLatEnvelopeType lonLatEnvelope;
    
    private Keywords keywords;
    
    /**
     * An Empty constructor
     */
    public CoverageOfferingBriefType(){
    }
    
    public CoverageOfferingBriefType(List<MetadataLinkType> metadataLink, String name, String label, String description,
            LonLatEnvelopeType lonLatEnvelope, Keywords keywords) {
        super(metadataLink, name, label, description);
        this.lonLatEnvelope = lonLatEnvelope;
        this.keywords       = keywords;
    }
    
    public LonLatEnvelopeType getLonLatEnvelope() {
        return this.lonLatEnvelope;
    }
    
    public void setLonLatEnvelope(LonLatEnvelopeType lonLatEnvelope) {
        this.lonLatEnvelope = lonLatEnvelope;
    } 
    
    public Keywords getKeywords() {
        return this.keywords;
    } 
}
