/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.internal.sql.table;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link ResultSet#getTimestamp(int,Calendar)}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 */
public final strictfp class TimeStampTest extends CatalogTestBase {
    /**
     * Creates a new test suite.
     */
    public TimeStampTest() {
        super(Table.class);
    }

    /**
     * Tests {@link ResultSet#getTimestamp(int,Calendar)}.
     *
     * @throws SQLException if an SQL error occurred.
     */
    @Test
    public void testGet() throws SQLException {
        final TimeZone UTC = TimeZone.getTimeZone("UTC");
        final Calendar cal = new GregorianCalendar(UTC, Locale.CANADA);
        final LocalCache cache = getDatabase().getLocalCache();
        final Date t1, t2;
        synchronized (cache) {
            try (Statement s = cache.connection().createStatement();
                 ResultSet r = s.executeQuery(
                    "SELECT \"startTime\", \"startTime\" FROM \"coverages\".\"GridCoverages\" " +
                    "WHERE series=100 AND filename='198601'"))
            {
                assertTrue(r.next());
                t1 = r.getTimestamp(1);
                t2 = r.getTimestamp(2, cal);
                assertFalse(r.next());
            }
        }
        // 'offset' sera expliqué plus bas...
        final int offset = TimeZone.getDefault().getOffset(t1.getTime());
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        /*
         * Vérifie la valeur formatée par Timestamp. L'heure t1 est construite de façon à ce qu'elle
         * apparaissent correctement lorsque affichée selon le fuseau horaire local, tandis que l'on
         * veut que t2 apparaisse correctement lorsque affichée selon le fuseau horaire UTC. On peut
         * vérifier que c'est bien le cas avec les pilote plus récents (les plus anciens on un bug).
         */
        assertEquals("1986-01-01 00:00:00.0", String.valueOf(t1));
        assertEquals("1986-01-01 00:00:00",        df.format(t1));
        if (offset == 0) {
            assertEquals(t1, t2);
        } else {
            assertFalse("The Calendar argument has been ignored.",
                    String.valueOf(t2).equals("1999-01-01 00:00:00.0"));
        }
        df.setTimeZone(UTC);
        assertEquals("1986-01-01 00:00:00", df.format(t2));
        /*
         * Tentative d'explication de ce qui se passe: offset est le laps de temps (en millisecondes)
         * qu'il faut ajouter au temps UTC afin d'obtenir le temps local. A l'est de Greenwich, cette
         * valeur est positive (par exemple GMT+1 en France). Cela signifie que l'heure t1, qui était
         * affichée comme 00:00 GMT+1, correspond à la valeur -01:00 UTC (ou 23:00 UTC de la veille)
         * en mémoire, puisque toutes les dates sont représentées en heure UTC en Java. Si l'on veut
         * que t2 soit affichée comme 00:00 UTC, on devrait avoir t2 = t1 + 01:00, donc t2 > t1.
         */
        assertEquals(t1.getTime(), t2.getTime() - offset);
        if (false) {
            // Print the dates in UTC timezone.
            System.out.println(df.format(t1));
            System.out.println(df.format(t2));
        }
    }
}
