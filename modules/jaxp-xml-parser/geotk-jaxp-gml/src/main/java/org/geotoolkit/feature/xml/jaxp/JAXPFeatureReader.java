/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.feature.xml.jaxp;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.xml.MarshallerPool;
import org.geotoolkit.internal.jaxb.ObjectFactory;
import org.geotoolkit.xml.StaxStreamReader;

import org.opengis.feature.type.FeatureType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public abstract class JAXPFeatureReader extends StaxStreamReader implements XmlFeatureReader {

    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.feature.xml.jaxp");
    private static MarshallerPool marshallpool;

    static {
        try {
            marshallpool = new MarshallerPool(ObjectFactory.class);
        } catch (JAXBException ex) {
            LOGGER.log(Level.SEVERE, "JAXB Exception while initalizing the marshaller pool", ex);
        }
    }
    protected List<FeatureType> featureTypes;
    protected final Unmarshaller unmarshaller;

    public JAXPFeatureReader(FeatureType featureType) throws JAXBException {
        this.featureTypes = Arrays.asList(featureType);
        this.unmarshaller = marshallpool.acquireUnmarshaller();

    }

    public JAXPFeatureReader(List<FeatureType> featureTypes) throws JAXBException {
        this.featureTypes = featureTypes;
        this.unmarshaller = marshallpool.acquireUnmarshaller();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setFeatureType(FeatureType featureType) {
        this.featureTypes = Arrays.asList(featureType);
    }

    @Override
    public void dispose() {
        marshallpool.release(unmarshaller);
    }
}
