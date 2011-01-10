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
package org.geotoolkit.xsd.xml.v2001;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.w3.org/2001/XMLSchema}openAttrs">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element ref="{http://www.w3.org/2001/XMLSchema}include"/>
 *           &lt;element ref="{http://www.w3.org/2001/XMLSchema}import"/>
 *           &lt;element ref="{http://www.w3.org/2001/XMLSchema}redefine"/>
 *         &lt;/choice>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;group ref="{http://www.w3.org/2001/XMLSchema}schemaTop"/>
 *           &lt;element ref="{http://www.w3.org/2001/XMLSchema}annotation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="targetNamespace" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="finalDefault" type="{http://www.w3.org/2001/XMLSchema}fullDerivationSet" default="" />
 *       &lt;attribute name="blockDefault" type="{http://www.w3.org/2001/XMLSchema}blockSet" default="" />
 *       &lt;attribute name="attributeFormDefault" type="{http://www.w3.org/2001/XMLSchema}formChoice" default="unqualified" />
 *       &lt;attribute name="elementFormDefault" type="{http://www.w3.org/2001/XMLSchema}formChoice" default="unqualified" />
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "includeOrImportOrRedefine",
    "simpleTypeOrComplexTypeOrGroup"
})
@XmlRootElement(name = "schema")
public class Schema extends OpenAttrs {

    @XmlElements({
        @XmlElement(name = "import", type = Import.class),
        @XmlElement(name = "redefine", type = Redefine.class),
        @XmlElement(name = "include", type = Include.class)
    })
    private List<OpenAttrs> includeOrImportOrRedefine;

    @XmlElements({
        @XmlElement(name = "complexType",    type = TopLevelComplexType.class),
        @XmlElement(name = "element",        type = TopLevelElement.class),
        @XmlElement(name = "notation",       type = Notation.class),
        @XmlElement(name = "attribute",      type = TopLevelAttribute.class),
        @XmlElement(name = "group",          type = NamedGroup.class),
        @XmlElement(name = "annotation",     type = Annotation.class),
        @XmlElement(name = "simpleType",     type = TopLevelSimpleType.class),
        @XmlElement(name = "attributeGroup", type = NamedAttributeGroup.class)
    })
    private List<OpenAttrs> simpleTypeOrComplexTypeOrGroup;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String targetNamespace;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    private String version;
    @XmlAttribute
    @XmlSchemaType(name = "fullDerivationSet")
    private List<String> finalDefault;
    @XmlAttribute
    @XmlSchemaType(name = "blockSet")
    private List<String> blockDefault;
    @XmlAttribute
    private FormChoice attributeFormDefault;
    @XmlAttribute
    private FormChoice elementFormDefault;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;
    @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace")
    private String lang;

    public Schema() {

    }

    public Schema(final FormChoice elementFormDefault, final String targetNamespace) {
        this.elementFormDefault = elementFormDefault;
        this.targetNamespace    = targetNamespace;
    }

    /**
     * Gets the value of the includeOrImportOrRedefine property.
     */
    public List<OpenAttrs> getIncludeOrImportOrRedefine() {
        if (includeOrImportOrRedefine == null) {
            includeOrImportOrRedefine = new ArrayList<OpenAttrs>();
        }
        return this.includeOrImportOrRedefine;
    }

    /**
     * Gets the value of the simpleTypeOrComplexTypeOrGroup property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link TopLevelComplexType }
     * {@link TopLevelElement }
     * {@link Notation }
     * {@link TopLevelAttribute }
     * {@link NamedGroup }
     * {@link Annotation }
     * {@link TopLevelSimpleType }
     * {@link NamedAttributeGroup }
     * 
     * 
     */
    public List<OpenAttrs> getSimpleTypeOrComplexTypeOrGroup() {
        if (simpleTypeOrComplexTypeOrGroup == null) {
            simpleTypeOrComplexTypeOrGroup = new ArrayList<OpenAttrs>();
        }
        return this.simpleTypeOrComplexTypeOrGroup;
    }

