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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractBinaryBlock;
import org.geotoolkit.swe.xml.AbstractEncoding;
import org.geotoolkit.swe.xml.AbstractEncodingProperty;
import org.geotoolkit.swe.xml.MultiplexedStreamFormat;
import org.geotoolkit.swe.xml.TextBlock;
import org.geotoolkit.swe.xml.XmlBlock;

/**
 *
 * @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractEncodingPropertyType", propOrder = {
    "encoding"
})

public class AbstractEncodingPropertyType implements AbstractEncodingProperty {

    /**
     * Decribe the data encoding.
     */
    @XmlElementRefs({
        @XmlElementRef(name = "Encoding", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class),
        @XmlElementRef(name = "TextBlock", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class)
    })
    private JAXBElement<? extends AbstractEncodingType> encoding;

    @XmlTransient
    private JAXBElement<? extends AbstractEncodingType> hiddenEncoding;

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

    @XmlTransient
    private static ObjectFactory sweFactory = new ObjectFactory();

    /**
     * An empty constructor used by JAXB
     */
    AbstractEncodingPropertyType() {

    }

    public AbstractEncodingPropertyType(final AbstractEncodingProperty enc) {
        if (enc != null) {
            this.actuate = enc.getActuate();
            this.arcrole = enc.getArcrole();
            this.href    = enc.getHref();
            this.remoteSchema = enc.getRemoteSchema();
            this.role         = enc.getRole();
            this.show         = enc.getShow();
            this.title        = enc.getTitle();
            this.type         = enc.getType();
            if (enc.getEncoding() != null) {
                if (enc.getEncoding() instanceof AbstractBinaryBlock) {
                    throw new IllegalArgumentException("Binnary block are not handled");

                } else if (enc.getEncoding() instanceof MultiplexedStreamFormat) {
                    this.encoding = sweFactory.createMultiplexedStreamFormat(new MultiplexedStreamFormatType((MultiplexedStreamFormat) enc.getEncoding()));

                } else if (enc.getEncoding() instanceof TextBlock) {
                    this.encoding = sweFactory.createTextBlock(new TextBlockType((TextBlock) enc.getEncoding()));

                } else if (enc.getEncoding() instanceof XmlBlock) {
                    this.encoding = sweFactory.createXMLBlock(new XMLBlockType((XmlBlock) enc.getEncoding()));
                }
            }
        }
    }

    public AbstractEncodingPropertyType(final AbstractEncoding enc) {
        if (enc != null) {

            if (enc instanceof AbstractBinaryBlock) {
                throw new IllegalArgumentException("Binnary block are not handled");

            } else if (enc instanceof MultiplexedStreamFormat){
                this.encoding = sweFactory.createMultiplexedStreamFormat(new MultiplexedStreamFormatType((MultiplexedStreamFormat)enc));

            } else if (enc instanceof TextBlock) {
                this.encoding = sweFactory.createTextBlock(new TextBlockType((TextBlock)enc));

            } else if (enc instanceof XmlBlock) {
                this.encoding = sweFactory.createXMLBlock(new XMLBlockType((XmlBlock)enc));
            }
        }
   }


    /**
     * Build a new Abstract encoding Property.
     */
    public AbstractEncodingPropertyType(final AbstractEncodingType encoding) {

        if (encoding instanceof TextBlockType) {
            this.encoding = sweFactory.createTextBlock((TextBlockType)encoding);
        } else if (encoding instanceof MultiplexedStreamFormatType) {
            this.encoding = sweFactory.createMultiplexedStreamFormat((MultiplexedStreamFormatType) encoding);

        } else if (encoding instanceof TextBlockType) {
            this.encoding = sweFactory.createTextBlock((TextBlockType) encoding);

        } else if (encoding instanceof XMLBlockType) {
            this.encoding = sweFactory.createXMLBlock((XMLBlockType) encoding);
        } else {
            throw new IllegalArgumentException("only TextBlock are allowed");
        }
    }

    /**
     * clone Abstract encoding Property.
     */
    public AbstractEncodingPropertyType(final AbstractEncodingPropertyType clone) {

        this.actuate        = clone.actuate;
        this.arcrole        = clone.arcrole;
        if (clone.encoding != null) {
            if (clone.encoding.getValue() instanceof TextBlockType) {
                this.encoding = sweFactory.createTextBlock((TextBlockType) clone.encoding.getValue());

            } else if (encoding.getValue() instanceof MultiplexedStreamFormatType) {
                this.encoding = sweFactory.createMultiplexedStreamFormat((MultiplexedStreamFormatType) encoding.getValue());

            } else if (encoding.getValue() instanceof TextBlockType) {
                this.encoding = sweFactory.createTextBlock((TextBlockType) encoding.getValue());

            } else if (encoding.getValue() instanceof XMLBlockType) {
                this.encoding = sweFactory.createXMLBlock((XMLBlockType) encoding.getValue());
            } else {
                throw new IllegalArgumentException("only TextBlock are allowed");
            }
        }
        if (clone.hiddenEncoding != null) {
            if (clone.hiddenEncoding.getValue() instanceof TextBlockType) {
                this.hiddenEncoding = sweFactory.createTextBlock((TextBlockType)clone.hiddenEncoding.getValue());
            } else {
                throw new IllegalArgumentException("only TextBlock are allowed");
            }
        }
        this.href           = clone.href;
        this.remoteSchema   = clone.remoteSchema;
        this.role           = clone.role;
        this.show           = clone.show;
        this.title          = clone.title;
        this.type           = clone.type;

    }

    @Override
    public void setToHref() {
        if (encoding != null) {
            this.href = encoding.getValue().getId();
            hiddenEncoding = encoding;
            encoding = null;
        }
    }

    /**
     * Gets the value of the encoding property.
     */
    @Override
    public AbstractEncodingType getEncoding() {
        if (encoding != null) {
            return encoding.getValue();
        } else if (hiddenEncoding != null) {
            return hiddenEncoding.getValue();
        }
        return null;
    }

    /**
     * Gets the value of the remoteSchema property.
     */
    @Override
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Gets the value of the type property.
     */
    @Override
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
    @Override
    public String getRole() {
        return role;
    }

    /**
     * Gets the value of the arcrole property.
     */
    @Override
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Gets the value of the title property.
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Gets the value of the show property.
     */
    @Override
    public String getShow() {
        return show;
    }

    /**
     * Gets the value of the actuate property.
     */
    @Override
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

        if (object instanceof AbstractEncodingPropertyType) {
            final AbstractEncodingPropertyType that = (AbstractEncodingPropertyType) object;
            final boolean enc;
            if (this.encoding != null && that.encoding != null) {
                enc = Objects.equals(this.encoding.getValue(), that.encoding.getValue());
            } else {
                enc = (this.encoding == null && that.encoding == null);
            }

            final boolean hiddenEnc;
            if (this.hiddenEncoding != null && that.hiddenEncoding != null) {
                hiddenEnc = Objects.equals(this.hiddenEncoding.getValue(), that.hiddenEncoding.getValue());
            } else {
                hiddenEnc = (this.hiddenEncoding == null && that.hiddenEncoding == null);
            }
            return enc                                                              &&
                   hiddenEnc                                                        &&
                   Objects.equals(this.actuate,            that.actuate)          &&
                   Objects.equals(this.arcrole,            that.arcrole)          &&
                   Objects.equals(this.type,               that.type)             &&
                   Objects.equals(this.href,               that.href)             &&
                   Objects.equals(this.remoteSchema,       that.remoteSchema)     &&
                   Objects.equals(this.show,               that.show)             &&
                   Objects.equals(this.role,               that.role)             &&
                   Objects.equals(this.title,              that.title);
        }
        return false;
    }


    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.encoding != null ? this.encoding.hashCode() : 0);
        hash = 47 * hash + (this.hiddenEncoding != null ? this.hiddenEncoding.hashCode() : 0);
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
        StringBuilder s = new StringBuilder();
        if (encoding != null) {
            s.append(encoding.getValue().toString()).append('\n');
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
        if(title != null) {
            s.append("title=").append(title).append('\n');
        }
        return s.toString();
    }

}
