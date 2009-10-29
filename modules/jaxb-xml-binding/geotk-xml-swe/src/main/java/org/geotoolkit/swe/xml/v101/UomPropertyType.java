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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.BaseUnitType;
import org.geotoolkit.gml.xml.v311.UnitDefinitionType;
import org.geotoolkit.swe.xml.UomProperty;
import org.geotoolkit.util.Utilities;

/**
 * <p>Java class for UomPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UomPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/gml}UnitDefinition"/>
 *         &lt;element ref="{http://www.opengis.net/gml}BaseUnit"/>
 *       &lt;/choice>
 *       &lt;attribute name="code" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nilReason">
 *         &lt;simpleType>
 *           &lt;list itemType="{http://www.w3.org/2001/XMLSchema}string" />
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute ref="{http://www.opengis.net/gml}remoteSchema"/>
 *       &lt;attribute ref="{http://www.w3.org/1999/xlink}actuate"/>
 *       &lt;attribute ref="{http://www.w3.org/1999/xlink}arcrole"/>
 *       &lt;attribute ref="{http://www.w3.org/1999/xlink}href"/>
 *       &lt;attribute ref="{http://www.w3.org/1999/xlink}role"/>
 *       &lt;attribute ref="{http://www.w3.org/1999/xlink}show"/>
 *       &lt;attribute ref="{http://www.w3.org/1999/xlink}title"/>
 *       &lt;attribute ref="{http://www.w3.org/1999/xlink}type"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UomPropertyType", propOrder = {
    "unitDefinition",
    "baseUnit"
})
public class UomPropertyType implements UomProperty {

    @XmlElement(name = "UnitDefinition", namespace = "http://www.opengis.net/gml")
    private UnitDefinitionType unitDefinition;
    @XmlElement(name = "BaseUnit", namespace = "http://www.opengis.net/gml")
    private BaseUnitType baseUnit;
    @XmlAttribute
    private String code;
    @XmlAttribute
    private List<String> nilReason;
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    private String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String actuate;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;

    
    public UomPropertyType() {}
    
    public UomPropertyType(String code, String href) {
        this.code = code;
        this.href = href;
    }
    
    /**
     * Gets the value of the unitDefinition property.
     */
    public UnitDefinitionType getUnitDefinition() {
        return unitDefinition;
    }

    /**
     * Gets the value of the baseUnit property.
     */
    public BaseUnitType getBaseUnit() {
        return baseUnit;
    }

    /**
     * Gets the value of the code property.
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the value of the nilReason property.
     * 
     */
    public List<String> getNilReason() {
        if (nilReason == null) {
             nilReason = new ArrayList<String>();
        }
        return nilReason;
    }

    /**
     * Gets the value of the remoteSchema property.
     */
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Gets the value of the actuate property.
     */
    public String getActuate() {
        return actuate;
    }

    /**
     * Gets the value of the arcrole property.
     */
    public String getArcrole() {
        return arcrole;
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
     * Gets the value of the show property.
     */
    public String getShow() {
        return show;
    }

    /**
     * Gets the value of the title property.
     */
    public String getTitle() {
        return title;
    }


    /**
     * Gets the value of the type property.
     */
    public String getType() {
        return type;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof UomPropertyType) {
            final UomPropertyType that = (UomPropertyType) object;
            return Utilities.equals(this.actuate,        that.actuate)        &&
                   Utilities.equals(this.arcrole,        that.arcrole)        &&
                   Utilities.equals(this.baseUnit,       that.baseUnit)       &&
                   Utilities.equals(this.code,           that.code)           &&
                   Utilities.equals(this.href,           that.href)           &&
                   Utilities.equals(this.nilReason,      that.nilReason)      &&
                   Utilities.equals(this.remoteSchema,   that.remoteSchema)   &&
                   Utilities.equals(this.role,           that.role)           &&
                   Utilities.equals(this.show,           that.show)           &&
                   Utilities.equals(this.title,          that.title)          &&
                   Utilities.equals(this.type,           that.type)           &&
                   Utilities.equals(this.unitDefinition, that.unitDefinition);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.unitDefinition != null ? this.unitDefinition.hashCode() : 0);
        hash = 41 * hash + (this.baseUnit != null ? this.baseUnit.hashCode() : 0);
        hash = 41 * hash + (this.code != null ? this.code.hashCode() : 0);
        hash = 41 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
        hash = 41 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 41 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 41 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 41 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 41 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 41 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 41 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 41 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if(actuate != null && !actuate.equals("")) 
            s.append("actuate:").append(actuate).append('\n');
        if(arcrole != null && !arcrole.equals("")) 
            s.append("arcrole:").append(arcrole).append('\n');
        if(baseUnit != null) 
            s.append("baseUnit:").append(baseUnit.toString()).append('\n');
        if(code != null && !code.equals("")) 
            s.append("code:").append(code).append('\n');
        if(href != null && !href.equals("")) 
            s.append("href:").append(href).append('\n');
        if (nilReason != null) {
            s.append("nilReason:").append('\n');
            for (String ss:nilReason) {
                s.append(ss).append('\n');
            }
        }
        if(remoteSchema != null && !remoteSchema.equals("")) 
            s.append("remoteSchema:").append(remoteSchema).append('\n');
        if(role != null && !role.equals("")) 
            s.append("role:").append(role).append('\n');
        if(show != null && !show.equals("")) 
            s.append("show:").append(show).append('\n');
        if(title != null && !title.equals("")) 
            s.append("title:").append(title).append('\n');
        if(type != null && !type.equals("")) 
            s.append("type:").append(type).append('\n');
        if(unitDefinition != null) 
            s.append("unitDefinition:").append(unitDefinition).append('\n');

        return s.toString();
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @param remoteSchema the remoteSchema to set
     */
    public void setRemoteSchema(String remoteSchema) {
        this.remoteSchema = remoteSchema;
    }

    /**
     * @param actuate the actuate to set
     */
    public void setActuate(String actuate) {
        this.actuate = actuate;
    }

    /**
     * @param arcrole the arcrole to set
     */
    public void setArcrole(String arcrole) {
        this.arcrole = arcrole;
    }

    /**
     * @param href the href to set
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @param show the show to set
     */
    public void setShow(String show) {
        this.show = show;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
