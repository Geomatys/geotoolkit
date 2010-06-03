package org.geotoolkit.data.model.kml;

import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Anglepos180 type:</p>
 *
 * <pre>
 * &lt;simpleType name="anglepos180Type">
 *  &lt;restriction base="double">
 *      &lt;minInclusive value="0"/>
 *      &lt;maxInclusive value="180.0"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Anglepos180 extends SimpleType {

    /**
     * @return The angle value.
     */
    public double getAngle();
}
