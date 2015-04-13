package org.geotoolkit.pending.demo.datamodel.customdatastore;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.feature.FeatureBuilder;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;

public class FishReader implements FeatureReader<FeatureType, Feature> {


    private final GeometryFactory gf = new GeometryFactory();
    private final FeatureBuilder sfb;
    private final FeatureType type;
    private final Scanner scanner;

    private Feature current = null;
    private int inc = 0;

    public FishReader(File file, FeatureType type) throws FileNotFoundException {
        this.type = type;
        sfb = new FeatureBuilder(type);
        scanner = new Scanner(file);
    }

    @Override
    public FeatureType getFeatureType() {
        return type;
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        read();
        final Feature ob = current;
        current = null;
        if (ob == null) {
            throw new FeatureStoreRuntimeException("No more records.");
        }
        return ob;
    }

    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        read();
        return current != null;
    }

    private void read() throws FeatureStoreRuntimeException {
        if (current != null) {
            return;
        }
        if (scanner.hasNextLine()) {
            sfb.reset();

            final String line = scanner.nextLine();
            final String[] parts = line.split("/");

            sfb.setPropertyValue("name", parts[0]);
            sfb.setPropertyValue("length", Integer.valueOf(parts[1]));
            final double x = Double.valueOf(parts[2]);
            final double y = Double.valueOf(parts[3]);
            sfb.setPropertyValue("position", gf.createPoint(new Coordinate(x, y)));

            current = sfb.buildFeature(Integer.toString(inc++));
        }
    }

    @Override
    public void close() {
        scanner.close();
    }

    @Override
    public void remove() {
        throw new FeatureStoreRuntimeException("Not supported.");
    }
}
