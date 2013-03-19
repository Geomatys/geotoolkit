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
package org.geotoolkit.process.chain.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ChainElement extends Positionable {
    public static final ChainElement BEGIN = new ChainElement(Integer.MIN_VALUE, "chain", "begin", -1, -1);
    public static final ChainElement END = new ChainElement(Integer.MAX_VALUE, "chain", "end", -1, -1);

    @XmlAttribute(name="id")
    private Integer id;
    @XmlElement(name="authority")
    private String authority;
    @XmlElement(name="code")
    private String code;

    private ChainElement() {}

    public ChainElement(final ChainElement toCopy){
        super(toCopy);
        this.id        = toCopy.id;
        this.authority = toCopy.authority;
        this.code      = toCopy.code;
    }
    
    public ChainElement(final int id, final String authority, final String code) {
        this(id,authority,code,0,0);
    }
    
    public ChainElement(final int id, final String authority, final String code, final int x, final int y) {
        super(x,y);
        this.id        = id;
        this.authority = authority;
        this.code      = code;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(final String authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChainElement) { // no looking for position
            final ChainElement that = (ChainElement) obj;
            return Utilities.equals(this.id,        that.id)
                && Utilities.equals(this.authority, that.authority)
                && Utilities.equals(this.code,      that.code);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.authority != null ? this.authority.hashCode() : 0);
        hash = 37 * hash + (this.code != null ? this.code.hashCode() : 0);
        hash = 37 * hash + this.id;
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        sb.append("id:").append(id).append('\n');
        if (code != null) {
            sb.append("code:").append(code).append('\n');
        }
        if (authority != null) {
            sb.append("authority:").append(authority).append('\n');
        }
        return sb.toString();
    }
}