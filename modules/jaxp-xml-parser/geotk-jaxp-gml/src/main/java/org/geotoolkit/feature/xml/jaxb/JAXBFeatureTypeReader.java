/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.feature.xml.jaxb;

import com.vividsolutions.jts.geom.Geometry;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.feature.xml.XmlFeatureTypeReader;
import org.geotoolkit.xml.AbstractConfigurable;
import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.xsd.xml.v2001.ComplexContent;
import org.geotoolkit.xsd.xml.v2001.Element;
import org.geotoolkit.xsd.xml.v2001.ExplicitGroup;
import org.geotoolkit.xsd.xml.v2001.ExtensionType;
import org.geotoolkit.xsd.xml.v2001.LocalSimpleType;
import org.geotoolkit.xsd.xml.v2001.Schema;
import org.geotoolkit.xsd.xml.v2001.TopLevelComplexType;
import org.geotoolkit.xsd.xml.v2001.TopLevelElement;
import org.geotoolkit.xsd.xml.v2001.XSDMarshallerPool;
import org.opengis.feature.type.ComplexType;

import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.w3c.dom.Node;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JAXBFeatureTypeReader extends AbstractConfigurable implements XmlFeatureTypeReader {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.feature.xml.jaxp");

    private static final MarshallerPool POOL = XSDMarshallerPool.getInstance();

    public JAXBFeatureTypeReader() {
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(final String xml) throws JAXBException {
        Unmarshaller unmarshaller = null;
        try {
            unmarshaller        = POOL.acquireUnmarshaller();
            final Schema schema = (Schema) unmarshaller.unmarshal(new StringReader(xml));
            return getAllFeatureTypeFromSchema(schema);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        } finally {
            if (unmarshaller != null) {
                POOL.release(unmarshaller);
            }
        }
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(final InputStream in) throws JAXBException {
        Unmarshaller unmarshaller = null;
        try {
            unmarshaller        = POOL.acquireUnmarshaller();
            final Schema schema = (Schema) unmarshaller.unmarshal(in);
            return getAllFeatureTypeFromSchema(schema);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        } finally {
            if (unmarshaller != null) {
                POOL.release(unmarshaller);
            }
        }
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(final Reader reader) throws JAXBException {
        Unmarshaller unmarshaller = null;
        try {
            unmarshaller         = POOL.acquireUnmarshaller();
            final Schema schema  = (Schema) unmarshaller.unmarshal(reader);
            return getAllFeatureTypeFromSchema(schema);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        } finally {
            if (unmarshaller != null) {
                POOL.release(unmarshaller);
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(final Node element) throws JAXBException {
        Unmarshaller unmarshaller = null;
        try {
            unmarshaller         = POOL.acquireUnmarshaller();
            final Schema schema  = (Schema) unmarshaller.unmarshal(element);
            return getAllFeatureTypeFromSchema(schema);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        } finally {
            if (unmarshaller != null) {
                POOL.release(unmarshaller);
            }
        }
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType read(final String xml, final String name) throws JAXBException {
        Unmarshaller unmarshaller = null;
        try {
            unmarshaller        = POOL.acquireUnmarshaller();
            final Schema schema = (Schema) unmarshaller.unmarshal(new StringReader(xml));
            return getFeatureTypeFromSchema(schema, name);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        } finally {
            if (unmarshaller != null) {
                POOL.release(unmarshaller);
            }
        }
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType read(final InputStream in, final String name) throws JAXBException {
        Unmarshaller unmarshaller = null;
        try {
            unmarshaller        = POOL.acquireUnmarshaller();
            final Schema schema = (Schema) unmarshaller.unmarshal(in);
            return getFeatureTypeFromSchema(schema, name);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        } finally {
            if (unmarshaller != null) {
                POOL.release(unmarshaller);
            }
        }
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType read(final Reader reader, final String name) throws JAXBException {
        Unmarshaller unmarshaller = null;
        try {
            unmarshaller        = POOL.acquireUnmarshaller();
            final Schema schema = (Schema) unmarshaller.unmarshal(reader);
            return getFeatureTypeFromSchema(schema, name);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        } finally {
            if (unmarshaller != null) {
                POOL.release(unmarshaller);
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType read(final Node node, final String name) throws JAXBException {
        Unmarshaller unmarshaller = null;
        try {
            unmarshaller        = POOL.acquireUnmarshaller();
            final Schema schema = (Schema) unmarshaller.unmarshal(node);
            return getFeatureTypeFromSchema(schema, name);
        } catch (SchemaException ex) {
            throw new JAXBException(ex);
        } finally {
            if (unmarshaller != null) {
                POOL.release(unmarshaller);
            }
        }
    }


    public List<FeatureType> getAllFeatureTypeFromSchema(final Schema schema) throws SchemaException {
        final List<FeatureType> result = new ArrayList<FeatureType>();
        for (TopLevelElement element : schema.getElements()) {
            final QName typeName = element.getType();
            if (typeName != null) {
                final TopLevelComplexType type = schema.getComplexTypeByName(typeName.getLocalPart());
                result.add(getFeatureTypeFromSchema(element.getName(), type, typeName.getNamespaceURI(), schema));
            } else {
                LOGGER.log(Level.WARNING, "null typeName for element:{0}", element.getName());
            }
        }
        return result;
    }


    public FeatureType getFeatureTypeFromSchema(final Schema schema, final String name) throws SchemaException {
        final TopLevelElement element = schema.getElementByName(name);
        if (element != null) {
            final QName typeName = element.getType();
            if (typeName != null) {
                final TopLevelComplexType type = schema.getComplexTypeByName(typeName.getLocalPart());
                return getFeatureTypeFromSchema(name, type, typeName.getNamespaceURI(), schema);
            } else {
                LOGGER.log(Level.WARNING, "the element:{0} has no type", name);
            }
        }
        return null;
    }

    private FeatureType getFeatureTypeFromSchema(final String name, final TopLevelComplexType type, final String namespace, final Schema schema) throws SchemaException {
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        if (type != null) {
            builder.setName(new DefaultName(namespace, name));
            final ComplexContent content = type.getComplexContent();
            if (content != null) {
                final ExtensionType ext = content.getExtension();
                if (ext != null) {
                    // TODO handle base
                    final ExplicitGroup sequence = ext.getSequence();
                    if (sequence != null) {
                        for (Element attributeElement : sequence.getElements()) {
                            elementToAttribute(attributeElement, namespace, schema, builder);
                        }
                    }
                }
            }
        }
        return builder.buildFeatureType();
    }

    private ComplexType getComplexTypeFromSchema(final Schema schema, final String namespace, final String name) throws SchemaException {
        final TopLevelComplexType complexType = schema.getComplexTypeByName(name);
        final FeatureTypeBuilder builder      = new FeatureTypeBuilder();
        if (complexType != null) {
            final String properName;
            if (name.endsWith("Type")) {
                properName = name.substring(0, name.lastIndexOf("Type"));
            } else {
                properName = name;
            }
            builder.setName(new DefaultName(namespace, properName));
            final ExplicitGroup sequence = complexType.getSequence();
            if (sequence != null) {
                for (Element attributeElement : sequence.getElements()) {
                    elementToAttribute(attributeElement, namespace, schema, builder);
                }
            }
            return builder.buildType();
        } else {
            LOGGER.log(Level.WARNING, "Unable to find complex type for:{0}", name);
            return null;
        }
    }

    private void elementToAttribute(final Element attributeElement, final String namespace, final Schema schema, final FeatureTypeBuilder builder) throws SchemaException {
        QName elementType = attributeElement.getType();
        // Try to extract base from a SimpleType
        if (elementType == null && attributeElement.getSimpleType() != null) {
            final LocalSimpleType simpleType = attributeElement.getSimpleType();
            if (simpleType.getRestriction() != null) {
                elementType = simpleType.getRestriction().getBase();
            }
        }
        final String typeName    = elementType.getLocalPart();
        final String elementName = attributeElement.getName();
        final Integer minAtt     = attributeElement.getMinOccurs();
        final String maxxAtt     = attributeElement.getMaxOccurs();
        final boolean nillable   = attributeElement.isNillable();
        final int min = (minAtt == null) ? 1 : minAtt;
        final int max;
        if (maxxAtt == null) {
            max = 1;
        } else if (maxxAtt.equalsIgnoreCase("unbounded")) {
            max = Integer.MAX_VALUE;
        } else {
            max = Integer.parseInt(maxxAtt);
        }
        CoordinateReferenceSystem crs = null;

        if ((typeName.endsWith("PropertyType") || typeName.endsWith("Type")) && !Utils.isGeometricType(elementType)) {

            final String cname;
            if (typeName.endsWith("PropertyType")) {
                cname = typeName.substring(0, typeName.lastIndexOf("PropertyType")) + "Type";
            } else {
                cname = typeName;
            }
            final ComplexType cType = getComplexTypeFromSchema(schema, namespace, cname);
            if (cType != null) {
                builder.add(cType, new DefaultName(namespace, elementName), null, min, max, nillable, null);
            }

        } else {
            final Class c = Utils.getTypeFromQName(elementType);
            if (c == null) {
                throw new SchemaException("The attribute:" + elementName + " does no have a declared type.");
            }
            if (Geometry.class.isAssignableFrom(c) || org.opengis.geometry.Geometry.class.isAssignableFrom(c)) {
                builder.add(new DefaultName(namespace, elementName), c, crs, min, max, nillable, null);
            } else {
                builder.add(new DefaultName(namespace, elementName), c, min, max, nillable, null);
            }
        }
    }
}
