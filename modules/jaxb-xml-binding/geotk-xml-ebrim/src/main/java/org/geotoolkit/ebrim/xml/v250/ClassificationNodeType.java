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
package org.geotoolkit.ebrim.xml.v250;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * 
 * ClassificationNode is the mapping of the same named interface in ebRIM.
 * It extends RegistryObject.
 * 
 * ClassificationNode is used to submit a Classification tree to the Registry.
 * 
 * The parent attribute is the id to the parent node. code is an optional code value for a ClassificationNode
 * often defined by an external taxonomy (e.g. NAICS)
 * 			
 * 
 * <p>Java class for ClassificationNodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClassificationNodeType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryObjectType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ClassificationNode" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="parent" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="code" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}LongName" />
 *       &lt;attribute name="path" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClassificationNodeType", propOrder = {
    "classificationNode"
})
@XmlRootElement(name = "ClassificationNode")        
public class ClassificationNodeType extends RegistryObjectType {

    @XmlElement(name = "ClassificationNode")
    private List<ClassificationNodeType> classificationNode;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String parent;
    @XmlAttribute
    private String code;
    @XmlAttribute
    private String path;

    /**
     * Gets the value of the classificationNode property.
     */
    public List<ClassificationNodeType> getClassificationNode() {
        if (classificationNode == null) {
            classificationNode = new ArrayList<ClassificationNodeType>();
        }
        return this.classificationNode;
    }
    
    /**
     * Sets the value of the classificationNode property.
     */
    public void setClassificationNode(final ClassificationNodeType classificationNode) {
        if (this.classificationNode == null) {
            this.classificationNode = new ArrayList<ClassificationNodeType>();
        }
        this.classificationNode.add(classificationNode);
    }
    
    /**
     * Sets the value of the classificationNode property.
     */
    public void setClassificationNode(final List<ClassificationNodeType> classificationNode) {
        this.classificationNode = classificationNode;
    }

    /**
     * Gets the value of the parent property.
     */
    public String getParent() {
        return parent;
    }

    /**
     * Sets the value of the parent property.
     */
    public void setParent(final String value) {
        this.parent = value;
    }

    /**
     * Gets the value of the code property.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     */
    public void setCode(final String value) {
        this.code = value;
    }

    /**
     * Gets the value of the path property.
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     */
    public void setPath(final String value) {
        this.path = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        if (code != null) {
            sb.append("code:").append(code).append('\n');
        }
        if (parent != null) {
            sb.append("parent:").append(parent).append('\n');
        }
        if (path != null) {
            sb.append("path:").append(path).append('\n');
        }
        if (classificationNode != null) {
            sb.append("classificationNode:\n");
            for (ClassificationNodeType p : classificationNode) {
                sb.append(p).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ClassificationNodeType && super.equals(obj)) {
            final ClassificationNodeType that = (ClassificationNodeType) obj;
            return Utilities.equals(this.classificationNode, that.classificationNode) &&
                   Utilities.equals(this.code,               that.code) &&
                   Utilities.equals(this.parent,             that.parent) &&
                   Utilities.equals(this.path,               that.path);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.classificationNode != null ? this.classificationNode.hashCode() : 0);
        hash = 79 * hash + (this.parent != null ? this.parent.hashCode() : 0);
        hash = 79 * hash + (this.code != null ? this.code.hashCode() : 0);
        hash = 79 * hash + (this.path != null ? this.path.hashCode() : 0);
        return hash;
    }
}
