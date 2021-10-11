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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.ows.xml.v110.DescriptionType;
import org.geotoolkit.ows.xml.v110.KeywordsType;
import org.geotoolkit.ows.xml.v110.LanguageStringType;
import org.geotoolkit.ows.xml.v110.MetadataType;
import org.geotoolkit.ows.xml.v110.WGS84BoundingBoxType;
import org.geotoolkit.wcs.xml.CoverageInfo;
import org.opengis.geometry.Envelope;


/**
 * Brief metadata describing one or more coverages available from this WCS server.
 *
 * <p>Java class for CoverageSummaryType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CoverageSummaryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}DescriptionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}WGS84BoundingBox" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="SupportedCRS" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="SupportedFormat" type="{http://www.opengis.net/ows/1.1}MimeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;sequence>
 *             &lt;element ref="{http://www.opengis.net/wcs}CoverageSummary" maxOccurs="unbounded"/>
 *             &lt;element ref="{http://www.opengis.net/wcs}Identifier" minOccurs="0"/>
 *           &lt;/sequence>
 *           &lt;element ref="{http://www.opengis.net/wcs}Identifier"/>
 *         &lt;/choice>
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
@XmlType(name = "CoverageSummaryType", propOrder = {
    "rest"
})
public class CoverageSummaryType extends DescriptionType implements CoverageInfo{

    @XmlElementRefs({
        @XmlElementRef(name = "CoverageSummary", namespace = "http://www.opengis.net/wcs/1.1.1", type = JAXBElement.class),
        @XmlElementRef(name = "SupportedFormat", namespace = "http://www.opengis.net/wcs/1.1.1", type = JAXBElement.class),
        @XmlElementRef(name = "Identifier", namespace = "http://www.opengis.net/wcs/1.1.1", type = JAXBElement.class),
        @XmlElementRef(name = "SupportedCRS", namespace = "http://www.opengis.net/wcs/1.1.1", type = JAXBElement.class),
        @XmlElementRef(name = "WGS84BoundingBox", namespace = "http://www.opengis.net/ows/1.1", type = JAXBElement.class),
        @XmlElementRef(name = "Metadata", namespace = "http://www.opengis.net/ows/1.1", type = JAXBElement.class)
    })
    private List<JAXBElement<?>> rest  = new ArrayList<JAXBElement<?>>();

    /**
     * An empty constructor used by JAXB.
     */
    public CoverageSummaryType() {
    }

    /**
     * An light constructor.
     */
    public CoverageSummaryType(final String identifier, final String title, final String _abstract, final WGS84BoundingBoxType bbox) {
        super(title, _abstract, null);
        if (identifier != null) {
            setIdentifier(identifier);
        }
        if (bbox != null) {
            setWGS84BoundingBox(bbox);
        }
    }

    /**
     * An light constructor.
     */
    public CoverageSummaryType(final List<LanguageStringType> title, final List<LanguageStringType> _abstract) {
        super(title, _abstract, null);
    }


    /**
     * An full constructor.
     */
    public CoverageSummaryType(final List<LanguageStringType> title,  final List<LanguageStringType> _abstract,
            final List<KeywordsType> keywords, final List<JAXBElement<?>> rest) {
        super(title, _abstract, keywords);
        this.rest = rest;
    }

    @Override
    public CodeType getIdentifier() {
        for (JAXBElement<?> jb : rest) {
            if ("Identifier".equals(jb.getName().getLocalPart())) {
                return new CodeType((String) jb.getValue());
            }
        }
        return null;
    }

    public final void setIdentifier(final String metadata) {
        // first we remove the old one
        for (int i = 0; i < rest.size(); i++) {
            JAXBElement<?> jb  = rest.get(i);
            if ("Identifier".equals(jb.getName().getLocalPart())) {
                rest.remove(i);
                break;
            }
        }
        if (metadata != null) {
            ObjectFactory factory = new ObjectFactory();
            this.rest.add(factory.createIdentifier(metadata));
        }
    }

    @Override
    public List<MetadataType> getMetadata() {
        for (JAXBElement<?> jb : rest) {
            if ("Metadata".equals(jb.getName().getLocalPart())) {
                return Arrays.asList((MetadataType) jb.getValue());
            }
        }
        return null;
    }

    @Override
    public void setMetadata(final String href) {
        if (href != null) {
            setMetadata(new MetadataType(href));
        }
    }

    public void setMetadata(final MetadataType metadata) {
        // first we remove the old one
        for (int i = 0; i < rest.size(); i++) {
            JAXBElement<?> jb  = rest.get(i);
            if ("Metadata".equals(jb.getName().getLocalPart())) {
                rest.remove(i);
                break;
            }
        }
        if (metadata != null) {
            org.geotoolkit.ows.xml.v110.ObjectFactory factory = new org.geotoolkit.ows.xml.v110.ObjectFactory();
            this.rest.add(factory.createMetadata(metadata));
        }
    }

    /**
     * Gets the rest of the content model.
     *
     */
    @Override
    public List<JAXBElement<?>> getRest() {
        return Collections.unmodifiableList(rest);
    }

    /**
     * Add a new Element to the list
     */
    public void addRest(final JAXBElement<?> element) {
        this.rest.add(element);
    }

    @Override
    public Envelope getLonLatEnvelope() {
        //should return the WGS84BoundingBox from the REST list
        return null;
    }

    public WGS84BoundingBoxType getWGS84BoundingBox() {
        for (JAXBElement<?> jb : rest) {
            if (jb.getValue() instanceof WGS84BoundingBoxType) {
                return (WGS84BoundingBoxType) jb.getValue();
            }
        }
        return null;
    }

    public final void setWGS84BoundingBox(final WGS84BoundingBoxType metadata) {
        // first we remove the old one
        for (int i = 0; i < rest.size(); i++) {
            JAXBElement<?> jb  = rest.get(i);
            if (jb.getValue() instanceof WGS84BoundingBoxType) {
                rest.remove(i);
                break;
            }
        }
        if (metadata != null) {
            org.geotoolkit.ows.xml.v110.ObjectFactory factory = new org.geotoolkit.ows.xml.v110.ObjectFactory();
            this.rest.add(factory.createWGS84BoundingBox(metadata));
        }
    }

}
