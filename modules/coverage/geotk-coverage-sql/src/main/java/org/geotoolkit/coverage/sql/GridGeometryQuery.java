/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.sql;

import org.geotoolkit.internal.sql.table.Query;
import org.geotoolkit.internal.sql.table.Column;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.Parameter;
import org.geotoolkit.internal.sql.table.QueryType;

import static org.geotoolkit.internal.sql.table.QueryType.*;


/**
 * The query to execute for a {@link GridGeometryTable}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class GridGeometryQuery extends Query {
    /**
     * Column to appear after the {@code "SELECT"} clause.
     */
    final Column identifier, width, height, scaleX, shearY, shearX, scaleY,
            translateX, translateY, horizontalSRID, verticalSRID, verticalOrdinates;

    /**
     * Parameter to appear after the {@code "FROM"} clause.
     */
    final Parameter byIdentifier, byWidth, byHeight, byScaleX, byShearY, byShearX, byScaleY,
            byTranslateX, byTranslateY, byHorizontalSRID;

    /**
     * Creates a new query for the specified database.
     *
     * @param database The database for which this query is created.
     */
    public GridGeometryQuery(final Database database) {
        super(database, "GridGeometries");
        final QueryType[] lse    = {LIST, SELECT, EXISTS        };
        final QueryType[] lsi    = {LIST, SELECT,         INSERT};
        final QueryType[] si     = {      SELECT,         INSERT};
        final QueryType[] select = {      SELECT, EXISTS, DELETE};
        final QueryType[] list   = {LIST                        };
        identifier        = addMandatoryColumn("identifier",              lse);
        width             = addMandatoryColumn("width",                    si);
        height            = addMandatoryColumn("height",                   si);
        scaleX            = addMandatoryColumn("scaleX",                   si);
        shearY            = addOptionalColumn ("shearY",               0,  si);
        shearX            = addOptionalColumn ("shearX",               0,  si);
        scaleY            = addMandatoryColumn("scaleY",                   si);
        translateX        = addMandatoryColumn("translateX",               si);
        translateY        = addMandatoryColumn("translateY",               si);
        horizontalSRID    = addMandatoryColumn("horizontalSRID",           si);
        verticalSRID      = addOptionalColumn ("verticalSRID",      null, lsi);
        verticalOrdinates = addOptionalColumn ("verticalOrdinates", null, lsi);
        byIdentifier      = addParameter(identifier,   select);
        byWidth           = addParameter(width,          list);
        byHeight          = addParameter(height,         list);
        byScaleX          = addParameter(scaleX,         list);
        byShearY          = addParameter(shearY,         list);
        byShearX          = addParameter(shearX,         list);
        byScaleY          = addParameter(scaleY,         list);
        byTranslateX      = addParameter(translateX,     list);
        byTranslateY      = addParameter(translateY,     list);
        byHorizontalSRID  = addParameter(horizontalSRID, list);
    }
}
