package org.geotoolkit.data.model.kml;

import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Angle90 type:</p>
 *
 * <pre>
 * &lt;simpleType name="angle90Type">
 *  &lt;restriction base="double">
 *      &lt;minInclusive value="-90"/>
 *      &lt;maxInclusive value="90.0"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Angle90 extends SimpleType{

    /**
     * @return The angle value.
     */
    public double getAngle();
}
