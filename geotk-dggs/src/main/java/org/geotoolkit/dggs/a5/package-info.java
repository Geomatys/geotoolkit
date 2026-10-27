/**
 * A5 DGGS.
 *
 * <p>Discrete global grid reference system based on the A5 pentagonal index, see https://a5geo.org.
 * The {@link org.geotoolkit.dggs.a5.A5} class exposes the A5 API, the refinement levels of the
 * DGGRS are the A5 resolutions 0 to 30.</p>
 *
 * <p>Cell identifiers follow the A5 v1 index, they are not compatible with the identifiers of A5 0.x
 * (geotoolkit versions based on a5 0.2). Use {@link org.geotoolkit.dggs.a5.A5#migrate(long)} to convert them.</p>
 *
 * Original code from https://github.com/felixpalmer/a5
 */
package org.geotoolkit.dggs.a5;
