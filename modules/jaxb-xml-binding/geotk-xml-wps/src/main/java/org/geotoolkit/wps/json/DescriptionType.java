/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.wps.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.ows.xml.AbstractAdditionalParameter;
import org.geotoolkit.ows.xml.AbstractAdditionalParameters;
import org.geotoolkit.ows.xml.AbstractDescription;
import org.geotoolkit.ows.xml.AbstractKeywords;
import org.geotoolkit.ows.xml.AbstractMetadata;

/**
 * DescriptionType
 */
public class DescriptionType {

    private String id = null;

    private String title = null;

    @JsonProperty("abstract")
    private String _abstract = null;

    private List<String> keywords = null;

    private DescriptionTypeOwsContext owsContext = null;

    private List<Metadata> metadata = null;

    private List<AdditionalParameters> additionalParameters = null;

    private List<JsonLink> links = null;

    public DescriptionType() {
    }

    public DescriptionType(String id, String title, String _abstract, List<String> keywords,
            List<Metadata> metadata, List<AdditionalParameters> additionalParameters) {
        this.id = id;
        this.title = title;
        this._abstract = _abstract;
        this.keywords = keywords;
        this.metadata = metadata;
        this.additionalParameters = additionalParameters;
    }

    public DescriptionType(DescriptionType desc) {
        if (desc != null) {
            this.id = desc.getId();
            this._abstract = desc.getAbstract();
            if (desc.keywords != null && !desc.keywords.isEmpty()) {
                this.keywords = new ArrayList<>(desc.getKeywords());
            }
            this.title = desc.getTitle();
            if (desc.metadata != null && !desc.metadata.isEmpty()) {
                this.metadata = new ArrayList<>();
                for (Metadata meta : desc.metadata) {
                    this.metadata.add(new Metadata(meta));
                }
            }
            if (desc.additionalParameters != null && !desc.additionalParameters.isEmpty()) {
                this.additionalParameters = new ArrayList<>();
                for (AdditionalParameters param : desc.additionalParameters) {
                    this.additionalParameters.add(new AdditionalParameters(param));
                }
            }
            if (desc.getOwsContext() != null) {
                this.owsContext = new DescriptionTypeOwsContext(desc.getOwsContext());
            }
        }
    }

    public DescriptionType(AbstractDescription desc) {
        if (desc != null) {
            this.id = desc.getIdentifier().getValue();
            this._abstract = desc.getFirstAbstract();
            if (desc.getKeywords() != null && !desc.getKeywords().isEmpty()) {
                this.keywords = new ArrayList<>();
                for (AbstractKeywords kw : desc.getKeywords()) {
                    this.keywords.addAll(kw.getKeywordList());
                }
            }
            this.title = desc.getFirstTitle();
            if (desc.getMetadata() != null && !desc.getMetadata().isEmpty()) {
                this.metadata = new ArrayList<>();
                for (AbstractMetadata meta : desc.getMetadata()) {
                    this.metadata.add(new Metadata(meta));
                }
            }
            if (desc.getAdditionalParameters() != null && !desc.getAdditionalParameters().isEmpty()) {
                this.additionalParameters = new ArrayList<>();
                for (AbstractAdditionalParameters params : desc.getAdditionalParameters()) {
                    List<AdditionalParameter> parameters = new ArrayList<>();
                    for (AbstractAdditionalParameter param : params.getAdditionalParameter()) {
                        List<String> sb = new ArrayList<>();
                        for (Object o : param.getValue()) {
                            if (o instanceof String) {
                                sb.add((String) o);
                            }
                        }
                        AdditionalParameter addi = new AdditionalParameter(param.getName().getValue(), sb);
                        parameters.add(addi);
                    }
                    this.additionalParameters.add(new AdditionalParameters(params.getRole(), parameters));
                }
            }
            if (desc.getOwsContext() != null) {
                this.owsContext = new DescriptionTypeOwsContext(desc.getOwsContext());
            }
        }
    }

    public DescriptionType id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     *
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DescriptionType title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Get title
     *
     * @return title
     *
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DescriptionType _abstract(String _abstract) {
        this._abstract = _abstract;
        return this;
    }

    /**
     * Get _abstract
     *
     * @return _abstract
     *
     */
    public String getAbstract() {
        return _abstract;
    }

    public void setAbstract(String _abstract) {
        this._abstract = _abstract;
    }

    public DescriptionType keywords(List<String> keywords) {
        this.keywords = keywords;
        return this;
    }

    public DescriptionType addKeywordsItem(String keywordsItem) {

        if (this.keywords == null) {
            this.keywords = new ArrayList<>();
        }

        this.keywords.add(keywordsItem);
        return this;
    }

    public DescriptionType addKeywordsItems(List<String> keywordsItems) {

        if (this.keywords == null) {
            this.keywords = new ArrayList<>();
        }

        this.keywords.addAll(keywordsItems);
        return this;
    }

    /**
     * Get keywords
     *
     * @return keywords
     *
     */
    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public DescriptionTypeOwsContext getOwsContext() {
        return owsContext;
    }

    public void setOwsContext(DescriptionTypeOwsContext owsContext) {
        this.owsContext = owsContext;
    }

    public DescriptionType metadata(List<Metadata> metadata) {
        this.metadata = metadata;
        return this;
    }

    public DescriptionType addMetadataItem(Metadata metadataItem) {

        if (this.metadata == null) {
            this.metadata = new ArrayList<>();
        }

        this.metadata.add(metadataItem);
        return this;
    }

    /**
     * Get metadata
     *
     * @return metadata
     *
     */
    public List<Metadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<Metadata> metadata) {
        this.metadata = metadata;
    }

    public DescriptionType additionalParameters(List<AdditionalParameters> additionalParameters) {
        this.additionalParameters = additionalParameters;
        return this;
    }

    public DescriptionType addAdditionalParametersItem(AdditionalParameters additionalParametersItem) {

        if (this.additionalParameters == null) {
            this.additionalParameters = new ArrayList<>();
        }

        this.additionalParameters.add(additionalParametersItem);
        return this;
    }

    /**
     * Get additionalParameters
     *
     * @return additionalParameters
     *
     */
    public List<AdditionalParameters> getAdditionalParameters() {
        return additionalParameters;
    }

    public void setAdditionalParameters(List<AdditionalParameters> additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    /**
     * @return the links
     */
    public List<JsonLink> getLinks() {
        return links;
    }

    /**
     * @param links the links to set
     */
    public void setLinks(List<JsonLink> links) {
        this.links = links;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DescriptionType descriptionType = (DescriptionType) o;
        return Objects.equals(this.id, descriptionType.id)
                && Objects.equals(this.title, descriptionType.title)
                && Objects.equals(this._abstract, descriptionType._abstract)
                && Objects.equals(this.keywords, descriptionType.keywords)
                && Objects.equals(this.owsContext, descriptionType.owsContext)
                && Objects.equals(this.metadata, descriptionType.metadata)
                && Objects.equals(this.additionalParameters, descriptionType.additionalParameters)
                && Objects.equals(this.links, descriptionType.links);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, title, _abstract, keywords, owsContext, metadata, additionalParameters, links);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DescriptionType {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    _abstract: ").append(toIndentedString(_abstract)).append("\n");
        sb.append("    keywords: ").append(toIndentedString(keywords)).append("\n");
        sb.append("    owsContext: ").append(toIndentedString(owsContext)).append("\n");
        sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
        sb.append("    additionalParameters: ").append(toIndentedString(additionalParameters)).append("\n");
        sb.append("    links: ").append(toIndentedString(links)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
