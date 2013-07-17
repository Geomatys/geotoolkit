/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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

import org.geotoolkit.util.FileUtilities;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.xml.v100.ReferenceType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of ObjectConverter to convert a reference into a String.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Alexis Manin (Geomatys).
 */
public final class ReferenceToStringConverter extends AbstractReferenceInputConverter<String> {

    private static ReferenceToStringConverter INSTANCE;

    private ReferenceToStringConverter() {
    }

    public static synchronized ReferenceToStringConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReferenceToStringConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<? extends String> getTargetClass() {
        return String.class;
    }

    /**
     * {@inheritDoc}
     *
     * @return File.
     */
    @Override
    public String convert(final ReferenceType source, final Map<String, Object> params) throws NonconvertibleObjectException {

        String result = null;
        InputStream in = null;
        try {

            in = getInputStreamFromReference(source);
            result = FileUtilities.getStringFromStream(in);

        } catch (IOException ex) {
            throw new NonconvertibleObjectException("Reference file invalid input : IO", ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                throw new NonconvertibleObjectException("Reference file invalid input : IO", ex);
            }
        }
        return result;
    }
}