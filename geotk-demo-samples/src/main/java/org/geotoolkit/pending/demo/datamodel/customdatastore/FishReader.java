package org.geotoolkit.pending.demo.datamodel.customdatastore;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

public class FishReader implements Iterator<Feature> {


    private final GeometryFactory gf = org.geotoolkit.geometry.jts.JTS.getFactory();
    private final FeatureType type;
    private final Scanner scanner;

    private Feature current = null;
    private int inc = 0;

    public FishReader(File file, FeatureType type) throws FileNotFoundException {
        this.type = type;
        scanner = new Scanner(file);
    }

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
            current = type.newInstance();
            current.setPropertyValue(AttributeConvention.IDENTIFIER, Integer.toString(inc++));
            final String line = scanner.nextLine();
            final String[] parts = line.split("/");

            current.setPropertyValue("name", parts[0]);
            current.setPropertyValue("length", Integer.valueOf(parts[1]));
            final double x = Double.valueOf(parts[2]);
            final double y = Double.valueOf(parts[3]);
            current.setPropertyValue("position", gf.createPoint(new Coordinate(x, y)));

        }
    }

    public void close() {
        scanner.close();
    }

    @Override
    public void remove() {
        throw new FeatureStoreRuntimeException("Not supported.");
    }
}
