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
    protected void readSubFields(String str) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
