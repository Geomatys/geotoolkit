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
import org.geotoolkit.feature.xml.Utils;
import org.geotoolkit.feature.xml.XmlFeatureTypeReader;
import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.xsd.xml.v2001.ComplexContent;
import org.geotoolkit.xsd.xml.v2001.Element;
import org.geotoolkit.xsd.xml.v2001.ExplicitGroup;
import org.geotoolkit.xsd.xml.v2001.ExtensionType;
import org.geotoolkit.xsd.xml.v2001.ObjectFactory;
import org.geotoolkit.xsd.xml.v2001.Schema;
import org.geotoolkit.xsd.xml.v2001.TopLevelComplexType;
import org.geotoolkit.xsd.xml.v2001.TopLevelElement;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JAXBFeatureTypeReader implements XmlFeatureTypeReader {

    private static Logger LOGGER = Logger.getLogger("org.geotoolkit.feature.xml.jaxp");
    
    private static MarshallerPool marshallpool;
    static {
        try {
            marshallpool = new MarshallerPool(ObjectFactory.class);
        } catch (JAXBException ex) {
            LOGGER.log(Level.WARNING, "JAXB Exception while initalizing the marshaller pool", ex);
        }
    }

    private FeatureTypeBuilder builder;

    public JAXBFeatureTypeReader() throws JAXBException {
         builder = new FeatureTypeBuilder();
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(String xml) {
        try {
            Unmarshaller unmarshaller = marshallpool.acquireUnmarshaller();
            Schema schema             = (Schema) unmarshaller.unmarshal(new StringReader(xml));
            return getAllFeatureTypeFromSchema(schema);
        } catch (JAXBException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
        return null;
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(InputStream in) {
        try {
            Unmarshaller unmarshaller = marshallpool.acquireUnmarshaller();
            Schema schema             = (Schema) unmarshaller.unmarshal(in);
            return getAllFeatureTypeFromSchema(schema);
        } catch (JAXBException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
        return null;
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureType> read(Reader reader) {
        try {
            Unmarshaller unmarshaller = marshallpool.acquireUnmarshaller();
            Schema schema             = (Schema) unmarshaller.unmarshal(reader);
            return getAllFeatureTypeFromSchema(schema);
        } catch (JAXBException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
        return null;
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType read(String xml, String name) {
        try {
            Unmarshaller unmarshaller = marshallpool.acquireUnmarshaller();
            Schema schema             = (Schema) unmarshaller.unmarshal(new StringReader(xml));
            return getFeatureTypeFromSchema(schema, name);
        } catch (JAXBException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
        return null;
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType read(InputStream in, String name) {
        try {
            Unmarshaller unmarshaller = marshallpool.acquireUnmarshaller();
            Schema schema             = (Schema) unmarshaller.unmarshal(in);
            return getFeatureTypeFromSchema(schema, name);
        } catch (JAXBException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
        return null;
    }

     /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType read(Reader reader, String name) {
        try {
            Unmarshaller unmarshaller = marshallpool.acquireUnmarshaller();
            Schema schema             = (Schema) unmarshaller.unmarshal(reader);
            return getFeatureTypeFromSchema(schema, name);
        } catch (JAXBException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
        return null;
    }


    private List<FeatureType> getAllFeatureTypeFromSchema(Schema schema) {
        List<FeatureType> result = new ArrayList<FeatureType>();
        for (TopLevelElement element : schema.getElements()) {
            QName typeName           = element.getType();
            builder.setName(new DefaultName(typeName.getNamespaceURI(), element.getName()));
            TopLevelComplexType type = schema.getComplexTypeByName(typeName.getLocalPart());
            result.add(getFeatureTypeFromSchema(type, typeName.getNamespaceURI()));
        }
        return result;
    }
    
    
    private FeatureType getFeatureTypeFromSchema(Schema schema, String name) {
        TopLevelElement element = schema.getElementByName(name);
        if (element != null) {
            QName typeName           = element.getType();
            if (typeName != null) {
                builder.setName(new DefaultName(typeName.getNamespaceURI(), name));
                TopLevelComplexType type = schema.getComplexTypeByName(typeName.getLocalPart());
                return getFeatureTypeFromSchema(type, typeName.getNamespaceURI());
            } else {
                LOGGER.warning("the element:" + name + " has no type");
            }
        }
        return null;
    }
    
    private FeatureType getFeatureTypeFromSchema(TopLevelComplexType type, String namespace) {
        if (type != null) {
            ComplexContent content = type.getComplexContent();
            if (content != null) {
                ExtensionType ext = content.getExtension();
                if (ext != null) {
                    // TODO handle base
                    ExplicitGroup sequence = ext.getSequence();
                    if (sequence != null) {
                        List<Element> elements = sequence.getElements();
                        for (Element attributeElement : elements) {
                            QName elementType  = attributeElement.getType();
                            String elementName = attributeElement.getName();
                            //System.out.println("adding:" + elementName + " type:" + Utils.getTypeFromQName(elementType));
                            CoordinateReferenceSystem crs = null;

                            final Class c = Utils.getTypeFromQName(elementType);
                            if(Geometry.class.isAssignableFrom(c) || org.opengis.geometry.Geometry.class.isAssignableFrom(c)){
                                builder.add(new DefaultName(namespace, elementName), c, crs);
                            }else{
                                builder.add(new DefaultName(namespace, elementName), c);
                            }

                            
                        }
                    }
                }
            }
        }

        return builder.buildSimpleFeatureType();
    }
}
