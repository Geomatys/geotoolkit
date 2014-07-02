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

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.internal.InternalUtilities;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.apache.sis.referencing.datum.BursaWolfParameters;
import org.apache.sis.referencing.datum.DefaultGeodeticDatum;
import org.apache.sis.referencing.datum.DefaultPrimeMeridian;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.util.FactoryException;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.measure.unit.NonSI;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.Utilities;

/**
 * A class which binds mapinfo datum codes with equivalent epsg code.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 06/03/13
 */
public final class DatumIdentifier {

    private static final DatumAuthorityFactory DATUM_AUTHORITY_FACTORY = AuthorityFactoryFinder.getDatumAuthorityFactory("EPSG", null);

    /** A map containing mapinfo datum codes as key, and EPSG code as value. */
    private static final Map<Integer, Integer> DATUM_TABLE = new HashMap<Integer, Integer>();

    /**
     * For datums we don't have EPSG equivalent, we get a list of Bursa Wolf parameters for each one (to WGS 84 transformation).
     * The key is the MapInfo code of the datum. The value is pair of String double array, where the String is the datum name,
     * and the double array is composed of :
     *                   0 --> Ellipsoid code (MapInfo code)
     *                   1 --> Shift on X axis (meters)
     *                   2 --> Shift on Y axis (meters)
     *                   3 --> Shift on Z axis (meters)
     *                   4 --> Rotation on X axis (arc second)
     *                   5 --> Rotation on Y axis (arc second)
     *                   6 --> Rotation on Z axis (arc second)
     *                   7 --> Scaling (in part per million)
     *                   8 --> Prime meridian longitude to greenwich
     */
    private static final Map<Integer, Map.Entry<String, double[]> > HANDED_DATUM_TABLE = new HashMap<Integer, Map.Entry<String, double[]> >();

    private static AbstractMap.SimpleImmutableEntry<String, double[]> buildValue(String name, double... values) {
        return new AbstractMap.SimpleImmutableEntry<String, double[]>(name, values);
    }

    /**
     * Return the EPSG code of the datum pointed by given MapInfo code.
     * @param mapinfoDatumCode The code of the datum to retrieve, as given by MapInfo.
     * @return EPSG code of a referenced datum, or null if no equivalent can be found. Zero will be return if mapinfo
     * code refer to user custom datum.
     */
    public static Integer getEPSGDatumCode(Integer mapinfoDatumCode) {
        ArgumentChecks.ensureNonNull("MapInfo Datum Code", mapinfoDatumCode);
        return DATUM_TABLE.get(mapinfoDatumCode);
    }

    /**
     * Search a MIF code for the given EPSG datum code.
     * @param epsgCode The EPSG code which represents the wanted datum.
     * @return The MIF code for the found datum, or -1 if no equivalent can be found.
     */
    public static int getMIFCodeFromEPSG(int epsgCode) {
        for(Map.Entry<Integer, Integer> pair : DATUM_TABLE.entrySet()) {
            if(pair.getValue().equals(epsgCode)) {
                return pair.getKey();
            }
        }
        return -1;
    }

