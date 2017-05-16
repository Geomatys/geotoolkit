/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2016, Geomatys
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

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;
import org.geotoolkit.xml.AbstractConfigurable;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.feature.xml.GMLConvention;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.xsd.xml.v2001.Attribute;
import org.geotoolkit.xsd.xml.v2001.ComplexContent;
import org.geotoolkit.xsd.xml.v2001.ExplicitGroup;
import org.geotoolkit.xsd.xml.v2001.ExtensionType;
import org.geotoolkit.xsd.xml.v2001.FormChoice;
import org.geotoolkit.xsd.xml.v2001.Import;
import org.geotoolkit.xsd.xml.v2001.LocalElement;
import org.geotoolkit.xsd.xml.v2001.Schema;
import org.geotoolkit.xsd.xml.v2001.TopLevelComplexType;
import org.geotoolkit.xsd.xml.v2001.TopLevelElement;
import org.geotoolkit.xsd.xml.v2001.XSDMarshallerPool;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JAXBFeatureTypeWriter extends AbstractConfigurable {

    private static final MarshallerPool POOL = XSDMarshallerPool.getInstance();

    private static final Import GML_IMPORT_311 = new Import("http://www.opengis.net/gml", "http://schemas.opengis.net/gml/3.1.1/base/gml.xsd");
    private static final Import GML_IMPORT_321 = new Import("http://www.opengis.net/gml/3.2", "http://schemas.opengis.net/gml/3.2.1/gml.xsd");

    private static final QName ABSTRACT_FEATURE_NAME_311 = new QName("http://www.opengis.net/gml", "_Feature");
    private static final QName ABSTRACT_FEATURE_TYPE_311 = new QName("http://www.opengis.net/gml", "AbstractFeatureType");
    private static final QName ABSTRACT_FEATURE_NAME_321 = new QName("http://www.opengis.net/gml/3.2", "AbstractFeature");
    private static final QName ABSTRACT_FEATURE_TYPE_321 = new QName("http://www.opengis.net/gml/3.2", "AbstractFeatureType");

    private final String gmlVersion;

    public JAXBFeatureTypeWriter() {
        this("3.1.1");
    }

    public JAXBFeatureTypeWriter(String gmlVersion) {
        this.gmlVersion = gmlVersion;
    }

    /**
     * Return an XML representation of the specified featureType.
     *
     * @param feature The featureType to marshall.
     * @return An XML string representing the featureType.
     */
    public String write(FeatureType feature) throws JAXBException {
        final StringWriter sw = new StringWriter();
        write(feature,sw);
        return sw.toString();
    }

    /**
     * Write an XML representation of the specified featureType into the Writer.
     *
     * @param feature The featureType to marshall.
     */
    public void write(FeatureType feature, Writer writer) throws JAXBException {
        final Schema schema = getSchemaFromFeatureType(feature);
        final Marshaller marshaller = POOL.acquireMarshaller();
        marshaller.marshal(schema, writer);
        POOL.recycle(marshaller);
    }

     /**
     * Write an XML representation of the specified featureType into the Stream.
     *
     * @param feature The featureType to marshall.
     */
    public void write(FeatureType feature, OutputStream stream) throws JAXBException {
        final Schema schema = getSchemaFromFeatureType(feature);
        final Marshaller marshaller = POOL.acquireMarshaller();
        marshaller.marshal(schema, stream);
        POOL.recycle(marshaller);
    }

    /**
     * Write an XML representation of the specified featureType into an Element.
     * @param feature
     * @return the xml element.
     * @throws JAXBException
     * @throws ParserConfigurationException
     */
    public Node writeToElement(FeatureType feature) throws JAXBException, ParserConfigurationException {

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // then we have to create document-loader:
        factory.setNamespaceAware(false);
        final DocumentBuilder loader = factory.newDocumentBuilder();

        // creating a new DOM-document...
        final Document document = loader.newDocument();

        final Schema schema = getSchemaFromFeatureType(feature);
        final Marshaller marshaller = POOL.acquireMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
        marshaller.marshal(schema, document);
        POOL.recycle(marshaller);
        return document.getDocumentElement();
    }

    /**
     * Create an xsd schema from a list of feature type.
     *
     * @param featureTypes
     * @return
     */
    public Schema getSchemaFromFeatureType(List<FeatureType> featureTypes) {
        final Schema schema = new Schema(FormChoice.QUALIFIED, null);
        if (featureTypes != null && featureTypes.size() > 0) {
            // we get the first namespace
            String typeNamespace = null;
            int i = 0;
            while (typeNamespace == null && i < featureTypes.size()) {
                typeNamespace = NamesExt.getNamespace(featureTypes.get(i).getName());
                i++;
            }
            schema.setTargetNamespace(typeNamespace);
            if ("3.2.1".equals(gmlVersion)) {
                schema.addImport(GML_IMPORT_321);
            } else {
                schema.addImport(GML_IMPORT_311);
            }
            final Set<String> alreadyWritten = new HashSet<>();
            for (FeatureType ftype : featureTypes) {
                fillSchemaWithFeatureType(ftype, schema, true, alreadyWritten);
            }
        }
        return schema;
    }

    /**
     * Create a xsd schema from a feature type.
     *
     * @param featureType
     * @return
     */
    public Schema getSchemaFromFeatureType(FeatureType featureType) {
        if (featureType != null) {
            final String typeNamespace = NamesExt.getNamespace(featureType.getName());
            final Schema schema = new Schema(FormChoice.QUALIFIED, typeNamespace);
            if ("3.2.1".equals(gmlVersion)) {
                schema.addImport(GML_IMPORT_321);
            } else {
                schema.addImport(GML_IMPORT_311);
            }
            fillSchemaWithFeatureType(featureType, schema, true, new HashSet<String>());
            return schema;
        }
        return null;
    }

    public Schema getExternalSchemaFromFeatureType(String namespace, List<FeatureType> featureTypes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void fillSchemaWithFeatureType(final FeatureType featureType, final Schema schema, boolean addTopElement, Set<String> alreadyWritten) {

        if (Utils.GML_FEATURE_TYPES.contains(featureType.getName())) {
           //this type is part of the standard GML types
           return;
        }

        //write parent types
        for (FeatureType parent : featureType.getSuperTypes()) {
            fillSchemaWithFeatureType(parent, schema, false, alreadyWritten);
        }

        final String typeNamespace    = NamesExt.getNamespace(featureType.getName());
        final String elementName      = featureType.getName().tip().toString();
        final String typeName         = elementName + "Type";

        if (addTopElement) {
            final TopLevelElement topElement;
            if ("3.2.1".equals(gmlVersion)) {
                topElement = new TopLevelElement(elementName, new QName(typeNamespace, typeName), ABSTRACT_FEATURE_NAME_321);
            } else {
                topElement = new TopLevelElement(elementName, new QName(typeNamespace, typeName), ABSTRACT_FEATURE_NAME_311);
            }
            schema.addElement(topElement);
        }
        boolean ar = alreadyWritten.add(typeName);

        final ExplicitGroup sequence  = new ExplicitGroup();
        final List<Attribute> attributes = new ArrayList<>();
        for (final PropertyType pdesc : featureType.getProperties(false)) {
            if (AttributeConvention.contains(pdesc.getName())) {
                //skip convention properties
                continue;
            }
            writeProperty(pdesc, sequence, schema, attributes, alreadyWritten);
        }

        if (addTopElement && ar) {
            final ComplexContent content      = getComplexContent(sequence);
            final TopLevelComplexType tlcType = new TopLevelComplexType(typeName, content);
            tlcType.getAttributeOrAttributeGroup().addAll(attributes);
            schema.addComplexType(1, tlcType);
        }
    }

    private void writeComplexType(final FeatureType ctype, final Schema schema, Set<String> alreadyWritten) {
        final GenericName ptypeName = ctype.getName();

        // PropertyType
        final String nameWithSuffix = Utils.getNameWithTypeSuffix(ptypeName.tip().toString());

        boolean write = schema.getTargetNamespace().equals(NamesExt.getNamespace(ptypeName));

        //search if this type has already been written
        if (alreadyWritten.contains(nameWithSuffix)) {
            return;
        }
        alreadyWritten.add(nameWithSuffix);


        //complex type
        final ExplicitGroup sequence      = new ExplicitGroup();
        final TopLevelComplexType tlcType = new TopLevelComplexType(nameWithSuffix, sequence);
        if (write) {
            schema.addComplexType(tlcType);
        }
        final List<Attribute> attributes = new ArrayList<>();
        for (final PropertyType pdesc : ctype.getProperties(true)) {
            writeProperty(pdesc, sequence, schema, attributes, alreadyWritten);
        }
        tlcType.getAttributeOrAttributeGroup().addAll(attributes);
    }

    private void writeProperty(final PropertyType pType, final ExplicitGroup sequence, final Schema schema, final List<Attribute> attributes, final Set<String> alreadyWritten) {
        if(pType instanceof Operation){
            //operation types are not written in the xsd.
            return;
        }

        if(pType instanceof AttributeType){
            final AttributeType attType = (AttributeType) pType;
            final String name        = attType.getName().tip().toString();
            final QName type         = Utils.getQNameFromType(attType, gmlVersion);
            final int minOccurs      = attType.getMinimumOccurs();
            final int maxOccurs      = attType.getMaximumOccurs();
            final boolean nillable   = FeatureExt.getCharacteristicValue(attType, GMLConvention.NILLABLE_PROPERTY.toString(), minOccurs==0);
            final String maxOcc;
            if (maxOccurs == Integer.MAX_VALUE) {
                maxOcc = "unbounded";
            } else {
                maxOcc = Integer.toString(maxOccurs);
            }
            if (AttributeConvention.contains(attType.getName())) {
                Attribute att = new Attribute();
                att.setName(name);
                att.setType(type);
                if (minOccurs == 0) {
                    att.setUse("optional");
                } else {
                    att.setUse("required");
                }
                attributes.add(att);
            } else {
                sequence.addElement(new LocalElement(name, type, minOccurs, maxOcc, nillable));
            }

        } else if (pType instanceof FeatureAssociationRole) {
            // for a complexType we have to add 2 complexType (PropertyType and type)
            final FeatureAssociationRole role = (FeatureAssociationRole) pType;
            final FeatureType valueType = role.getValueType();
            final String name        = role.getName().tip().toString();
            final QName type         = Utils.getQNameFromType(role, gmlVersion);
            final String typeName    = Utils.getNameWithoutTypeSuffix(valueType.getName().tip().toString());
            final String propertyName = Utils.getNameWithPropertyTypeSuffix(typeName);
            final QName proptype;
            if ("3.2.1".equals(gmlVersion)) {
                proptype = new QName(GMLConvention.GML_321_NAMESPACE, propertyName);
            } else {
                proptype = new QName(GMLConvention.GML_311_NAMESPACE, propertyName);
            }

            //property type
            //<xsd:element name="Address" type="gml:AddressType" xmlns:gml="http://www.opengis.net/gml" nillable="false" minOccurs="1" maxOccurs="1" />
            final ExplicitGroup exp = new ExplicitGroup();
            final TopLevelComplexType tlcType = new TopLevelComplexType(propertyName, exp);
            final LocalElement le = new LocalElement(typeName, type, 1, "1",Boolean.FALSE);
            le.setType(Utils.getQNameFromType(valueType, gmlVersion));
            exp.addElement(le);
            schema.addComplexType(tlcType);

            //attribute type
            final int minOccurs      = role.getMinimumOccurs();
            final int maxOccurs      = role.getMaximumOccurs();
            final boolean nillable   = FeatureExt.getCharacteristicValue(role, GMLConvention.NILLABLE_PROPERTY.toString(), minOccurs==0);
            final String maxOcc;
            if (maxOccurs == Integer.MAX_VALUE) {
                maxOcc = "unbounded";
            } else {
                maxOcc = Integer.toString(maxOccurs);
            }
            sequence.addElement(new LocalElement(name, proptype, minOccurs, maxOcc, nillable));

            //real type
            writeComplexType(role.getValueType(), schema, alreadyWritten);
        }
    }

    private ComplexContent getComplexContent(final ExplicitGroup sequence) {
        final ExtensionType extension;
        if ("3.2.1".equals(gmlVersion)) {
            extension = new ExtensionType(ABSTRACT_FEATURE_TYPE_321, sequence);
        } else {
            extension = new ExtensionType(ABSTRACT_FEATURE_TYPE_311, sequence);
        }
        return new ComplexContent(extension);
    }

}
