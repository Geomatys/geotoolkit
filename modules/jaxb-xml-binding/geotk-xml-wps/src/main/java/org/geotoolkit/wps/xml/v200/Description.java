/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.wps.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractDescription;
import org.geotoolkit.ows.xml.v200.BasicIdentificationType;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.ows.xml.v200.KeywordsType;
import org.geotoolkit.ows.xml.v200.LanguageStringType;
import org.geotoolkit.ows.xml.v200.MetadataType;

import static org.geotoolkit.wps.xml.WPSMarshallerPool.OWS_2_0_NAMESPACE;

/**
 *
 * Description type for process or input/output data items.
 *
 *
 * <p>Java class for Description complex type.

 <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Description">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.opengis.net/ows/2.0}BasicIdentificationType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Title"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Abstract" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Keywords" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Identifier"/>
 *         &lt;element ref="{http://www.opengis.net/ows/2.0}Metadata" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * TODO: directly extend {@link BasicIdentificationType} when we've found how to
 * solve version problems.
 *
 */
@XmlType(name = "DescriptionType", propOrder = {
    "title",
    "_abstract",
    "keywords",
    "identifier",
    "metadata"
})
@XmlSeeAlso({
    ProcessDescription.class,
    GenericProcess.class,
    ProcessSummary.class,
    OutputDescription.class,
    InputDescription.class,
    GenericOutput.class,
    GenericInput.class
})
public class Description implements AbstractDescription {

    @XmlElement(name = "Identifier", namespace=OWS_2_0_NAMESPACE, required=true)
    private CodeType identifier;
    @XmlElement(name = "Title", namespace=OWS_2_0_NAMESPACE)
    private LanguageStringType title;
    @XmlElement(name = "Abstract", namespace=OWS_2_0_NAMESPACE)
    private List<LanguageStringType> _abstract;
    @XmlElement(name = "Keywords", namespace=OWS_2_0_NAMESPACE)
    private List<KeywordsType> keywords;
    @XmlElement(name = "Metadata", namespace=OWS_2_0_NAMESPACE)
    private List<MetadataType> metadata;

    public Description() {}

    public Description(
            CodeType identifier,
            final LanguageStringType title,
            final List<LanguageStringType> _abstract,
            final List<KeywordsType> keywords
    ) {
        this.identifier = identifier;
        this.title = title;
        this._abstract = _abstract;
        this.keywords = keywords;
    }

    public CodeType getIdentifier() {
        return identifier;
    }

    public LanguageStringType getTitle() {
        return title;
    }

    public List<LanguageStringType> getAbstract() {
        if (_abstract == null) {
            _abstract = new ArrayList<>();
        }
        return _abstract;
    }

    public List<KeywordsType> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<>();
        }
        return keywords;
    }

    public List<MetadataType> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<>();
        }
        return metadata;
    }

    @Override
    public String getFirstTitle() {
        return title == null? null : title.getValue();
    }

    @Override
    public String getFirstAbstract() {
        final List<LanguageStringType> a = getAbstract();
        if (a.isEmpty()) return null;
        return a.get(0).getValue();
    }
}
