package org.geotoolkit.storage.dggs;

import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.geometry.Envelope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;

import static org.geotoolkit.storage.coverage.BandedCoverageResource.sample;
import org.geotoolkit.storage.rs.Address;
import org.geotoolkit.storage.rs.AddressIterator;
import org.geotoolkit.storage.rs.ReferencedGridTransform;

public class DiscreteGlobalGridResourceToGridCoverageResourceConverter {

    public static GridCoverage toGridCoverageResource(DiscreteGlobalGridResource resource, int... range)
            throws DataStoreException, CannotEvaluateException
    {
        ArgumentChecks.ensureNonNull("resource", resource);

        // 1. If not provided, build a domain representing the full extent of the data
        Optional<Envelope> envelopeOpt = resource.getEnvelope();
        if (envelopeOpt.isEmpty()) throw new DataStoreException("Resource has no envelope.");
        Envelope envelope = envelopeOpt.get();

        int gridWidth = 1, gridHeight = 1;
        GridGeometry domain;

        // Extract highest available resolution or grid arrangement
        int level = resource.getAvailableDepths().getMaxValue();
        final DiscreteGlobalGridGeometry gridGeometry = resource.getGridGeometry();
        final ReferencedGridTransform gridToRS = gridGeometry.getGridToRS();
        final DiscreteGlobalGridReferenceSystem dggrs = gridGeometry.getReferenceSystem();
        final DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();

        DiscreteGlobalGridCoverage dggsCoverage = resource.read((GridGeometry) null); // Read all zones at default level if null

        try {
            coder.setPrecisionLevel(level);
            AddressIterator it = dggsCoverage.createIterator();

            List<Object> ids = new ArrayList<>();
            it.rewind();
            while (it.next()) {
                int[] position = it.getPosition();
                final Address address = gridToRS.toAddress(position);
                ids.add(address.getOrdinate(0));
            }
            int numZones = ids.size();

            gridWidth = numZones;
            GridExtent extent = new GridExtent(gridWidth, 1);
            GeneralEnvelope genv = new GeneralEnvelope(envelope); // the bounding box of all zones
            domain = new GridGeometry(extent, genv, GridOrientation.REFLECTION_Y);
        } catch (Exception e) {
            throw new DataStoreException(e);
        }

        return sample(resource, domain, range);
    }















//    public static GridCoverageResource toGridCoverageResource(DiscreteGlobalGridCoverage dggCoverage) throws DataStoreException, TransformException {
//        // 1. Get DGGS geometry, number of bands
//        DiscreteGlobalGridGeometry geom = dggCoverage.getGeometry();
//        int numBands = dggCoverage.getNumBands();
//        ZoneIterator it = dggCoverage.createIterator();
//
//        // 2. Enumerate zones (order is arbitrary—if you want regular grid, you may need to impose an order)
//        List<ZonalIdentifier> ids = new ArrayList<>();
//        List<double[]> zoneValues = new ArrayList<>();
//
//        it.rewind();
//        while (it.next()) {
//            ids.add(it.getPosition());
//            zoneValues.add(it.getCell((double[]) null));
//        }
//
//        // 3. Build a regular grid extent and geometry — depends heavily on your DGGS structure;
//        // if 1D: [ids.size()]
//        // if 2D: [width, height] and map ids to grid cells
//
//        int gridSize = ids.size();
//        int[] gridShape = new int[] { gridSize };  // Simplest: 1D grid
//
//        // TODO: for 2D or more, organize ids into a grid and fill accordingly
//
//        // 4. Construct data array (bands x cells) or suitable raster
//        double[][] data = new double[numBands][gridSize];
//        for (int i = 0; i < gridSize; i++) {
//            double[] cell = zoneValues.get(i);
//            for (int b = 0; b < numBands; b++) data[b][i] = cell[b];
//        }
//
//        // 5. Get sample dimensions (bands)
//        List<SampleDimension> bands = dggCoverage.getSampleDimensions();
//
//        // 6. Define a grid geometry (envelope, extent)
//        // The envelope may be constructed as the union of all zone envelopes,
//        // or you may reconstruct a regular grid if the DGGS permits.
//        GeneralEnvelope env = new GeneralEnvelope(1);
//        env.setRange(0, 0, gridSize - 1);
//        GridExtent extent = new GridExtent(gridShape);
//        GridGeometry gridGeom = new GridGeometry(extent, env);
//
//        // 7. Build a suitable GridCoverageResource (you may need to wrap as an anonymous class with GridCoverage inside)
//        return new ArrayGridCoverageResource(gridGeom, bands, data); // Replace with your actual resource type.
//    }
//
//
//    public static GridCoverageResource toGridCoverageResource(DiscreteGlobalGridResource dggResource, int level) throws DataStoreException {
//        // 1. DGGS system and zones
//        DiscreteGlobalGridReferenceSystem dggs = dggResource.getGridReferenceSystem();
//        DiscreteGlobalGridCoverage dggsCoverage = dggResource.read((GridGeometry) null); // Read all zones at default level if null
//        DiscreteGlobalGridReferenceSystem.Coder coder = dggs.createCoder();
//        ZoneIterator it = dggsCoverage.createIterator();
//
//        // Get all zones/cells for this level
//        List<Zone> zones = coder.
//        if (zones.isEmpty()) throw new DataStoreException("No zones found at level " + level);
//
//        // 2. Determine band/sample info and allocate the data array
//        List<SampleDimension> bands = dggResource.getSampleDimensions();
//        int numBands = bands.size();
//        int numCells = zones.size();
//
//        // 2. Enumerate zones (order is arbitrary—if you want regular grid, you may need to impose an order)
//        List<ZonalIdentifier> ids = new ArrayList<>();
//        List<double[]> zoneValues = new ArrayList<>();
//
//
//        it.rewind();
//        while (it.next()) {
//            ids.add(it.getPosition());
//            zoneValues.add(it.getCell((double[]) null));
//        }
//
//        // For 1D grid (cells), simply numCells entries. For raster-like grid, map accordingly.
//        double[][] data = new double[numBands][numCells];
//
//        // 3. Read values for each zone (using zone identifiers)
//        List<ZonalIdentifier> identifiers = zones.stream().map(Zone::getIdentifier).toList();
//        DiscreteGlobalGridCoverage dggCoverage = dggResource.read(identifiers);
//
//        // 4. Fill data array band-wise
//        for (int b = 0; b < numBands; b++) {
//            TupleArray bandValues = dggCoverage.getBand(b);
//            for (int i = 0; i < numCells; i++) {
//                data[b][i] = bandValues.doubleValue(i);
//            }
//        }
//
//        // 5. Create a minimal grid geometry.
//        // You may want to reconstruct a 2D grid if DGGS supports that; here we use 1D.
//        GridExtent extent = new GridExtent(numCells);
//        GeneralEnvelope env = new GeneralEnvelope(1);
//        // Optionally, union all cell envelopes for the bounding box, or center cells linearly
//        env.setRange(0, 0, numCells - 1); // Placeholder
//        GridGeometry gridGeom = new GridGeometry(extent, env);
//
//        // 6. Create and return a new GridCoverageResource
//        // You need a simple implementation for a GridCoverageResource backed by array data, or use Geotoolkit's.
//        // Here’s an example using a hypothetical ArrayGridCoverageResource:
//        return new ArrayGridCoverageResource(gridGeom, bands, data);
//
//        GridCoverageResource resource = new GridCoverageResource() {
//            @Override
//            public GridGeometry getGridGeometry() throws DataStoreException {
//                return null;
//            }
//
//            @Override
//            public List<SampleDimension> getSampleDimensions() throws DataStoreException {
//                return List.of();
//            }
//
//            @Override
//            public GridCoverage read(GridGeometry domain, int... ranges) throws DataStoreException {
//                return null;
//            }
//
//            @Override
//            public Optional<GenericName> getIdentifier() throws DataStoreException {
//                return Optional.empty();
//            }
//
//            @Override
//            public Metadata getMetadata() throws DataStoreException {
//                return null;
//            }
//
//            @Override
//            public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
//
//            }
//
//            @Override
//            public <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
//
//            }
//        }
//    }

}
