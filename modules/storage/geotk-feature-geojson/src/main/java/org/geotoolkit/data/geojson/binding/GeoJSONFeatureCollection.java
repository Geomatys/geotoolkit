/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.data.geojson.binding;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.geojson.utils.GeoJSONFeatureIterator;
import org.geotoolkit.data.geojson.utils.GeoJSONParser;
import org.geotoolkit.data.geojson.utils.GeoJSONTypes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class GeoJSONFeatureCollection extends GeoJSONObject implements GeoJSONFeatureIterator<GeoJSONFeature> {

    private List<GeoJSONFeature> features = new ArrayList<>();

    transient JsonLocation startIdx = null;
    transient JsonLocation endIdx = null;
    transient JsonLocation currentIdx = null;
    transient GeoJSONFeature current = null;
    transient GeoJSONParser jsonParser;
    transient JsonParser parser;

    public GeoJSONFeatureCollection() {
        setType(GeoJSONTypes.FEATURE_COLLECTION);
    }

    public List<GeoJSONFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<GeoJSONFeature> features) {
        this.features = features;
    }

    public void setStartIdx(JsonLocation startIdx) {
        this.startIdx = startIdx;
    }

    public void setEndIdx(JsonLocation endIdx) {
        this.endIdx = endIdx;
    }

    @Override
    public boolean hasNext() {
        try {
            parse();
            return current != null;
        } catch (IOException e) {
            throw new FeatureStoreRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public GeoJSONFeature next() {
        try {
            parse();
            final GeoJSONFeature ob = current;
            current = null;
            if (ob == null) {
                throw new FeatureStoreRuntimeException("No more feature.");
            }
            return ob;
        } catch (IOException e) {
            throw new FeatureStoreRuntimeException(e.getMessage(), e);
        }
    }

    private void parse() throws IOException {
        if (current != null) return;
        if (startIdx == null || endIdx == null) return;

        if (parser == null) {
            jsonParser = new GeoJSONParser();
            Object sourceRef = startIdx.getSourceRef();
            if (sourceRef instanceof File) {
                parser = GeoJSONParser.FACTORY.createParser((File) sourceRef);
            } else if (sourceRef instanceof InputStream) {
                parser = GeoJSONParser.FACTORY.createParser((InputStream) sourceRef);
            }
        }

        //loop to FeatureCollection start
        if (currentIdx == null) {
            while (!startIdx.equals(currentIdx)) {
                parser.nextToken();
                currentIdx = parser.getCurrentLocation();
            }
        }

        current = null;


        // set parser to feature object start
        while (parser.getCurrentToken() != JsonToken.START_OBJECT && !currentIdx.equals(endIdx)) {
            parser.nextToken();
            currentIdx = parser.getCurrentLocation();
        }

        if (!currentIdx.equals(endIdx)) {
            GeoJSONObject obj = jsonParser.parseGeoJSONObject(parser);
            if (obj instanceof GeoJSONFeature) {
                current = (GeoJSONFeature) obj;
            }
            currentIdx = parser.getCurrentLocation();
        }
    }

    @Override
    public void remove() {
        //do nothing
    }

    @Override
    public void close() {
        if (parser != null) {
            try {
                parser.close();
            } catch (IOException e) {
                throw new FeatureStoreRuntimeException(e.getMessage(), e);
            }
        }
    }
}
