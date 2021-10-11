/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.processing.chain.model;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.processing.chain.ConstantUtilities;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Constant extends Positionable {

    @XmlAttribute(name = "id")
    private int id;
    @XmlElement(name = "type")
    @XmlJavaTypeAdapter(value = Chain.ClassAdaptor.class)
    private Class type;
    @XmlElement(name = "value")
    private String value;

    private Constant() {

    }

    public Constant(final Constant toCopy) {
        super(toCopy);
        this.id    = toCopy.id;
        this.type  = toCopy.type;
        this.value = toCopy.value;
    }

    public Constant(final int id, final Class type, final String value) {
        this(id,type,value,0,0);
    }

    public Constant(final int id, final Class type, final Object value, final int x, final int y) {
        this(id,type, ConstantUtilities.valueToString(value),x,x);
    }

    public Constant(final int id, final Class type, final String value, final int x, final int y) {
        super(x,y);
        this.id    = id;
        this.type  = type;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Constant && super.equals(obj)) {
            final Constant that = (Constant) obj;
            return Objects.equals(this.id,    that.id)
                && Objects.equals(this.value, that.value)
                && Objects.equals(this.type,  that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 23;
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append("id:").append(id).append('\n');
        if (type != null) {
            sb.append("type:").append(type).append('\n');
        }
        if (value != null) {
            sb.append("value:").append(value).append('\n');
        }
        return sb.toString();
    }
}
