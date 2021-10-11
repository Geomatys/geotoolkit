/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.w3._2005.atom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.geotoolkit.ows.xml.BoundingBox;


/**
 *
 *              The Atom entry construct is defined in section 4.1.2 of the format spec.
 *
 *
 * <p>Java class for entryType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="entryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="author" type="{http://www.w3.org/2005/Atom}personType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="category" type="{http://www.w3.org/2005/Atom}categoryType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="content" type="{http://www.w3.org/2005/Atom}contentType" minOccurs="0"/>
 *         &lt;element name="contributor" type="{http://www.w3.org/2005/Atom}personType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2005/Atom}idType"/>
 *         &lt;element name="link" type="{http://www.w3.org/2005/Atom}linkType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="published" type="{http://www.w3.org/2005/Atom}dateTimeType" minOccurs="0"/>
 *         &lt;element name="rights" type="{http://www.w3.org/2005/Atom}textType" minOccurs="0"/>
 *         &lt;element name="source" type="{http://www.w3.org/2005/Atom}sourceType" minOccurs="0"/>
 *         &lt;element name="summary" type="{http://www.w3.org/2005/Atom}textType" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2005/Atom}textType"/>
 *         &lt;element name="updated" type="{http://www.w3.org/2005/Atom}dateTimeType"/>
 *         &lt;any namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.w3.org/2005/Atom}commonAttributes"/>
 *       &lt;anyAttribute namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "entryType", propOrder = {
    "authorOrCategoryOrContent"
})
public class EntryType {

    @XmlElementRefs({
        @XmlElementRef(name = "rights", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "content", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "source", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "summary", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "id", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "title", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "link", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "category", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "contributor", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "author", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "updated", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "published", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false)
    })
    @XmlAnyElement(lax = true)
    protected List<Object> authorOrCategoryOrContent;
    @XmlAttribute(name = "base", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlSchemaType(name = "anyURI")
    protected String base;
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String lang;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    private static final ObjectFactory OBJ_ATOM_FACT = new ObjectFactory();
    private static final org.geotoolkit.georss.xml.v100.ObjectFactory GEORSS_FACT = new org.geotoolkit.georss.xml.v100.ObjectFactory();

    public EntryType() {

    }

    public EntryType(List<Object> authorOrCategoryOrContent) {
        this.authorOrCategoryOrContent = authorOrCategoryOrContent;
    }

    public EntryType(List<Object> authorOrCategoryOrContent, String base, String lang) {
        this.authorOrCategoryOrContent = authorOrCategoryOrContent;
        this.base = base;
        this.lang = lang;
    }

    /**
     * Gets the value of the authorOrCategoryOrContent property.
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link TextType }{@code >}
     * {@link JAXBElement }{@code <}{@link ContentType }{@code >}
     * {@link Object }
     * {@link JAXBElement }{@code <}{@link SourceType }{@code >}
     * {@link JAXBElement }{@code <}{@link IdType }{@code >}
     * {@link JAXBElement }{@code <}{@link TextType }{@code >}
     * {@link JAXBElement }{@code <}{@link CategoryType }{@code >}
     * {@link JAXBElement }{@code <}{@link LinkType }{@code >}
     * {@link JAXBElement }{@code <}{@link TextType }{@code >}
     * {@link JAXBElement }{@code <}{@link PersonType }{@code >}
     * {@link JAXBElement }{@code <}{@link PersonType }{@code >}
     * {@link JAXBElement }{@code <}{@link DateTimeType }{@code >}
     * {@link JAXBElement }{@code <}{@link DateTimeType }{@code >}
     *
     *
     */
    public List<Object> getAuthorOrCategoryOrContent() {
        if (authorOrCategoryOrContent == null) {
            authorOrCategoryOrContent = new ArrayList<>();
        }
        return this.authorOrCategoryOrContent;
    }

    public void addId(IdType id) {
        if (id != null) {
            getAuthorOrCategoryOrContent().add(OBJ_ATOM_FACT.createEntryTypeId(id));
        }
    }

    public IdType getId() {
        for (Object obj : getAuthorOrCategoryOrContent()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (ObjectFactory._EntryTypeId_QNAME.equals(elem.getName()) &&
                    elem.getValue() instanceof IdType) {
                    return (IdType) elem.getValue();
                }
            }
        }
        return null;
    }

    public void addTitle(TextType title) {
        if (title != null) {
            getAuthorOrCategoryOrContent().add(OBJ_ATOM_FACT.createEntryTypeTitle(title));
        }
    }

    public TextType getTitle() {
        for (Object obj : getAuthorOrCategoryOrContent()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (ObjectFactory._EntryTypeTitle_QNAME.equals(elem.getName()) &&
                    elem.getValue() instanceof TextType) {
                    return (TextType) elem.getValue();
                }
            }
        }
        return null;
    }

    public void addContent(ContentType content) {
        if (content != null) {
            getAuthorOrCategoryOrContent().add(OBJ_ATOM_FACT.createEntryTypeContent(content));
        }
    }

    public List<ContentType> getContents() {
        List<ContentType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContent()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (ObjectFactory._EntryTypeContent_QNAME.equals(elem.getName()) &&
                    elem.getValue() instanceof ContentType) {
                   results.add((ContentType) elem.getValue());
                }
            }
        }
        return results;
    }

    public void addCategory(CategoryType category) {
        if (category != null) {
            getAuthorOrCategoryOrContent().add(OBJ_ATOM_FACT.createEntryTypeCategory(category));
        }
    }

    public List<CategoryType> getCategory() {
        List<CategoryType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContent()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (ObjectFactory._EntryTypeCategory_QNAME.equals(elem.getName()) &&
                    elem.getValue() instanceof ContentType) {
                   results.add((CategoryType) elem.getValue());
                }
            }
        }
        return results;
    }

    public void addAuthor(PersonType author) {
        if (author != null) {
            getAuthorOrCategoryOrContent().add(OBJ_ATOM_FACT.createEntryTypeAuthor(author));
        }
    }

    public List<PersonType> getAuthor() {
        List<PersonType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContent()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (ObjectFactory._EntryTypeAuthor_QNAME.equals(elem.getName()) &&
                    elem.getValue() instanceof PersonType) {
                   results.add((PersonType) elem.getValue());
                }
            }
        }
        return results;
    }

    public void addContributor(PersonType contributor) {
        if (contributor != null) {
            getAuthorOrCategoryOrContent().add(OBJ_ATOM_FACT.createEntryTypeContributor(contributor));
        }
    }

    public List<PersonType> getContributor() {
        List<PersonType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContent()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (ObjectFactory._EntryTypeContributor_QNAME.equals(elem.getName()) &&
                    elem.getValue() instanceof PersonType) {
                   results.add((PersonType) elem.getValue());
                }
            }
        }
        return results;
    }

    public void addUpdated(DateTimeType updated) {
        if (updated != null) {
            getAuthorOrCategoryOrContent().add(OBJ_ATOM_FACT.createEntryTypeUpdated(updated));
        }
    }

    public List<DateTimeType> getUpdated() {
        List<DateTimeType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContent()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (ObjectFactory._EntryTypeUpdated_QNAME.equals(elem.getName()) &&
                    elem.getValue() instanceof DateTimeType) {
                   results.add((DateTimeType) elem.getValue());
                }
            }
        }
        return results;
    }

    public void addSummary(TextType summ) {
        if (summ != null) {
            getAuthorOrCategoryOrContent().add(OBJ_ATOM_FACT.createEntryTypeSummary(summ));
        }
    }

    public List<TextType> getSummary() {
        List<TextType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContent()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (ObjectFactory._EntryTypeSummary_QNAME.equals(elem.getName()) &&
                    elem.getValue() instanceof TextType) {
                   results.add((TextType) elem.getValue());
                }
            }
        }
        return results;
    }

    public void addRight(TextType right) {
        if (right != null) {
            getAuthorOrCategoryOrContent().add(OBJ_ATOM_FACT.createEntryTypeRights(right));
        }
    }

    public List<TextType> getRight() {
        List<TextType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContent()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (ObjectFactory._EntryTypeRights_QNAME.equals(elem.getName()) &&
                    elem.getValue() instanceof TextType) {
                   results.add((TextType) elem.getValue());
                }
            }
        }
        return results;
    }

    public void addBox(BoundingBox box) {
        if (box != null) {
            List<Double> coord = new ArrayList<>();
            coord.add(box.getLowerCorner().get(0));
            coord.add(box.getUpperCorner().get(0));
            coord.add(box.getLowerCorner().get(1));
            coord.add(box.getUpperCorner().get(1));
            getAuthorOrCategoryOrContent().add(GEORSS_FACT.createBox(coord));
        }
    }

    public void addBox(List<Double> coord) {
        if (coord != null) {
            getAuthorOrCategoryOrContent().add(GEORSS_FACT.createBox(coord));
        }
    }

    public List<Double> getBox() {
        for (Object obj : getAuthorOrCategoryOrContent()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (org.geotoolkit.georss.xml.v100.ObjectFactory._Box_QNAME.equals(elem.getName()) &&
                    elem.getValue() instanceof List) {
                   return (List<Double>) elem.getValue();
                }
            }
        }
        return new ArrayList<>();
    }

    public void addLink(LinkType link) {
        if (link != null) {
            getAuthorOrCategoryOrContent().add(OBJ_ATOM_FACT.createFeedTypeLink(link));
        }
    }

    public List<LinkType> getLinks() {
        List<LinkType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContent()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (ObjectFactory._EntryTypeLink_QNAME.equals(elem.getName()) &&
                    elem.getValue() instanceof LinkType) {
                   results.add((LinkType) elem.getValue());
                }
            }
        }
        return results;
    }

    public List<LinkType> getLinksByRel(String rel) {
        List<LinkType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContent()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (ObjectFactory._EntryTypeLink_QNAME.equals(elem.getName()) &&
                    elem.getValue() instanceof LinkType) {
                    LinkType l = (LinkType) elem.getValue();
                    if (Objects.equals(l.rel, rel)) {
                        results.add(l);
                    }
                }
            }
        }
        return results;
    }


    /**
     * Gets the value of the base property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBase() {
        return base;
    }

    /**
     * Sets the value of the base property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBase(String value) {
        this.base = value;
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
    public void setLang(String value) {
        this.lang = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
