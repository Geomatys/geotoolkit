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

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.ows.xml.AbstractAdditionalParameter;
import org.geotoolkit.ows.xml.AbstractAdditionalParameters;
import org.geotoolkit.ows.xml.AbstractKeywords;
import org.geotoolkit.ows.xml.AbstractMetadata;
import org.geotoolkit.wps.xml.v200.BoundingBoxData;
import org.geotoolkit.wps.xml.v200.ComplexData;
import org.geotoolkit.wps.xml.v200.InputDescription;
import org.geotoolkit.wps.xml.v200.LiteralData;
import org.geotoolkit.wps.xml.v200.SupportedCRS;
import org.geotoolkit.wps.xml.v200.DataDescription;

/**
 * InputType
 */
public class InputType extends InputTypeChoice {

    private String id = null;

    private String title = null;

    private List<String> keywords = null;

    private List<FormatDescription> formats = new ArrayList<>();

    private String minOccurs = null;

    private String maxOccurs = null;

    private String _abstract = null;

    private DescriptionTypeOwsContext owsContext = null;

    private List<Metadata> metadata = null;

    private List<AdditionalParameters> additionalParameters = null;

    public InputType() {

    }

    // literal
    public InputType(String id, String title, String _abstract, List<String> keywords,
            List<Metadata> metadata, List<AdditionalParameters> additionalParameters,
            List<FormatDescription> formats, String minOccurs, String maxOccurs, DescriptionTypeOwsContext owsContext,
            LiteralDataDomain literalDataDomain) {
        super(literalDataDomain);
        this._abstract = _abstract;
        this.additionalParameters = additionalParameters;
        this.formats = formats;
        this.id = id;
        this.keywords = keywords;
        this.metadata = metadata;
        this.owsContext = owsContext;
        this.title = title;
        this.minOccurs = minOccurs;
        this.maxOccurs = maxOccurs;

    }

    // bbox
    public InputType(String id, String title, String _abstract, List<String> keywords,
            List<Metadata> metadata, List<AdditionalParameters> additionalParameters,
            List<FormatDescription> formats, String minOccurs, String maxOccurs, DescriptionTypeOwsContext owsContext,
            List<String> supportedCRS) {
        super(supportedCRS);
        this._abstract = _abstract;
        this.additionalParameters = additionalParameters;
        this.formats = formats;
        this.id = id;
        this.keywords = keywords;
        this.metadata = metadata;
        this.owsContext = owsContext;
        this.title = title;
        this.minOccurs = minOccurs;
        this.maxOccurs = maxOccurs;

    }

    // complex
    public InputType(String id, String title, String _abstract, List<String> keywords,
            List<Metadata> metadata, List<AdditionalParameters> additionalParameters,
            List<FormatDescription> formats, String minOccurs, String maxOccurs, DescriptionTypeOwsContext owsContext) {
        this._abstract = _abstract;
        this.additionalParameters = additionalParameters;
        this.formats = formats;
        this.id = id;
        this.keywords = keywords;
        this.metadata = metadata;
        this.owsContext = owsContext;
        this.title = title;
        this.minOccurs = minOccurs;
        this.maxOccurs = maxOccurs;

    }


