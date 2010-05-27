package org.geotoolkit.data.model.kml;

import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Angle180 type:</p>
 *
 * <br />&lt;simpleType name="angle180Type">
 * <br />&lt;restriction base="double">
 * <br />&lt;minInclusive value="-180"/>
 * <br />&lt;maxInclusive value="180.0"/>
 * <br />&lt;/restriction>
 * <br />&lt;/simpleType>
 *
 * @author Samuel Andrés
 */
public interface Angle180 extends SimpleType{

    /**
     * @return The angle value.
     */
    public double getAngle();
}
