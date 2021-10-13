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
package org.geotoolkit.swe.xml.v100;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.internal.simple.SimpleCitation;
import org.apache.sis.metadata.MetadataStandard;
import org.geotoolkit.gml.xml.v311.AbstractGMLType;
import org.geotoolkit.swe.xml.AbstractDataComponent;
import org.apache.sis.util.ComparisonMode;


/**
 * Base type for all data components.
 *          This is implemented as an XML Schema complexType because it includes both element and attribute content.
 *
 * <p>Java class for AbstractDataComponentType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AbstractDataComponentType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGMLType">
 *       &lt;attribute name="fixed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="definition" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractDataComponentType")
@XmlSeeAlso({
    AbstractDataArrayType.class,
    AbstractDataRecordType.class,
    Category.class,
    Text.class,
    BooleanType.class,
    QuantityType.class,
    TimeType.class,
    Count.class,
    ObservableProperty.class,
    TimeRange.class,
    QuantityRange.class,
    CountRange.class
})
public abstract class AbstractDataComponentType extends AbstractGMLType implements AbstractDataComponent {

    @XmlAttribute
    private java.lang.Boolean fixed;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String definition;

    public AbstractDataComponentType() {

    }

    public AbstractDataComponentType(final String definition) {
        this.definition = definition;
    }

    public AbstractDataComponentType(final AbstractDataComponent component) {
        super(component);
        if (component != null) {
            this.definition = component.getDefinition();
            this.fixed      = component.isFixed();
        }
    }

    public AbstractDataComponentType(final String id, final String definition, final Boolean fixed) {
        super(id);
        if (definition != null) {
            this.definition = definition;
        }
        this.fixed      = fixed;
    }

    @Override
    public MetadataStandard getStandard() {
        return  new MetadataStandard(new SimpleCitation("SWE"), Package.getPackage("org.geotoolkit.swe.xml"));
    }

    /**
     * Gets the value of the fixed property.
     */
    @Override
    public Boolean isFixed() {
        return fixed;
    }

    /**
     * Sets the value of the fixed property.
     */
    public void setFixed(final java.lang.Boolean value) {
        this.fixed = value;
    }

    /**
     * Gets the value of the definition property.
     */
    @Override
    public String getDefinition() {
        return definition;
    }

    /**
     * Sets the value of the definition property.
      */
    public void setDefinition(final String value) {
        this.definition = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof AbstractDataComponentType && super.equals(object, mode)) {
            final AbstractDataComponentType that = (AbstractDataComponentType) object;

            return Objects.equals(this.definition, that.definition) &&
                   Objects.equals(this.fixed,      that.fixed);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.fixed != null ? this.fixed.hashCode() : 0);
        hash = 29 * hash + (this.definition != null ? this.definition.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (fixed != null) {
            s.append("fixed = ").append(fixed).append('\n');
        }
        if (definition != null) {
            s.append(" definition = ").append(definition).append('\n');
        }
        return s.toString();
    }

}
