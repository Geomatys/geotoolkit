package org.geotoolkit.pending.demo.datamodel.customdatastore;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

public class FishReader implements FeatureReader<FeatureType, Feature> {


    private final GeometryFactory gf = new GeometryFactory();
    private final SimpleFeatureBuilder sfb;
    private final FeatureType type;
    private final Scanner scanner;

    private SimpleFeature current = null;
    private int inc = 0;

    public FishReader(File file, SimpleFeatureType type) throws FileNotFoundException {
        this.type = type;
        sfb = new SimpleFeatureBuilder(type);
        scanner = new Scanner(file);
    }

    @Override
    public FeatureType getFeatureType() {
        return type;
    }

    @Override
    public SimpleFeature next() throws DataStoreRuntimeException {
        read();
        final SimpleFeature ob = current;
        current = null;
        if (ob == null) {
            throw new DataStoreRuntimeException("No more records.");
        }
        return ob;
    }

    @Override
    public boolean hasNext() throws DataStoreRuntimeException {
        read();
        return current != null;
    }

    private void read() throws DataStoreRuntimeException {
        if (current != null) {
            return;
        }
        if (scanner.hasNextLine()) {
            sfb.reset();

            final String line = scanner.nextLine();
            final String[] parts = line.split("/");

            sfb.set("name", parts[0]);
            sfb.set("length", Integer.valueOf(parts[1]));
            final double x = Double.valueOf(parts[2]);
            final double y = Double.valueOf(parts[3]);
            sfb.set("position", gf.createPoint(new Coordinate(x, y)));

            current = sfb.buildFeature(Integer.toString(inc++));
        }
    }

    @Override
    public void close() {
        scanner.close();
    }

    @Override
    public void remove() {
        throw new DataStoreRuntimeException("Not supported.");
    }
}
