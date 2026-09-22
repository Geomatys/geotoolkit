/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.hdf.convention;

/**
 * Dimension Scale API is used to reference columns of a dataset to another dataset
 * which in turn describes the dimension.
 *
 * @see <a href="https://support.hdfgroup.org/HDF5/doc/HL/H5DS_Spec.pdf">HDF5 Dimension Scale Specification (PDF)</a>
 * @see <a href="https://confluence.hdfgroup.org/display/HDF5/Dimension+Scales">Dimension Scales</a>
 * @see <a href="https://docs.hdfgroup.org/hdf5/develop/group___h5_d_s.html">Dimension Scales (H5DS) API reference</a>
 * @author Johann Sorel (Geomatys)
 */
public final class HDF5DimensionScaleAPI {

    /** Dataset attribute holding, for each dimension, references to the dimension scales attached to it. */
    public static final String ATT_DIMENSION_LIST = "DIMENSION_LIST";
    /** Dimension scale attribute listing the datasets and dimension indexes the scale is attached to. */
    public static final String ATT_REFERENCE_LIST = "REFERENCE_LIST";
    /** Attribute identifying the kind of object, {@link #VALUE_DIMENSION_SCALE} for a dimension scale. */
    public static final String ATT_CLASS = "CLASS";

    /** Value of the {@link #ATT_CLASS} attribute identifying a dimension scale. */
    public static final String VALUE_DIMENSION_SCALE = "DIMENSION_SCALE";

    private HDF5DimensionScaleAPI(){}

}
