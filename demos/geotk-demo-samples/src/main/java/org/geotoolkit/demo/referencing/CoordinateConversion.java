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
import org.geotoolkit.referencing.crs.PredefinedCRS;
import org.apache.sis.geometry.GeneralDirectPosition;


/**
 * Demonstration of a few coordinate conversions. All demos in this file convert the same
 * point (the Everest Mount) from and to different CRS.
 * <p>
 * Requirements:
 * <ul>
 *   <li>{@code geotk-utility} module</li>
 *   <li>{@code geotk-referencing} module</li>
 *   <li>{@code geotk-epsg} module (only for {@link #geographicToProjected()})</li>
 *   <li>JavaDB or Derby (only for {@link #geographicToProjected()})</li>
 * </ul>
 * <p>
 * For convenience, all the above requirements can be replaced by a single dependency toward
 * the {@code geotk-epsg-javadb} module, which will bring everything else through transitive
 * dependencies.
 */
public class CoordinateConversion {
    /**
     * Converts a coordinate from Geocentric CRS to Geographic CRS.
     * This demo uses predefined CRS constants for simplicity. The
     * coordinate is Everest Mount (27°59'17"N 86°55'31"E﻿).
     * <p>
     * Note that the geographic ordinates for this demo are in
     * (<var>longitude</var>, <var>latitude</var>, <var>altitude</var>) order.
     *
     * @throws FactoryException If an error occurred while searching for a conversion.
     * @throws TransformException If an error occurred while performing the conversion.
     */
    public static void geocentricToGeographic() throws FactoryException, TransformException {
        CoordinateReferenceSystem sourceCRS = PredefinedCRS.GEOCENTRIC;
        CoordinateReferenceSystem targetCRS = PredefinedCRS.WGS84_3D;
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
     * This is the same demo than above converting the same point, but the source and target CRS
     * are different.
     *
     * <ul>
     *   <li><p>The source CRS is now a geographic CRS with axis order compliant to the common usage
     *   in geodesy, as defined in the EPSG database: (<var>latitude</var>, <var>longitude</var>).
     *   Note that this is the opposite axis order than the output of previous demos.</p></li>
     *
     *   <li><p>The target CRS is now a projected one instead than a geographic one. Because there
     *   is so many projected CRS available, Geotoolkit.org does not define any constant for them.
     *   We have to pick one from a database.</p></li>
     * </ul>
     *
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
    public static void geographicToProjected() throws FactoryException, TransformException {
        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");  // WGS 84
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:3395");  // WGS 84 / World Mercator
        MathTransform tr = CRS.findMathTransform(sourceCRS, targetCRS);
        /*
         * From this point we can convert an arbitrary amount of coordinates using the
         * same MathTransform object. It could be in concurrent threads if we wish.
         */
        DirectPosition sourcePt = new GeneralDirectPosition(
                27 + (59 + 17.0 / 60) / 60,   // 27°59'17"N
                86 + (55 + 31.0 / 60) / 60);  // 86°55'31"E
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
        System.out.println("Geographic to Projected CRS (World Mercator)");
        System.out.println("Please compare the order of ordinate values in \"Source Point\" below");
        System.out.println("with the order of ordinate values in \"Target Point\" above.");
        geographicToProjected();
    }
}
