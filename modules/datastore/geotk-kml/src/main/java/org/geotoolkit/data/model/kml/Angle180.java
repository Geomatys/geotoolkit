package org.geotoolkit.data.model.kml;

import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Angle180 type:</p>
 *
 * <pre>
 * &lt;simpleType name="angle180Type">
 *  &lt;restriction base="double">
 *      &lt;minInclusive value="-180"/>
 *      &lt;maxInclusive value="180.0"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>S
 *
 * @author Samuel Andrés
 */
public interface Angle180 extends SimpleType{

    /**
     * @return The angle value.
     */
    public double getAngle();
}
