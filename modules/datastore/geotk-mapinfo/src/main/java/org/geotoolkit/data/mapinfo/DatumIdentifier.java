package org.geotoolkit.data.mapinfo;

import org.geotoolkit.util.ArgumentChecks;

import java.util.HashMap;
import java.util.Map;

/**
 * A class which binds mapinfo datum codes with equivalent epsg code.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 06/03/13
 */
public final class DatumIdentifier {

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

        //3 differents epsg code can be found for mapinfo code 1000 --> 6746, 6745, 6314
        DATUM_TABLE.put(1000, 6314);
        DATUM_TABLE.put(1001, 6284);
        DATUM_TABLE.put(1002, 6807);
        DATUM_TABLE.put(1003, 4149);

        DATUM_TABLE.put(9999, 0);
    }

}
