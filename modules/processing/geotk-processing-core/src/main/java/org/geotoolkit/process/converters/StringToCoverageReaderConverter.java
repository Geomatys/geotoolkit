/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.converters;

import java.net.MalformedURLException;
import java.net.URL;

import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.util.converter.SimpleConverter;


/**
 * Implementation of ObjectConverter to convert a String into a CoordinateReferenceSystem.
 * The String will be a CRS code like :"EPSG:3395" or "EPSG:4326"
 * @author Quentin Boileau
 * @module pending
 */
public class StringToCoverageReaderConverter extends SimpleConverter<String, GridCoverageReader> {

    private static StringToCoverageReaderConverter INSTANCE;

    private StringToCoverageReaderConverter() {
    }

    public static StringToCoverageReaderConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StringToCoverageReaderConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? super String> getSourceClass() {
        return String.class;
    }

    @Override
    public Class<? extends GridCoverageReader> getTargetClass() {
        return GridCoverageReader.class;
    }

    @Override
    public GridCoverageReader convert(final String s) throws NonconvertibleObjectException {
        if (s == null) {
            throw new NonconvertibleObjectException("Empty Coverage File");
        }

        try {
            String url = new String();
            if (s.startsWith("file:")) {
                url = s;
            } else {
                url = "file:" + s;
            }
            GridCoverageReader reader = CoverageIO.createSimpleReader(new URL(url));

            if(reader == null){
                throw new NonconvertibleObjectException("Invalid Coverage File");
            }

            return reader;
        } catch (MalformedURLException ex) {
            throw new NonconvertibleObjectException(ex);
        } catch (CoverageStoreException ex) {
            throw new NonconvertibleObjectException(ex);
        }
    }
}
