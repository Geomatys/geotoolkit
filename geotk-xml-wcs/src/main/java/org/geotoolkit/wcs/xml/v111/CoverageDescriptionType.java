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
package org.geotoolkit.wcs.xml.v111;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.ows.xml.v110.DescriptionType;
import org.geotoolkit.ows.xml.v110.KeywordsType;
import org.geotoolkit.ows.xml.v110.LanguageStringType;
import org.geotoolkit.ows.xml.v110.MetadataType;
import org.geotoolkit.wcs.xml.CoverageInfo;
import org.opengis.geometry.Envelope;


/**
 * Full description of one coverage available from a WCS server.
 * This description shall include sufficient information to allow all valid GetCoverage operation requests to be prepared by a WCS client.
 *
 * <p>Java class for CoverageDescriptionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CoverageDescriptionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}DescriptionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs}Identifier"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Domain" type="{http://www.opengis.net/wcs}CoverageDomainType"/>
 *         &lt;element name="Range" type="{http://www.opengis.net/wcs}RangeType"/>
 *         &lt;element name="SupportedCRS" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
 *         &lt;element name="SupportedFormat" type="{http://www.opengis.net/ows/1.1}MimeType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoverageDescriptionType", propOrder = {
    "identifier",
    "metadata",
    "domain",
    "range",
    "supportedCRS",
    "supportedFormat"
})
public class CoverageDescriptionType extends DescriptionType implements CoverageInfo {

    @XmlElement(name = "Identifier", required = true)
    private String identifier;
    @XmlElement(name = "Metadata", namespace = "http://www.opengis.net/ows/1.1")
    private List<MetadataType> metadata = new ArrayList<>();
    @XmlElement(name = "Domain", required = true)
    private CoverageDomainType domain;
    @XmlElement(name = "Range", required = true)
    private RangeType range;
    @XmlElement(name = "SupportedCRS", required = true)
    @XmlSchemaType(name = "anyURI")
    private List<String> supportedCRS = new ArrayList<String>();
    @XmlElement(name = "SupportedFormat", required = true)
    private List<String> supportedFormat = new ArrayList<String>();


    /**
     * Empty constructor used by JAXB
     */
    CoverageDescriptionType() {
        super();
    }

    /**
     * build a light coverage description.
     */
    public CoverageDescriptionType(final String title,  final String _abstract,
            final List<String> keywords, final String identifier, final CoverageDomainType domain,
            final RangeType range, final List<String> supportedCRS, final List<String> supportedFormat) {
        super(title, _abstract, keywords);
        this.domain          = domain;
        this.identifier      = identifier;
        this.range           = range;
        this.supportedCRS    = supportedCRS;
        this.supportedFormat = supportedFormat;
    }

    /**
     * build a light coverage description.
     */
    public CoverageDescriptionType(final List<LanguageStringType> title,  final List<LanguageStringType> _abstract,
            final List<KeywordsType> keywords, final String identifier, final CoverageDomainType domain,
            final RangeType range, final List<String> supportedCRS, final List<String> supportedFormat) {
        super(title, _abstract, keywords);
        this.domain          = domain;
        this.identifier      = identifier;
        this.range           = range;
        this.supportedCRS    = supportedCRS;
        this.supportedFormat = supportedFormat;
    }

    /**
     * build a full coverage description.
     */
    public CoverageDescriptionType(final List<LanguageStringType> title,  final List<LanguageStringType> _abstract,
            final List<KeywordsType> keywords, final String identifier, final CoverageDomainType domain, final List<MetadataType> metadata,
            final RangeType range, final List<String> supportedCRS, final List<String> supportedFormat) {
        super(title, _abstract, keywords);
        this.domain          = domain;
        this.identifier      = identifier;
        this.metadata        = metadata;
        this.range           = range;
        this.supportedCRS    = supportedCRS;
        this.supportedFormat = supportedFormat;
    }


    /**
     * Unambiguous identifier of this coverage, unique for this WCS server.
     */
    @Override
    public CodeType getIdentifier() {
        return new CodeType(identifier);
    }

    /**
     * Optional unordered list of more metadata elements about this coverage. A list of metadata elements for CoverageDescriptions could be specified in a WCS Application Profile. Gets the value of the metadata property.
     */
    @Override
    public List<MetadataType> getMetadata() {
        return Collections.unmodifiableList(metadata);
    }

    @Override
    public void setMetadata(final String href) {
        if (href != null) {
            this.metadata = new ArrayList<>();
            this.metadata.add(new MetadataType(href));
        }
    }

    /**
     * Gets the value of the domain property.
     */
    public CoverageDomainType getDomain() {
        return domain;
    }

    /**
     * Gets the value of the range property.
     */
    public RangeType getRange() {
        return range;
    }

    /**
     * Gets the value of the supportedCRS property.
     */
    public List<String> getSupportedCRS() {
        return Collections.unmodifiableList(supportedCRS);
    }

    /**
     * Gets the value of the supportedFormat property.
     */
    public List<String> getSupportedFormat() {
        return Collections.unmodifiableList(supportedFormat);
    }

    @Override
    public Envelope getLonLatEnvelope() {
        throw new UnsupportedOperationException("TODO.");
    }

    @Override
    public List<?> getRest() {
        throw new UnsupportedOperationException("TODO.");
    }

}
