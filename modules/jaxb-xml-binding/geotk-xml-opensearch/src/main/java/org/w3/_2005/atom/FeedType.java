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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.geotoolkit.georss.xml.v100.WhereType;
import org.geotoolkit.ops.xml.OpenSearchResponse;


/**
 *
 *              The Atom feed construct is defined in section 4.1.1 of the format spec.
 *
 *
 * <p>Java class for feedType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="feedType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="3">
 *         &lt;element name="author" type="{http://www.w3.org/2005/Atom}personType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="category" type="{http://www.w3.org/2005/Atom}categoryType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="contributor" type="{http://www.w3.org/2005/Atom}personType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="generator" type="{http://www.w3.org/2005/Atom}generatorType" minOccurs="0"/>
 *         &lt;element name="icon" type="{http://www.w3.org/2005/Atom}iconType" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2005/Atom}idType"/>
 *         &lt;element name="link" type="{http://www.w3.org/2005/Atom}linkType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="logo" type="{http://www.w3.org/2005/Atom}logoType" minOccurs="0"/>
 *         &lt;element name="rights" type="{http://www.w3.org/2005/Atom}textType" minOccurs="0"/>
 *         &lt;element name="subtitle" type="{http://www.w3.org/2005/Atom}textType" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2005/Atom}textType"/>
 *         &lt;element name="updated" type="{http://www.w3.org/2005/Atom}dateTimeType"/>
 *         &lt;element name="entry" type="{http://www.w3.org/2005/Atom}entryType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "feedType", propOrder = {
    "authorOrCategoryOrContributor"
})
@XmlRootElement(name="feed")
public class FeedType implements OpenSearchResponse {

    @XmlElementRefs({
        @XmlElementRef(name = "title", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "updated", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "id", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "logo", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "where", namespace = "http://www.georss.org/georss", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "entry", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "author", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "contributor", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "rights", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "category", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "generator", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "link", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "subtitle", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "icon", namespace = "http://www.w3.org/2005/Atom", type = JAXBElement.class, required = false)
    })
    @XmlAnyElement(lax = true)
    protected List<Object> authorOrCategoryOrContributor;
    @XmlAttribute(name = "base", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlSchemaType(name = "anyURI")
    protected String base;
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String lang;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    private static final ObjectFactory OBJ_ATOM_FACT = new ObjectFactory();
    private static final org.geotoolkit.georss.xml.v100.ObjectFactory OBJ_GEORSS_FACT = new org.geotoolkit.georss.xml.v100.ObjectFactory();

    public FeedType() {

    }

    /**
     * Simple constructor with mandatory elements
     */
    public FeedType(String id, String title, PersonType author, String source) {
        if (id != null) {
            addId(new IdType(id));
        }
        if (title != null) {
            addTitle(new TextType(title));
        }
        if (author != null) {
            addAuthor(author);
        }
        if (source != null) {
            LinkType srcLink = new LinkType(source, "search", "application/opensearchdescription+xml");
            addLink(srcLink);
        }

    }

    /**
     * Gets the value of the authorOrCategoryOrContributor property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link TextType }{@code >}
     * {@link JAXBElement }{@code <}{@link DateTimeType }{@code >}
     * {@link Object }
     * {@link JAXBElement }{@code <}{@link IdType }{@code >}
     * {@link JAXBElement }{@code <}{@link LogoType }{@code >}
     * {@link JAXBElement }{@code <}{@link EntryType }{@code >}
     * {@link JAXBElement }{@code <}{@link PersonType }{@code >}
     * {@link JAXBElement }{@code <}{@link PersonType }{@code >}
     * {@link JAXBElement }{@code <}{@link TextType }{@code >}
     * {@link JAXBElement }{@code <}{@link CategoryType }{@code >}
     * {@link JAXBElement }{@code <}{@link GeneratorType }{@code >}
     * {@link JAXBElement }{@code <}{@link LinkType }{@code >}
     * {@link JAXBElement }{@code <}{@link TextType }{@code >}
     * {@link JAXBElement }{@code <}{@link IconType }{@code >}
     *
     *
     */
    public List<Object> getAuthorOrCategoryOrContributor() {
        if (authorOrCategoryOrContributor == null) {
            authorOrCategoryOrContributor = new ArrayList<>();
        }
        return this.authorOrCategoryOrContributor;
    }

    public void addId(IdType id) {
        if (id != null) {
            getAuthorOrCategoryOrContributor().add(OBJ_ATOM_FACT.createFeedTypeId(id));
        }
    }

    public IdType getId() {
        for (Object obj : getAuthorOrCategoryOrContributor()) {
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
            getAuthorOrCategoryOrContributor().add(OBJ_ATOM_FACT.createFeedTypeTitle(title));
        }
    }

    public TextType getTitle() {
        for (Object obj : getAuthorOrCategoryOrContributor()) {
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

    public void addAuthor(PersonType author) {
        if (author != null) {
            getAuthorOrCategoryOrContributor().add(OBJ_ATOM_FACT.createFeedTypeAuthor(author));
        }
    }

    public List<PersonType> getAuthor() {
        List<PersonType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContributor()) {
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
            getAuthorOrCategoryOrContributor().add(OBJ_ATOM_FACT.createFeedTypeContributor(contributor));
        }
    }

    public List<PersonType> getContributor() {
        List<PersonType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContributor()) {
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
            getAuthorOrCategoryOrContributor().add(OBJ_ATOM_FACT.createFeedTypeUpdated(updated));
        }
    }

    public List<DateTimeType> getUpdated() {
        List<DateTimeType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContributor()) {
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

    public void addWhere(WhereType where) {
        if (where != null) {
            getAuthorOrCategoryOrContributor().add(OBJ_GEORSS_FACT.createWhere(where));
        }
    }

    public List<WhereType> getWhere() {
        List<WhereType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContributor()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (org.geotoolkit.georss.xml.v100.ObjectFactory._WhereType_QNAME.equals(elem.getName()) &&
                    elem.getValue() instanceof WhereType) {
                   results.add((WhereType) elem.getValue());
                }
            }
        }
        return results;
    }


    public void addEntry(EntryType entry) {
        if (entry != null) {
            getAuthorOrCategoryOrContributor().add(OBJ_ATOM_FACT.createEntry(entry));
        }
    }

    public void addLink(LinkType link) {
        if (link != null) {
            getAuthorOrCategoryOrContributor().add(OBJ_ATOM_FACT.createFeedTypeLink(link));
        }
    }

    public List<LinkType> getLinks() {
        List<LinkType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContributor()) {
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

    public void addRight(TextType right) {
        if (right != null) {
            getAuthorOrCategoryOrContributor().add(OBJ_ATOM_FACT.createFeedTypeRights(right));
        }
    }

    public List<TextType> getRight() {
        List<TextType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContributor()) {
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

    public void addCategory(CategoryType category) {
        if (category != null) {
            getAuthorOrCategoryOrContributor().add(OBJ_ATOM_FACT.createFeedTypeCategory(category));
        }
    }

    public List<CategoryType> getCategory() {
        List<CategoryType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContributor()) {
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

    public List<EntryType> getEntries() {
        List<EntryType> results = new ArrayList<>();
        for (Object obj : getAuthorOrCategoryOrContributor()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (ObjectFactory._Entry_QNAME.equals(elem.getName())) {
                    results.add((EntryType) elem.getValue());
                }
            }
        }
        return results;
    }
    public Integer getTotalResults() {
        for (Object obj : getAuthorOrCategoryOrContributor()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (org.geotoolkit.ops.xml.v110.ObjectFactory._TotalResults_QNAME.equals(elem.getName())) {
                    return Integer.valueOf(elem.getValue().toString());
                }
            }
        }
        return null;
    }

    public Integer getStartIndex() {
        for (Object obj : getAuthorOrCategoryOrContributor()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (org.geotoolkit.ops.xml.v110.ObjectFactory._StartIndex_QNAME.equals(elem.getName())) {
                    return Integer.valueOf(elem.getValue().toString());
                }
            }
        }
        return null;
    }

    public Integer getItemsPerPage() {
        for (Object obj : getAuthorOrCategoryOrContributor()) {
            if (obj instanceof JAXBElement) {
                JAXBElement elem = (JAXBElement) obj;
                if (org.geotoolkit.ops.xml.v110.ObjectFactory._ItemsPerPage_QNAME.equals(elem.getName())) {
                    return Integer.valueOf(elem.getValue().toString());
                }
            }
        }
        return null;
    }
}