    /**
     * Return all the Top elements of the schema
     * @return
     */
    public List<TopLevelElement> getElements() {
         List<TopLevelElement> result = new ArrayList<TopLevelElement>();
        if (simpleTypeOrComplexTypeOrGroup != null) {
            for (OpenAttrs element : simpleTypeOrComplexTypeOrGroup) {
                if (element instanceof TopLevelElement) {
                    result.add((TopLevelElement) element);
                }
            }
        }
        return result;
    }

    public TopLevelElement getElementByName(final String name) {
        if (name != null) {
            if (simpleTypeOrComplexTypeOrGroup != null) {
                for (OpenAttrs element : simpleTypeOrComplexTypeOrGroup) {
                    if (element instanceof TopLevelElement) {
                        TopLevelElement tLElement = (TopLevelElement) element;
                        if (tLElement.getName().equals(name)) {
                            return tLElement;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Return all the ComplexType of the schema
     * @return
     */
    public List<TopLevelComplexType> getComplexTypes() {
         List<TopLevelComplexType> result = new ArrayList<TopLevelComplexType>();
        if (simpleTypeOrComplexTypeOrGroup != null) {
            for (OpenAttrs element : simpleTypeOrComplexTypeOrGroup) {
                if (element instanceof TopLevelComplexType) {
                    result.add((TopLevelComplexType) element);
                }
            }
        }
        return result;
    }

    public TopLevelComplexType getComplexTypeByName(final String name) {
        if (name != null) {
            if (simpleTypeOrComplexTypeOrGroup != null) {
                for (OpenAttrs element : simpleTypeOrComplexTypeOrGroup) {
                    if (element instanceof TopLevelComplexType) {
                        TopLevelComplexType tLElement = (TopLevelComplexType) element;
                        if (tLElement.getName().equals(name)) {
                            return tLElement;
                        }
                    }
                }
            }
        }
        return null;
    }


    public void addImport(final Import _import) {
        if (includeOrImportOrRedefine == null) {
            includeOrImportOrRedefine = new ArrayList<OpenAttrs>();
        }
        this.includeOrImportOrRedefine.add(_import);
    }

    public void addElement(final TopLevelElement element) {
        if (simpleTypeOrComplexTypeOrGroup == null) {
            simpleTypeOrComplexTypeOrGroup = new ArrayList<OpenAttrs>();
        }
        this.simpleTypeOrComplexTypeOrGroup.add(element);
    }

    public void addComplexType(final TopLevelComplexType element) {
        if (simpleTypeOrComplexTypeOrGroup == null) {
            simpleTypeOrComplexTypeOrGroup = new ArrayList<OpenAttrs>();
        }
        this.simpleTypeOrComplexTypeOrGroup.add(element);
    }


    /**
     * Gets the value of the targetNamespace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * Sets the value of the targetNamespace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetNamespace(final String value) {
        this.targetNamespace = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(final String value) {
        this.version = value;
    }

    /**
     * Gets the value of the finalDefault property.
     */
    public List<String> getFinalDefault() {
        if (finalDefault == null) {
            finalDefault = new ArrayList<String>();
        }
        return this.finalDefault;
    }

    /**
     * Gets the value of the blockDefault property.
     */
    public List<String> getBlockDefault() {
        if (blockDefault == null) {
            blockDefault = new ArrayList<String>();
        }
        return this.blockDefault;
    }

    /**
     * Gets the value of the attributeFormDefault property.
     * 
     * @return
     *     possible object is
     *     {@link FormChoice }
     *     
     */
    public FormChoice getAttributeFormDefault() {
        if (attributeFormDefault == null) {
            return FormChoice.UNQUALIFIED;
        } else {
            return attributeFormDefault;
        }
    }

    /**
     * Sets the value of the attributeFormDefault property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormChoice }
     *     
     */
    public void setAttributeFormDefault(final FormChoice value) {
        this.attributeFormDefault = value;
    }

    /**
     * Gets the value of the elementFormDefault property.
     * 
     * @return
     *     possible object is
     *     {@link FormChoice }
     *     
     */
    public FormChoice getElementFormDefault() {
        if (elementFormDefault == null) {
            return FormChoice.UNQUALIFIED;
        } else {
            return elementFormDefault;
        }
    }

    /**
     * Sets the value of the elementFormDefault property.
     * 
     * @param value
     *     allowed object is
     *     {@link FormChoice }
     *     
     */
    public void setElementFormDefault(final FormChoice value) {
        this.elementFormDefault = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(final String value) {
        this.id = value;
    }

    /**
     * Gets the value of the lang property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLang(final String value) {
        this.lang = value;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Schema && super.equals(object)) {
            final Schema that = (Schema) object;
            return Utilities.equals(this.attributeFormDefault,           that.attributeFormDefault)           &&
                   Utilities.equals(this.blockDefault,                   that.blockDefault)                   &&
                   Utilities.equals(this.elementFormDefault,             that.elementFormDefault)             &&
                   Utilities.equals(this.finalDefault,                   that.finalDefault)                   &&
                   Utilities.equals(this.id,                             that.id)                             &&
                   Utilities.equals(this.includeOrImportOrRedefine,      that.includeOrImportOrRedefine)      &&
                   Utilities.equals(this.lang,                           that.lang)                           &&
                   Utilities.equals(this.simpleTypeOrComplexTypeOrGroup, that.simpleTypeOrComplexTypeOrGroup) &&
                   Utilities.equals(this.targetNamespace,                that.targetNamespace)                &&
                   Utilities.equals(this.version,                        that.version) ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.includeOrImportOrRedefine != null ? this.includeOrImportOrRedefine.hashCode() : 0);
        hash = 67 * hash + (this.simpleTypeOrComplexTypeOrGroup != null ? this.simpleTypeOrComplexTypeOrGroup.hashCode() : 0);
        hash = 67 * hash + (this.targetNamespace != null ? this.targetNamespace.hashCode() : 0);
        hash = 67 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 67 * hash + (this.finalDefault != null ? this.finalDefault.hashCode() : 0);
        hash = 67 * hash + (this.blockDefault != null ? this.blockDefault.hashCode() : 0);
        hash = 67 * hash + (this.attributeFormDefault != null ? this.attributeFormDefault.hashCode() : 0);
        hash = 67 * hash + (this.elementFormDefault != null ? this.elementFormDefault.hashCode() : 0);
        hash = 67 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 67 * hash + (this.lang != null ? this.lang.hashCode() : 0);
        return hash;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append('\n');
        if (id != null) {
            sb.append("id:").append(id).append('\n');
        }
        if (lang != null) {
            sb.append("lang:").append(lang).append('\n');
        }
        if (version != null) {
            sb.append("version:").append(version).append('\n');
        }
        if (targetNamespace != null) {
            sb.append("targetNamespace:").append(targetNamespace).append('\n');
        }
        if (attributeFormDefault != null) {
            sb.append("attributeFormDefault:").append(attributeFormDefault).append('\n');
        }
        if (elementFormDefault != null) {
            sb.append("elementFormDefault:").append(elementFormDefault).append('\n');
        }
        if (blockDefault != null) {
            sb.append("blockDefault:\n");
            for (String s : blockDefault) {
                sb.append(s).append('\n');
            }
        }
        if (finalDefault != null) {
            sb.append("finalDefault:\n");
            for (String s : finalDefault) {
                sb.append(s).append('\n');
            }
        }
        if (includeOrImportOrRedefine != null) {
            sb.append("includeOrImportOrRedefine:\n");
            for (OpenAttrs s : includeOrImportOrRedefine) {
                sb.append(s).append('\n');
            }
        }
        if (simpleTypeOrComplexTypeOrGroup != null) {
            sb.append("simpleTypeOrComplexTypeOrGroup:\n");
            for (OpenAttrs s : simpleTypeOrComplexTypeOrGroup) {
                sb.append(s).append('\n');
            }
        }
        return  sb.toString();
    }
}
