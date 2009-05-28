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
package org.geotoolkit.gml.xml.v311modified;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Container for a feature - follow gml:AssociationType pattern.
 * 
 * <p>Java class for FeaturePropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FeaturePropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/gml}_Feature"/>
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
@XmlType(name = "FeaturePropertyType", propOrder = {
    "abstractFeature"
})
public class FeaturePropertyType {

    @XmlElementRef(name= "AbstractFeature", namespace="http://www.opengis.net/gml", type=JAXBElement.class)
    private JAXBElement<? extends AbstractFeatureEntry> abstractFeature;
    
    /**
     * Allow to record the feature when its in href mode
     */
    @XmlTransient
    AbstractFeatureEntry  hiddenFeature;
    
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

    /**
     * An empty constructor used by JAXB.
     */
    FeaturePropertyType() {
        
    }
    
    /**
     * Build a new feature.
     */
    public FeaturePropertyType(JAXBElement<? extends AbstractFeatureEntry> feature) {
        this.abstractFeature = feature;
    }
    
    /**
     * Set the feature into href mode.
     */
    public void setToHref() {
        if (abstractFeature != null && abstractFeature.getValue() != null) {
            this.href       = abstractFeature.getValue().getName();
            hiddenFeature   = abstractFeature.getValue();
            abstractFeature = null;
        }
    }
    
    /**
     * Gets the value of the feature property.
     */
    public AbstractFeatureEntry getFeature() {
        if (abstractFeature != null) {
            return abstractFeature.getValue();
        }  else if (hiddenFeature != null) {
            return hiddenFeature;     
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
        if (object instanceof FeaturePropertyType) {
            final FeaturePropertyType that = (FeaturePropertyType) object;

            boolean feat = false;
            if (this.abstractFeature == null && that.abstractFeature == null) {
                feat = true;
            } else if (this.abstractFeature != null && that.abstractFeature != null) {
                feat = Utilities.equals(this.abstractFeature.getValue(),    that.abstractFeature.getValue());
            }
            return feat                                                             &&
                   Utilities.equals(this.hiddenFeature,      that.hiddenFeature)    &&
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
        hash = 47 * hash + (this.abstractFeature != null ? this.abstractFeature.hashCode() : 0);
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
        StringBuilder s = new StringBuilder("[featurePropertyType]");
        if (abstractFeature != null && abstractFeature.getValue() != null)
            s.append(abstractFeature.getValue().toString()).append('\n');
        
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