    /**
     * Build a MIF datum from a Geotk one. If we can find an existing one MapInfo datums, we just return its code.
     * Otherwise, we must build a custom datum using Bursa Wolf transformation (to WGS 84).
     * @param source The datum to write.
     * @return A String representing the given datum as MapInfo needs it.
     * @throws FactoryException If we don't find a per-existing code, and we cannot retrieve datum ellipsoid.
     * @throws DataStoreException Same conditions as FactoryException.
     */
    public static String getMIFDatum(GeodeticDatum source) throws FactoryException, DataStoreException {
        StringBuilder builder = new StringBuilder();

        Integer epsgCode = IdentifiedObjects.lookupEpsgCode(source, false);
        if(epsgCode == null) {
            epsgCode = -1;
        }
        int mifCode = getMIFCodeFromEPSG(epsgCode);

        // If we can't find a code matching, we use the datum ellipsoid / Bursa Wolf parameters (to WGS 84) to search in
        // the handed-built list, or create a custom datum.
        if(mifCode < 0) {
            if(!(source instanceof DefaultGeodeticDatum)) {
                throw new DataStoreException("Unsupported datum type.");
            }

            double primeShift = 0;
            int customCode = 999;
            if(source.getPrimeMeridian() != CommonCRS.WGS84.primeMeridian()) {
                customCode = 9999;
                primeShift = source.getPrimeMeridian().getGreenwichLongitude();
            }
            int ellipsoidCode = EllipsoidIdentifier.getMIFCode(source.getEllipsoid());
            if(ellipsoidCode < 0) {
                throw new DataStoreException("We're unable to find an ellipsoid for source datum.");
            }
            BursaWolfParameters bwParams = null;
            final GeodeticDatum targetDatum = CommonCRS.WGS84.datum();
            for (BursaWolfParameters param : ((DefaultGeodeticDatum) source).getBursaWolfParameters()) {
                if (Utilities.deepEquals(targetDatum, param.getTargetDatum(), ComparisonMode.IGNORE_METADATA)) {
                    bwParams = param;
                    break;
                }
            }
            if(bwParams == null) {
                bwParams = new BursaWolfParameters(targetDatum, null);
            }

            // search in the handed-built list
            double[] comparisonParams = new double[]{
                    ellipsoidCode,
                    bwParams.tX,
                    bwParams.tY,
                    bwParams.tZ,
                    bwParams.rX,
                    bwParams.rY,
                    bwParams.rZ,
                    bwParams.dS,
                    primeShift};
            for(Map.Entry<Integer, Map.Entry<String, double[]>> entry : HANDED_DATUM_TABLE.entrySet()) {
                if(Arrays.equals(comparisonParams, entry.getValue().getValue())) {
                    mifCode = entry.getKey();
                    break;
                }
            }

            if(mifCode >= 0) {
                builder.append(mifCode);
            } else {
                // build a custom CRS.
                builder .append(customCode).append(", ")
                        .append(ellipsoidCode).append(", ")
                        .append(bwParams.tX).append(", ")
                        .append(bwParams.tY).append(", ")
                        .append(bwParams.tZ).append(", ")
                        .append(bwParams.rX).append(", ")
                        .append(bwParams.rY).append(", ")
                        .append(bwParams.rZ).append(", ")
                        .append(bwParams.dS);
                if(primeShift != 0) {
                    builder.append(", ").append(primeShift);
                }
            }
        } else {
            builder.append(mifCode);
        }
        return builder.toString();
    }

    /**
     * Build a Geodetic datum from a given MIF code (should not be 999 or 9999)
     * @param datumCode The code to find a matching datum for.
     * @return A datum matching the given MapInfo code, or null otherwise (Ex: if input code is 999).
     * @throws FactoryException If we get a problem while browsing datum database.
     * @throws DataStoreException If there's a problem building our datum.
     */
    public static GeodeticDatum getDatumFromMIFCode(int datumCode) throws FactoryException, DataStoreException {
        GeodeticDatum datum = null;
        final Integer epsgDatum = DatumIdentifier.getEPSGDatumCode(datumCode);
        if(epsgDatum != null) {
            datum = DATUM_AUTHORITY_FACTORY.createGeodeticDatum(epsgDatum.toString());
        } else {
            if(HANDED_DATUM_TABLE.containsKey(datumCode)) {
                Map.Entry<String, double[]> datumInfo = HANDED_DATUM_TABLE.get(datumCode);
                datum = buildCustomDatum(datumInfo.getKey(), datumInfo.getValue());
            }
        }
        return datum;
    }

