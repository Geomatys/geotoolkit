/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.benchmarks;

import java.util.Random;
import java.util.stream.IntStream;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.geometry.jts.JTS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.util.FactoryException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Measure the impact of the chosen {@link CoordinateSequence coordinate sequence} type over some geometric operations.
 * There is room for improvement, because test geometries are fully random, and might not properly reflect any real
 * use-case.
 *
 * @author Alexis Manin (Geomatys)
 */
@Threads(4)
@Fork(value = 2)
public class JTSCoordinateSequenceType {
    private static Random RAND = new Random();
    private static final GeometryFactory GF = JTS.getFactory();
    private static final CoordinateReferenceSystem SOURCE_CRS = CommonCRS.defaultGeographic();
    private static final CoordinateReferenceSystem TARGET_CRS;
    private static final CoordinateOperation SOURCE_TO_TARGET;
    static {
        try {
            TARGET_CRS = CRS.forCode("EPSG:3857");
            SOURCE_TO_TARGET = CRS.findOperation(SOURCE_CRS, TARGET_CRS, null);
        } catch (FactoryException e) {
            final ExceptionInInitializerError err = new ExceptionInInitializerError("Cannot initialize coordinate operation for reprojection benchmark");
            err.initCause(e);
            throw err;
        }

    }

    @State(Scope.Benchmark)
    public static class Input {

        private final  Random rand = new Random();

        @Param({"10", "100", "1000"})
        int nbPoints;

        @Param({"true", "false"})
        boolean packed;

        private LineString geometry;

        @Setup
        public void createGeometry() {
            if (packed) {
                final PackedCoordinateSequence.Double packed = new PackedCoordinateSequence.Double(nbPoints, 2, 0);
                final double[] values = packed.getRawCoordinates();
                IntStream.range(0, nbPoints).forEach(i -> {
                    values[i*2] = rand.nextDouble() * 180;
                    values[i*2+1] = rand.nextDouble() * 90;
                });
                geometry = GF.createLineString(packed);
            } else {
                final Coordinate[] coords = IntStream.range(0, nbPoints)
                        .mapToObj(i -> new Coordinate(rand.nextDouble() * 180, rand.nextDouble() * 90))
                        .toArray(Coordinate[]::new);
                geometry = GF.createLineString(coords);
            }
            geometry.setUserData(SOURCE_CRS);
        }

        public LineString get() {
            return geometry;
        }
    }

    @State(Scope.Benchmark)
    public static class Clip {

        private final  Random rand = new Random();
        Geometry clip;

        @Setup
        public void create() {
            double centerX = rand.nextDouble() * 360 - 180;
            double centerY = rand.nextDouble() * 180 - 90;
            double radiusX = rand.nextDouble() * 45;
            double radiusY = rand.nextDouble() * 45;

            clip = GF.createPolygon(new Coordinate[]{
                    new Coordinate(centerX - radiusX, centerY - radiusY),
                    new Coordinate(centerX + radiusX, centerY - radiusY),
                    new Coordinate(centerX + radiusX, centerY + radiusY),
                    new Coordinate(centerX - radiusX, centerY + radiusY),
                    new Coordinate(centerX - radiusX, centerY - radiusY),
            });
            clip.setUserData(SOURCE_CRS);
        }

        public Geometry get() {
            return clip;
        }
    }

    @Warmup(iterations = 2, time = 5)
    @Measurement(iterations = 2, time = 5)
    @Benchmark
    public void reprojection(Input input) throws Exception {
        final LineString geom = input.get();
        final Geometry output;
        if (input.packed) {
            final PackedCoordinateSequence.Double packedCs = (PackedCoordinateSequence.Double) geom.getCoordinateSequence();
            final PackedCoordinateSequence.Double copy = packedCs.copy();
            SOURCE_TO_TARGET.getMathTransform().transform(packedCs.getRawCoordinates(), 0, copy.getRawCoordinates(), 0, packedCs.size());
            output = GF.createLineString(copy);
            output.setUserData(geom.getUserData());
        } else {
            output = JTS.transform(geom, SOURCE_TO_TARGET.getMathTransform());
        }

        if (output.getNumPoints() != geom.getNumPoints()) throw new IllegalStateException("Reprojection has done something weird");
    }

    @Warmup(iterations = 4, time = 5)
    @Measurement(iterations = 4, time = 5)
    @Benchmark
    public void clip(Input input, Clip clip) throws Exception {
        final Geometry difference = input.get().difference(clip.get());
        if (difference == null) throw new IllegalStateException("Should never return null");
    }
}
