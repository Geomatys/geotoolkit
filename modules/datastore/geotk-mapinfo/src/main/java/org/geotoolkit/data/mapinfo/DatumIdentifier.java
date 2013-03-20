package org.geotoolkit.data.mapinfo;

import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.referencing.factory.epsg.DirectEpsgFactory;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.datum.GeodeticDatum;
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

    public static GeodeticDatum getDatumFromMIFCode(int datumCode) throws FactoryException {
        GeodeticDatum datum = null;
        final Integer epsgDatum = DatumIdentifier.getEPSGDatumCode(datumCode);
        if(epsgDatum != null) {
            datum = DATUM_AUTHORITY_FACTORY.createGeodeticDatum(epsgDatum.toString());
        } else {

        }
        return datum;
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
