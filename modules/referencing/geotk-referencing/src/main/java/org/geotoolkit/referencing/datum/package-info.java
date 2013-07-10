/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

/**
 * {@linkplain org.geotoolkit.referencing.datum.AbstractDatum Datum} implementations. An explanation
 * for this package is provided in the {@linkplain org.opengis.referencing.datum OpenGIS&reg; javadoc}.
 * The remaining discussion on this page is specific to the Geotk implementation.
 *
 * <p>Some useful constants defined in this package are:</p>
 *
 * <blockquote><table>
 *   <tr><td nowrap>Geodetic datum:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.datum.DefaultGeodeticDatum#WGS84            WGS84}
 *   </td></tr>
 *   <tr><td nowrap>Vertical datum:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.datum.DefaultVerticalDatum#GEOIDAL          GEOIDAL},
 *     {@link org.geotoolkit.referencing.datum.DefaultVerticalDatum#ELLIPSOIDAL      ELLIPSOIDAL}
 *   </td></tr>
 *   <tr><td nowrap>Temporal datum:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.datum.DefaultTemporalDatum#JULIAN           JULIAN},
 *     {@link org.geotoolkit.referencing.datum.DefaultTemporalDatum#MODIFIED_JULIAN  MODIFIED_JULIAN},
 *     {@link org.geotoolkit.referencing.datum.DefaultTemporalDatum#TRUNCATED_JULIAN TRUNCATED_JULIAN},
 *     {@link org.geotoolkit.referencing.datum.DefaultTemporalDatum#DUBLIN_JULIAN    DUBLIN_JULIAN},
 *     {@link org.geotoolkit.referencing.datum.DefaultTemporalDatum#UNIX             UNIX}
 *   </td></tr>
 *   <tr><td nowrap>Other datum:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.datum.DefaultEngineeringDatum#UNKNOWN       UNKNOWN}
 *   </td></tr>
 *   <tr><td nowrap>Ellipsoids:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.datum.DefaultEllipsoid#WGS84                WGS84},
 *     {@link org.geotoolkit.referencing.datum.DefaultEllipsoid#GRS80                GRS80},
 *     {@link org.geotoolkit.referencing.datum.DefaultEllipsoid#INTERNATIONAL_1924   INTERNATIONAL_1924},
 *     {@link org.geotoolkit.referencing.datum.DefaultEllipsoid#CLARKE_1866          CLARKE_1866},
 *     {@link org.geotoolkit.referencing.datum.DefaultEllipsoid#SPHERE               SPHERE}
 *   </td></tr>
 *   <tr><td nowrap>Prime meridians:&nbsp;</td><td>
 *     {@link org.geotoolkit.referencing.datum.DefaultPrimeMeridian#GREENWICH        GREENWICH}
 *   </td></tr>
 * </table></blockquote>
 *
 * <p>Some worthy methods defined in this package are:</p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.datum.DefaultEllipsoid#orthodromicDistance}</li>
 *   <li>{@link org.geotoolkit.referencing.datum.DefaultGeodeticDatum#getAffineTransform}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.20
 *
 * @since 1.2
 * @module
 */
@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED, namespace = Namespaces.GML, xmlns = {
    @XmlNs(prefix = "gmd", namespaceURI = Namespaces.GMD),
    @XmlNs(prefix = "gco", namespaceURI = Namespaces.GCO),
    @XmlNs(prefix = "xsi", namespaceURI = Namespaces.XSI)
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(EX_Extent.class),
    @XmlJavaTypeAdapter(CD_Ellipsoid.class),
    @XmlJavaTypeAdapter(CD_PrimeMeridian.class),
    @XmlJavaTypeAdapter(CD_VerticalDatumType.class),
    @XmlJavaTypeAdapter(StringAdapter.class),
    @XmlJavaTypeAdapter(InternationalStringConverter.class)
})
package org.geotoolkit.referencing.datum;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import org.apache.sis.xml.Namespaces;
import org.apache.sis.internal.jaxb.gco.*;
import org.apache.sis.internal.jaxb.metadata.*;
import org.geotoolkit.internal.jaxb.referencing.*;