    public InputType(InputDescription desc) {
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

            if (desc.getDataDescription() != null) {
                DataDescription dataDesc = desc.getDataDescription();
                if (dataDesc.getFormat() != null && !dataDesc.getFormat().isEmpty()) {
                    this.formats = new ArrayList<>();
                    for (org.geotoolkit.wps.xml.v200.Format f : dataDesc.getFormat()) {
                        this.formats.add(new FormatDescription(f));
                    }
                }
            }

            if (desc.getDataDescription() instanceof LiteralData) {
                LiteralData lit = (LiteralData) desc.getDataDescription();
                if (lit.getLiteralDataDomain() != null && !lit.getLiteralDataDomain().isEmpty()) {
                    super.setLiteralDataDomain(new LiteralDataDomain(lit.getLiteralDataDomain().get(0)));
                }
            } else if (desc.getDataDescription() instanceof ComplexData) {
                // do nothing
            } else if (desc.getDataDescription() instanceof BoundingBoxData) {
                BoundingBoxData bbox = (BoundingBoxData) desc.getDataDescription();
                if (bbox.getSupportedCRS() != null && !bbox.getSupportedCRS().isEmpty()) {
                    List<String> crs = new ArrayList<>();
                    for (SupportedCRS c : bbox.getSupportedCRS()) {
                        crs.add(c.getValue());
                    }
                    super.setSupportedCRS(crs);
                }
            }
        }

    }

    public InputType(InputType that) {
        super(that);
        if (that != null) {
            this._abstract = that._abstract;
            this.id = that.id;
            this.title = that.title;
            this.minOccurs = that.minOccurs;
            this.maxOccurs = that.maxOccurs;
            if (that.keywords != null && !that.keywords.isEmpty()) {
                this.keywords = new ArrayList<>(that.keywords);
            }
            if (that.metadata != null && !that.metadata.isEmpty()) {
                this.metadata = new ArrayList<>();
                for (Metadata meta : that.metadata) {
                    this.metadata.add(new Metadata(meta));
                }
            }
            if (that.additionalParameters != null && !that.additionalParameters.isEmpty()) {
                this.additionalParameters = new ArrayList<>();
                for (AdditionalParameters param : that.additionalParameters) {
                    this.additionalParameters.add(new AdditionalParameters(param));
                }
            }
            if (that.formats != null && !that.formats.isEmpty()) {
                this.formats = new ArrayList<>();
                for (FormatDescription f : that.formats) {
                    this.formats.add(new FormatDescription(f));
                }
            }
            if (that.owsContext != null) {
                this.owsContext = new DescriptionTypeOwsContext(that.owsContext);
            }
        }

    }


    public InputType minOccurs(String minOccurs) {
        this.minOccurs = minOccurs;
        return this;
    }

    /**
     * Get minOccurs
     *
     * @return minOccurs
  *
     */
    public String getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(String minOccurs) {
        this.minOccurs = minOccurs;
    }

    public InputType maxOccurs(String maxOccurs) {
        this.maxOccurs = maxOccurs;
        return this;
    }

    /**
     * Get maxOccurs
     *
     * @return maxOccurs
  *
     */
    public String getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(String maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public InputType formats(List<FormatDescription> formats) {
        this.formats = formats;
        return this;
    }

    public InputType addFormatsItem(FormatDescription formatsItem) {

        this.formats.add(formatsItem);
        return this;
    }

    /**
     * Get formats
     *
     * @return formats
  *
     */
    public List<FormatDescription> getFormats() {
        return formats;
    }

    public void setFormats(List<FormatDescription> formats) {
        this.formats = formats;
    }

    public InputType id(String id) {
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

    public InputType title(String title) {
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

    public InputType _abstract(String _abstract) {
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

    public InputType keywords(List<String> keywords) {
        this.keywords = keywords;
        return this;
    }

    public InputType addKeywordsItem(String keywordsItem) {

        if (this.keywords == null) {
            this.keywords = new ArrayList<String>();
        }

        this.keywords.add(keywordsItem);
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

    public InputType owsContext(DescriptionTypeOwsContext owsContext) {
        this.owsContext = owsContext;
        return this;
    }

    /**
     * Get owsContext
     *
     * @return owsContext
  *
     */
    public DescriptionTypeOwsContext getOwsContext() {
        return owsContext;
    }

    public void setOwsContext(DescriptionTypeOwsContext owsContext) {
        this.owsContext = owsContext;
    }

    public InputType metadata(List<Metadata> metadata) {
        this.metadata = metadata;
        return this;
    }

    public InputType addMetadataItem(Metadata metadataItem) {

        if (this.metadata == null) {
            this.metadata = new ArrayList<Metadata>();
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

    public InputType additionalParameters(List<AdditionalParameters> additionalParameters) {
        this.additionalParameters = additionalParameters;
        return this;
    }

    public InputType addAdditionalParametersItem(AdditionalParameters additionalParametersItem) {

        if (this.additionalParameters == null) {
            this.additionalParameters = new ArrayList<AdditionalParameters>();
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

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InputType inputType = (InputType) o;
        return Objects.equals(this.minOccurs, inputType.minOccurs)
                && Objects.equals(this.maxOccurs, inputType.maxOccurs)
                && Objects.equals(this.formats, inputType.formats)
                && Objects.equals(this.id, inputType.id)
                && Objects.equals(this.title, inputType.title)
                && Objects.equals(this._abstract, inputType._abstract)
                && Objects.equals(this.keywords, inputType.keywords)
                && Objects.equals(this.owsContext, inputType.owsContext)
                && Objects.equals(this.metadata, inputType.metadata)
                && Objects.equals(this.additionalParameters, inputType.additionalParameters)
                && super.equals(o);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(minOccurs, maxOccurs, formats, id, title, _abstract, keywords, owsContext, metadata, additionalParameters, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class InputType {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    minOccurs: ").append(toIndentedString(minOccurs)).append("\n");
        sb.append("    maxOccurs: ").append(toIndentedString(maxOccurs)).append("\n");
        sb.append("    formats: ").append(toIndentedString(formats)).append("\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    _abstract: ").append(toIndentedString(_abstract)).append("\n");
        sb.append("    keywords: ").append(toIndentedString(keywords)).append("\n");
        sb.append("    owsContext: ").append(toIndentedString(owsContext)).append("\n");
        sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
        sb.append("    additionalParameters: ").append(toIndentedString(additionalParameters)).append("\n");
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
