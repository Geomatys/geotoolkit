/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ubjson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.*;

/**
 * Jackson mapper implementation for UBJSON.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class UBJsonMapper extends ObjectMapper {

    public static class Builder extends MapperBuilder<UBJsonMapper, Builder> {

        Builder(UBJsonMapper mapper) {
            super(mapper);
        }
    }

    public UBJsonMapper() {
        this(new UBJsonFactory());
    }

    public UBJsonMapper(UBJsonFactory f) {
        super(f);
    }

    public static Builder builder() {
        return new Builder(new UBJsonMapper());
    }

    public static Builder builder(UBJsonFactory streamFactory) {
        return new Builder(new UBJsonMapper(streamFactory));
    }

    @Override
    public UBJsonMapper copy() {
        _checkInvalidCopy(UBJsonMapper.class);
        return new UBJsonMapper(tokenStreamFactory().copy());
    }

    // *********************************************************************
    // Basic accessor overrides
    // *********************************************************************

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public UBJsonFactory tokenStreamFactory() {
        return (UBJsonFactory) _jsonFactory;
    }
}
