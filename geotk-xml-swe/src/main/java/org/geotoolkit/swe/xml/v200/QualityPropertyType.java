/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swe.xml.v200;

import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractDataComponent;
import org.geotoolkit.swe.xml.AbstractQualityProperty;
import org.geotoolkit.xlink.xml.v100.ActuateType;
import org.geotoolkit.xlink.xml.v100.ShowType;
import org.geotoolkit.xlink.xml.v100.TypeType;


/**
 * <p>Java class for QualityPropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="QualityPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;group ref="{http://www.opengis.net/swe/2.0}Quality"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/swe/2.0}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QualityPropertyType", propOrder = {
    "quantity",
    "quantityRange",
    "category",
    "text"
})
public class QualityPropertyType implements AbstractQualityProperty {

    @XmlElement(name = "Quantity")
    private QuantityType quantity;
    @XmlElement(name = "QuantityRange")
    private QuantityRangeType quantityRange;
    @XmlElement(name = "Category")
    private CategoryType category;
    @XmlElement(name = "Text")
    private TextType text;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private TypeType type;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private ShowType show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private ActuateType actuate;

    /**
     * A empty constructor used by JAXB
     */
    public QualityPropertyType() {

    }

    public QualityPropertyType(final AbstractQualityProperty aq) {
        if (aq != null) {
            this.actuate = aq.getActuate() != null ? ActuateType.fromValue(aq.getActuate()) : null;
            this.arcrole = aq.getArcrole();
            if (aq.getCategory() != null) {
                this.category = new CategoryType(aq.getCategory());
            }
            this.href = aq.getHref();
            if (aq.getQuantity() != null) {
                this.quantity = new QuantityType(aq.getQuantity());
            }
            if (aq.getQuantityRange() != null) {
                this.quantityRange = new QuantityRangeType(aq.getQuantityRange());
            }
            this.role = aq.getRole();
            this.show = aq.getShow() != null ? ShowType.fromValue(aq.getShow()) : null;
            if (aq.getText() != null) {
                this.text = new TextType(aq.getText());
            }
            this.title = aq.getTitle();
            this.type = aq.getType() != null ? TypeType.fromValue(aq.getType()) : null;
        }
    }

    public QualityPropertyType(AbstractDataComponent dc) {
        if (dc instanceof QuantityType q) {
            this.quantity = q;
        } else if (dc instanceof QuantityRangeType qr) {
            this.quantityRange = qr;
        } else if (dc instanceof CategoryType c) {
            this.category = c;
        } else if (dc instanceof TextType t) {
            this.text = t;
        } else if (dc != null) {
            throw new IllegalArgumentException("Unexpected data component type:" + dc.getClass().getName());
        }
    }

    @Override
    public AbstractDataComponent getDataComponent() {
        if (quantity != null) {
            return quantity;
        } else if (quantityRange != null) {
            return quantityRange;
        } else if (category != null) {
            return category;
        } else if (text != null) {
            return text;
        }
        return null;
    }

    /**
     * Gets the value of the quantity property.
     *
     * @return
     *     possible object is
     *     {@link QuantityType }
     *
     */
    @Override
    public QuantityType getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     *
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *
     */
    public void setQuantity(QuantityType value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the quantityRange property.
     *
     * @return
     *     possible object is
     *     {@link QuantityRangeType }
     *
     */
    @Override
    public QuantityRangeType getQuantityRange() {
        return quantityRange;
    }

    /**
     * Sets the value of the quantityRange property.
     *
     * @param value
     *     allowed object is
     *     {@link QuantityRangeType }
     *
     */
    public void setQuantityRange(QuantityRangeType value) {
        this.quantityRange = value;
    }

    /**
     * Gets the value of the category property.
     *
     * @return
     *     possible object is
     *     {@link CategoryType }
     *
     */
    @Override
    public CategoryType getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     *
     * @param value
     *     allowed object is
     *     {@link CategoryType }
     *
     */
    public void setCategory(CategoryType value) {
        this.category = value;
    }

    /**
     * Gets the value of the text property.
     *
     * @return
     *     possible object is
     *     {@link TextType }
     *
     */
    @Override
    public TextType getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     *
     * @param value
     *     allowed object is
     *     {@link TextType }
     *
     */
    public void setText(TextType value) {
        this.text = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link TypeType }
     *
     */
    @Override
    public String getType() {
        if (type == null) {
            return TypeType.SIMPLE.toString();
        } else {
            return type.toString();
        }
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link TypeType }
     *
     */
    public void setType(TypeType value) {
        this.type = value;
    }

    /**
     * Gets the value of the href property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Gets the value of the role property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Gets the value of the arcrole property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Sets the value of the arcrole property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setArcrole(String value) {
        this.arcrole = value;
    }

    /**
     * Gets the value of the titleTemp property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the titleTemp property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the show property.
     *
     * @return
     *     possible object is
     *     {@link ShowType }
     *
     */
    @Override
    public String getShow() {
        if (show != null) {
            return show.toString();
        }
        return null;
    }

    /**
     * Sets the value of the show property.
     *
     * @param value
     *     allowed object is
     *     {@link ShowType }
     *
     */
    public void setShow(ShowType value) {
        this.show = value;
    }

    /**
     * Gets the value of the actuate property.
     *
     * @return
     *     possible object is
     *     {@link ActuateType }
     *
     */
    @Override
    public String getActuate() {
        if (actuate != null) {
            return actuate.toString();
        }
        return null;
    }

    /**
     * Sets the value of the actuate property.
     *
     * @param value
     *     allowed object is
     *     {@link ActuateType }
     *
     */
    public void setActuate(ActuateType value) {
        this.actuate = value;
    }

    @Override
    public String getRemoteSchema() {
        return null;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof QualityPropertyType) {
            final QualityPropertyType that = (QualityPropertyType) object;

            return Objects.equals(this.quantity,      that.quantity) &&
                   Objects.equals(this.quantityRange, that.quantityRange) &&
                   Objects.equals(this.category,      that.category) &&
                   Objects.equals(this.text,          that.text) &&
                   Objects.equals(this.type,          that.type) &&
                   Objects.equals(this.href,          that.href) &&
                   Objects.equals(this.role,          that.role) &&
                   Objects.equals(this.arcrole,       that.arcrole) &&
                   Objects.equals(this.title,         that.title) &&
                   Objects.equals(this.show,          that.show) &&
                   Objects.equals(this.actuate,       that.actuate);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.quantity != null ? this.quantity.hashCode() : 0);
        hash = 47 * hash + (this.quantityRange != null ? this.quantityRange.hashCode() : 0);
        hash = 47 * hash + (this.category != null ? this.category.hashCode() : 0);
        hash = 47 * hash + (this.text != null ? this.text.hashCode() : 0);
        hash = 47 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 47 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 47 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 47 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 47 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 47 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 47 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder("[QualityPropertyType]\n");
        if (quantity != null) {
            s.append("quantity=").append(quantity).append('\n');
        }
        if (quantityRange != null) {
            s.append("quantityRange=").append(quantityRange).append('\n');
        }
        if (category != null) {
            s.append("category=").append(category).append('\n');
        }
        if (text != null) {
            s.append("text=").append(text).append('\n');
        }
        if (type != null) {
            s.append("type=").append(type).append('\n');
        }
        if (href != null) {
            s.append("href=").append(href).append('\n');
        }
        if (role != null) {
            s.append("role=").append(role).append('\n');
        }
        if (arcrole != null) {
            s.append("arcrole=").append(arcrole).append('\n');
        }
        if (title != null) {
            s.append("title=").append(title).append('\n');
        }
        if (show != null) {
            s.append("show=").append(show).append('\n');
        }
        if (actuate != null) {
            s.append("actuate=").append(actuate).append('\n');
        }
        return s.toString();
    }
}
