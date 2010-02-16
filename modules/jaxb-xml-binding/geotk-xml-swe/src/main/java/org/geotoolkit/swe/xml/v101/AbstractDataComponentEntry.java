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
package org.geotoolkit.swe.xml.v101;

import java.net.URI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.CodeType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.swe.xml.AbstractDataComponent;
/**
 *
 * @version $Id:
 * @author Guilhem Legal
 * @module pending
 */
@XmlSeeAlso({AbstractDataRecordEntry.class, TimeType.class, BooleanType.class, QuantityType.class, Text.class})
@XmlType(name="AbstractDataComponent")
@XmlAccessorType(XmlAccessType.FIELD)
public class AbstractDataComponentEntry implements AbstractDataComponent {
    
    /**
     * The identifier of the component (override from abstractGML Type).
     */
    @XmlAttribute(namespace="http://www.opengis.net/gml")
    private String id;

    /**
     * (override from abstractGML Type).
     */
    @XmlElement(namespace="http://www.opengis.net/gml")
    private String description;

    /**
     * (override from abstractGML Type).
     */
    @XmlElement(namespace="http://www.opengis.net/gml")
    private CodeType parameterName;
    
    @XmlTransient //@XmlAttribute
    private boolean fixed;
    
    /**
     * definition of the record.
     */
    @XmlAttribute
    private URI definition;
    
    /**
     * Constructor used by jaxb.
     */
    AbstractDataComponentEntry() {}

    public AbstractDataComponentEntry(AbstractDataComponentEntry component) {
        this.definition = component.definition;
        this.fixed      = component.fixed;
        this.id         = component.id;
    }

    public AbstractDataComponentEntry(String definition) {
        if (definition != null) {
            this.definition = URI.create(definition);
        }
    }
    
    /**
     * a simple constructor used by the sub classes to initialize l'Entry.
     */
    public AbstractDataComponentEntry(String id, String definition, boolean fixed) {
        this.id         = id;
        if (definition != null) {
            this.definition = URI.create(definition);
        }
        this.fixed      = fixed;
    }
    
    /**
     * Return the identifier of this data record.
     */
    public String getId() {
        return id;
    }

    /**
     * Return the identifier of this data record.
     */
    public String getName() {
        return id;
    }

    /**
     * Set the identifier of this data record.
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public URI getDefinition() {
        return definition;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean isFixed() {
        return fixed;
    }

    
    
    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AbstractDataComponentEntry) {
            final AbstractDataComponentEntry that = (AbstractDataComponentEntry) object;
            return Utilities.equals(this.id,         that.id)         &&
                   Utilities.equals(this.definition, that.definition) &&
                   Utilities.equals(this.fixed,      that.fixed);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (id !=  null) {
            return id.hashCode();
        }

        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.fixed ? 1 : 0);
        hash = 97 * hash + (this.definition != null ? this.definition.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[").append(this.getClass().getName()).append("]\n");
        if (id != null) {
            s.append("id =").append(id);
        }
        if (definition != null)
            s.append(" definition = ").append(definition);
        
        s.append(" fixed = ").append(fixed).append('\n');
        return s.toString();
    }

    /**
     * @return the parameterName
     */
    public CodeType getParameterName() {
        return parameterName;
    }

    /**
     * @param parameterName the parameterName to set
     */
    public void setParameterName(CodeType parameterName) {
        this.parameterName = parameterName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param definition the definition to set
     */
    public void setDefinition(String definition) {
        this.definition = URI.create(definition);
    }
    
}
