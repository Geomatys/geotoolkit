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
 * This class was created for the autocompletion textfield.
 *
 * @author Mehdi Sidhoum
 */
public class Word {

    /**
     * The label of this word.
     */
    private String label;

    /**
     * The thesaurus that contains this word.
     */
    private String thesaurus;

    /**
     * The URI identifier of this concept.
     */
    private String uriConcept;

    public Word() {

    }

    /**
     * Creates a new instance of Word
     */
    public Word(final String label, final String thesaurus, final String uriConcept) {
        this.label      = label;
        this.thesaurus  = thesaurus;
        this.uriConcept = uriConcept;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getThesaurus() {
        return thesaurus;
    }

    public void setThesaurus(final String thesaurus) {
        this.thesaurus = thesaurus;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Word) {
            final Word that = (Word) object;

            return Objects.equals(this.label,      that.label)     &&
                   Objects.equals(this.thesaurus,  that.thesaurus) &&
                   Objects.equals(this.getUriConcept(), that.getUriConcept());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.label      != null ? this.label.hashCode()      : 0);
        hash = 17 * hash + (this.thesaurus  != null ? this.thesaurus.hashCode()  : 0);
        hash = 17 * hash + (this.getUriConcept() != null ? this.getUriConcept().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString(){
        final StringBuilder s = new StringBuilder("[Word]\n");
        if (this.label != null) {
            s.append("label: ").append(label).append('\n');
        }
        if (thesaurus != null) {
             s.append("thesaurus:").append(thesaurus).append('\n');
        }
        if (getUriConcept() != null) {
             s.append("uri_concept:").append(getUriConcept()).append('\n');
        }
        return s.toString();
    }

    /**
     * @return the uriConcept
     */
    public String getUriConcept() {
        return uriConcept;
    }

    /**
     * @param uriConcept the uriConcept to set
     */
    public void setUriConcept(final String uriConcept) {
        this.uriConcept = uriConcept;
    }

}
