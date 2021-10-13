/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.ows.xml.v100;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.Sections;


/**
 * Unordered list of zero or more names of requested sections in complete service metadata document. Each Section value shall contain an allowed section name as specified by each OWS specification. See Sections parameter subclause for more information.
 *
 * <p>Java class for SectionsType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SectionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Section" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SectionsType", propOrder = {
    "section"
})
public class SectionsType implements Sections {

    @XmlElement(name = "Section")
    private List<String> section = new ArrayList<>();

    @XmlTransient
    private static final List<String> existingSections = new ArrayList<>(4);
    static {
        existingSections.add("ServiceIdentification");
        existingSections.add("ServiceProvider");
        existingSections.add("OperationsMetadata");
        existingSections.add("Filter_Capabilities");
        existingSections.add("FeatureTypeList");
        existingSections.add("All");
    }

    /**
     * Empty constructor used bye JAXB.
     */
    SectionsType(){
    }

    /**
     * Build a new list of Section.
     *
     * @param section one or more of "ServiceIdentification", "ServiceProvider", "OperationsMetadata", "Filter_Capabilities", "All".
     */
    public SectionsType(final List<String> section){
        this.section = section;
    }

    /**
     * Build a new list of Section.
     *
     * @param section one or more of "ServiceIdentification", "ServiceProvider", "OperationsMetadata", "Filter_Capabilities", "All".
     */
    public SectionsType(final String... section){
        this.section = new ArrayList<>();
        for (String s: section) {
            this.section.add(s);
        }

    }

    /**
     * return the list of section names (unmodifiable).
     */
    @Override
    public List<String> getSection() {
        if (section == null){
            section = new ArrayList<>();
        }
       return section;
    }

    /**
     * Return true if the Sections contains the specified section.
     *
     * @param sectionName The name of the searched section.
     * @return true if the Section contains the specified section.
     */
    @Override
    public boolean containsSection(final String sectionName) {
        if (section != null) {
            for (String s : section) {
                if (s.equalsIgnoreCase(sectionName)) {
                    return true;
                }
            }
        }
        return false;
    }

     /**
     * Add a new section to the list.
     *
     * @param section a new section.
     */
    @Override
    public void add(final String section) {
        this.section.add(section);
    }

    /**
     * Return a List of all the existing sections.
     */
    public static List<String> getExistingSections() {
        return existingSections;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof SectionsType) {
            final SectionsType that = (SectionsType) object;
            return Objects.equals(this.section, that.section);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.section != null ? this.section.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Sections:").append('\n');
        for (String sec:section) {
            s.append(sec).append('\n');
        }
        return s.toString();
    }
}
