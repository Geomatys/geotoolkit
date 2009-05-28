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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractEncodingProperty;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractEncodingPropertyType", propOrder = {
    "encoding"
})

public class AbstractEncodingPropertyType implements AbstractEncodingProperty {
    
    /**
     * Decribe the data encoding.
     */
    @XmlElementRef(name = "Encoding", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class)
    private JAXBElement<? extends AbstractEncodingEntry> encoding;
    
    @XmlTransient
    private JAXBElement<? extends AbstractEncodingEntry> hiddenEncoding;
    
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlSchemaType(name = "anyURI")
    private String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String actuate;

    @XmlTransient
    private static ObjectFactory sweFactory = new ObjectFactory();
    
    /**
     * An empty constructor used by JAXB
     */
    AbstractEncodingPropertyType() {
        
    }
    
    /**
     * Build a new Abstract encoding Property.
     */
    public AbstractEncodingPropertyType(AbstractEncodingEntry encoding) {
        
        if (encoding instanceof TextBlockEntry) {
            this.encoding = sweFactory.createTextBlock((TextBlockEntry)encoding);
        } else {
            throw new IllegalArgumentException("only TextBlock are allowed");
        }
    }
    
    /**
     * clone Abstract encoding Property.
     */
    public AbstractEncodingPropertyType(AbstractEncodingPropertyType clone) {
        
        this.actuate        = clone.actuate;
        this.arcrole        = clone.arcrole;
        if (clone.encoding != null) {
            if (clone.encoding.getValue() instanceof TextBlockEntry) {
                this.encoding = sweFactory.createTextBlock((TextBlockEntry)clone.encoding.getValue());
            } else {
                throw new IllegalArgumentException("only TextBlock are allowed");
            }
        }
        if (clone.hiddenEncoding != null) {
            if (clone.hiddenEncoding.getValue() instanceof TextBlockEntry) {
                this.hiddenEncoding = sweFactory.createTextBlock((TextBlockEntry)clone.hiddenEncoding.getValue());
            } else {
                throw new IllegalArgumentException("only TextBlock are allowed");
            }
        }
        this.href           = clone.href;
        this.remoteSchema   = clone.remoteSchema;
        this.role           = clone.role;
        this.show           = clone.show;
        this.title          = clone.title;
        this.type           = clone.type;
        
    }
    
    public void setToHref() {
        if (encoding != null) {
            this.href = encoding.getValue().getId();
            hiddenEncoding = encoding;
            encoding = null;
        }
    }
    
    /**
     * Gets the value of the encoding property.
     */
    public AbstractEncodingEntry getencoding() {
        if (encoding != null) {
            return encoding.getValue();
        } else if (hiddenEncoding != null) {
            return hiddenEncoding.getValue();
        }
        return null;
    }

    /**
     * Gets the value of the remoteSchema property.
     */
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Gets the value of the type property.
     */
    public String getType() {
        if (type == null) {
            return "simple";
        } else {
            return type;
        }
    }

    /**
     * Gets the value of the href property.
     */
    public String getHref() {
        return href;
    }

    /**
     * Gets the value of the role property.
     */
    public String getRole() {
        return role;
    }
    
    /**
     * Gets the value of the arcrole property.
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Gets the value of the title property.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the value of the show property.
     */
    public String getShow() {
        return show;
    }

    /**
     * Gets the value of the actuate property.
     */
    public String getActuate() {
        return actuate;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof AbstractEncodingPropertyType) {
            final AbstractEncodingPropertyType that = (AbstractEncodingPropertyType) object;
            boolean enc = false;
            if (this.encoding != null && that.encoding != null) {
                enc = Utilities.equals(this.encoding.getValue(), that.encoding.getValue());
                //System.out.println("encoding NOT NULL :" + pheno);
            } else {
                enc = (this.encoding == null && that.encoding == null);
                //System.out.println("encoding NULL :" + pheno);
            }

            boolean hiddenEnc = false;
            if (this.hiddenEncoding != null && that.hiddenEncoding != null) {
                hiddenEnc = Utilities.equals(this.hiddenEncoding.getValue(), that.hiddenEncoding.getValue());
                //System.out.println("feature NOT NULL :" + pheno);
            } else {
                hiddenEnc = (this.hiddenEncoding == null && that.hiddenEncoding == null);
                //System.out.println("feature NULL :" + pheno);
            }

            return enc                                                              &&
                   hiddenEnc                                                        &&
                   Utilities.equals(this.actuate,            that.actuate)          &&
                   Utilities.equals(this.arcrole,            that.arcrole)          &&
                   Utilities.equals(this.type,               that.type)             &&
                   Utilities.equals(this.href,               that.href)             &&
                   Utilities.equals(this.remoteSchema,       that.remoteSchema)     &&
                   Utilities.equals(this.show,               that.show)             &&
                   Utilities.equals(this.role,               that.role)             &&
                   Utilities.equals(this.title,              that.title);
        }
        return false;
    }

    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.encoding != null ? this.encoding.hashCode() : 0);
        hash = 47 * hash + (this.hiddenEncoding != null ? this.hiddenEncoding.hashCode() : 0);
        hash = 47 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 47 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 47 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 47 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 47 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 47 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 47 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 47 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    /**
     * Retourne une representation de l'objet.
     */
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (encoding != null)
            s.append(encoding.getValue().toString()).append('\n');
        
        if(actuate != null) {
            s.append("actuate=").append(actuate).append('\n');
        }
        if(arcrole != null) {
            s.append("arcrole=").append(arcrole).append('\n');
        }
        if(href != null) {
            s.append("href=").append(href).append('\n');
        }
        if(role != null) {
            s.append("role=").append(role).append('\n');
        }
        if(show != null) {
            s.append("show=").append(show).append('\n');
        }
        if(title != null) {
            s.append("title=").append(title).append('\n');
        }
        if(title != null) {
            s.append("title=").append(title).append('\n');
        }
        return s.toString();
    }
    
}
