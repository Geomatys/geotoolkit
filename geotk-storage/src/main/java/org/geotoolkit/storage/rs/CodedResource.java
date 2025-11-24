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
package org.geotoolkit.storage.rs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.measure.Quantities;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGrid;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridHierarchy;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.referencing.rs.ReferenceSystems;
import org.geotoolkit.storage.coverage.BandedCoverageResource;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.storage.dggs.internal.shared.GridAsDiscreteGlobalGridResource;
import org.geotoolkit.storage.rs.internal.shared.CodeTransforms;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * A Resource which offer acces to a coverage structured in located cells.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface CodedResource extends BandedCoverageResource {

    @Override
    default Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(getSampleType().getName());
    }

    /**
     * Returns the description of the samples stored.
     *
     * @return Feature type
     */
    FeatureType getSampleType() throws DataStoreException;

    /**
     * Get default resource geometry.
     * @return resource geometry
     */
    CodedGeometry getGridGeometry() throws DataStoreException;

    /**
     * List alternative geometry available.
     * First entry is the default ReferencedGridGeometry from getGridGeometry().
     *
     * @return list of alternative geometry available.
     */
    default List<CodedGeometry> getAlternateGridGeometry() throws DataStoreException{
        return List.of(getGridGeometry());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public default CodedCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        final CodedGeometry resourceGeometry = getGridGeometry();


        //cut domain in single crs grid geometries
        final CoordinateReferenceSystem queryCrs = domain.getCoordinateReferenceSystem();
        final List<SingleCRS> queryCrss = CRS.getSingleComponents(queryCrs);
        final List<CodedGeometry> domainSlices = new ArrayList<>();
        for (SingleCRS scrs : queryCrss) {
            GridGeometry slice;
            try {
                slice = CodeTransforms.slice(domain, scrs);
            } catch (FactoryException ex) {
                throw new DataStoreException(ex);
            }
            CodedGeometry d = new CodedGeometry(slice);
            domainSlices.add(d);
        }



        //ensure we extract a single slice on axes where no range has been defined
        final List<CodedGeometry> querySlices = new ArrayList<>();
        final List<ReferenceSystem> singleComponents = ReferenceSystems.getSingleComponents(resourceGeometry.getReferenceSystem(), true);
        loop:
        for (ReferenceSystem rs : singleComponents) {
            if (rs instanceof DiscreteGlobalGridReferenceSystem dggrs) {
                try {
                    final DiscreteGlobalGridGeometry dgggeom = toDiscreteGlobalGridGeometry(domain, dggrs);
                    querySlices.add(dgggeom);
                } catch (TransformException | IncommensurableException ex) {
                    throw new DataStoreException(ex);
                }
            } else {
                for (CodedGeometry slicegeom : domainSlices) {
                    if (slicegeom.getReferenceSystem().equals(rs)) {
                        continue loop;
                    }
                }
                //not found
                final Optional<CodedGeometry> slice = resourceGeometry.slice(rs);
                if (!slice.isPresent()) continue;
                final CodedGeometry slicegeom = slice.get();
                final Optional<GridGeometry> regularGrid = slicegeom.isRegularGrid();
                if (!regularGrid.isPresent()) continue;
                double ratio = 1.0;
                if (rs instanceof VerticalCRS) {
                    ratio = 0.0; //prefer the lowest level
                }
                GridGeometry build = regularGrid.get().derive().sliceByRatio(ratio).build();
                querySlices.add(new CodedGeometry(build));
            }
        }


        //place RS in order : horiontal > vertical > others
        querySlices.sort(new Comparator<CodedGeometry>() {
            @Override
            public int compare(CodedGeometry o1, CodedGeometry o2) {
                final ReferenceSystem rs1 = o1.getReferenceSystem();
                final ReferenceSystem rs2 = o2.getReferenceSystem();
                if (rs1 instanceof DiscreteGlobalGridReferenceSystem) return -1;
                if (rs2 instanceof DiscreteGlobalGridReferenceSystem) return +1;

                final CoordinateReferenceSystem crs1 = (CoordinateReferenceSystem) rs1;
                final CoordinateReferenceSystem crs2 = (CoordinateReferenceSystem) rs2;
                if (CRS.isHorizontalCRS(crs1)) return -1;
                if (CRS.isHorizontalCRS(crs2)) return +1;
                if (crs1 instanceof VerticalCRS) return -1;
                if (crs2 instanceof VerticalCRS) return +1;
                return 0;
            }
        });
        final CodedGeometry query = CodedGeometry.compound(querySlices.toArray(CodedGeometry[]::new));

        return read(query, range);
    }

    private static DiscreteGlobalGridGeometry toDiscreteGlobalGridGeometry(GridGeometry domain, DiscreteGlobalGridReferenceSystem dggrs) throws TransformException, IncommensurableException {
        Quantity<?> coverageResolution = GridAsDiscreteGlobalGridResource.computeAverageResolution(domain);

        coverageResolution = Quantities.create(coverageResolution.getValue().doubleValue() * 10, Units.METRE);

        //extract zones in the wanted area and resolution
        final DiscreteGlobalGridHierarchy hierarchy = dggrs.getGridSystem().getHierarchy();
        final DiscreteGlobalGrid grid = hierarchy.getGrid(coverageResolution);

        final List<Object> zoneIds;
        try (Stream<Zone> zones = grid.getZones(domain.getEnvelope(dggrs.getGridSystem().getCrs()))) {
            zoneIds = zones.map(Zone::getIdentifier).toList();
        }
        return new DiscreteGlobalGridGeometry(dggrs, zoneIds, null);
    }

    /**
     * Retrieve a set of DGGRS zone data.
     *
     * @param geometry to read
     * @param range bands to select
     * @return DiscreteGlobalGridCoverage, never null
     * @throws DataStoreException
     */
    public CodedCoverage read(CodedGeometry geometry, int ... range) throws DataStoreException;
}
