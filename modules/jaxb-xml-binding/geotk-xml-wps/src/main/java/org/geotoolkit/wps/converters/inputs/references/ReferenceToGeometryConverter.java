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
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import static org.geotoolkit.data.geojson.GeoJSONFeatureStoreFactory.PARAMETERS_DESCRIPTOR;
import static org.geotoolkit.data.geojson.GeoJSONFeatureStoreFactory.PATH;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.GeometryAttribute;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.wps.xml.Reference;
import org.opengis.util.GenericName;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Implementation of ObjectConverter to convert a reference into a Geometry.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
 */
public final class ReferenceToGeometryConverter extends AbstractReferenceInputConverter<Geometry> {

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
    public Class<Geometry> getTargetClass() {
        return Geometry.class;
    }

    /**
     * {@inheritDoc}
     *
     * @return Geometry.
     */
    @Override
    public Geometry convert(final Reference source, final Map<String, Object> params) throws UnconvertibleObjectException {

        final String mime = source.getMimeType() != null ? source.getMimeType() : WPSMimeType.TEXT_XML.val();
        final InputStream stream = getInputStreamFromReference(source);

        if (mime.equalsIgnoreCase(WPSMimeType.TEXT_XML.val()) || mime.equalsIgnoreCase(WPSMimeType.APP_GML.val())
                || mime.equalsIgnoreCase(WPSMimeType.TEXT_GML.val())) {

            Unmarshaller unmarsh = null;
            try {
                unmarsh = WPSMarshallerPool.getInstance().acquireUnmarshaller();
                Object value = unmarsh.unmarshal(stream);
                WPSMarshallerPool.getInstance().recycle(unmarsh);
                if (value != null && value instanceof JAXBElement) {
                    value = ((JAXBElement) value).getValue();
                }
                return GeometrytoJTS.toJTS((AbstractGeometryType) value);

            } catch (NoSuchAuthorityCodeException ex) {
                throw new UnconvertibleObjectException("Reference geometry invalid input", ex);
            } catch (FactoryException ex) {
                throw new UnconvertibleObjectException("Reference geometry invalid input", ex);
            } catch (JAXBException ex) {
                throw new UnconvertibleObjectException("Reference geometry invalid input : Unmarshallable geometry", ex);
            }
        } else if (mime.equalsIgnoreCase(WPSMimeType.APP_GEOJSON.val())) {
            ParameterValueGroup param = PARAMETERS_DESCRIPTOR.createValue();
            try {
                param.parameter(PATH.getName().getCode()).setValue(URI.create(source.getHref()));
                FeatureStore store = (FeatureStore) DataStores.open(param);
                Iterator<GenericName> iterator = store.getNames().iterator();

                int typesSize = store.getNames().size();
                if (typesSize != 1)
                    throw new UnconvertibleObjectException("Expected one Geometry. Found " + typesSize);

                GenericName name = iterator.next();

                Session session = store.createSession(false);
                FeatureCollection featureCollection = session.getFeatureCollection(QueryBuilder.all(name));

                int collectionSize = featureCollection.size();
                if (collectionSize != 1)
                    throw new UnconvertibleObjectException("Expected one geometry. Found " + collectionSize);

                try (FeatureIterator featureCollectionIterator = featureCollection.iterator()) {
                    Feature feature = featureCollectionIterator.next();
                    GeometryAttribute defaultGeom = feature.getDefaultGeometryProperty();

                    if (defaultGeom == null)
                        throw new UnconvertibleObjectException("No geometry found");

                    Object value = defaultGeom.getValue();

                    if (!(value instanceof Geometry))
                        throw new UnconvertibleObjectException("The found value may not be of Geometry type or may be null");

                    return (Geometry) value;
                }
            } catch (DataStoreException ex) {
                throw new UnconvertibleObjectException(ex);
            }
        }
        else
            throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + source.getMimeType());
    }
}