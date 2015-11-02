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
import org.geotoolkit.skos.xml.Concept;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ScoredConcept {

    public double score;

    public String uriConcept;

    public ISOLanguageCode language;

    public Thesaurus originThesaurus;

    public ScoredConcept(final String uriConcept, final Thesaurus originThesaurus, final double score, final ISOLanguageCode language) {
        this.score           = score;
        this.uriConcept      = uriConcept;
        this.originThesaurus = originThesaurus;
        this.language        = language;
    }

    public Concept getConcept() {
        return originThesaurus.getConcept(uriConcept);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ScoredConcept) {
            final ScoredConcept that = (ScoredConcept) obj;
            return Objects.equals(this.language, that.language) &&
                   Objects.equals(this.originThesaurus, that.originThesaurus) &&
                   Objects.equals(this.score, that.score) &&
                   Objects.equals(this.uriConcept, that.uriConcept);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.score) ^ (Double.doubleToLongBits(this.score) >>> 32));
        hash = 43 * hash + (this.uriConcept != null ? this.uriConcept.hashCode() : 0);
        hash = 43 * hash + (this.language != null ? this.language.hashCode() : 0);
        hash = 43 * hash + (this.originThesaurus != null ? this.originThesaurus.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[ScoredConcept]\n");
        if (uriConcept != null) {
            sb.append("uriConcept:").append(uriConcept).append('\n');
        }
        if (language != null) {
            sb.append("language:").append(language).append('\n');
        }
        sb.append("score:").append(score).append('\n');
        if (originThesaurus != null) {
            sb.append("originThesaurus:").append(originThesaurus).append('\n');
        }
        return sb.toString();
    }
}
