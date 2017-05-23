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

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;

import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.xml.AbstractConfigurable;
import org.apache.sis.xml.MarshallerPool;
import org.apache.sis.xml.Namespaces;
import org.geotoolkit.feature.xml.GMLConvention;
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
import org.geotoolkit.util.NamesExt;

import org.opengis.util.GenericName;
import org.geotoolkit.xsd.xml.v2001.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JAXBFeatureTypeWriterOld extends AbstractConfigurable {

    private static final MarshallerPool POOL = XSDMarshallerPool.getInstance();

    private static final Import GML_IMPORT_311 = new Import("http://www.opengis.net/gml", "http://schemas.opengis.net/gml/3.1.1/base/gml.xsd");
    private static final Import GML_IMPORT_321 = new Import("http://www.opengis.net/gml/3.2", "http://schemas.opengis.net/gml/3.2.1/gml.xsd");

    private static final QName ABSTRACT_FEATURE_NAME_311 = new QName("http://www.opengis.net/gml", "_Feature");
    private static final QName ABSTRACT_FEATURE_TYPE_311 = new QName("http://www.opengis.net/gml", "AbstractFeatureType");
    private static final QName ABSTRACT_FEATURE_NAME_321 = new QName("http://www.opengis.net/gml/3.2", "AbstractFeature");
    private static final QName ABSTRACT_FEATURE_TYPE_321 = new QName("http://www.opengis.net/gml/3.2", "AbstractFeatureType");

    private int lastUnknowPrefix = 0;

    private final Map<String, String> unknowNamespaces = new HashMap<>();

    private final String gmlVersion;

    public JAXBFeatureTypeWriterOld(){
        gmlVersion = "3.1.1";
    }

    public JAXBFeatureTypeWriterOld(final String gmlVersion){
        this.gmlVersion = gmlVersion;
    }

    public String write(final FeatureType feature) throws JAXBException {
        final StringWriter sw = new StringWriter();
        write(feature,sw);
        return sw.toString();
    }

    public void write(final FeatureType feature, final Writer writer) throws JAXBException {
        final Schema schema = getSchemaFromFeatureType(feature);
        final Marshaller marshaller = POOL.acquireMarshaller();
        marshaller.marshal(schema, writer);
        POOL.recycle(marshaller);
    }

    public void write(final FeatureType feature, final OutputStream stream) throws JAXBException {
        final Schema schema = getSchemaFromFeatureType(feature);
        final Marshaller marshaller = POOL.acquireMarshaller();
        marshaller.marshal(schema, stream);
        POOL.recycle(marshaller);
    }

    public Node writeToElement(final FeatureType feature) throws JAXBException, ParserConfigurationException {

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

    public Schema getSchemaFromFeatureType(final List<FeatureType> featureTypes) {
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

    public Schema getSchemaFromFeatureType(final FeatureType featureType) {
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

    public Schema getExternalSchemaFromFeatureType(final String namespace, final List<FeatureType> featureTypes) {
        if (featureTypes != null && featureTypes.size() > 0) {
            final Schema schema = new Schema(FormChoice.QUALIFIED, namespace);
            if ("3.2.1".equals(gmlVersion)) {
                schema.addImport(GML_IMPORT_321);
            } else {
                schema.addImport(GML_IMPORT_311);
            }
            final Set<String> alreadyWritten = new HashSet<>();
            for (FeatureType ftype : featureTypes) {
                fillSchemaWithFeatureType(ftype, schema, false, alreadyWritten);
            }
            return schema;
        }
        return null;
    }

    private void fillSchemaWithFeatureType(final FeatureType featureType, final Schema schema, boolean addTopElement, Set<String> alreadyWritten) {
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
        for (final PropertyType pdesc : featureType.getProperties(true)) {
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
        if(alreadyWritten.contains(nameWithSuffix)) return;
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
            final boolean isAttribute = name.startsWith("@");
            if (isAttribute || AttributeConvention.contains(attType.getName())) {
                Attribute att = new Attribute();
                att.setName(name.substring(isAttribute ? 1 : 0));
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
            writeComplexType((FeatureType)pType, schema, alreadyWritten);
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

     /**
     * Returns the prefix for the given namespace.
     *
     * @param namespace The namespace for which we want the prefix.
     */
    private JAXBFeatureTypeWriterOld.Prefix getPrefix(final String namespace) {
        String prefix = Namespaces.getPreferredPrefix(namespace, null);
        boolean unknow = false;
        if (prefix == null) {
            prefix = unknowNamespaces.get(namespace);
            if (prefix == null) {
                prefix = "ns" + lastUnknowPrefix;
                lastUnknowPrefix++;
                unknow = true;
                unknowNamespaces.put(namespace, prefix);
            }
        }
        return new JAXBFeatureTypeWriterOld.Prefix(unknow, prefix);
    }


    /**
     * Inner class for handling prefix and if it is already known.
     */
    private final class Prefix {
        public boolean unknow;
        public String prefix;

        public Prefix(final boolean unknow, final String prefix) {
            this.prefix = prefix;
            this.unknow = unknow;
        }
    }

}
