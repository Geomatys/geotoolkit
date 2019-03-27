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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractDescription;
import org.geotoolkit.ows.xml.v200.AdditionalParameter;
import org.geotoolkit.ows.xml.v200.AdditionalParametersType;
import org.geotoolkit.ows.xml.v200.BasicIdentificationType;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.ows.xml.v200.KeywordsType;
import org.geotoolkit.ows.xml.v200.LanguageStringType;
import org.geotoolkit.ows.xml.v200.MetadataType;
import org.geotoolkit.ows.xml.v200.OwsContextDescriptionType;
import org.geotoolkit.ows.xml.v200.OwsContextOfferingType;
import static org.geotoolkit.wps.xml.WPSMarshallerPool.OWS_1_1_NAMESPACE;

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
    "identifierV1",
    "title",
    "_abstract",
    "keywords",
    "identifierV2",
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

    private CodeType identifier;
    @XmlElement(name = "Title", namespace=OWS_2_0_NAMESPACE)
    private LanguageStringType title;
    @XmlElement(name = "Abstract", namespace=OWS_2_0_NAMESPACE)
    private List<LanguageStringType> _abstract;
    @XmlElement(name = "Keywords", namespace=OWS_2_0_NAMESPACE)
    private List<KeywordsType> keywords;
    @XmlElement(name = "Metadata", namespace=OWS_2_0_NAMESPACE)
    private List<MetadataType> metadata;

    @XmlTransient
    private OwsContextDescriptionType owsContext;

    @XmlTransient
    private List<AdditionalParametersType> additionalParameters;

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

    public Description(
            final CodeType identifier,
            final LanguageStringType title,
            final List<LanguageStringType> _abstract,
            final List<KeywordsType> keywords,
            final OwsContextDescriptionType owsContext
    ) {
        this.identifier = identifier;
        this.title = title;
        this._abstract = _abstract;
        this.keywords = keywords;
        this.owsContext = owsContext;
    }

    public Description(
            CodeType identifier,
            final LanguageStringType title,
            final LanguageStringType _abstract,
            final KeywordsType keywords
    ) {
        this(identifier, title, _abstract, keywords, null);
    }

    public Description(
            CodeType identifier,
            final LanguageStringType title,
            final LanguageStringType _abstract,
            final KeywordsType keywords,
            final List<AdditionalParametersType> additionalParameters
    ) {
        this.identifier = identifier;
        this.title = title;
        if (_abstract != null) {
            this._abstract = new ArrayList<>();
            this._abstract.add(_abstract);
        }
        if (keywords != null) {
            this.keywords = new ArrayList<>();
            this.keywords.add(keywords);
        }
        this.additionalParameters = additionalParameters;
    }

    public Description(Description desc) {
        this(desc.getIdentifier(), desc.getTitle(), desc.getAbstract(), desc.getKeywords());
    }

    public Description(org.geotoolkit.wps.json.DescriptionType desc) {
        this(new CodeType(desc.getId()), new LanguageStringType(desc.getTitle()), new LanguageStringType(desc.getAbstract()), new KeywordsType(desc.getKeywords()));
        if (desc.getAdditionalParameters() != null) {
            this.additionalParameters = new ArrayList<>();
            for (org.geotoolkit.wps.json.AdditionalParameters addParams : desc.getAdditionalParameters()) {
                List<AdditionalParameter> params = new ArrayList<>();
                for (org.geotoolkit.wps.json.AdditionalParameter addParam : addParams.getParameters()) {
                    params.add(new AdditionalParameter(new CodeType(addParam.getName()), new ArrayList<>(addParam.getValues())));
                }
                this.additionalParameters.add(new AdditionalParametersType(addParams.getRole(), params));
            }
        }
        if (desc.getOwsContext() != null && desc.getOwsContext().getOffering() != null) {
            String code = desc.getOwsContext().getOffering().getCode();
            String href = null;
            if (desc.getOwsContext().getOffering().getContent() != null) {
                href = desc.getOwsContext().getOffering().getContent().getHref();
            }
            this.owsContext = new OwsContextDescriptionType(new OwsContextOfferingType(code, href));
        }
    }

    public Description(org.geotoolkit.wps.json.InputType desc) {
        this(new CodeType(desc.getId()), new LanguageStringType(desc.getTitle()), new LanguageStringType(desc.getAbstract()), new KeywordsType(desc.getKeywords()));
        if (desc.getAdditionalParameters() != null) {
            this.additionalParameters = new ArrayList<>();
            for (org.geotoolkit.wps.json.AdditionalParameters addParams : desc.getAdditionalParameters()) {
                List<AdditionalParameter> params = new ArrayList<>();
                for (org.geotoolkit.wps.json.AdditionalParameter addParam : addParams.getParameters()) {
                    params.add(new AdditionalParameter(new CodeType(addParam.getName()), new ArrayList<>(addParam.getValues())));
                }
                this.additionalParameters.add(new AdditionalParametersType(addParams.getRole(),params));
            }
        }
    }

    @Override
    public CodeType getIdentifier() {
        return identifier;
    }

    @XmlElement(name = "Identifier", namespace = OWS_2_0_NAMESPACE)
    private CodeType getIdentifierV2() {
        if (FilterByVersion.isV2()) {
            return identifier;
        }
        return null;
    }

    private void setIdentifierV2(CodeType identifier) {
        this.identifier = identifier;
    }

    @XmlElement(name = "Identifier", namespace = OWS_1_1_NAMESPACE)
    private CodeType getIdentifierV1() {
        if (FilterByVersion.isV1()) {
            return identifier;
        }
        return null;
    }

    private void setIdentifierV1(CodeType identifier) {
        this.identifier = identifier;
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

    @Override
    public List<KeywordsType> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<>();
        }
        return keywords;
    }

    @Override
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

    /**
     * @return the additionalParameters
     */
    @Override
    public List<AdditionalParametersType> getAdditionalParameters() {
        return additionalParameters;
    }

    /**
     * @param additionalParameters the additionalParameters to set
     */
    public void setAdditionalParameters(List<AdditionalParametersType> additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (identifier != null) {
            sb.append("identifier:").append(identifier).append('\n');
        }
        if (title != null) {
            sb.append("title:").append(title).append('\n');
        }
        if (_abstract != null) {
            sb.append("_abstract:\n");
            for (LanguageStringType jb : _abstract) {
                sb.append(jb).append('\n');
            }
        }
        if (keywords != null) {
            sb.append("keywords:\n");
            for (KeywordsType jb : keywords) {
                sb.append(jb).append('\n');
            }
        }
        if (metadata != null) {
            sb.append("metadata:\n");
            for (MetadataType jb : metadata) {
                sb.append(jb).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verify that this entry is identical to the specified object.
     *
     * @param object Object to compare
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Description) {
            final Description that = (Description) object;
            return Objects.equals(this._abstract, that._abstract)
                    && Objects.equals(this.identifier, that.identifier)
                    && Objects.equals(this.keywords, that.keywords)
                    && Objects.equals(this.metadata, that.metadata)
                    && Objects.equals(this.title, that.title);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this._abstract);
        hash = 97 * hash + Objects.hashCode(this.identifier);
        hash = 97 * hash + Objects.hashCode(this.keywords);
        hash = 97 * hash + Objects.hashCode(this.metadata);
        hash = 97 * hash + Objects.hashCode(this.title);
        return hash;
    }

    /**
     * @return the owsContext
     */
    public OwsContextDescriptionType getOwsContext() {
        return owsContext;
    }

    /**
     * @param owsContext the owsContext to set
     */
    public void setOwsContext(OwsContextDescriptionType owsContext) {
        this.owsContext = owsContext;
    }
}
