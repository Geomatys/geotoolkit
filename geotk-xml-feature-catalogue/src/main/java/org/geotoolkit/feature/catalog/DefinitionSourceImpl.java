/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.feature.catalog;

import java.util.Iterator;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.sis.metadata.AbstractMetadata;
import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.util.ComparisonMode;
import org.opengis.feature.catalog.DefinitionSource;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.Responsibility;



/**
 * Class that specifies the source of a definition.
 *
 * <p>Java class for FC_DefinitionSource_Type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FC_DefinitionSource_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="source" type="{http://www.isotc211.org/2005/gmd}CI_Citation_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FC_DefinitionSource_Type", propOrder = {
    "source"
})
@XmlRootElement(name = "FC_DefinitionSource")
public class DefinitionSourceImpl extends AbstractMetadata implements DefinitionSource, Referenceable {

    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    @XmlElement(required = true)
    private Citation source;

    @XmlTransient
    private boolean isReference = false;

     /**
     * An empty constructor used by JAXB
     */
    public DefinitionSourceImpl() {

    }

    /**
     * Clone a DefinitionSource
     */
    public DefinitionSourceImpl(final DefinitionSource feature) {
        if (feature != null) {
            this.source = feature.getSource();
            this.id     = feature.getId();
        }

    }

     /**
     * build a new definition source
     */
    public DefinitionSourceImpl(final String id, final Citation source) {
        this.id     = id;
        this.source = source;
    }

    /**
     * Gets the value of the source property.
    */
    @Override
    public Citation getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     *
     */
    public void setSource(final Citation value) {
        this.source = value;
    }

    @Override
    public void setReference(final boolean isReference) {
        this.isReference = isReference;
    }

    @Override
    public boolean isReference() {
        return isReference;
    }

    @Override
    public DefinitionSourceImpl getReferenceableObject() {
        DefinitionSourceImpl result = new DefinitionSourceImpl(this);
        result.setReference(true);
        return result;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "[DefinitionSource]: id:" + id + '\n' + "source: " + source;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefinitionSourceImpl) {
            final DefinitionSourceImpl that = (DefinitionSourceImpl) object;



            //we redefine the Equals method of Citation
            boolean sourceb = Objects.equals(this.source.getCollectiveTitle(),         that.source.getCollectiveTitle())         &&
                              Objects.equals(this.source.getEdition(),                 that.source.getEdition())                 &&
                              Objects.equals(this.source.getEditionDate(),             that.source.getEditionDate())             &&
                              Objects.equals(this.source.getISBN(),                    that.source.getISBN())                    &&
                              Objects.equals(this.source.getISSN(),                    that.source.getISSN())                    &&
                              Objects.equals(this.source.getIdentifiers(),             that.source.getIdentifiers())             &&
                              Objects.equals(this.source.getOtherCitationDetails(),    that.source.getOtherCitationDetails())    &&
                              Objects.equals(this.source.getSeries(),                  that.source.getSeries())                  &&
                              Objects.equals(this.source.getTitle(),                   that.source.getTitle());
            if (Objects.equals(this.source.getDates().size(), that.source.getDates().size())) {
                Iterator<? extends CitationDate> thisIT = this.source.getDates().iterator();
                Iterator<? extends CitationDate> thatIT = that.source.getDates().iterator();

                while (thisIT.hasNext() && thatIT.hasNext()) {
                    if (!Objects.equals(thisIT.next(), thatIT.next())) {
                        sourceb = false;
                    }
                }
            } else {
                sourceb = false;
            }

            if (Objects.equals(this.source.getCitedResponsibleParties().size(), that.source.getCitedResponsibleParties().size())) {
                Iterator<? extends Responsibility> thisIT = this.source.getCitedResponsibleParties().iterator();
                Iterator<? extends Responsibility> thatIT = that.source.getCitedResponsibleParties().iterator();

                while (thisIT.hasNext() && thatIT.hasNext()) {
                    if (!Objects.equals(thisIT.next(), thatIT.next())) {
                        sourceb = false;
                    }
                }
            } else {
                sourceb = false;
            }
            return Objects.equals(this.id, that.id) &&
                   sourceb;

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (this.id     != null ? this.id.hashCode()     : 0);
        hash = 31 * hash + (this.source != null ? this.source.hashCode() : 0);
        return hash;
    }

    @Override
    public MetadataStandard getStandard() {
        return FeatureCatalogueStandard.ISO_19110;
    }

}
