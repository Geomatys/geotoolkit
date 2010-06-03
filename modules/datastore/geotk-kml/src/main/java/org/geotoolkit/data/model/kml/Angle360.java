package org.geotoolkit.data.model.kml;

import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Angle360 type:</p>
 *
 * <pre>
 * &lt;simpleType name="angle360Type">
 *  &lt;restriction base="double">
 *      &lt;minInclusive value="-360"/>
 *      &lt;maxInclusive value="360.0"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Angle360 extends SimpleType {

    /**
     * @return The angle value.
     */
    public double getAngle();
}
