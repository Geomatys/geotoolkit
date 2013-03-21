package org.geotoolkit.data.mapinfo;

import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.datum.BursaWolfParameters;
import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.datum.DefaultPrimeMeridian;
import org.geotoolkit.referencing.factory.epsg.DirectEpsgFactory;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.referencing.datum.*;
import org.opengis.util.FactoryException;

import javax.measure.unit.Unit;
import java.util.HashMap;
import java.util.Map;

/**
 * A class which binds mapinfo datum codes with equivalent epsg code.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 06/03/13
 */
public final class DatumIdentifier {

    private static final DatumAuthorityFactory DATUM_AUTHORITY_FACTORY = AuthorityFactoryFinder.getDatumAuthorityFactory("EPSG", null);

    // A map containing mapinfo datum codes as key, and EPSG code as value.
    private static final Map<Integer, Integer> DATUM_TABLE = new HashMap<Integer, Integer>();

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
     * Build a MIF datum from a Geotk one. If we can find an existing one MapInfo datums, we just erturn its code.
     * Otherwise, we must build a custom datum using Bursa Wolf transformation (to WGS 84).
     * @param source The datum to write.
     * @return A String representing the given datum as MapInfo needs it.
     * @throws FactoryException If we don't find a per-existing code, and we cannot retrieve datum ellipsoid.
     * @throws DataStoreException Same conditions as FactoryException.
     */
    public static String getMIFDatum(GeodeticDatum source) throws FactoryException, DataStoreException {
        StringBuilder builder = new StringBuilder();

        final int epsgCode = IdentifiedObjects.lookupEpsgCode(source, false);
        final int mifCode = getMIFCodeFromEPSG(epsgCode);

        // If we can't find a code matching, we build a custom CRS.
        if(mifCode < 0) {
            if(!(source instanceof DefaultGeodeticDatum)) {
                throw new DataStoreException("Unsupported datum type.");
            }

            int customCode = 999;

            int ellipsoidCode = EllipsoidIdentifier.getMIFCode(source.getEllipsoid());
            if(ellipsoidCode < 0) {
                throw new DataStoreException("We're unable to find an ellipsoid for source datum.");
            }

            BursaWolfParameters bwParams = ((DefaultGeodeticDatum) source).getBursaWolfParameters(DefaultGeodeticDatum.WGS84);

            double primeShift = 0;
            if(source.getPrimeMeridian() != DefaultPrimeMeridian.GREENWICH) {
                customCode = 9999;
                primeShift = source.getPrimeMeridian().getGreenwichLongitude();
            }

            builder .append(customCode).append(", ")
                    .append(ellipsoidCode).append(", ")
                    .append(bwParams.dx).append(", ")
                    .append(bwParams.dy).append(", ")
                    .append(bwParams.dz).append(", ")
                    .append(bwParams.ex).append(", ")
                    .append(bwParams.ey).append(", ")
                    .append(bwParams.ez).append(", ")
                    .append(bwParams.ppm);

            if(primeShift != 0) {
                builder.append(", ").append(primeShift);
            }
        } else {
            builder.append(mifCode);
        }
        return builder.toString();
    }

    public static GeodeticDatum getDatumFromMIFCode(int datumCode) throws FactoryException {
        GeodeticDatum datum = null;
        final Integer epsgDatum = DatumIdentifier.getEPSGDatumCode(datumCode);
        if(epsgDatum != null) {
            datum = DATUM_AUTHORITY_FACTORY.createGeodeticDatum(epsgDatum.toString());
        } else {

        }
        return datum;
    }

    /**
     * Build a Geodetic datum from a MapInfo ellipsoid code and Bursa Wolf parameters to WGS84.
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
    public static GeodeticDatum buildCustomDatum(double[] parameters) throws DataStoreException, FactoryException {
        BursaWolfParameters bwParams = new BursaWolfParameters(DefaultGeodeticDatum.WGS84);
        if(parameters.length < 1) {
            throw new DataStoreException("There's not enough parameters to build a valid datum. An ellipsoid code is required.");
        }

        // Get the ellipsoid
        int ellipsoidCode = (int) parameters[0];
        Ellipsoid ellipse = EllipsoidIdentifier.getEllipsoid(ellipsoidCode);

        if(parameters.length > 3) {
            bwParams.dx = parameters[1];
            bwParams.dy = parameters[2];
            bwParams.dz = parameters[3];
        }

        if(parameters.length > 7) {
            bwParams.ex  = parameters[4];
            bwParams.ey  = parameters[5];
            bwParams.ez  = parameters[6];
            bwParams.ppm = parameters[7];
        }

        // If we've got 9 parameters, the datum is not based on Greenwich meridian.
        PrimeMeridian pMeridian = DefaultPrimeMeridian.GREENWICH;
        if(parameters.length > 8) {
            pMeridian = new DefaultPrimeMeridian("Greenwich"+((parameters[8] > 0)? "+"+parameters[8] : parameters[8]), parameters[8]);
        }

        final Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(GeodeticDatum.NAME_KEY, ellipse.getName().getCode()+"_custom_datum");
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
    }
}
