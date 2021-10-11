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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.PhenomenonProperty;


/**
 * <p>Java class for PhenomenonPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PhenomenonPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}Phenomenon"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PhenomenonPropertyType", propOrder = {
   "phenomenon",
   "compoundPhenomenon",
   "compositePhenomenon"
})
public class PhenomenonPropertyType implements PhenomenonProperty {

    @XmlElement(name = "Phenomenon")
    private PhenomenonType phenomenon;
    @XmlElement(name = "CompoundPhenomenon")
    private CompoundPhenomenonType compoundPhenomenon;
    @XmlElement(name = "CompositePhenomenon")
    private CompositePhenomenonType compositePhenomenon;

    /**
     * Allow to record the pehnomenon when its in href mode
     */
    @XmlTransient
    PhenomenonType hiddenPhenomenon;

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
    PhenomenonPropertyType() {

    }

    /**
     * An empty constructor used by JAXB.
     */
    public PhenomenonPropertyType(final String href) {
        this.href = href;
    }

    /**
     * Build a new Phenomenon Property.
     */
    public PhenomenonPropertyType(final PhenomenonType observedProperty) {

        if (observedProperty instanceof CompositePhenomenonType) {
            this.compositePhenomenon       = (CompositePhenomenonType)observedProperty;
        } else if (observedProperty instanceof CompoundPhenomenonType) {
            this.compoundPhenomenon        = (CompoundPhenomenonType)observedProperty;
        } else if (observedProperty instanceof PhenomenonType) {
            this.phenomenon    =  observedProperty;
        } else {
            throw new IllegalArgumentException("only phenomenonType, CompositePhenomenonType and compoundPhenomenonType are allowed was:" + observedProperty);
        }
    }

    /**
     * Set the phenomenon into href mode.
     */
    @Override
    public void setToHref() {
        PhenomenonType pheno = getPhenomenon();
        if (pheno != null) {
            if (pheno.getName() != null) {
                this.href = pheno.getName().getCode();
            }
            hiddenPhenomenon = pheno;
            setPhenomenon(null);
        }
    }

    /**
     * Gets the value of the phenomenon property.
     */
    @Override
    public PhenomenonType getPhenomenon() {
        if (phenomenon != null) {
            return phenomenon;
        } else if (compositePhenomenon != null) {
            return compositePhenomenon;
        } else if (compoundPhenomenon != null) {
            return compoundPhenomenon;
        } else if (hiddenPhenomenon != null) {
            return hiddenPhenomenon;
        }
            return null;
    }

    public void setPhenomenon(final PhenomenonType pheno) {

        if (pheno instanceof CompositePhenomenonType) {
            this.compositePhenomenon   = (CompositePhenomenonType)pheno;
            this.phenomenon            = null;
            this.compoundPhenomenon    = null;
        } else if (pheno instanceof CompoundPhenomenonType) {
            this.compoundPhenomenon    = (CompoundPhenomenonType)pheno;
            this.phenomenon            = null;
            this.compositePhenomenon   = null;

        } else if (pheno instanceof PhenomenonType) {
            this.phenomenon           =  pheno;
            this.compositePhenomenon   = null;
            this.compoundPhenomenon    = null;
        } else {
            this.phenomenon           =  null;
            this.compositePhenomenon   = null;
            this.compoundPhenomenon    = null;
        }
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
        return type;
    }

    /**
     * Gets the value of the href property.
     */
    @Override
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
        if (object instanceof PhenomenonPropertyType) {
            final PhenomenonPropertyType that = (PhenomenonPropertyType) object;

            return Objects.equals(this.hiddenPhenomenon,    that.hiddenPhenomenon)    &&
                   Objects.equals(this.phenomenon,          that.phenomenon)          &&
                   Objects.equals(this.compoundPhenomenon,  that.compoundPhenomenon)  &&
                   Objects.equals(this.compositePhenomenon, that.compositePhenomenon) &&
                   Objects.equals(this.actuate,             that.actuate)             &&
                   Objects.equals(this.arcrole,             that.arcrole)             &&
                   Objects.equals(this.type,                that.type)                &&
                   Objects.equals(this.href,                that.href)                &&
                   Objects.equals(this.remoteSchema,        that.remoteSchema)        &&
                   Objects.equals(this.show,                that.show)                &&
                   Objects.equals(this.role,                that.role)                &&
                   Objects.equals(this.title,               that.title);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.phenomenon != null ? this.phenomenon.hashCode() : 0);
        hash = 97 * hash + (this.compoundPhenomenon != null ? this.compoundPhenomenon.hashCode() : 0);
        hash = 97 * hash + (this.compositePhenomenon != null ? this.compositePhenomenon.hashCode() : 0);
        hash = 97 * hash + (this.hiddenPhenomenon != null ? this.hiddenPhenomenon.hashCode() : 0);
        hash = 97 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 97 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 97 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 97 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 97 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 97 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 97 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 97 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        return hash;
    }

    /**
     * Retourne une representation de l'objet.
     */

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[PhenomenonPropertyType]");
        if (phenomenon != null)
            s.append(phenomenon).append('\n');
        if (compositePhenomenon != null)
            s.append(compositePhenomenon).append('\n');
        if (compoundPhenomenon != null)
            s.append(compoundPhenomenon).append('\n');

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
