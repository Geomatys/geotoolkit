/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.mapinfo;

import java.util.Collections;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.apache.sis.referencing.datum.DefaultEllipsoid;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.util.FactoryException;

import java.util.HashMap;
import java.util.Map;
import org.apache.sis.measure.Units;
import org.apache.sis.util.ArgumentChecks;

import static org.geotoolkit.internal.InternalUtilities.epsilonEqual;

/**
 * A class to identify mapInfo ellipsoïds defined by MapInfo. We give them an EPSG equivalent, or just register it's
 * data as specified in MapInfo.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 21/03/13
 */
public class EllipsoidIdentifier {

    /** A datum authority factory to build ellipsoïd from their EPSG code. */
    private static final DatumAuthorityFactory DATUM_AUTHORITY_FACTORY = AuthorityFactoryFinder.getDatumAuthorityFactory("EPSG", null);

    /** A table to map MapInfo ellipsoïd codes with their EPSG equivalent. */
    private static final Map<Integer, Integer> ELLIPSOID_TABLE = new HashMap<Integer, Integer>();

    /** A map to bind MapInfo ellipsoïd codes with built ellipsoïds. It only defines ellipsoïds which don't get EPSG codes */
    private static final Map<Integer, Ellipsoid> CUSTOM_ELLIPSOIDS = new HashMap<Integer, Ellipsoid>();

    /**
     * Return the {@link Ellipsoid} pointed by given MapInfo code.
     * @param mapinfoCode The code of the datum to retrieve, as given by MapInfo.
     * @return The ellipsoid which the given MapInfo code represents, or null if we can't find any matching ellipsoid.
     * @throws FactoryException if we got a problem while building ellipsoid.
     */
    public static Ellipsoid getEllipsoid(Integer mapinfoCode) throws FactoryException {
        Ellipsoid result = null;
        ArgumentChecks.ensureNonNull("MapInfo Ellipsoïd Code", mapinfoCode);
        Integer epsgCode = ELLIPSOID_TABLE.get(mapinfoCode);
        if(epsgCode != null) {
            result = DATUM_AUTHORITY_FACTORY.createEllipsoid(epsgCode.toString());
        } else {
            result = CUSTOM_ELLIPSOIDS.get(mapinfoCode);
        }

        return result;
    }

    /**
     * Search a MIF code for the given EPSG ellipsoid code.
     * @param epsgCode The EPSG code which represents the wanted ellipsoid.
     * @return The MIF code for the found ellipsoid, or -1 if no equivalent can be found.
     */
    public static int getMIFCodeFromEPSG(int epsgCode) {
        for(Map.Entry<Integer, Integer> pair : ELLIPSOID_TABLE.entrySet()) {
            if(pair.getValue().equals(epsgCode)) {
                return pair.getKey();
            }
        }
        return -1;
    }

    /**
     * Search a MIF code for the given ellipsoid.
     * @param source the ellipsoid we want an equivalent for.
     * @return the MIF code which match the given ellipsoid.
     */
    public static int getMIFCode(Ellipsoid source) throws FactoryException {
        int mifCode = getMIFCodeFromEPSG(IdentifiedObjects.lookupEpsgCode(source, false));

        if (mifCode < 0) {
            for (Map.Entry<Integer, Ellipsoid> ellipsoid : CUSTOM_ELLIPSOIDS.entrySet()) {
                final double smAxis = ellipsoid.getValue().getSemiMajorAxis();
                final double flat   = ellipsoid.getValue().getInverseFlattening();
                if (epsilonEqual(smAxis, source.getSemiMajorAxis()) && epsilonEqual(flat, source.getInverseFlattening())) {
                    mifCode = ellipsoid.getKey();
                    break;
                }
            }
        }

        return mifCode;
    }

