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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.skos.xml.Concept;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class Result {

    private List<String> matchingLanguage;

    private List<Concept> matchingConcept;

    private List<String> operators;

    public Result() {
    }

    public Result(final List<String> matchingLanguage, final List<Concept> matchingConcept) {
        this.matchingConcept  = matchingConcept;
        this.matchingLanguage = matchingLanguage;
    }

    public Result(final List<String> matchingLanguage, final List<Concept> matchingConcept, final List<String> operators) {
        this.matchingConcept  = matchingConcept;
        this.matchingLanguage = matchingLanguage;
        this.operators        = operators;
    }

    /**
     * @return the matchingConcept
     */
    public List<Concept> getMatchingConcept() {
        if (matchingConcept == null) {
            matchingConcept = new ArrayList<>();
        }
        return matchingConcept;
    }

    public Concept alreadyContainsConcept(final String uriConcept) {
        if (matchingConcept == null) {
            matchingConcept = new ArrayList<>();
        }
        for (Concept c : matchingConcept) {
            if (c.getAbout().equals(uriConcept)) {
                return c;
            }
        }
        return null;
    }
    /**
     * @param matchingConcept the matchingConcept to set
     */
    public void setMatchingConcept(final List<Concept> matchingConcept) {
        this.matchingConcept = matchingConcept;
    }

    /**
     */
    public void addAllMatchingConcept(final List<Concept> matchingConcept) {
        if (this.matchingConcept == null) {
            this.matchingConcept = new ArrayList<>();
        }
        if (matchingConcept != null) {
            this.matchingConcept.addAll(matchingConcept);
        }
    }

    public void addMatchingConcept(final Concept matchingConcept) {
        if (this.matchingConcept == null) {
            this.matchingConcept = new ArrayList<>();
        }
        if (matchingConcept != null) {
            this.matchingConcept.add(matchingConcept);
        }
    }

    /**
     * @return the matchingLanguage
     */
    public List<String> getMatchingLanguage() {
        if (this.matchingLanguage == null) {
            this.matchingLanguage = new ArrayList<>();
        }
        return matchingLanguage;
    }

    /**
     * @param matchingLanguage the matchingLanguage to set
     */
    public void setMatchingLanguage(final List<String> matchingLanguage) {
        this.matchingLanguage = matchingLanguage;
    }

    public void addMatchingLanguage(final String matchingLanguage) {
        if (this.matchingLanguage == null) {
            this.matchingLanguage = new ArrayList<>();
        }
        if (matchingLanguage != null) {
            this.matchingLanguage.add(matchingLanguage);
        }
    }

    /**
     * @return the operators
     */
    public List<String> getOperators() {
        return operators;
    }

    /**
     * @param operators the operators to set
     */
    public void setOperators(final List<String> operators) {
        this.operators = operators;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[Result]:\n");
        if (matchingLanguage != null) {
            sb.append("matchingLanguage:\n");
            for (String s : matchingLanguage) {
                sb.append(s).append('\n');
            }
        }
        if (matchingConcept != null) {
            sb.append("matchingConcept:\n");
            for (Concept s : matchingConcept) {
                sb.append(s).append('\n');
            }
        }
        if (operators != null) {
            sb.append("operators:\n");
            for (String s : operators) {
                sb.append(s).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Result) {
            final Result that = (Result) obj;
            return Objects.equals(this.matchingConcept,  that.matchingConcept) &&
                   Objects.equals(this.matchingLanguage, that.matchingLanguage) &&
                   Objects.equals(this.operators,        that.operators);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.matchingLanguage != null ? this.matchingLanguage.hashCode() : 0);
        hash = 59 * hash + (this.matchingConcept != null ? this.matchingConcept.hashCode() : 0);
        hash = 59 * hash + (this.operators != null ? this.operators.hashCode() : 0);
        return hash;
    }
}
