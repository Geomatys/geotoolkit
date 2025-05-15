/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ogcapi.model.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import org.geotoolkit.ogcapi.model.DataTransferObject;

/**
 * The language used for textual values in this record.
 */
@JsonPropertyOrder({
    Language.JSON_PROPERTY_CODE,
    Language.JSON_PROPERTY_NAME,
    Language.JSON_PROPERTY_ALTERNATE,
    Language.JSON_PROPERTY_DIR
})
@XmlRootElement(name = "Language")
@XmlAccessorType(XmlAccessType.FIELD)
@JacksonXmlRootElement(localName = "Language")
public final class Language extends DataTransferObject {

    public static final String JSON_PROPERTY_CODE = "code";
    @XmlElement(name = "code")
    @jakarta.annotation.Nonnull
    private String code;

    public static final String JSON_PROPERTY_NAME = "name";
    @XmlElement(name = "name")
    @jakarta.annotation.Nullable
    private String name;

    public static final String JSON_PROPERTY_ALTERNATE = "alternate";
    @XmlElement(name = "alternate")
    @jakarta.annotation.Nullable
    private String alternate;


    public static final String JSON_PROPERTY_DIR = "dir";
    @XmlElement(name = "dir")
    @jakarta.annotation.Nullable
    private LanguageDirection dir = LanguageDirection.LTR;

    public Language() {
    }

    public Language code(@jakarta.annotation.Nonnull String code) {
        this.code = code;
        return this;
    }

    /**
     * The language tag as per RFC-5646.
     *
     * @return code
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_CODE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "code")
    public String getCode() {
        return code;
    }

    @JsonProperty(JSON_PROPERTY_CODE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    @JacksonXmlProperty(localName = "code")
    public void setCode(@jakarta.annotation.Nonnull String code) {
        this.code = code;
    }

    public Language name(@jakarta.annotation.Nullable String name) {
        this.name = name;
        return this;
    }

    /**
     * The untranslated name of of the language.
     *
     * @return name
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "name")
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "name")
    public void setName(@jakarta.annotation.Nullable String name) {
        this.name = name;
    }

    public Language alternate(@jakarta.annotation.Nullable String alternate) {
        this.alternate = alternate;
        return this;
    }

    /**
     * The name of the language in another well-understood language, usually
     * English.
     *
     * @return alternate
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ALTERNATE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "alternate")
    public String getAlternate() {
        return alternate;
    }

    @JsonProperty(JSON_PROPERTY_ALTERNATE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "alternate")
    public void setAlternate(@jakarta.annotation.Nullable String alternate) {
        this.alternate = alternate;
    }

    public Language dir(@jakarta.annotation.Nullable LanguageDirection dir) {
        this.dir = dir;
        return this;
    }

    /**
     * The direction for text in this language. The default, &#x60;ltr&#x60;
     * (left-to-right), represents the most common situation. However, care
     * should be taken to set the value of &#x60;dir&#x60; appropriately if the
     * language direction is not &#x60;ltr&#x60;. Other values supported are
     * &#x60;rtl&#x60; (right-to-left), &#x60;ttb&#x60; (top-to-bottom), and
     * &#x60;btt&#x60; (bottom-to-top).
     *
     * @return dir
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_DIR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "dir")
    public LanguageDirection getDir() {
        return dir;
    }

    @JsonProperty(JSON_PROPERTY_DIR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    @JacksonXmlProperty(localName = "dir")
    public void setDir(@jakarta.annotation.Nullable LanguageDirection dir) {
        this.dir = dir;
    }

    /**
     * Return true if this language object is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Language language = (Language) o;
        return Objects.equals(this.code, language.code)
                && Objects.equals(this.name, language.name)
                && Objects.equals(this.alternate, language.alternate)
                && Objects.equals(this.dir, language.dir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name, alternate, dir);
    }

}
