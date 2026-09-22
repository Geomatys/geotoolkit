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
package org.geotoolkit.hdf.api;

import java.io.IOException;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.hdf.ObjectHeader;
import org.geotoolkit.hdf.io.Connector;

/**
 * A dataset associated with a dimension of another dataset.
 * Not yet fully implemented: it currently behaves as a plain dataset.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_h5_d_s__u_g.html#subsec_dim_scales_spec_store">HDF5 Dimension Scale Specification - Storage</a>
 * @author Johann Sorel (Geomatys)
 */
public class DimensionScale extends Dataset {

    /**
     * Create a dimension scale from the address of its object header.
     *
     * @param parent parent group, not {@code null}
     * @param connector file connector, not {@code null}
     * @param objectHeaderAddress address of this dataset object header
     * @param name name of this dataset within its parent
     * @throws IOException if an I/O error occurred.
     * @throws DataStoreException if the object header is invalid or unsupported.
     */
    public DimensionScale(Group parent, Connector connector, long objectHeaderAddress, String name) throws IOException, DataStoreException {
        super(parent, connector, objectHeaderAddress, name);
    }

    DimensionScale(Group parent, Connector connector, long objectHeaderAddress, String name, ObjectHeader header) throws IOException, DataStoreException {
        super(parent, connector, objectHeaderAddress, name);
    }
}
