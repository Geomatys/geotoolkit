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
package org.geotoolkit.ogcapi.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;
import org.geotoolkit.ogcapi.dto.DataTransferObject;

/**
 * The language used for textual values in this record.
 */
@JsonPropertyOrder({
    Language.JSON_PROPERTY_CODE,
    Language.JSON_PROPERTY_NAME,
    Language.JSON_PROPERTY_ALTERNATE,
    Language.JSON_PROPERTY_DIR
})
public final class Language extends DataTransferObject {

    public static final String JSON_PROPERTY_CODE = "code";
    private String code;

    public static final String JSON_PROPERTY_NAME = "name";
    private String name;

    public static final String JSON_PROPERTY_ALTERNATE = "alternate";
    private String alternate;


    public static final String JSON_PROPERTY_DIR = "dir";
    private LanguageDirection dir = LanguageDirection.LTR;

    public Language() {
    }

    public Language code(String code) {
        this.code = code;
        return this;
    }

    /**
     * The language tag as per RFC-5646.
     *
     * @return code
     */
    @JsonProperty(JSON_PROPERTY_CODE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public String getCode() {
        return code;
    }

    @JsonProperty(JSON_PROPERTY_CODE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setCode(String code) {
        this.code = code;
    }

    public Language name(String name) {
        this.name = name;
        return this;
    }

    /**
     * The untranslated name of of the language.
     *
     * @return name
     */
    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getName() {
        return name;
    }

    @JsonProperty(JSON_PROPERTY_NAME)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setName(String name) {
        this.name = name;
    }

    public Language alternate(String alternate) {
        this.alternate = alternate;
        return this;
    }

    /**
     * The name of the language in another well-understood language, usually
     * English.
     *
     * @return alternate
     */
    @JsonProperty(JSON_PROPERTY_ALTERNATE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public String getAlternate() {
        return alternate;
    }

    @JsonProperty(JSON_PROPERTY_ALTERNATE)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setAlternate(String alternate) {
        this.alternate = alternate;
    }

    public Language dir(LanguageDirection dir) {
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
    @JsonProperty(JSON_PROPERTY_DIR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public LanguageDirection getDir() {
        return dir;
    }

    @JsonProperty(JSON_PROPERTY_DIR)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setDir(LanguageDirection dir) {
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
