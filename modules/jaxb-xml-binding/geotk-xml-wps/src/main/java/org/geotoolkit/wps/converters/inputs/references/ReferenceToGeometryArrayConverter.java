/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
import com.vividsolutions.jts.geom.GeometryCollection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.Reference;
import org.opengis.feature.Feature;

/**
 *
 * @author Theo Zozime
 */
public class ReferenceToGeometryArrayConverter extends AbstractReferenceInputConverter<Geometry[]> {

    private static ReferenceToGeometryArrayConverter INSTANCE;

    public static synchronized ReferenceToGeometryArrayConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ReferenceToGeometryArrayConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Geometry[]> getTargetClass() {
        return Geometry[].class;
    }

    @Override
    public Geometry[] convert(Reference source, Map<String, Object> params) throws UnconvertibleObjectException {
        if (WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(source.getMimeType())) {
            try {
                final FeatureCollection featureCollection = WPSConvertersUtils.readFeatureCollectionFromJson(URI.create(source.getHref()));

                if (featureCollection.size() != 1)
                    throw new UnconvertibleObjectException("Expected size for feature collection was 1." + "Found : " + featureCollection.size());

                try (final FeatureIterator iterator = featureCollection.iterator()) {
                    final Feature feature = iterator.next();
                    final Geometry defaultGeometry = (Geometry) FeatureExt.getDefaultGeometryAttributeValue(feature);

                    if (!(defaultGeometry instanceof GeometryCollection))
                        throw new UnconvertibleObjectException("No geometry collection found.");

                    final GeometryCollection geometryCollection = (GeometryCollection) defaultGeometry;
                    final Geometry[] geometryArray = new Geometry[geometryCollection.getNumGeometries()];
                    for (int index = 0; index < geometryArray.length; index++)
                        geometryArray[index] = geometryCollection.getGeometryN(index);

                    return geometryArray;
                }
            } catch (DataStoreException | MalformedURLException ex) {
                throw new UnconvertibleObjectException(ex);
            } catch (URISyntaxException | IOException ex) {
                throw new UnconvertibleObjectException(ex);
            }
        }
        else
            throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + source.getMimeType());
    }

}
