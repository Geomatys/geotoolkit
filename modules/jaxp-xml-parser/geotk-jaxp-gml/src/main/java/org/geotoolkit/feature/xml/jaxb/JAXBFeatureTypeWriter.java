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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.feature.xml.XmlFeatureTypeWriter;
import org.geotoolkit.xml.AbstractConfigurable;
import org.apache.sis.xml.MarshallerPool;
import org.apache.sis.xml.Namespaces;
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
import org.geotoolkit.feature.type.ComplexType;

import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.feature.type.PropertyType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JAXBFeatureTypeWriter extends AbstractConfigurable implements XmlFeatureTypeWriter {

    private static final MarshallerPool POOL = XSDMarshallerPool.getInstance();

    private static final Import GML_IMPORT_311 = new Import("http://www.opengis.net/gml", "http://schemas.opengis.net/gml/3.1.1/base/gml.xsd");
    private static final Import GML_IMPORT_321 = new Import("http://www.opengis.net/gml/3.2", "http://schemas.opengis.net/gml/3.2.1/gml.xsd");

    private static final QName ABSTRACT_FEATURE_NAME_311 = new QName("http://www.opengis.net/gml", "_Feature");
    private static final QName ABSTRACT_FEATURE_TYPE_311 = new QName("http://www.opengis.net/gml", "AbstractFeatureType");
    private static final QName ABSTRACT_FEATURE_NAME_321 = new QName("http://www.opengis.net/gml/3.2", "AbstractFeature");
    private static final QName ABSTRACT_FEATURE_TYPE_321 = new QName("http://www.opengis.net/gml/3.2", "AbstractFeatureType");

    private int lastUnknowPrefix = 0;

    private final Map<String, String> unknowNamespaces = new HashMap<String, String>();

    private final String gmlVersion;

    public JAXBFeatureTypeWriter(){
        gmlVersion = "3.1.1";
    }

    public JAXBFeatureTypeWriter(final String gmlVersion){
        this.gmlVersion = gmlVersion;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String write(final FeatureType feature) throws JAXBException {
        final StringWriter sw = new StringWriter();
        write(feature,sw);
        return sw.toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void write(final FeatureType feature, final Writer writer) throws JAXBException {
        final Schema schema = getSchemaFromFeatureType(feature);
        final Marshaller marshaller = POOL.acquireMarshaller();
        marshaller.marshal(schema, writer);
        POOL.recycle(marshaller);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void write(final FeatureType feature, final OutputStream stream) throws JAXBException {
        final Schema schema = getSchemaFromFeatureType(feature);
        final Marshaller marshaller = POOL.acquireMarshaller();
        marshaller.marshal(schema, stream);
        POOL.recycle(marshaller);
    }

    /**
     * {@inheritDoc }
     */
    @Override
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

    /**
     * {@inheritDoc }
     */
    @Override
    public Schema getSchemaFromFeatureType(final List<FeatureType> featureTypes) {
        final Schema schema = new Schema(FormChoice.QUALIFIED, null);
        if (featureTypes != null && featureTypes.size() > 0) {
            // we get the first namespace
            String typeNamespace = null;
            int i = 0;
            while (typeNamespace == null && i < featureTypes.size()) {
                typeNamespace = featureTypes.get(i).getName().getNamespaceURI();
                i++;
            }
            schema.setTargetNamespace(typeNamespace);
            if ("3.2.1".equals(gmlVersion)) {
                schema.addImport(GML_IMPORT_321);
            } else {
                schema.addImport(GML_IMPORT_311);
            }
            for (FeatureType ftype : featureTypes) {
                fillSchemaWithFeatureType(ftype, schema);
            }
        }
        return schema;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Schema getSchemaFromFeatureType(final FeatureType featureType) {
        if (featureType != null) {
            final String typeNamespace = featureType.getName().getNamespaceURI();
            final Schema schema = new Schema(FormChoice.QUALIFIED, typeNamespace);
            if ("3.2.1".equals(gmlVersion)) {
                schema.addImport(GML_IMPORT_321);
            } else {
                schema.addImport(GML_IMPORT_311);
            }
            fillSchemaWithFeatureType(featureType, schema);
            return schema;
        }
        return null;
    }

    private void fillSchemaWithFeatureType(final FeatureType featureType, final Schema schema) {
        final String typeNamespace    = featureType.getName().getNamespaceURI();
        final String elementName      = featureType.getName().getLocalPart();
        final String typeName         = elementName + "Type";
        final TopLevelElement topElement;
        if ("3.2.1".equals(gmlVersion)) {
            topElement = new TopLevelElement(elementName, new QName(typeNamespace, typeName), ABSTRACT_FEATURE_NAME_321);
        } else {
            topElement = new TopLevelElement(elementName, new QName(typeNamespace, typeName), ABSTRACT_FEATURE_NAME_311);
        }
        schema.addElement(topElement);

        final ExplicitGroup sequence  = new ExplicitGroup();
        for (final PropertyDescriptor pdesc : featureType.getDescriptors()) {
            writeProperty(pdesc, sequence, schema);
        }
        final ComplexContent content  = getComplexContent(sequence);
        schema.addComplexType(1, new TopLevelComplexType(typeName, content));
    }

    private void writeComplexType(final ComplexType ctype, final Schema schema) {
        // PropertyType
        final Name ptypeName = ctype.getName();
        final String nameWithSuffix = Utils.getNameWithTypeSuffix(ptypeName.getLocalPart());

        //search if this type has already been written
        if(schema.getComplexTypeByName(nameWithSuffix)!=null) return;


        //complex type
        final ExplicitGroup sequence  = new ExplicitGroup();
        schema.addComplexType(new TopLevelComplexType(nameWithSuffix, sequence));

        for (final PropertyDescriptor pdesc : ctype.getDescriptors()) {
            writeProperty(pdesc, sequence, schema);
        }

    }

    private void writeProperty(final PropertyDescriptor pdesc, final ExplicitGroup sequence, final Schema schema) {
        final PropertyType pType = pdesc.getType();
        final String name        = pdesc.getName().getLocalPart();
        final QName type         = Utils.getQNameFromType(pType, gmlVersion);
        final int minOccurs      = pdesc.getMinOccurs();
        final int maxOccurs      = pdesc.getMaxOccurs();
        final boolean nillable   = pdesc.isNillable();
        final String maxOcc;
        if (maxOccurs == Integer.MAX_VALUE) {
            maxOcc = "unbounded";
        } else {
            maxOcc = Integer.toString(maxOccurs);
        }
        sequence.addElement(new LocalElement(name, type, minOccurs, maxOcc, nillable));

        // for a complexType we have to add 2 complexType (PropertyType and type)
        if (pType instanceof ComplexType) {
            writeComplexType((ComplexType)pType, schema);
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
    private JAXBFeatureTypeWriter.Prefix getPrefix(final String namespace) {
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
        return new JAXBFeatureTypeWriter.Prefix(unknow, prefix);
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
