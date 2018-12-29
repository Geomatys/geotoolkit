/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012 - 2014, Geomatys
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
package org.geotoolkit.storage.coverage;

import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.util.Collection;
import java.util.List;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.data.multires.MultiResolutionResource;
import org.geotoolkit.data.multires.Pyramid;

/**
 * May be implemented by Coverage reference when the underlying structure is a
 * pyramid.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface PyramidalCoverageResource extends GridCoverageResource, MultiResolutionResource {

    @Override
    Collection<Pyramid> getModels() throws DataStoreException;

    /**
     * Get the defined mode in which datas are stored.
     *
     * @return ViewType, never null
     * @throws org.apache.sis.storage.DataStoreException
     */
    ViewType getPackMode() throws DataStoreException;

    /**
     * Set stored data mode.
     * This won't change the data itself.
     *
     * This method should be called before adding any data.
     *
     * @param packMode
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setPackMode(ViewType packMode) throws DataStoreException;

    /**
     * List sample dimensions.
     *
     * This method should be called before adding any data.
     *
     * @return can be null
     * @throws DataStoreException
     */
    List<GridSampleDimension> getGridSampleDimensions() throws DataStoreException;

    /**
     * Set sample dimensions.
     *
     * @param dimensions
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setGridSampleDimensions(final List<GridSampleDimension> dimensions) throws DataStoreException;

    /**
     * Get default color model.
     *
     * @return ColorModel can be null
     * @throws org.apache.sis.storage.DataStoreException
     */
    ColorModel getColorModel() throws DataStoreException;

    /**
     * Set color model, the store is not require to respect completely the model.
     * The object given is a hint for the store to choose more accurately
     * the storage parameters.
     *
     * This method should be called before adding any data.
     *
     * @param colorModel
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setColorModel(ColorModel colorModel) throws DataStoreException;

    /**
     * Get sample model.
     *
     * @return SampleModel can be null
     * @throws org.apache.sis.storage.DataStoreException
     */
    SampleModel getSampleModel() throws DataStoreException;

    /**
     * Set sample model, the store is not require to respect completely the model
     * The object given is a hint for the store to choose more accurately
     * the storage parameters.
     *
     * This method should be called before adding any data.
     *
     * @param sampleModel
     * @throws org.apache.sis.storage.DataStoreException
     */
    void setSampleModel(SampleModel sampleModel) throws DataStoreException;

    /**
     *
     * @return true if model can be modified
     * @throws org.geotoolkit.coverage.io.CoverageStoreException
     */
    @Override
    boolean isWritable() throws CoverageStoreException;

}
