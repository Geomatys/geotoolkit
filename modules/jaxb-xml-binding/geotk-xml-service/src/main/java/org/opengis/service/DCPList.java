/*$************************************************************************************************
 **
 ** $Id: 
 **
 ** $URL: https://geoapi.svn.sourceforge.net/svnroot/geoapi/trunk/geoapi/src/main/java/org/opengis/services/DCPList.java $
 **
 ** Copyright (C) 2004-2005 Open GIS Consortium, Inc.
 ** All Rights Reserved. http://www.opengis.org/legal/
 **
 *************************************************************************************************/

package org.opengis.service;

import java.util.ArrayList;
import java.util.List;
import org.opengis.annotation.UML;
import org.opengis.util.CodeList;

import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;


/**
 * 
 * @author <A HREF="http://www.opengeospatial.org/standards/as# 02-112">ISO 19119</A>
 * @author Guilhem Legal 
 * @module pending
 */
public class DCPList extends CodeList<DCPList> {

   /**
     * List of all enumerations of this type.
     * Must be declared before any enum declaration.
     */
    private static final List<DCPList> VALUES = new ArrayList<DCPList>(6);

    /**
     * 
     */
    //@UML(identifier="XML", obligation=CONDITIONAL, specification=ISO_19119)
    public static final DCPList XML = new DCPList("XML");

    /**
     *
     */
    //@UML(identifier="CORBA", obligation=CONDITIONAL, specification=ISO_19119)
    public static final DCPList CORBA = new DCPList("CORBA");

    /**
     *
     */
    //@UML(identifier="JAVA", obligation=CONDITIONAL, specification=ISO_19119)
    public static final DCPList JAVA = new DCPList("JAVA");

    /**
     *
     */
    //@UML(identifier="COM", obligation=CONDITIONAL, specification=ISO_19119)
    public static final DCPList COM = new DCPList("COM");

    /**
     * 
     */
    //@UML(identifier="SQL", obligation=CONDITIONAL, specification=ISO_19119)
    public static final DCPList SQL = new DCPList("SQL");

    /**
     * 
     */
    //@UML(identifier="WebServices", obligation=CONDITIONAL, specification=ISO_19119)
    public static final DCPList WEBSERVICES = new DCPList("WEBSERVICES");


    /**
     * Constructs an enum with the given name. The new enum is
     * automatically added to the list returned by {@link #values}.
     *
     * @param name The enum name. This name must not be in use by an other enum of this type.
     */
    private DCPList(final String name) {
        super(name, VALUES);
    }

    /**
     * Returns the list of {@code DCPList}s.
     */
    public static DCPList[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new DCPList[VALUES.size()]);
        }
    }

    /**
     * Returns the list of enumerations of the same kind than this enum.
     */
    public DCPList[] family() {
        return values();
    }

    /**
     * Returns the DCPList that matches the given string, or returns a
     * new one if none match it.
     */
    public static DCPList valueOf(final String code) {
        return valueOf(DCPList.class, code);
    }
}
