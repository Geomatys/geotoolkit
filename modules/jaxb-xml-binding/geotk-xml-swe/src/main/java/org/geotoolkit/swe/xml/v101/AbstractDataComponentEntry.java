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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.swe.xml.AbstractDataComponent;
/**
 *
 * @version $Id:
 * @author Guilhem Legal
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
    
    @XmlTransient //@XmlAttribute
    private boolean fixed;
    
    /**
     * definition of the record.
     */
    @XmlAttribute
    private String definition;
    
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
        this.definition = definition;
    }
    
    /**
     * a simple constructor used by the sub classes to initialize l'Entry.
     */
    public AbstractDataComponentEntry(String id, String definition, boolean fixed) {
        this.id         = id;
        this.definition = definition;
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
    public String getDefinition() {
        return definition;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isFixed() {
        return fixed;
    }

    /**
     * Return the numeric code identifiyng this entry.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
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
    public String toString() {
        StringBuilder s = new StringBuilder('[').append(this.getClass().getName()).append("]\n");
        if (id != null) {
            s.append("id =").append(id);
        }
        if (definition != null)
            s.append(" definition = ").append(definition);
        
        s.append(" fixed = ").append(fixed).append('\n');
        return s.toString();
    }
    
}
