/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wps.converters.inputs.references;

import com.vividsolutions.jts.geom.Geometry;
import java.io.InputStream;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.ReferenceType;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;

/**
 * Implementation of ObjectConverter to convert a reference into a Geometry.
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class ReferenceToGeometryConverter extends AbstractReferenceInputConverter {

    private static ReferenceToGeometryConverter INSTANCE;

    private ReferenceToGeometryConverter() {
    }

    public static synchronized ReferenceToGeometryConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReferenceToGeometryConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? extends Object> getTargetClass() {
        return Geometry.class;
    }

    /**
     * {@inheritDoc}
     *
     * @return Geometry.
     */
    @Override
    public Geometry convert(final ReferenceType source, final Map<String, Object> params) throws NonconvertibleObjectException {

        final String mime = source.getMimeType() != null ? source.getMimeType() : WPSMimeType.TEXT_XML.val();
        final InputStream stream = getInputStreamFromReference(source);

        if (mime.equalsIgnoreCase(WPSMimeType.TEXT_XML.val()) || mime.equalsIgnoreCase(WPSMimeType.APP_GML.val())
                || mime.equalsIgnoreCase(WPSMimeType.TEXT_GML.val())) {

            Unmarshaller unmarsh = null;
            try {
                unmarsh = WPSMarshallerPool.getInstance().acquireUnmarshaller();
                Object value = unmarsh.unmarshal(stream);
                if (value != null && value instanceof JAXBElement) {
                    value = ((JAXBElement) value).getValue();
                }
                return GeometrytoJTS.toJTS((AbstractGeometryType) value);

            } catch (NoSuchAuthorityCodeException ex) {
                throw new NonconvertibleObjectException("Reference geometry invalid input", ex);
            } catch (FactoryException ex) {
                throw new NonconvertibleObjectException("Reference geometry invalid input", ex);
            } catch (JAXBException ex) {
                throw new NonconvertibleObjectException("Reference geometry invalid input : Unmarshallable geometry", ex);
            } finally {
                if (unmarsh != null) {
                    WPSMarshallerPool.getInstance().release(unmarsh);
                }
            }
        } else {
            throw new NonconvertibleObjectException("Reference data mime is not supported");
        }
    }
}