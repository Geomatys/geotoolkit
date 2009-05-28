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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.swe.xml.DataComponentProperty;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author Guilhem Legal (Geomatys).
 */
public class DataComponentPropertyType implements DataComponentProperty {

    @XmlElementRef(name = "AbstractDataRecord", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class)
    private JAXBElement<? extends AbstractDataRecordEntry> abstractDataRecord;
    
    @XmlTransient
    private JAXBElement<? extends AbstractDataRecordEntry> hiddenAbstractDataRecord;
    
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String name;
    
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
    private final static ObjectFactory sweFactory = new ObjectFactory();
    
    /**
     * An empty constructor used by JAXB
     */
    DataComponentPropertyType(){
        
    }
    
    /**
     * 
     */
    public DataComponentPropertyType(AbstractDataRecordEntry component, String name) {
        this.name = name;
        if (component instanceof SimpleDataRecordEntry) {
            this.abstractDataRecord = sweFactory.createSimpleDataRecord((SimpleDataRecordEntry)component);
        } else {
            throw new IllegalArgumentException("only SimpleDataRecord is allowed in dataComponentPropertyType");
        }
    }
    /**
     * Gets the value of the timeGeometricPrimitive property.
      */
    public AbstractDataRecordEntry getAbstractRecord() {
        if (abstractDataRecord != null) {
            return abstractDataRecord.getValue();
        } else if (hiddenAbstractDataRecord != null){
            return hiddenAbstractDataRecord.getValue();
        }
        return null;
    }

    public void setToHref() {
        if (abstractDataRecord != null) {
            this.href = abstractDataRecord.getValue().getId();
            hiddenAbstractDataRecord = abstractDataRecord;
            abstractDataRecord       = null;
        }
    }
    /**
     * 
     */
    public String getName(){
        return this.name;
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
        boolean time = false;
        final DataComponentPropertyType that = (DataComponentPropertyType) object;
        if (this.abstractDataRecord != null && that.abstractDataRecord != null) {
            time = Utilities.equals(this.abstractDataRecord.getValue(),that.abstractDataRecord.getValue());
        } else {
            time = (this.abstractDataRecord == null && that.abstractDataRecord == null);
        }
        
        return time                                                             &&
               Utilities.equals(this.actuate,            that.actuate)          &&
               Utilities.equals(this.arcrole,            that.arcrole)          &&  
               Utilities.equals(this.type,               that.type)             &&
               Utilities.equals(this.href,               that.href)             &&
               Utilities.equals(this.remoteSchema,       that.remoteSchema)     &&
               Utilities.equals(this.show,               that.show)             &&
               Utilities.equals(this.role,               that.role)             &&
               Utilities.equals(this.title,              that.title);
    }

    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.abstractDataRecord != null ? this.abstractDataRecord.hashCode() : 0);
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
        if (abstractDataRecord != null)
            s.append(abstractDataRecord.getValue().toString()).append('\n');
        
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

