package org.geotoolkit.data.model.kml;

import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Angle90pos type:</p>
 *
 * <pre>
 * &lt;simpleType name="angle90posType">
 *  &lt;restriction base="double">
 *      &lt;minInclusive value="0"/>
 *      &lt;maxInclusive value="90.0"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Anglepos90 extends SimpleType{

    /**
     * @return The angle value.
     */
    public double getAngle();
}
