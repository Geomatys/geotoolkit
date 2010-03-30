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
package org.geotoolkit.sml.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.sml.xml.AbstractRights;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}documentation"/>
 *       &lt;/sequence>
 *       &lt;attribute name="copyRights" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="intellectualPropertyRights" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="privacyAct" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute ref="{http://www.opengis.net/gml}id"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "documentation"
})
@XmlRootElement(name = "Rights")
public class Rights implements AbstractRights {

    @XmlElement(required = true)
    private Documentation documentation;
    @XmlAttribute
    private Boolean copyRights;
    @XmlAttribute
    private Boolean intellectualPropertyRights;
    @XmlAttribute
    private Boolean privacyAct;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;

    public Rights() {

    }

    public Rights(Boolean copyRights, Boolean privacyAct, Documentation docu) {
        this.copyRights = copyRights;
        this.privacyAct = privacyAct;
        this.documentation = docu;
    }

    /**
     * Gets the value of the documentation property.
     */
    public Documentation getDocumentation() {
        return documentation;
    }

    /**
     * Sets the value of the documentation property.
     */
    public void setDocumentation(Documentation value) {
        this.documentation = value;
    }

    /**
     * Sets the value of the documentation property.
     */
    public void setDocumentation(Document value) {
        this.documentation = new Documentation(value);
    }

    /**
     * Gets the value of the copyRights property.
     */
    public Boolean isCopyRights() {
        return copyRights;
    }

    /**
     * Sets the value of the copyRights property.
     */
    public void setCopyRights(Boolean value) {
        this.copyRights = value;
    }

    /**
     * Gets the value of the intellectualPropertyRights property.
     */
    public Boolean isIntellectualPropertyRights() {
        return intellectualPropertyRights;
    }

    /**
     * Sets the value of the intellectualPropertyRights property.
     */
    public void setIntellectualPropertyRights(Boolean value) {
        this.intellectualPropertyRights = value;
    }

    /**
     * Gets the value of the privacyAct property.
     */
    public Boolean isPrivacyAct() {
        return privacyAct;
    }

    /**
     * Sets the value of the privacyAct property.
     */
    public void setPrivacyAct(Boolean value) {
        this.privacyAct = value;
    }

    /**
     * Gets the value of the id property.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     */
    public void setId(String value) {
        this.id = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[Rights]").append("\n");
        if (id != null) {
            sb.append("id: ").append(id).append('\n');
        }
        if (copyRights != null) {
            sb.append("copyRights: ").append(copyRights).append('\n');
        }
        if (intellectualPropertyRights != null) {
            sb.append("intellectualPropertyRights: ").append(intellectualPropertyRights).append('\n');
        }
        if (privacyAct != null) {
            sb.append("privacyAct: ").append(privacyAct).append('\n');
        }
        if (documentation != null) {
            sb.append("documentation: ").append(documentation).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof Rights) {
            final Rights that = (Rights) object;
            return Utilities.equals(this.copyRights,    that.copyRights)       &&
                   Utilities.equals(this.documentation, that.documentation)       &&
                   Utilities.equals(this.id,            that.id)          &&
                   Utilities.equals(this.intellectualPropertyRights, that.intellectualPropertyRights)  &&
                   Utilities.equals(this.privacyAct,    that.privacyAct);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.documentation != null ? this.documentation.hashCode() : 0);
        hash = 13 * hash + (this.copyRights != null ? this.copyRights.hashCode() : 0);
        hash = 13 * hash + (this.intellectualPropertyRights != null ? this.intellectualPropertyRights.hashCode() : 0);
        hash = 13 * hash + (this.privacyAct != null ? this.privacyAct.hashCode() : 0);
        hash = 13 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

}
