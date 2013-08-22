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
package org.geotoolkit.s52.dai;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Forms unique module identification within the exchange set.
 *
 * @author Johann Sorel (Geomatys)
 */
public class LibraryIdentification extends DAIField{

    /** A(2) : Module Name ‑ two alphabetic characters 'LI' indicating module type. */
    public String MODN;
    /** I(5) : Record Identifier ‑00000 < x < 32768; with MODN
     * shall form unique identification within the exchange set. */
    public int RCID;
    /** A(3) : Exchange Purpose ‑
     * NEW Denotes that the exchange set is a NEW library.
     * REV Denotes that the exchange set is a REVision to an existing library. */
    public String EXPP;
    /** A(1/15) : Product Type – e.g.'IHO' */
    public String PTYP;
    /** A(1/15) : Exchange Set Identification Number - continuous serial number. */
    public String ESID;
    /** A(1/15) : Edition Number ‑ continuous serial number. */
    public String EDTN;
    /** A(8) : Compilation Date of Exchange Set ‑ YYYYMMDD */
    public String CODT;
    /** A(6) : Compilation Time of Exchange Set ‑ HHMMSS */
    public String COTI;
    /** A(8) : Library‑Profile Versions Date – YYYYMMDD */
    public String VRDT;
    /** A(2) : Library Application Profile – PN  Presentation New Information PR  Pres. Revision Information */
    public String PROF;
    /** A(8) : Date of Version of the applied Object Catalogue ‑ YYYYMMDD */
    public String OCDT;
    /** A(1/15) : Comment */
    public String COMT;

    public LibraryIdentification() {
        super("LBID");
    }

    @Override
    public Map<String, Object> getSubFields() {
        final Map<String,Object> map = new LinkedHashMap<>();
        map.put("MODN", MODN);
        map.put("RCID", RCID);
        map.put("EXPP", EXPP);
        map.put("PTYP", PTYP);
        map.put("ESID", ESID);
        map.put("EDTN", EDTN);
        map.put("CODT", CODT);
        map.put("COTI", COTI);
        map.put("VRDT", VRDT);
        map.put("PROF", PROF);
        map.put("OCDT", OCDT);
        map.put("COMT", COMT);
        return map;
    }

    @Override
    protected void readSubFields(String str) {
        final int[] offset = new int[1];
        MODN = readStringBySize(str, offset, 2);
        RCID = readIntBySize(str, offset, 5);
        EXPP = readStringBySize(str, offset, 3);
        PTYP = readStringByDelim(str, offset, DELIM_1F);
        ESID = readStringByDelim(str, offset, DELIM_1F);
        EDTN = readStringByDelim(str, offset, DELIM_1F);
        CODT = readStringBySize(str, offset, 8);
        COTI = readStringBySize(str, offset, 6);
        VRDT = readStringBySize(str, offset, 8);
        PROF = readStringBySize(str, offset, 2);
        OCDT = readStringBySize(str, offset, 8);
        COMT = readStringByDelim(str, offset, DELIM_1F);
    }

}