    /**
     * Build a Geodetic datum from a MapInfo ellipsoid code and Bursa Wolf parameters to WGS84.
     * @param name The name to give to built datum. If null, a name will be built from ellipsoid name.
     * @param parameters The parameters for datum making. Order is :
     *                   0 --> Ellipsoid code (MapInfo code)
     *                   1 --> Shift on X axis (meters)
     *                   2 --> Shift on Y axis (meters)
     *                   3 --> Shift on Z axis (meters)
     *                   Next are facultative :
     *                   4 --> Rotation on X axis (arc second)
     *                   5 --> Rotation on Y axis (arc second)
     *                   6 --> Rotation on Z axis (arc second)
     *                   7 --> Scaling (in part per million)
     *                   8 --> Prime meridian longitude to greenwich
     * @return A geodetic datum object.
     * @throws DataStoreException If there's not enough parameters.
     * @throws FactoryException If we've got a problem while ellipsoid building.
     */
    public static GeodeticDatum buildCustomDatum(String name, double[] parameters) throws DataStoreException, FactoryException {
        BursaWolfParameters bwParams = new BursaWolfParameters(CommonCRS.WGS84.datum(), null);
        if(parameters.length < 1) {
            throw new DataStoreException("There's not enough parameters to build a valid datum. An ellipsoid code is required.");
        }

        // Get the ellipsoid
        int ellipsoidCode = (int) parameters[0];
        Ellipsoid ellipse = EllipsoidIdentifier.getEllipsoid(ellipsoidCode);

        if(parameters.length > 3) {
            bwParams.tX = parameters[1];
            bwParams.tY = parameters[2];
            bwParams.tZ = parameters[3];
        }

        if(parameters.length > 7) {
            bwParams.rX = parameters[4];
            bwParams.rY = parameters[5];
            bwParams.rZ = parameters[6];
            bwParams.dS = parameters[7];
        }

        // If we've got 9 parameters, the datum is not based on Greenwich meridian.
        PrimeMeridian pMeridian = CommonCRS.WGS84.primeMeridian();
        if(parameters.length > 8 && !InternalUtilities.epsilonEqual(parameters[8], 0)) {
            pMeridian = new DefaultPrimeMeridian(Collections.singletonMap(PrimeMeridian.NAME_KEY,
                    "Greenwich" + ((parameters[8] > 0) ? "+" + parameters[8] : parameters[8])), parameters[8], NonSI.DEGREE_ANGLE);
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(GeodeticDatum.NAME_KEY, (name != null)? name : ellipse.getName().getCode()+"_custom_datum");
        properties.put(DefaultGeodeticDatum.BURSA_WOLF_KEY, bwParams);

        return new DefaultGeodeticDatum(properties, ellipse, pMeridian);
    }

    //Fill table
    static {
        DATUM_TABLE.put(1, 6201);
        DATUM_TABLE.put(2, 6205);
        DATUM_TABLE.put(3, 6204);
        DATUM_TABLE.put(4, 6708);
        DATUM_TABLE.put(5, 6209);
        DATUM_TABLE.put(6, 6210);
        DATUM_TABLE.put(7, 6712);
        DATUM_TABLE.put(8, 6709);
        DATUM_TABLE.put(9, 6707);
        DATUM_TABLE.put(10, 6710);
        DATUM_TABLE.put(11, 6711);
        DATUM_TABLE.put(12, 6202);
        DATUM_TABLE.put(13, 6203);
        DATUM_TABLE.put(14, 6714);
        DATUM_TABLE.put(15, 6216);
        DATUM_TABLE.put(16, 6218);
        DATUM_TABLE.put(17, 6221);
        DATUM_TABLE.put(18, 6716);
        DATUM_TABLE.put(19, 6222);
        DATUM_TABLE.put(20, 6717);
        DATUM_TABLE.put(21, 6223);
        DATUM_TABLE.put(22, 6672);
        DATUM_TABLE.put(23, 6224);
        DATUM_TABLE.put(24, 6225);
        DATUM_TABLE.put(25, 6813);
        // 26
        DATUM_TABLE.put(27, 6719);
        DATUM_TABLE.put(28, 6230);
        DATUM_TABLE.put(29, 6668);
        DATUM_TABLE.put(30, 6684);
        DATUM_TABLE.put(31, 6272);
        DATUM_TABLE.put(32, 6036);
        DATUM_TABLE.put(33, 6019);
        DATUM_TABLE.put(34, 6675);
        // 35
        DATUM_TABLE.put(36, 6254);
        DATUM_TABLE.put(37, 6658);
        DATUM_TABLE.put(38, 6738);
        DATUM_TABLE.put(39, 6236);
        DATUM_TABLE.put(40, 6131);
        // 41
        DATUM_TABLE.put(42, 6300);
        DATUM_TABLE.put(43, 6724);
        DATUM_TABLE.put(44, 6725);
        DATUM_TABLE.put(45, 6244);
        DATUM_TABLE.put(46, 6698);
        DATUM_TABLE.put(47, 6245);
        DATUM_TABLE.put(48, 6726);
        DATUM_TABLE.put(49, 6251);
        DATUM_TABLE.put(50, 6253);
        DATUM_TABLE.put(50, 6253);
        // 51
        DATUM_TABLE.put(52, 6256);
        DATUM_TABLE.put(53, 6616);
        DATUM_TABLE.put(54, 6262);
        DATUM_TABLE.put(55, 6261);
        DATUM_TABLE.put(56, 6727);
        DATUM_TABLE.put(57, 6263);
        // 58
        // 59
        DATUM_TABLE.put(60, 6270);
        DATUM_TABLE.put(61, 6271);
        DATUM_TABLE.put(62, 6267);
        DATUM_TABLE.put(63, 6267);
        DATUM_TABLE.put(64, 6267);
        DATUM_TABLE.put(65, 6267);
        DATUM_TABLE.put(66, 6609);
        DATUM_TABLE.put(67, 6608);
        DATUM_TABLE.put(68, 6267);
        DATUM_TABLE.put(69, 6267);
        DATUM_TABLE.put(70, 6267);
        DATUM_TABLE.put(71, 6267);
        DATUM_TABLE.put(72, 6267);
        DATUM_TABLE.put(73, 6268);
        DATUM_TABLE.put(74, 6140);
        DATUM_TABLE.put(75, 6129);
        DATUM_TABLE.put(76, 6229);
        DATUM_TABLE.put(77, 6135);
        DATUM_TABLE.put(78, 6134);
        DATUM_TABLE.put(79, 6277);
        DATUM_TABLE.put(80, 6728);
        DATUM_TABLE.put(81, 6729);
        DATUM_TABLE.put(82, 6248);
        DATUM_TABLE.put(83, 6139);
        DATUM_TABLE.put(83, 6139);
        DATUM_TABLE.put(84, 6614);
        DATUM_TABLE.put(85, 6194);
        DATUM_TABLE.put(86, 6626);
        DATUM_TABLE.put(86, 6626);
        DATUM_TABLE.put(87, 6806);
        DATUM_TABLE.put(88, 6663);
        DATUM_TABLE.put(89, 6664);
        DATUM_TABLE.put(90, 6292);
        DATUM_TABLE.put(91, 6293);
        DATUM_TABLE.put(92, 1075);
        // 93
        DATUM_TABLE.put(94, 6664);
        DATUM_TABLE.put(95, 6665);
        DATUM_TABLE.put(96, 6298);
        DATUM_TABLE.put(97, 6301);
        DATUM_TABLE.put(98, 6734);
        DATUM_TABLE.put(99, 6752);
        DATUM_TABLE.put(100, 6732);
        // 101
        DATUM_TABLE.put(102, 6760);
        DATUM_TABLE.put(103, 6322);
        DATUM_TABLE.put(104, 6326);
        DATUM_TABLE.put(105, 6309);
        DATUM_TABLE.put(106, 6311);
        DATUM_TABLE.put(107, 6275);
        DATUM_TABLE.put(108, 6231);
        DATUM_TABLE.put(110, 6313);
        DATUM_TABLE.put(111, 6043);
        DATUM_TABLE.put(112, 6124);
        // 113
        DATUM_TABLE.put(114, 6274);
        // 115 ?
        DATUM_TABLE.put(116, 6283);
        DATUM_TABLE.put(117, 6167);
        DATUM_TABLE.put(118, 6169);
        // 119
        DATUM_TABLE.put(120, 6713);
        DATUM_TABLE.put(121, 6219);
        DATUM_TABLE.put(122, 6180);
        DATUM_TABLE.put(123, 6155);
        DATUM_TABLE.put(124, 6736);
        // 125
        DATUM_TABLE.put(126, 6183);
        DATUM_TABLE.put(127, 6255);
        // 128
        // 129
        DATUM_TABLE.put(130, 6239);
        DATUM_TABLE.put(131, 6131);
        DATUM_TABLE.put(132, 6240);
        DATUM_TABLE.put(133, 6238);
        // 134
        DATUM_TABLE.put(135, 6735);
        DATUM_TABLE.put(136, 6250);
        DATUM_TABLE.put(137, 6604);
        // 138
        DATUM_TABLE.put(139, 6307);
        DATUM_TABLE.put(140, 6182);
        DATUM_TABLE.put(141, 6620);
        DATUM_TABLE.put(142, 6282);
        DATUM_TABLE.put(143, 6615);
        DATUM_TABLE.put(144, 6616);
        DATUM_TABLE.put(145, 6175);
        DATUM_TABLE.put(146, 6818);
        DATUM_TABLE.put(147, 6297);
        DATUM_TABLE.put(148, 6671);
        // 149
        DATUM_TABLE.put(150, 6148);
        DATUM_TABLE.put(151, 6122);
        DATUM_TABLE.put(152, 6612);
        DATUM_TABLE.put(157, 6326);

        //3 differents epsg code can be found for mapinfo code 1000 --> 6746, 6745, 6314
        DATUM_TABLE.put(1000, 6314);
        DATUM_TABLE.put(1001, 6284);
        DATUM_TABLE.put(1002, 6807);
        DATUM_TABLE.put(1003, 4149);
        DATUM_TABLE.put(1004, 6237);
        DATUM_TABLE.put(1005, 6222);
        // 1006, 1007, 1008 & 1009 are australian geodetic datums (6202 & 6203) modified. How should we manage it ?
        DATUM_TABLE.put(1010, 6272);
        DATUM_TABLE.put(1011, 6124);
        // 1012
        DATUM_TABLE.put(1013, 6178);
        DATUM_TABLE.put(1014, 6200);
        DATUM_TABLE.put(1015, 6301);
        DATUM_TABLE.put(1016, 6123);
        DATUM_TABLE.put(1017, 6610);
        DATUM_TABLE.put(1018, 6284);
        DATUM_TABLE.put(1019, 6313);
        DATUM_TABLE.put(1020, 6818);

        DATUM_TABLE.put(9999, 0);

        /***********************************
              No EPSG equivalent datums
         ***********************************/

        HANDED_DATUM_TABLE.put(26, buildValue("Dos_1968",                     4, 230, -199, -752,0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(35, buildValue("Gux_1_Astro",                  4, 252, -209, -751,0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(41, buildValue("Indian_Bangladesh",           11, 289, 734,  257, 0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(51, buildValue("Luzon_Mindanao_Island",       7, -133, -79,  -72, 0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(58, buildValue("Nahrwan_Masirah_Island",      6, -247, -148, 369, 0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(59, buildValue("Nahrwan_Un_Arab_Emirates",    6, -249, -156, 381, 0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(93, buildValue("South_Asia",                  19,7,    -10, -26,  0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(101, buildValue("WGS_60",                     26,0,    0,   0,    0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(113, buildValue("Lisboa_DLX",                 4, -303, -62, 105,  0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(115, buildValue("Euref_98",                   0, 0,    0,   0,    0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(119, buildValue("Antigua_Astro_1965",         6, -270, 13,  62,   0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(125, buildValue("Fort_Thomas_1955",           6, -7, 215,   225,  0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(128, buildValue("Hermanns_Kogel",             10,682, -203, 480,  0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(129, buildValue("Indian",                     50,283, 682,  231,  0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(134, buildValue("ISTS061_Astro_1968",         4, -794, 119, -298, 0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(138, buildValue("Mporaloko",                  6, -74, -130, 42,   0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(149, buildValue("Virol_1960",                 6, -123, -206,219,  0, 0, 0, 0, 0));
        HANDED_DATUM_TABLE.put(1006, buildValue("AGD84_7_Param_Aust",        2, -117.763,-51.51, 139.061, -0.292, -0.443, -0.277, -0.191, 0));
        HANDED_DATUM_TABLE.put(1007, buildValue("AGD66_7_Param_ACT",         2, -129.193,-41.212, 130.73, -0.246, -0.374, -0.329, -2.955, 0));
        HANDED_DATUM_TABLE.put(1008, buildValue("AGD66_7_Param_TAS",         2, -120.271,-64.543, 161.632, -0.2175, 0.0672, 0.1291, 2.4985, 0));
        HANDED_DATUM_TABLE.put(1009, buildValue("AGD66_7_Param_VIC_NSW",     2, -119.353,-48.301, 139.484, -0.415, -0.26, -0.437, -0.613, 0));
        HANDED_DATUM_TABLE.put(1012, buildValue("Russia_PZ90",               52, -1.08,-0.27,-0.9,0, 0, -0.16,-0.12, 0));
    }
}
