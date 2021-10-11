/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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

package org.geotoolkit.thw.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * INSPIRE uses the ISO 639-2 language codes to distinguish languages.
 *
 * @author Adrian Custer (Geomatys)
 * @author Guilhem Legal (Geomatys)
 * @since 2.0.5
 *
 */
//TODO: extend into a formal list of all ISO 639-2
// @see:  http://en.wikipedia.org/wiki/ISO_639-2
// @see:  http://en.wikipedia.org/wiki/List_of_ISO_639-2_codes
//TODO: extend to use equivalents
//TODO: (later) we will probably need to be able to relate these to locale.
public enum ISOLanguageCode {

    ALB("Albanian", new String[]{"ALB", "SQI"},"SQ"),
    AMH("Amharic", "AMH", "AM"),
    ARA("Arabic", "ARA", "AR"),
    BAQ("Basque", new String[]{"BAQ", "EUS"}, "EU"),
    BEL("Belarusian", "BEL", "BE"),
    BEN("Bengali", "BEN", "BN"),
    BUL("Bulgarian", "BUL", "BG"),
    CAT("Catalan", "CAT", "CA"),
    CHI("Chinese", new String[]{"CHI", "ZHO"}, "ZH"),
    CZE("Czech", new String[]{"CZE", "CES"}, "CS"),
    DUT("Dutch", new String[]{"DUT", "NLD"}, "NL"),
    DAN("Danish", new String[]{"DAN"}, "DA"),
    ENG("English", "ENG", "EN"),
    EST("Estonian", new String[]{"EST"}, "ET"),
    FIN("Finnish", "FIN", "FI"),
    FRE("Francais", new String[]{"FRE", "FRA"}, "FR", Arrays.asList(new char[]{'e', 'é' , 'è'})),
    GER("German", new String[]{"GER", "DEU"}, "DE"),
    GLE("Gaelic", "GLE","GA"),
    GRE("Greek modern", new String[]{"GRE", "ELL"}, "EL"),
    HRV("Croatian", "HRV", "HR"),
    HUN("Hungarian", new String[]{"HUN"}, "HU"),
    ITA("Italian", "ITA", "IT"),
    ICE("Islandic", new String[]{"ICE", "ISL"},"IS"),
    LAT("Latin", "LAT", "LA"),
    LAV("Latvian", "LAV", "LV"),
    LIT("Lithuanian", "LIT", "LT"),
    LTZ("Luxembourgish","LTZ","LB"),
    NOR("Norwegian","NOR","NO"),
    MAC("Macedonian", new String[]{"MAC", "MKD"},"MK"),
    MLT("Maltese", "MLT","MT"),
    JPN("Japanese", "JPN", "JA"),
    PER("Persian", new String[]{"PER", "FAS"}, "FA"),
    POL("Polish", "POL", "PL"),
    POR("Portugese", "POR", "PT"),
    RUM("Romanian", new String[]{"RUM", "RON"}, "RO"),
    RUS("Russian", "RUS", "RU"),
    SLK("Slovak", new String[]{"SLO", "SLK"}, "SK"),
    SLV("Slovenian", "SLV", "SL"),
    SPA("Spanish", "SPA", "ES"),
    SRP("Serbian","SRP","SR"),
    SWE("Swedish", "SWE", "SV"),
    TUR("Turkish", "TUR", "TR"),
    UKR("Ukrainian","UKR","UK"),
    WOL("Wolof", "WOL", "WO");


    private static final Map<String,ISOLanguageCode> MAP = new HashMap<String, ISOLanguageCode>();
    static {
        for(final ISOLanguageCode iso : values()){
            MAP.put(iso.codetwo, iso);
            for(final String three : iso.codethrees){
                MAP.put(three, iso);
            }
        }
    }

    /**
     * The name of the language
     */
    private final String name;
    private final String[] codethrees;
    private final String codetwo;
    /**
     * A list of special character and their common replacement character.
     * for exemple in french the users often write 'e' instead of 'é' .
     */
    private final List<char[]> specialCharacter;

    ISOLanguageCode(final String name, final String codethree, final String codetwo) {
        this(name,codethree,codetwo,null);
    }

    ISOLanguageCode(final String name, final String[] codethrees, final String codetwo) {
        this(name,codethrees,codetwo,null);
    }

    ISOLanguageCode(final String name, final String codethree, final String codetwo, final List<char[]> specialCharacter) {
        this(name,new String[]{codethree},codetwo,specialCharacter);
    }

    ISOLanguageCode(final String name, final String[] codethrees, final String codetwo, final List<char[]> specialCharacter) {
        this.name = name;
        this.codetwo = codetwo;

        if (codethrees == null) {
            this.codethrees = new String[0];
        } else {
            this.codethrees = Arrays.copyOf(codethrees, codethrees.length);
        }

        if (specialCharacter == null) {
            this.specialCharacter = Collections.EMPTY_LIST;
        } else {
            this.specialCharacter = specialCharacter;
        }
    }

    /**
     * The English language name for the language.
     *
     * @return the name of the language in English.
     */
    public String getName() {
        return name;
    }

    /**
     * The three letter code for the given language.
     *
     * @return the three letter code.
     */
    public String getCode() {
        if (codethrees != null && codethrees.length > 0) {
            return codethrees[0];
        }
        return null;
    }

    /**
     * The two letter code for the given language.
     *
     * @return the three letter code.
     */
    public String getTwoLetterCode() {
        return codetwo;
    }

    /**
     * @return the specialCharacter
     */
    public List<char[]> getSpecialCharacter() {
        return specialCharacter;
    }

    /**
     * Returns the corresponding ISOLanguageCode from a string code.
     * @param code
     * @return
     */
    public static ISOLanguageCode fromCode(final String code) {
        final ISOLanguageCode iso = MAP.get(code.toUpperCase());
        if(iso == null){
            throw new IllegalArgumentException("unsupported ISOLanguageCode code:" + code);
        }
        return iso;
    }

}
