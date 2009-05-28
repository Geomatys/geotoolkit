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
package org.geotoolkit.ows.xml.v110;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.AbstractOnlineResourceType;
import org.geotoolkit.util.Utilities;


/**
 * For OWS use in the service metadata document, the CI_OnlineResource class was XML encoded as the attributeGroup "xlink:simpleLink", as used in GML. 
 * 
 * <p>Java class for OnlineResourceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OnlineResourceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{http://www.w3.org/1999/xlink}simpleLink"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OnlineResourceType")
@XmlSeeAlso({
    RequestMethodType.class
})
public class OnlineResourceType extends AbstractOnlineResourceType { 

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

    /**
     * Empty constructor used by JAXB.
     */
    OnlineResourceType(){
    }
    
    /**
     * Build a new Online resource with only the href property (most of the case).
     */
    public OnlineResourceType(String href){
        this.href = href;
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
     * Sets the value of the href property.
     */
    public void setHref(String href) {
        this.href = href;
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
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof OnlineResourceType) {
            final OnlineResourceType that = (OnlineResourceType) object;

            return Utilities.equals(this.actuate, that.actuate) &&
                   Utilities.equals(this.arcrole, that.arcrole) &&
                   Utilities.equals(this.href,    that.href)    &&
                   Utilities.equals(this.role,    that.role)    &&
                   Utilities.equals(this.show,    that.show)    &&
                   Utilities.equals(this.title,   that.title)   &&
                   Utilities.equals(this.type,    that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 37 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 37 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 37 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 37 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 37 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 37 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
    
     /**
     * Retourne une representation de l'objet.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
       
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
