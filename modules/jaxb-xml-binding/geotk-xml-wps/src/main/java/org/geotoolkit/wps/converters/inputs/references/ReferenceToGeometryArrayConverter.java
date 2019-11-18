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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Reference;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
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
                final FeatureSet featureset = WPSConvertersUtils.readFeatureCollectionFromJson(URI.create(source.getHref()));

                Long count = FeatureStoreUtilities.getCount(featureset);
                if (count != 1)
                    throw new UnconvertibleObjectException("Expected size for feature collection was 1." + "Found : " + count);

                try (Stream<Feature> stream = featureset.features(false)) {
                    final Feature feature = stream.findFirst().get();
                    final GeometryCollection geometryCollection = FeatureExt.getDefaultGeometryValue(feature)
                            .filter(GeometryCollection.class::isInstance)
                            .map(GeometryCollection.class::cast)
                            .orElseThrow(() -> new UnconvertibleObjectException("The found value may not be of GeometryCollection type or may be null"));

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
