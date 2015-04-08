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
package org.geotoolkit.gml.xml.v311;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.sis.internal.jaxb.IdentifierMapWithSpecialCases;
import org.geotoolkit.gml.xml.AbstractGML;
import org.geotoolkit.internal.sql.table.Entry;
import org.apache.sis.metadata.AbstractMetadata;
import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.xml.IdentifiedObject;
import org.apache.sis.xml.IdentifierMap;
import org.apache.sis.xml.IdentifierSpace;
import org.opengis.metadata.Identifier;
import org.geotoolkit.gml.GMLStandard;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;


/**
 * All complexContent GML elements are directly or indirectly derived from this abstract supertype
 * 	to establish a hierarchy of GML types that may be distinguished from other XML types by their ancestry.
 * 	Elements in this hierarchy may have an ID and are thus referenceable.
 *
 * <p>Java class for AbstractGMLType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AbstractGMLType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/gml}StandardObjectProperties"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.opengis.net/gml}id"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractGMLType", propOrder = {
    //"metaDataProperty",
    "description",
    "descriptionReference",
    "name",
    "parameterName"
})
@XmlSeeAlso({
    ArrayType.class,
    AbstractTimeObjectType.class,
    DefinitionType.class,
    BagType.class,
    AbstractGeometryType.class,
    AbstractFeatureType.class
})
public abstract class AbstractGMLType extends AbstractMetadata implements AbstractGML, Serializable, Entry, IdentifiedObject  {
    /**
     * The value to be returned by {@link #getStandard()}.
     */
    public static final GMLStandard STANDARD = new GMLStandard("GML 3.1.1", AbstractGML.class.getPackage(), "v311", MetadataStandard.ISO_19111);

    //protected List<MetaDataPropertyType> metaDataProperty;
    private String description;
    private ReferenceType descriptionReference;
    private String name;
    @XmlAttribute(namespace = "http://www.opengis.net/gml", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;
    private CodeType parameterName;

    /**
     * All identifiers associated with this metadata, or {@code null} if none.
     * This field is initialized to a non-null value when first needed.
     */
    @XmlTransient
    protected Set<Identifier> identifiers;

    /**
     *  Empty constructor used by JAXB.
     */
    protected AbstractGMLType() {}

    /**
     *  clone constructor.
     */
    public AbstractGMLType(final AbstractGML a) {
        if (a != null) {
            this.description = a.getDescription();
            if (a.getDescriptionReference() != null) {
                this.descriptionReference = new ReferenceType(a.getDescriptionReference());
            }
            setId(a.getId());
            if (a.getName() != null) {
                this.name = a.getName().getCode();
            }
            if (a.getParameterName() != null){
                this.parameterName = new CodeType(a.getParameterName().getValue(), a.getParameterName().getCodeSpace());
            }
        }
    }

    /**
     *  Simple super constructor to initialize the entry name.
     */
    public AbstractGMLType(final String id) {
        setId(id);
    }

    public AbstractGMLType(final String id, final String name, final String description, final ReferenceType descriptionReference) {
        setId(id);
        this.name = name;
        this.description = description;
        this.descriptionReference = descriptionReference;
    }

    @Override
    public MetadataStandard getStandard() {
        return STANDARD;
    }

    /**
     * Gets the value of the description property.
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     */
    @Override
    public void setDescription(final String value) {
        this.description = value;
    }

    /**
     * Gets the value of the description reference property.
     */
    @Override
    public ReferenceType getDescriptionReference() {
        return descriptionReference;
    }

    /**
     * Sets the value of the description reference property.
     */
    public void setDescription(final ReferenceType value) {
        this.descriptionReference = value;
    }

    /**
     *
     */
    @Override
    public Identifier getName() {
        if (name != null) {
            return new DefaultIdentifier(name);
        }
        return null;
    }

    /**
     *
     */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getId() {
        if (id == null) {
            id = getIdentifierMap().get(IdentifierSpace.ID);
        }
        return id;
    }

    @Override
    public String getIdentifier() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setId(final String value) {
        if (value != null) {
            getIdentifierMap().put(IdentifierSpace.ID, value);
        } else {
            getIdentifierMap().remove(IdentifierSpace.ID);
        }
        this.id = value;
    }

    /**
     * @return the parameterName
     */
    @Override
    public CodeType getParameterName() {
        return parameterName;
    }

    /**
     * @param parameterName the parameterName to set
     */
    public void setParameterName(final CodeType parameterName) {
        this.parameterName = parameterName;
    }

    @Override
    public Set<Identifier> getIdentifiers() {
        if (identifiers == null) {
            identifiers = new HashSet<>();
        }
        return identifiers;
    }

    @Override
    public IdentifierMap getIdentifierMap() {
        /*
         * Do not invoke getIdentifiers(), because some subclasses like DefaultCitation and
         * DefaultObjective override getIdentifiers() in order to return a filtered list.
         */
        if (identifiers == null) {
            identifiers = new HashSet<>();
        }
        /*
         * We do not cache (for now) the IdentifierMap because it is cheap to create, and if were
         * caching it we would need anyway to check if 'identifiers' still references the same list.
         */
        return new IdentifierMapWithSpecialCases(identifiers);
    }
    
    public Collection<GenericName> getAlias() {
        return null;
    }

    public InternationalString getRemarks() {
        if (description != null) {
            return new SimpleInternationalString(description);
        }
        return null;
    }

    public String toWKT() throws UnsupportedOperationException {
        return null;
    }

    @Override
    public void prune() {
        // do nothing TODO implements for each sub type
    }

    
    /**
     * @todo fix the id problem.
     */
    @Override
    public boolean equals(final Object obj, final ComparisonMode mode) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof AbstractGMLType) {
            final AbstractGMLType that = (AbstractGMLType) obj;
            return Objects.equals(this.description,          that.description)          &&
                   Objects.equals(this.descriptionReference, that.descriptionReference) &&
                   Objects.equals(this.getId(),              that.getId())              &&
                   Objects.equals(this.getName(),            that.getName())            &&
                   Objects.equals(this.parameterName,        that.parameterName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 67 * hash + (this.descriptionReference != null ? this.descriptionReference.hashCode() : 0);
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 67 * hash + (this.parameterName != null ? this.parameterName.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append(']').append('\n');
        if (id != null) {
            sb.append("id:").append(getId()).append('\n');
        }
        if (name != null) {
            sb.append("name:").append(name).append('\n');
        }
        if (description != null) {
            sb.append("description:").append(description).append('\n');
        }
        if (descriptionReference != null) {
            sb.append("description reference:").append(descriptionReference).append('\n');
        }
        return sb.toString();
    }
}
