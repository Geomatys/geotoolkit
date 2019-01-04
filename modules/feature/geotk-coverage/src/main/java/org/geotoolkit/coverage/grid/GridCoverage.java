/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2004-2019 Open Geospatial Consortium, Inc.
 *    All Rights Reserved. http://www.opengeospatial.org/ogc/legal
 *
 *    Permission to use, copy, and modify this software and its documentation, with
 *    or without modification, for any purpose and without fee or royalty is hereby
 *    granted, provided that you include the following on ALL copies of the software
 *    and documentation or portions thereof, including modifications, that you make:
 *
 *    1. The full text of this NOTICE in a location viewable to users of the
 *       redistributed or derivative work.
 *    2. Notice of any changes or modifications to the OGC files, including the
 *       date changes were made.
 *
 *    THIS SOFTWARE AND DOCUMENTATION IS PROVIDED "AS IS," AND COPYRIGHT HOLDERS MAKE
 *    NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *    TO, WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT
 *    THE USE OF THE SOFTWARE OR DOCUMENTATION WILL NOT INFRINGE ANY THIRD PARTY
 *    PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.
 *
 *    COPYRIGHT HOLDERS WILL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL OR
 *    CONSEQUENTIAL DAMAGES ARISING OUT OF ANY USE OF THE SOFTWARE OR DOCUMENTATION.
 *
 *    The name and trademarks of copyright holders may NOT be used in advertising or
 *    publicity pertaining to the software without specific, written prior permission.
 *    Title to copyright in this software and any associated documentation will at all
 *    times remain with copyright holders.
 */
package org.geotoolkit.coverage.grid;

import java.awt.image.RenderedImage;
import java.util.List;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;
import org.opengis.annotation.UML;
import org.opengis.coverage.Coverage;
import org.apache.sis.coverage.grid.GridGeometry;


/**
 * Represent the basic implementation which provides access to grid coverage data.
 * A {@code GridCoverage} implementation may provide the ability to update grid values.
 *
 * <div class="warning"><b>Warning — this class will change</b><br>
 * Current API is derived from OGC <a href="http://www.opengis.org/docs/01-004.pdf">Grid Coverages Implementation specification 1.0</a>.
 * We plan to replace it by new interfaces derived from ISO 19123 (<cite>Schema for coverage geometry
 * and functions</cite>). Current interfaces should be considered as legacy and are included in this
 * distribution only because they were part of GeoAPI 1.0 release. We will try to preserve as much
 * compatibility as possible, but no migration plan has been determined yet.
 * </div>
 *
 * @version <A HREF="http://www.opengis.org/docs/01-004.pdf">Grid Coverage specification 1.0</A>
 * @author  Martin Desruisseaux (IRD)
 * @since   GeoAPI 1.0
 *
 * @see RenderedImage
 */
@UML(identifier="CV_GridCoverage", specification=OGC_01004)
public interface GridCoverage extends Coverage {

    /**
     * Information for the grid coverage geometry.
     * Grid geometry includes the valid range of grid coordinates and the georeferencing.
     *
     * @return the information for the grid coverage geometry.
     */
    @UML(identifier="gridGeometry", obligation=MANDATORY, specification=OGC_01004)
    GridGeometry getGridGeometry();

    /**
     * Returns the sources data for a grid coverage. If the {@code GridCoverage} was
     * produced from an underlying dataset, this method should returns an empty list.
     *
     * If the {@code GridCoverage} was produced using
     * {link org.opengis.coverage.processing.GridCoverageProcessor} then it should return the
     * source grid coverages of the one used as input to {@code GridCoverageProcessor}.
     * In general this method is intended to return the original {@code GridCoverage}
     * on which it depends.
     *
     * This is intended to allow applications to establish what {@code GridCoverage}s
     * will be affected when others are updated, as well as to trace back to the "raw data".
     *
     * @return the sources data for a grid coverage.
     */
    List<GridCoverage> getSources();

}
