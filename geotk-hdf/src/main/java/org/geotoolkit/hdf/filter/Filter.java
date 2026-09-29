/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.hdf.filter;

import java.io.IOException;

/**
 * A filter of the HDF-5 filter pipeline, applied on the bytes of each chunk.
 * <p>
 * Filters are applied in pipeline order when writing, and reversed in the
 * opposite order when reading.
 *
 * @author Johann Sorel (Geomatys)
 * @see org.geotoolkit.hdf.message.FilterPipelineMessage
 */
public interface Filter {

    /**
     * Reverses this filter on the bytes of a chunk as stored in the file.
     *
     * @param chunkdata filtered chunk bytes
     * @return decoded bytes, may be the given array if the filter has no effect
     * @throws IOException if the bytes could not be decoded.
     */
    byte[] decode(byte[] chunkdata) throws IOException;

    /**
     * Applies this filter on the raw bytes of a chunk.
     *
     * @param chunkdata raw chunk bytes
     * @return encoded bytes, may be the given array if the filter has no effect
     * @throws IOException if the bytes could not be encoded.
     */
    byte[] encode(byte[] chunkdata) throws IOException;

}