    static {
        ELLIPSOID_TABLE.put( 9, 7001);
        ELLIPSOID_TABLE.put(13, 7002);
        ELLIPSOID_TABLE.put(51, 7041);
        ELLIPSOID_TABLE.put( 2, 7003);
        ELLIPSOID_TABLE.put(10, 7004);
        // 35
        // 14
        ELLIPSOID_TABLE.put(36, 7007);
        ELLIPSOID_TABLE.put( 7, 7008);
        ELLIPSOID_TABLE.put( 8, 7009);
        ELLIPSOID_TABLE.put( 6, 7034);
        ELLIPSOID_TABLE.put(15, 7013);
        ELLIPSOID_TABLE.put(30, 7011);
        // 37
        // 16
        // 38
        ELLIPSOID_TABLE.put(39, 7016);
        ELLIPSOID_TABLE.put(11, 7015);
        ELLIPSOID_TABLE.put(40, 7044);
        // 50
        ELLIPSOID_TABLE.put(17, 7018);
        ELLIPSOID_TABLE.put(48, 7018);
        // 18
        // 19
        // 20
        ELLIPSOID_TABLE.put(21, 7036);
        ELLIPSOID_TABLE.put( 0, 7019);
        ELLIPSOID_TABLE.put( 5, 7022);
        ELLIPSOID_TABLE.put(22, 7020);
        ELLIPSOID_TABLE.put(23, 7053);
        ELLIPSOID_TABLE.put(31, 7049);
        ELLIPSOID_TABLE.put(41, 7021);
        ELLIPSOID_TABLE.put( 4, 7022);
        // 49
        // 3
        // 32
        // 33
        ELLIPSOID_TABLE.put(42, 7025);
        ELLIPSOID_TABLE.put(43, 7043);
        ELLIPSOID_TABLE.put(43, 7043);
        ELLIPSOID_TABLE.put(44, 7032);
        ELLIPSOID_TABLE.put(45, 7033);
        ELLIPSOID_TABLE.put(46, 7027);
        // 52
        ELLIPSOID_TABLE.put(24, 7003);
        ELLIPSOID_TABLE.put(12, 7052);
        ELLIPSOID_TABLE.put(47, 7028);
        // 34
        ELLIPSOID_TABLE.put(25, 7029);
        // 26
        ELLIPSOID_TABLE.put(27, 7025);
        ELLIPSOID_TABLE.put( 1, 7043);
        ELLIPSOID_TABLE.put(28, 7030);
        ELLIPSOID_TABLE.put(29, 7030);
        ELLIPSOID_TABLE.put(54, 7030);

        // Here we build ellipsoïds we did not found epsg equivalent for.
        CUSTOM_ELLIPSOIDS.put(35, DefaultEllipsoid.createFlattenedSphere(
                name("Bessel 1841 (modified for NGO 1948)"), 6377492.0176, 299.15281, Units.METRE));
        CUSTOM_ELLIPSOIDS.put(14, DefaultEllipsoid.createFlattenedSphere(
                name("Bessel 1841 (modified for Schwarzeck)"), 6377483.865, 299.1528128, Units.METRE));

        CUSTOM_ELLIPSOIDS.put(37, DefaultEllipsoid.createFlattenedSphere(
                name("Clarke 1880 (modified for Jamaica)"), 6378249.136, 293.46631, Units.METRE));
        CUSTOM_ELLIPSOIDS.put(16, DefaultEllipsoid.createFlattenedSphere(
                name("Clarke 1880 (modified for Merchich)"), 6378249.2, 293.46598, Units.METRE));
        CUSTOM_ELLIPSOIDS.put(38, DefaultEllipsoid.createFlattenedSphere(
                name("Clarke 1880 (modified for Palestine)"), 6378300.79, 293.46623, Units.METRE));

        CUSTOM_ELLIPSOIDS.put(50, DefaultEllipsoid.createFlattenedSphere(
                name("Everest (Pakistan)"), 6377309.613, 300.8017, Units.METRE));

        CUSTOM_ELLIPSOIDS.put(18, DefaultEllipsoid.createFlattenedSphere(
                name("Fischer 1960"), 6378166.0, 298.3, Units.METRE));
        CUSTOM_ELLIPSOIDS.put(19, DefaultEllipsoid.createFlattenedSphere(
                name("Fischer 1960 (modified for South Asia)"), 6378155.0, 298.3, Units.METRE));
        CUSTOM_ELLIPSOIDS.put(20, DefaultEllipsoid.createFlattenedSphere(
                name("Fischer 1968"), 6378150.0, 298.3, Units.METRE));

        CUSTOM_ELLIPSOIDS.put(49, DefaultEllipsoid.createFlattenedSphere(
                name("Irish (WOFO)"), 6377542.178, 299.325, Units.METRE));

        CUSTOM_ELLIPSOIDS.put( 3, DefaultEllipsoid.createFlattenedSphere(
                name("Krassovsky"), 6378245.0, 298.3, Units.METRE));

        CUSTOM_ELLIPSOIDS.put(32, DefaultEllipsoid.createFlattenedSphere(
                name("MERIT 83"), 6378137.0, 298.257, Units.METRE));

        CUSTOM_ELLIPSOIDS.put(33, DefaultEllipsoid.createFlattenedSphere(
                name("New International 1967"), 6378157.5, 298.25, Units.METRE));

        CUSTOM_ELLIPSOIDS.put(52, DefaultEllipsoid.createFlattenedSphere(
                name("PZ90"), 6378136.0, 298.257839303, Units.METRE));

        CUSTOM_ELLIPSOIDS.put(34, DefaultEllipsoid.createFlattenedSphere(
                name("Walbeck"), 6376896.0, 302.78, Units.METRE));

        CUSTOM_ELLIPSOIDS.put(26, DefaultEllipsoid.createFlattenedSphere(
                name("WGS 60"), 6378165.0, 298.3, Units.METRE));
    }

    private static Map<String,?> name(final String name) {
        return Collections.singletonMap(Ellipsoid.NAME_KEY, name);
    }
}
