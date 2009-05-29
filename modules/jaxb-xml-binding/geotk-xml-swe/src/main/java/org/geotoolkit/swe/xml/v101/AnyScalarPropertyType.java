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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AnyScalar;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for componentPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="componentPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}component"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnyScalarPropertyType", propOrder = {
    "abstractDataComponent",
    "time",
    "_boolean",
    "quantity",
    "text",
    "name"
})
public class AnyScalarPropertyType implements AnyScalar {

    @XmlTransient
    private String idDataRecord;
    
    @XmlAttribute
    private String name;
    
    @XmlElement(name = "AbstractDataComponent")
    protected AbstractDataComponentEntry abstractDataComponent;
    @XmlElement(name = "Time")
    protected TimeType time;
    @XmlElement(name = "Boolean")
    protected BooleanType _boolean;
    @XmlElement(name = "Quantity")
    protected QuantityType quantity;
    @XmlElement(name = "Text")
    protected Text text;
    
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

        
    public AnyScalarPropertyType() {

    }

    public AnyScalarPropertyType(String name, QuantityType quantity) {
        this.name     = name;
        this.quantity = quantity;
    }
    
    /**
     * Build a new component Property.
     */
    public AnyScalarPropertyType(String idDataRecord, String name, AbstractDataComponentEntry component) {
        this.name         = name;
        this.idDataRecord = idDataRecord;
        if (component instanceof TimeType) {
            this.time = (TimeType)component;
        } else if (component instanceof QuantityType) {
            this.quantity = (QuantityType)component;
        } else if (component instanceof BooleanType) {
            this._boolean = (BooleanType)component;
        } else if (component instanceof Text) {
            this.text = (Text)component;
        } else {
            abstractDataComponent = component;
        }
    }

    public AnyScalarPropertyType(String name, Text text) {
        this.name  = name;
        this.text  = text;
    }
    
    /**
     * surcharge le getName() de Entry
     */
    public String getName() {
        return this.name;
    }
    /** 
     * retourne l'identifiant du data record qui contient ce champ.
     */
    public String getIdDataRecord() {
        return idDataRecord;
    }
    
    /**
     * Gets the value of the phenomenon property.
     */
    public AbstractDataComponentEntry getComponent() {
        if (abstractDataComponent != null) {
            return abstractDataComponent;
        } else if (time != null){
            return time;
        } else if (_boolean != null){
            return _boolean;
        } else if (quantity != null){
            return quantity;
        } return null;
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
        if (object instanceof AnyScalarPropertyType) {
            final AnyScalarPropertyType that = (AnyScalarPropertyType) object;

            return Utilities.equals(this._boolean,           that._boolean)         &&
                   Utilities.equals(this.abstractDataComponent, that.abstractDataComponent)          &&
                   Utilities.equals(this.quantity,           that.quantity)         &&
                   Utilities.equals(this.time,               that.time)             &&
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
        hash = 47 * hash + (this.abstractDataComponent != null ? this.abstractDataComponent.hashCode() : 0);
        hash = 47 * hash + (this._boolean != null ? this._boolean.hashCode() : 0);
        hash = 47 * hash + (this.quantity != null ? this.quantity.hashCode() : 0);
        hash = 47 * hash + (this.time != null ? this.time.hashCode() : 0);
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
        if (abstractDataComponent != null)
            s.append(abstractDataComponent).append('\n');
        if (_boolean != null)
            s.append(_boolean).append('\n');
        if (quantity != null)
            s.append(quantity).append('\n');
        if (time != null)
            s.append(time).append('\n');
        
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
