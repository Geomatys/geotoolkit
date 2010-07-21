/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 */
package org.geotoolkit.demo.referencing;

import org.opengis.util.FactoryException;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeocentricCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.geometry.GeneralDirectPosition;


/**
 * Demonstration of a few coordinate conversions.
 * <p>
 * Requirements:
 * <ul>
 *   <li>{@code geotk-utility} module</li>
 *   <li>{@code geotk-metadata} module</li>
 *   <li>{@code geotk-referencing} module</li>
 *   <li>{@code geotk-epsg} module (only for {@link #geocentricToProjected()})</li>
 *   <li>JavaDB or Derby (only for {@link #geocentricToProjected()})</li>
 * </ul>
 */
public class CoordinateConversion {
    /**
     * Converts a coordinate from Geocentric CRS to Geographic CRS.
     * This demo uses predefined CRS constants for simplicity. The
     * coordinate is Everest Mount (27°59'17"N 86°55'31"E﻿).
     *
     * @throws FactoryException If an error occurred while searching for a conversion.
     * @throws TransformException If an error occurred while performing the conversion.
     */
    public static void geocentricToGeographic() throws FactoryException, TransformException {
        CoordinateReferenceSystem sourceCRS = DefaultGeocentricCRS.CARTESIAN;
        CoordinateReferenceSystem targetCRS = DefaultGeographicCRS.WGS84_3D;
        MathTransform tr = CRS.findMathTransform(sourceCRS, targetCRS);
        /*
         * From this point we can convert an arbitrary amount of coordinates using the
         * same MathTransform object. It could be in concurrent threads if we wish.
         */
        DirectPosition sourcePt = new GeneralDirectPosition(302742.5, 5636029.0, 2979489.2);
        DirectPosition targetPt = tr.transform(sourcePt, null);
        System.out.println("Source point: " + sourcePt);
        System.out.println("Target point: " + targetPt);
    }

    /**
     * This is the same demo than above, except that the target CRS is now a
     * projected one instead than a geographic one. Because there is so many
     * projected CRS available, Geotoolkit.org does not define any constant
     * for them. We have to pick one from a database.
     * <p>
     * For this demo, we use EPSG:3395 which stands for "WGS 84 / World Mercator".
     * For browsing projections codes on-line, see http://www.epsg-registry.org/
     *
     * {@note For running this demo, the JavaDB or Derby database must be available
     * on the classpath. JavaDB is bundled in Sun JDK distribution and doesn't need
     * a separated download, but still need to be declared explicitly on the classpath.
     * It is usually located in a "db" folder in the Java installation directory.}
     *
     * @throws FactoryException If an error occurred while searching for a conversion.
     * @throws TransformException If an error occurred while performing the conversion.
     */
    public static void geocentricToProjected() throws FactoryException, TransformException {
        CoordinateReferenceSystem sourceCRS = DefaultGeocentricCRS.CARTESIAN;
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3395");
        MathTransform tr = CRS.findMathTransform(sourceCRS, targetCRS);
        /*
         * From this point we can convert an arbitrary amount of coordinates using the
         * same MathTransform object. It could be in concurrent threads if we wish.
         */
        DirectPosition sourcePt = new GeneralDirectPosition(302742.5, 5636029.0, 2979489.2);
        DirectPosition targetPt = tr.transform(sourcePt, null);
        System.out.println("Source point: " + sourcePt);
        System.out.println("Target point: " + targetPt);
    }

    /**
     * Runs the demo from the command line.
     *
     * @param  args Command-line arguments (ignored).
     * @throws FactoryException If an error occurred while searching for a conversion.
     * @throws TransformException If an error occurred while performing the conversion.
     */
    public static void main(String[] args) throws FactoryException, TransformException {
        System.out.println("Geocentric to Geographic CRS");
        geocentricToGeographic();

        System.out.println();
        System.out.println("Geocentric to Projected CRS (World Mercator)");
        geocentricToProjected();
    }
}
