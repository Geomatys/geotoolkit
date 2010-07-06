package org.geotoolkit.data.kml.model;

import com.vividsolutions.jts.geom.CoordinateSequence;
import org.geotoolkit.data.kml.xsd.SimpleType;

/**
 *
 * <pre>
 * &lt;simpleType name="coordinatesType">
 *  &lt;list itemType="string"/>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Coordinates extends SimpleType, CoordinateSequence {

}
