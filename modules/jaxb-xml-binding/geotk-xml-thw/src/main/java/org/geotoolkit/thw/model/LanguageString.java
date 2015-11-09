/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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

package org.geotoolkit.thw.model;

import java.util.Objects;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class LanguageString {

    private String language;

    private String string;

    public LanguageString() {

    }

    public LanguageString(final String language, final String string) {
        this.language = language;
        this.string   = string;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(final String language) {
        this.language = language;
    }

    /**
     * @return the string
     */
    public String getString() {
        return string;
    }

    /**
     * @param string the string to set
     */
    public void setString(final String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[LanguageString]:\n");
        if (language != null) {
            sb.append("language:").append(language).append('\n');
        }
        if (string != null) {
            sb.append("string:").append(string).append('\n');
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof LanguageString) {
            final LanguageString that = (LanguageString) obj;
            return Objects.equals(this.language, that.language) &&
                   Objects.equals(this.string,   that.string);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (this.language != null ? this.language.hashCode() : 0);
        hash = 43 * hash + (this.string != null ? this.string.hashCode() : 0);
        return hash;
    }
}
