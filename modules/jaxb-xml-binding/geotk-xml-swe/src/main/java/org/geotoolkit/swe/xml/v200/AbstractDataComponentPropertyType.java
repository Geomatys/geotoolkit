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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractBoolean;
import org.geotoolkit.swe.xml.AbstractCategory;
import org.geotoolkit.swe.xml.AbstractCount;
import org.geotoolkit.swe.xml.AbstractCountRange;
import org.geotoolkit.swe.xml.AbstractDataArray;
import org.geotoolkit.swe.xml.AbstractDataRecord;
import org.geotoolkit.swe.xml.AbstractQuantityRange;
import org.geotoolkit.swe.xml.AbstractText;
import org.geotoolkit.swe.xml.AbstractTime;
import org.geotoolkit.swe.xml.AbstractTimeRange;
import org.geotoolkit.swe.xml.DataComponentProperty;
import org.geotoolkit.swe.xml.Quantity;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.xlink.xml.v100.ActuateType;
import org.geotoolkit.xlink.xml.v100.ShowType;
import org.geotoolkit.xlink.xml.v100.TypeType;


/**
 * <p>Java class for AbstractDataComponentPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractDataComponentPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/swe/2.0}AbstractDataComponent"/>
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
@XmlType(name = "AbstractDataComponentPropertyType", propOrder = {
    "abstractDataComponent"
})
@XmlSeeAlso({
    DataArrayType.ElementType.class,
    org.geotoolkit.swe.xml.v200.DataStreamType.ElementType.class,
    DataChoiceType.Item.class,
    Field.class
})
public class AbstractDataComponentPropertyType implements DataComponentProperty {

    @XmlElementRef(name = "AbstractDataComponent", namespace = "http://www.opengis.net/swe/2.0", type = JAXBElement.class)
    private JAXBElement<? extends AbstractDataComponentType> abstractDataComponent;
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

    public AbstractDataComponentPropertyType() {
        
    }
    
    public AbstractDataComponentPropertyType(final AbstractDataComponentPropertyType that) {
        this.abstractDataComponent = that.abstractDataComponent;
        this.actuate = that.actuate;
        this.arcrole = that.arcrole;
        this.href    = that.href;
        this.role    = that.role;
        this.show    = that.show;
        this.title   = that.title;
        this.type    = that.type;
    }
    
    public AbstractDataComponentPropertyType(final AbstractDataComponentType data) {
        this.abstractDataComponent = getJAXBElement(data);
    }
    
    public static JAXBElement<? extends AbstractDataComponentType> getJAXBElement(final AbstractDataComponentType data) {
        final ObjectFactory factory = new ObjectFactory();
        if (data instanceof BooleanType) {
            return factory.createBoolean((BooleanType)data);
        } else if (data instanceof VectorType) {
            return factory.createVector((VectorType)data);
        } else if (data instanceof TimeType) {
            return factory.createTime((TimeType)data);
        } else if (data instanceof CategoryRangeType) {
            return factory.createCategoryRange((CategoryRangeType)data);
        } else if (data instanceof DataChoiceType) {
            return factory.createDataChoice((DataChoiceType)data);
        } else if (data instanceof MatrixType) {
            return factory.createMatrix((MatrixType)data);
        } else if (data instanceof TimeRangeType) {
            return factory.createTimeRange((TimeRangeType)data);
        } else if (data instanceof CategoryType) {
            return factory.createCategory((CategoryType)data);
        } else if (data instanceof DataRecordType) {
            return factory.createDataRecord((DataRecordType)data);
        } else if (data instanceof DataArrayType) {
            return factory.createDataArray((DataArrayType)data);
        } else if (data instanceof QuantityRangeType) {
            return factory.createQuantityRange((QuantityRangeType)data);
        } else if (data instanceof CountRangeType) {
            return factory.createCountRange((CountRangeType)data);
        } else if (data instanceof QuantityType) {
            return factory.createQuantity((QuantityType)data);
        } else if (data instanceof TextType) {
            return factory.createText((TextType)data);
        } else if (data instanceof CountType) {
            return factory.createCount((CountType)data);
        } else if (data instanceof AbstractSimpleComponentType) {
            return factory.createAbstractSimpleComponent((AbstractSimpleComponentType)data);
        } else if (data instanceof AbstractDataComponentType) {
            return factory.createAbstractDataComponent((AbstractDataComponentType)data);
        }
        return null;
    }
    
    /**
     * Gets the value of the abstractDataComponent property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BooleanType }{@code >}
     *     {@link JAXBElement }{@code <}{@link VectorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CategoryRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataChoiceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MatrixType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSimpleComponentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CategoryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantityRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CountRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantityType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TextType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDataComponentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CountType }{@code >}
     *     
     */
    public JAXBElement<? extends AbstractDataComponentType> getAbstractDataComponent() {
        return abstractDataComponent;
    }
    
    public AbstractDataComponentType getValue() {
        if (abstractDataComponent != null) {
            return abstractDataComponent.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the abstractDataComponent property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BooleanType }{@code >}
     *     {@link JAXBElement }{@code <}{@link VectorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CategoryRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataChoiceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link MatrixType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractSimpleComponentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TimeRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CategoryType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataRecordType }{@code >}
     *     {@link JAXBElement }{@code <}{@link DataArrayType }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantityRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CountRangeType }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantityType }{@code >}
     *     {@link JAXBElement }{@code <}{@link TextType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AbstractDataComponentType }{@code >}
     *     {@link JAXBElement }{@code <}{@link CountType }{@code >}
     *     
     */
    public void setAbstractDataComponent(JAXBElement<? extends AbstractDataComponentType> value) {
        this.abstractDataComponent = ((JAXBElement<? extends AbstractDataComponentType> ) value);
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
    public void setToHref() {
        // do nothing
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public AbstractCount getCount() {
        if (abstractDataComponent != null) {
            if (abstractDataComponent.getValue() instanceof CountType) {
                return (CountType)abstractDataComponent.getValue();
            }
        }
        return null;
    }

    @Override
    public Quantity getQuantity() {
        if (abstractDataComponent != null) {
            if (abstractDataComponent.getValue() instanceof QuantityType) {
                return (QuantityType)abstractDataComponent.getValue();
            }
        }
        return null;
    }

    @Override
    public AbstractTime getTime() {
        if (abstractDataComponent != null) {
            if (abstractDataComponent.getValue() instanceof TimeType) {
                return (TimeType)abstractDataComponent.getValue();
            }
        }
        return null;
    }

    @Override
    public AbstractBoolean getBoolean() {
        if (abstractDataComponent != null) {
            if (abstractDataComponent.getValue() instanceof BooleanType) {
                return (BooleanType)abstractDataComponent.getValue();
            }
        }
        return null;
    }

    @Override
    public AbstractCategory getCategory() {
        if (abstractDataComponent != null) {
            if (abstractDataComponent.getValue() instanceof CategoryType) {
                return (CategoryType)abstractDataComponent.getValue();
            }
        }
        return null;
    }

    @Override
    public AbstractText getText() {
        if (abstractDataComponent != null) {
            if (abstractDataComponent.getValue() instanceof TextType) {
                return (TextType)abstractDataComponent.getValue();
            }
        }
        return null;
    }

    @Override
    public AbstractQuantityRange getQuantityRange() {
        if (abstractDataComponent != null) {
            if (abstractDataComponent.getValue() instanceof QuantityRangeType) {
                return (QuantityRangeType)abstractDataComponent.getValue();
            }
        }
        return null;
    }

    @Override
    public AbstractCountRange getCountRange() {
        if (abstractDataComponent != null) {
            if (abstractDataComponent.getValue() instanceof CountRangeType) {
                return (CountRangeType)abstractDataComponent.getValue();
            }
        }
        return null;
    }

    @Override
    public AbstractTimeRange getTimeRange() {
        if (abstractDataComponent != null) {
            if (abstractDataComponent.getValue() instanceof TimeRangeType) {
                return (TimeRangeType)abstractDataComponent.getValue();
            }
        }
        return null;
    }

    @Override
    public AbstractDataRecord getAbstractRecord() {
        if (abstractDataComponent != null) {
            if (abstractDataComponent.getValue() instanceof DataRecordType) {
                return (DataRecordType)abstractDataComponent.getValue();
            }
        }
        return null;
    }

    @Override
    public AbstractDataArray getAbstractArray() {
        if (abstractDataComponent != null) {
            if (abstractDataComponent.getValue() instanceof DataArrayType) {
                return (DataArrayType)abstractDataComponent.getValue();
            }
        }
        return null;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof AbstractDataComponentPropertyType) {
            final AbstractDataComponentPropertyType that = (AbstractDataComponentPropertyType) object;
            boolean compo = false;
            if (this.abstractDataComponent == null && that.abstractDataComponent == null) {
                compo = true;
            } else if (this.abstractDataComponent != null && that.abstractDataComponent != null) {
                compo = Utilities.equals(this.abstractDataComponent.getValue(), that.abstractDataComponent.getValue());
            }
            return compo     &&
                   Utilities.equals(this.actuate,            that.actuate)          &&
                   Utilities.equals(this.arcrole,            that.arcrole)          &&
                   Utilities.equals(this.type,               that.type)             &&
                   Utilities.equals(this.href,               that.href)             &&
                   Utilities.equals(this.show,               that.show)             &&
                   Utilities.equals(this.role,               that.role)             &&
                   Utilities.equals(this.title,              that.title);
        }
        return false;
    }

    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.abstractDataComponent != null ? this.abstractDataComponent.hashCode() : 0);
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
        final StringBuilder s = new StringBuilder("[AbstractDataComponentPropertyType]\n");
        if(abstractDataComponent != null) {
            s.append("abstractDataComponent=").append(abstractDataComponent.getValue()).append('\n');
        }
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
        if(type != null) {
            s.append("type=").append(type).append('\n');
        }
        return s.toString();
    }
}
