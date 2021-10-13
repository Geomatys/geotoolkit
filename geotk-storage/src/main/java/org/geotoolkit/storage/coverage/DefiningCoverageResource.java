/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.storage.AbstractGridResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;

/**
 * Define the structure and properties of a CoverageResource to be created.
 *
 * <p>
 * A DefiningCoverageResource is meant to be passed to {@link WritableAggregate#add(org.apache.sis.storage.Resource) }.
 * It allows the target Aggregate to create and prepare space for a new type of coverages
 * without providing any coverage yet.
 * </p>
 * <p>
 * Special implementations, such as tiled formats are encouraged to provider a custom
 * sub-class of DefiningCoverageResource to store additional creation informations.
 * </p>
 * Example of possible informations are :
 * <ul>
 * <li>CoordinateReferenceSystem</li>
 * <li>Pyramid depth</li>
 * <li>encoding</li>
 * <li>compression</li>
 * </ul>
 *
 * <p>
 * Note : this class is experimental and should be moved to SIS when ready.
 * </p>
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefiningCoverageResource extends AbstractGridResource implements org.apache.sis.storage.GridCoverageResource {

    private final GenericName name;
    private GridGeometry gridGeometry;
    private final List<SampleDimension> sampleDimensions = new ArrayList<>();

    /**
     *
     * @param name mandatory new resource name
     */
    public DefiningCoverageResource(String name) {
        this(NamesExt.valueOf(name));
    }

    /**
     *
     * @param name mandatory new resource name
     */
    public DefiningCoverageResource(GenericName name) {
        super(null);
        ArgumentChecks.ensureNonNull("name", name);
        this.name = name;
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        return Optional.of(name);
    }

    /**
     * New resource wanted name.
     *
     * @return new resource name.
     */
    public GenericName getName() {
        return name;
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return gridGeometry;
    }

    public void setGridGeometry(GridGeometry gridGeometry) {
        this.gridGeometry = gridGeometry;
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return Collections.unmodifiableList(sampleDimensions);
    }

    public void setSampleDimensions(List<SampleDimension> sampleDimensions) {
        this.sampleDimensions.clear();
        this.sampleDimensions.addAll(sampleDimensions);
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

}
