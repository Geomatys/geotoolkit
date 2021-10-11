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
import org.geotoolkit.wcs.xml.CoverageInfo;


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
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoverageOfferingBriefType")
@XmlSeeAlso({
    CoverageOfferingType.class
})
public class CoverageOfferingBriefType extends AbstractDescriptionType implements CoverageInfo{

    private LonLatEnvelopeType lonLatEnvelope;

    private Keywords keywords;

    /**
     * An Empty constructor
     */
    public CoverageOfferingBriefType(){
    }

    public CoverageOfferingBriefType(final String name, final String label, final String description,
            final LonLatEnvelopeType lonLatEnvelope) {
        super(null, name, label, description);
        this.lonLatEnvelope = lonLatEnvelope;
    }

    public CoverageOfferingBriefType(final List<MetadataLinkType> metadataLink, final String name, final String label, final String description,
            final LonLatEnvelopeType lonLatEnvelope, final Keywords keywords) {
        super(metadataLink, name, label, description);
        this.lonLatEnvelope = lonLatEnvelope;
        this.keywords       = keywords;
    }

    public CoverageOfferingBriefType(final List<MetadataLinkType> metadataLink, final String name, final String label, final String description,
            final LonLatEnvelopeType lonLatEnvelope, final List<String> keywords) {
        super(metadataLink, name, label, description);
        this.lonLatEnvelope = lonLatEnvelope;
        if (keywords != null && !keywords.isEmpty()) {
            this.keywords = new Keywords(keywords);
        }
    }

    @Override
    public LonLatEnvelopeType getLonLatEnvelope() {
        return this.lonLatEnvelope;
    }

    public void setLonLatEnvelope(final LonLatEnvelopeType lonLatEnvelope) {
        this.lonLatEnvelope = lonLatEnvelope;
    }

    public Keywords getKeywords() {
        return this.keywords;
    }

    public void setKeywords(final Keywords keywords) {
        this.keywords = keywords;
    }

    @Override
    public void setKeywordValues(final List<String> keywords) {
        this.keywords = new Keywords(keywords);
    }

}
