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
import org.geotoolkit.data.geojson.utils.GeoJSONUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class GeoJSONFeatureCollection extends GeoJSONObject implements GeoJSONFeatureIterator<GeoJSONFeature> {

    private List<GeoJSONFeature> features = new ArrayList<>();

    transient JsonLocation currentPos = null;
    transient GeoJSONFeature current = null;
    transient int currentIdx = 0;
    transient InputStream readStream;
    transient JsonParser parser;

    /**
     * If current GeoJSONFeatureCollection is in lazy parsing mode,
     * sourceInput should be not {@code null} and used to create {@link JsonParser object}
     */
    transient Path sourceInput = null;
    transient JsonLocation startPos = null;
    transient JsonLocation endPos = null;
    transient Boolean lazyMode;

    public GeoJSONFeatureCollection(Boolean lazyMode) {
        setType(GeoJSONTypes.FEATURE_COLLECTION);
        this.lazyMode = lazyMode;
    }

    public List<GeoJSONFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<GeoJSONFeature> features) {
        this.features = features;
    }

    public void setStartPosition(JsonLocation startPos) {
        this.startPos = startPos;
    }

    public void setEndPosition(JsonLocation endPos) {
        this.endPos = endPos;
    }

    public void setSourceInput(Path input) {
        this.sourceInput = input;
    }

    public Boolean isLazyMode() {
        return lazyMode;
    }

    @Override
    public boolean hasNext() {
        try {
            findNext();
            return current != null;
        } catch (IOException e) {
            throw new FeatureStoreRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public GeoJSONFeature next() {
        try {
            findNext();
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

    /**
     * Find next Feature from features list or as lazy parsing.
     * @throws IOException
     */
    private void findNext() throws IOException {
        if (current != null) return;
        if (lazyMode) {
            if (sourceInput == null || startPos == null || endPos == null) return;

            if (parser == null) {
                readStream = Files.newInputStream(sourceInput);
                parser = GeoJSONParser.FACTORY.createParser(readStream);
            }

            //loop to FeatureCollection start
            if (currentPos == null) {
                while (!GeoJSONUtils.equals(startPos, currentPos)) {
                    parser.nextToken();
                    currentPos = parser.getCurrentLocation();
                }
            }

            current = null;

            // set parser to feature object start
            while (parser.getCurrentToken() != JsonToken.START_OBJECT && !GeoJSONUtils.equals(endPos, currentPos)) {
                parser.nextToken();
                currentPos = parser.getCurrentLocation();
            }

            if (!GeoJSONUtils.equals(endPos, currentPos)) {
                GeoJSONObject obj = GeoJSONParser.parseGeoJSONObject(parser);
                if (obj instanceof GeoJSONFeature) {
                    current = (GeoJSONFeature) obj;
                }
                currentPos = parser.getCurrentLocation();
            }
        } else {
            if (currentIdx < features.size()) {
                current = features.get(currentIdx++);
            } else {
                current = null;
            }
        }
    }

    @Override
    public void remove() {
        //do nothing
    }

    @Override
    public void close() {
        //close read stream
        if (readStream != null) {
            try {
                readStream.close();
            } catch (IOException e) {
                throw new FeatureStoreRuntimeException(e.getMessage(), e);
            }
        }
        //close parser
        if (parser != null && !parser.isClosed()) {
            try {
                parser.close();
            } catch (IOException e) {
                throw new FeatureStoreRuntimeException(e.getMessage(), e);
            }
        }
    }
}
