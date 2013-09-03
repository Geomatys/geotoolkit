package org.geotoolkit.coverage;

/**
 * Define coverage type. For the time being, contains 4 values :
 * <ul>
 * <li>GRID :		A discrete coverage made of quadrilateral cells.</li>
 * <li>PYRAMID :	A {@linkplain #GRID} coverage where cells are regrouped in tiles, and different set of tiles are pre-computed for different resolutions.</li>
 * <li>OTHER :		Other coverage not define</li>
 * </ul>
 * 
 * 
 * @author Benjamin Garcia (Geomatys)
 *
 */
public enum CoverageType {

	/**
	 * A discrete coverage made of quadrilateral cells.
	 */
	GRID,
	
	/**
	 * A {@linkplain #GRID} coverage where cells are regrouped in tiles, and different set of tiles are pre-computed for different resolutions.
	 */
	PYRAMID,
	
	/**
	 * Other coverage not define
	 */
	OTHER
}
