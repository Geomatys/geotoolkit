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
import java.util.List;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.feature.xml.XmlFeatureTypeWriter;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.xsd.xml.v2001.ComplexContent;
import org.geotoolkit.xsd.xml.v2001.ExplicitGroup;
import org.geotoolkit.xsd.xml.v2001.ExtensionType;
import org.geotoolkit.xsd.xml.v2001.FormChoice;
import org.geotoolkit.xsd.xml.v2001.Import;
import org.geotoolkit.xsd.xml.v2001.ObjectFactory;
import org.geotoolkit.xsd.xml.v2001.Schema;
import org.geotoolkit.xsd.xml.v2001.TopLevelComplexType;
import org.geotoolkit.xsd.xml.v2001.TopLevelElement;

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JAXBFeatureTypeWriter implements XmlFeatureTypeWriter {

    private static MarshallerPool marshallpool;
    static {
        try {
            marshallpool = new MarshallerPool(ObjectFactory.class, org.geotoolkit.internal.jaxb.ObjectFactory.class);
        } catch (JAXBException ex) {
            Logging.getLogger("org.geotoolkit.feature.xml.jaxp")
                .log(Level.SEVERE, "JAXB Exception while initalizing the marshaller pool", ex);
        }
    }

    private static final Import gmlImport = new Import("http://www.opengis.net/gml", "http://schemas.opengis.net/gml/3.1.1/base/gml.xsd");

    private static final QName featureName = new QName("http://www.opengis.net/gml", "_Feature");


    public JAXBFeatureTypeWriter() throws JAXBException {
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
        Marshaller marshaller = null;
        try {
            marshaller = marshallpool.acquireMarshaller();
            marshaller.marshal(schema, writer);
        } finally {
            if (marshaller != null) {
                marshallpool.release(marshaller);
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void write(final FeatureType feature, final OutputStream stream) throws JAXBException {
        final Schema schema = getSchemaFromFeatureType(feature);
        Marshaller marshaller = null;
        try {
            marshaller = marshallpool.acquireMarshaller();
            marshaller.marshal(schema, stream);
        } finally {
            if (marshaller != null) {
                marshallpool.release(marshaller);
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Schema getSchemaFromFeatureType(final List<FeatureType> featureTypes) {
        if (featureTypes != null && featureTypes.size() > 0) {
            // we get the first namespace
            String typeNamespace = null;
            int i = 0;
            while (typeNamespace == null && i < featureTypes.size()) {
                typeNamespace = featureTypes.get(i).getName().getNamespaceURI();
                i++;
            }
            final Schema schema = new Schema(FormChoice.QUALIFIED, typeNamespace);
            schema.addImport(gmlImport);
            for (FeatureType ftype : featureTypes) {
                fillSchemaWithFeatureType(ftype, schema);
            }
            return schema;
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Schema getSchemaFromFeatureType(final FeatureType featureType) {
        if (featureType != null) {
            final String typeNamespace = featureType.getName().getNamespaceURI();
            final Schema schema = new Schema(FormChoice.QUALIFIED, typeNamespace);
            schema.addImport(gmlImport);
            fillSchemaWithFeatureType(featureType, schema);
            return schema;
        }
        return null;
    }

    private void fillSchemaWithFeatureType(final FeatureType featureType, final Schema schema) {
        
        final String typeNamespace    = featureType.getName().getNamespaceURI();
        final String elementName      = featureType.getName().getLocalPart();
        final String typeName         = elementName + "Type";
        schema.addElement(new TopLevelElement(elementName, new QName(typeNamespace, typeName)));

        final ExplicitGroup sequence  = new ExplicitGroup();
        
        for(final PropertyDescriptor pdesc : featureType.getDescriptors()) {
            final String name   = pdesc.getName().getLocalPart();
            final QName type    = Utils.getQNameFromType(pdesc.getType().getBinding());
            final int minOccurs = pdesc.getMinOccurs();
            final int maxOccurs = pdesc.getMaxOccurs();
            final boolean nillable = pdesc.isNillable();
            final String maxOcc;
            if (maxOccurs == Integer.MAX_VALUE) {
                maxOcc = "unbounded";
            } else {
                maxOcc = Integer.toString(maxOccurs);
            }
            sequence.addElement(new TopLevelElement(name, type, minOccurs, maxOcc, nillable));
        }

        final ExtensionType extension = new ExtensionType(featureName, sequence);
        final ComplexContent content  = new ComplexContent(extension);
        schema.addComplexType(new TopLevelComplexType(typeName, content));
    }

}
