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
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.feature.xml.XmlFeatureTypeWriter;
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
 */
public class JAXBFeatureTypeWriter implements XmlFeatureTypeWriter {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.feature.xml.jaxp");

    private static MarshallerPool marshallpool;

    private static final Import gmlImport = new Import("http://www.opengis.net/gml", "http://schemas.opengis.net/gml/3.1.1/base/gml.xsd");

    private static final QName featureName = new QName("http://www.opengis.net/gml", "_Feature");


    public JAXBFeatureTypeWriter() throws JAXBException {
        marshallpool = new MarshallerPool(ObjectFactory.class);
    }

    public String write(FeatureType feature) {
        Schema schema         = getSchemaFromFeatureType(feature);
        Marshaller marshaller = null;
        try {
            marshaller = marshallpool.acquireMarshaller();
            StringWriter sw = new StringWriter();
            marshaller.marshal(schema, sw);
            return sw.toString();
        } catch (JAXBException ex) {
            LOGGER.severe("JAXB exception while marshalling the schema");
        } finally {
            if (marshaller != null) {
                marshallpool.release(marshaller);
            }
        }
        return null;
    }

    public void write(FeatureType feature, Writer writer) {
        Schema schema         = getSchemaFromFeatureType(feature);
        Marshaller marshaller = null;
        try {
            marshaller = marshallpool.acquireMarshaller();
            marshaller.marshal(schema, writer);
        } catch (JAXBException ex) {
            LOGGER.severe("JAXB exception while marshalling the schema");
        } finally {
            if (marshaller != null) {
                marshallpool.release(marshaller);
            }
        }
    }

    public void write(FeatureType feature, OutputStream stream) {
        Schema schema         = getSchemaFromFeatureType(feature);
        Marshaller marshaller = null;
        try {
            marshaller = marshallpool.acquireMarshaller();
            marshaller.marshal(schema, stream);
        } catch (JAXBException ex) {
            LOGGER.severe("JAXB exception while marshalling the schema");
        } finally {
            if (marshaller != null) {
                marshallpool.release(marshaller);
            }
        }
    }

    private Schema getSchemaFromFeatureType(FeatureType featureType) {
        String typeNamespace = featureType.getName().getNamespaceURI();
        String elementName   = featureType.getName().getLocalPart();
        String typeName      = elementName + "Type";
        Schema schema = new Schema(FormChoice.QUALIFIED, typeNamespace);
        schema.addImport(gmlImport);
        TopLevelElement element = new TopLevelElement(elementName, new QName(typeNamespace, typeName));
        schema.addElement(element);

        ExplicitGroup sequence  = new ExplicitGroup();
        // we put the geom at the end (why it comme first ??)
        TopLevelElement geomElement = null;
        for (PropertyDescriptor pdesc : featureType.getDescriptors()) {
            String name   = pdesc.getName().getLocalPart();
            QName type    = Utils.getQNameFromType(pdesc.getType().getBinding());
            int minOccurs = pdesc.getMinOccurs();
            int maxOccurs = pdesc.getMaxOccurs();
            String maxOcc;
            if (maxOccurs == Integer.MAX_VALUE) {
                maxOcc = "unbounded";
            } else {
                maxOcc = maxOccurs + "";
            }
            TopLevelElement localElement = new TopLevelElement(name, type, minOccurs, maxOcc);
            if (!type.getLocalPart().equals("GeometryPropertyType")) {
                sequence.addElement(localElement);
            } else {
                geomElement = localElement;
            }
        }
        if (geomElement != null) {
            sequence.addElement(geomElement);
        }
        ExtensionType extension = new ExtensionType(featureName, sequence);
        ComplexContent content  = new ComplexContent(extension);
        TopLevelComplexType complexType = new TopLevelComplexType(typeName, content);
        schema.addComplexType(complexType);
        return schema;
    }

}
