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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.internal.sql.table.Entry;
import org.geotoolkit.swe.xml.AbstractDataComponent;
import org.geotoolkit.swe.xml.DataBlockDefinition;
import org.geotoolkit.util.Utilities;

/**
 * Resultat d'une observation de type DataBlockDefinition.
 *
 * @version $Id:
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataBlockDefinition", propOrder = {
    "components",
    "encoding"})
    public class DataBlockDefinitionType implements DataBlockDefinition, Entry {
    
    /**
     * L'identifiant du resultat.
     */
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    private String id;
    
    /**
     * Liste de composant Data record.
     */
    @XmlElementRef(name = "AbstractDataComponent", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractDataComponentType>> components;
    
    /**
     * Decrit l'encodage des données.
     */
    private AbstractEncodingPropertyType encoding;
    
    /**
     * constructeur utilisé par jaxB
     */
    DataBlockDefinitionType() {}

    public DataBlockDefinitionType(final DataBlockDefinition db) {
        if (db != null) {
            this.id = db.getId();
            if (db.getEncoding() != null) {
                this.encoding = new AbstractEncodingPropertyType(db.getEncoding());
            }
            if (db.getComponents() != null) {
                final ObjectFactory factory = new ObjectFactory();
                this.components = new ArrayList<JAXBElement< ? extends AbstractDataComponentType>>();
                for (AbstractDataComponent c : db.getComponents()) {
                    if (c instanceof BooleanType) {
                        this.components.add(factory.createBoolean(new BooleanType((BooleanType)c)));
                    } else if (c instanceof ConditionalValueType) {
                        this.components.add(factory.createConditionalValue(new ConditionalValueType((ConditionalValueType)c)));
                    } else if (c instanceof DataArrayType) {
                        this.components.add(factory.createDataArray(new DataArrayType((DataArrayType)c)));
                    } else if (c instanceof DataRecordType) {
                        this.components.add(factory.createDataRecord(new DataRecordType((DataRecordType)c)));
                    } else if (c instanceof EnvelopeType) {
                        this.components.add(factory.createEnvelope(new EnvelopeType((EnvelopeType)c)));
                    } else if (c instanceof GeoLocationArea) {
                        this.components.add(factory.createGeoLocationArea(new GeoLocationArea((GeoLocationArea)c)));
                    } else if (c instanceof PositionType) {
                        this.components.add(factory.createPosition(new PositionType((PositionType)c)));
                    } else if (c instanceof QuantityType) {
                        this.components.add(factory.createQuantity(new QuantityType((QuantityType)c)));
                    } else if (c instanceof SimpleDataRecordType) {
                        this.components.add(factory.createSimpleDataRecord(new SimpleDataRecordType((SimpleDataRecordType)c)));
                    } else if (c instanceof SquareMatrixType) {
                        this.components.add(factory.createSquareMatrix(new SquareMatrixType((SquareMatrixType)c)));
                    } else if (c instanceof TimeType) {
                        this.components.add(factory.createTime(new TimeType((TimeType)c)));
                    } else if (c instanceof VectorType) {
                        this.components.add(factory.createVector(new VectorType((VectorType)c)));
                    } else if (c instanceof AbstractDataRecordType) {
                        this.components.add(factory.createAbstractDataRecord(new AbstractDataRecordType((AbstractDataRecordType)c)));
                    } else if (c instanceof AbstractDataComponentType) {
                        this.components.add(factory.createAbstractDataComponent(new AbstractDataComponentType((AbstractDataComponentType)c)));
                    } else {
                        throw new IllegalArgumentException("unexpected type for component:" + c.getClass().getName());
                    }
                    
                }
            }
        }
    }
    
    /**
     * créé un nouveau resultat d'observation.
     * Liste de valeur decrite dans swe:DatablockDefinition de type simple,
     * pour valeur scalaire ou textuelle.
     *
     * @param id l'identifiant du resultat.
     * @param components liste de composant data record.
     * @param encoding encodage des données.
     */
    public DataBlockDefinitionType(final String id, final Collection<? extends AbstractDataComponentType> components,
            final AbstractEncodingType encoding) {
        this.id         = id;
        this.components = new ArrayList<JAXBElement< ? extends AbstractDataComponentType>>();
        final ObjectFactory factory = new ObjectFactory();
        for (AbstractDataComponent c : components) {
            if (c instanceof BooleanType) {
                this.components.add(factory.createBoolean((BooleanType) c));
            } else if (c instanceof ConditionalValueType) {
                this.components.add(factory.createConditionalValue((ConditionalValueType) c));
            } else if (c instanceof DataArrayType) {
                this.components.add(factory.createDataArray((DataArrayType) c));
            } else if (c instanceof DataRecordType) {
                this.components.add(factory.createDataRecord((DataRecordType) c));
            } else if (c instanceof EnvelopeType) {
                this.components.add(factory.createEnvelope((EnvelopeType) c));
            } else if (c instanceof GeoLocationArea) {
                this.components.add(factory.createGeoLocationArea((GeoLocationArea) c));
            } else if (c instanceof PositionType) {
                this.components.add(factory.createPosition((PositionType) c));
            } else if (c instanceof QuantityType) {
                this.components.add(factory.createQuantity((QuantityType) c));
            } else if (c instanceof SimpleDataRecordType) {
                this.components.add(factory.createSimpleDataRecord((SimpleDataRecordType) c));
            } else if (c instanceof SquareMatrixType) {
                this.components.add(factory.createSquareMatrix((SquareMatrixType) c));
            } else if (c instanceof TimeType) {
                this.components.add(factory.createTime((TimeType) c));
            } else if (c instanceof VectorType) {
                this.components.add(factory.createVector((VectorType) c));
            } else if (c instanceof AbstractDataArrayType) {
                this.components.add(factory.createAbstractDataArray((AbstractDataArrayType) c));
            } else if (c instanceof AbstractDataRecordType) {
                this.components.add(factory.createAbstractDataRecord((AbstractDataRecordType) c));
            } else if (c instanceof AbstractDataComponentType) {
                this.components.add(factory.createAbstractDataComponent((AbstractDataComponentType) c));
            } else {
                throw new IllegalArgumentException("unexpected type for component:" + c.getClass().getName());
            }

        }
        this.encoding   = new AbstractEncodingPropertyType(encoding);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getId() {
        return id;
    }

    public String getIdentifier() {
        return id;
    }

    public String getName() {
        return id;
    }
    
    /**
     * {@inheritDoc}
     */
    public Collection<? extends AbstractDataComponentType> getComponents() {
        final List<AbstractDataComponentType> r = new ArrayList<AbstractDataComponentType>();
        for (JAXBElement<? extends AbstractDataComponentType> jb : components) {
            r.add((AbstractDataComponentType)jb.getValue());
        }
        final List<? extends AbstractDataComponentType> response = r;
        return response;
    }
    
    /**
     * {@inheritDoc}
     */
    public AbstractEncodingPropertyType getEncoding() {
        return encoding;
    }
    
    /**
     * Retourne un code représentant ce dataBlock.
     */
    @Override
    public final int hashCode() {
        return id.hashCode();
    }
    
    /**
     * Vérifie si cette entré est identique à l'objet spécifié.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        
        final DataBlockDefinitionType that = (DataBlockDefinitionType) object;
        if (this.components != null && that.components != null) {
            if (this.components.size() != that.components.size()) {
                return false;
            }
        
            final Iterator<? extends JAXBElement<? extends AbstractDataComponentType>> i  = this.components.iterator();
            final Iterator<? extends JAXBElement<? extends AbstractDataComponentType>> i2 = that.components.iterator();
            while (i.hasNext() && i2.hasNext()) {
                if (!Utilities.equals(i.next().getValue(), i2.next().getValue())) return false;
            }
        } else {
            if (this.components != null || that.components != null) {
                return false;
            }
        }
        return Utilities.equals(this.id,         that.id) &&
               Utilities.equals(this.encoding,   that.encoding) ;
    }
    
    /**
     * Retourne une representation de l'objet (debug).
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        final String lineSeparator = System.getProperty("line.separator", "\n");
        buffer.append('[').append(this.getClass().getSimpleName()).append("]:").append(id).append(lineSeparator);
        buffer.append("encoding: ").append(this.encoding.toString()).append(lineSeparator);
        appendTo(buffer, "", lineSeparator);
        return buffer.toString();
    }
    
    /**
     * Ajoute la description des composants du dataBlock definition.
     */
    private void appendTo(final StringBuilder buffer, String margin, final String lineSeparator) {
        buffer.append("components: ").append(lineSeparator);
        margin += "  ";
        if (components != null) {
            for (final JAXBElement<? extends AbstractDataComponentType> a : components) {
                buffer.append(margin).append(a.getValue()).append(lineSeparator);
            }
        }
    }
    
}
