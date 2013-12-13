/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
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
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.xml.IdentifierMap;
import org.apache.sis.xml.IdentifierSpace;
import org.opengis.metadata.Identifier;
import org.apache.sis.xml.IdentifiedObject;


/**
 * <p>Java class for AbstractGMLType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AbstractGMLType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/gml/3.2}StandardObjectProperties"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.opengis.net/gml/3.2}id use="required""/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractGMLType", propOrder = {
    "metaDataProperty",
    "description",
    "descriptionReference",
    "identifier",
    "name"
})
@XmlSeeAlso({
    BagType.class,
    ArrayType.class,
    AbstractTimeSliceType.class,
    AbstractTimeObjectType.class,
    CompositeValueType.class,
    AbstractGeometryType.class,
    AbstractTopologyType.class,
    AbstractFeatureType.class,
    DefinitionBaseType.class
})
public abstract class AbstractGMLType extends AbstractMetadata implements AbstractGML, Serializable, Entry, IdentifiedObject {

    private List<MetaDataPropertyType> metaDataProperty;
    private StringOrRefType description;
    private ReferenceType descriptionReference;
    private CodeWithAuthorityType identifier;
    private List<CodeType> name;
    @XmlAttribute(namespace = "http://www.opengis.net/gml/3.2", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;

    /**
     * All identifiers associated with this metadata, or {@code null} if none.
     * This field is initialized to a non-null value when first needed.
     */
    @XmlTransient
    protected Collection<Identifier> identifiers;

    /**
     *  Empty constructor used by JAXB.
     */
    protected AbstractGMLType() {}

    /**
     *  Simple super constructor to initialize the entry name.
     */
    public AbstractGMLType(final String id) {
        setId(id);
    }

    public AbstractGMLType(final AbstractGML that) {
        if (that != null) {
            if (that.getDescription() != null) {
                this.description = new StringOrRefType(that.getDescription());
            }
            if (that.getDescriptionReference() != null) {
                this.descriptionReference = new ReferenceType(that.getDescriptionReference());
            }
            setId(that.getId());
            if (that.getName() != null) {
                this.name = new ArrayList<>();
                this.name.add(new CodeType(that.getName()));
            }
            if (that instanceof AbstractGMLType) {
                final AbstractGMLType thatGML = (AbstractGMLType) that;
                if (thatGML.identifier != null) {
                    this.identifier = new CodeWithAuthorityType(thatGML.identifier);
                }
                if (thatGML.metaDataProperty != null) {
                    this.metaDataProperty = new ArrayList<>();
                    for (MetaDataPropertyType m : thatGML.metaDataProperty) {
                        this.metaDataProperty.add(new MetaDataPropertyType(m));
                    }
                }
            }
        }
    }

    public AbstractGMLType(final String id, final String name, final String description, final ReferenceType descriptionReference) {
        setId(id);
        if (name != null) {
            this.name = new ArrayList<>();
            this.name.add(new CodeType(name));
        }
        if (description != null) {
            this.description = new StringOrRefType(description);
        }
        this.descriptionReference = descriptionReference;
    }


    @Override
    public MetadataStandard getStandard() {
        return MetadataStandard.ISO_19111;
    }

    /**
     * Gets the value of the metaDataProperty property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link MetaDataPropertyType }
     */
    public List<MetaDataPropertyType> getMetaDataProperty() {
        if (metaDataProperty == null) {
            metaDataProperty = new ArrayList<>();
        }
        return this.metaDataProperty;
    }

    /**
     * Gets the value of the description property.
     *
     * @return
     *     possible object is
     *     {@link StringOrRefType }
     *
     */
    @Override
    public String getDescription() {
        if (description != null) {
            return description.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value
     *     allowed object is
     *     {@link StringOrRefType }
     *
     */
    public void setDescription(final StringOrRefType value) {
        this.description = value;
    }

    @Override
    public void setDescription(final String value) {
        this.description = new StringOrRefType(value);
    }

    /**
     * Gets the value of the descriptionReference property.
     *
     * @return
     *     possible object is
     *     {@link ReferenceType }
     *
     */
    @Override
    public ReferenceType getDescriptionReference() {
        return descriptionReference;
    }

    /**
     * Sets the value of the descriptionReference property.
     *
     * @param value
     *     allowed object is
     *     {@link ReferenceType }
     *
     */
    public void setDescriptionReference(ReferenceType value) {
        this.descriptionReference = value;
    }

    /**
     * Gets the value of the identifier property.
     *
     * @return
     *     possible object is
     *     {@link CodeWithAuthorityType }
     *
     */
    @Override
    public String getIdentifier() {
        if (id == null) {
            id = getIdentifierMap().get(IdentifierSpace.ID);
        }
        return id;
    }

    public CodeWithAuthorityType getFullIdentifier() {
        return identifier;
    }

    /**
     * Sets the value of the identifier property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeWithAuthorityType }
     *
     */
    public void setIdentifier(CodeWithAuthorityType value) {
        this.identifier = value;
    }

    /**
     * Gets the value of the name property.
     *
     */
    public List<CodeType> getNames() {
        if (name == null) {
            name = new ArrayList<>();
        }
        return this.name;
    }

    @Override
    public String getName() {
        if (name != null && !name.isEmpty()) {
            return name.get(0).getValue();
        }
        return null;
    }

    /**
     *
     * @param name
     */
    @Override
    public void setName(final String name) {
        if (this.name == null) {
            this.name = new ArrayList<>();
        }
        this.name.add(0, new CodeType(name));
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

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    @Override
    public void setId(String value) {
        if (value != null) {
            getIdentifierMap().put(IdentifierSpace.ID, value);
        }
        this.id = value;
    }

    @Override
    public org.geotoolkit.gml.xml.v311.CodeType getParameterName() {
        return null; // not implemented in 3.2.1
    }

    @Override
    public boolean equals(final Object obj, final ComparisonMode mode) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof AbstractGMLType) {
            final AbstractGMLType that = (AbstractGMLType) obj;
            return Objects.equals(this.description,          that.description)          &&
                   Objects.equals(this.descriptionReference, that.descriptionReference) &&
                   Objects.equals(this.id,                   that.id)                   &&
                   Objects.equals(this.identifier,           that.identifier)           &&
                   Objects.equals(this.metaDataProperty,     that.metaDataProperty)     &&
                   Objects.equals(this.getName(),            that.getName());
        }
        return false;
    }

    @Override
    public Collection<Identifier> getIdentifiers() {
        if (identifiers == null) {
            identifiers = new ArrayList<>();
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
            identifiers = new ArrayList<Identifier>();
        }
        /*
         * We do not cache (for now) the IdentifierMap because it is cheap to create, and if were
         * caching it we would need anyway to check if 'identifiers' still references the same list.
         */
        return new IdentifierMapWithSpecialCases(identifiers);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 67 * hash + (this.descriptionReference != null ? this.descriptionReference.hashCode() : 0);
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 67 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
        hash = 67 * hash + (this.metaDataProperty != null ? this.metaDataProperty.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append(']').append('\n');
        if (id != null) {
            sb.append("id:").append(id).append('\n');
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
        if (identifier != null) {
            sb.append("identifier:").append(identifier).append('\n');
        }
        if (metaDataProperty != null) {
            sb.append("metaDataProperty:\n");
            for (MetaDataPropertyType process : metaDataProperty) {
                sb.append(process).append('\n');
            }
        }
        return sb.toString();
    }
}
