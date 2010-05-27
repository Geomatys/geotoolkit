package org.geotoolkit.data.model.kml;

import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Angle90pos type:</p>
 *
 * <br />&lt;simpleType name="angle90posType">
 * <br />&lt;restriction base="double">
 * <br />&lt;minInclusive value="0"/>
 * <br />&lt;maxInclusive value="90.0"/>
 * <br />&lt;/restriction>
 * <br />&lt;/simpleType>
 *
 * @author Samuel Andrés
 */
public interface Anglepos90 extends SimpleType{

    /**
     * @return The angle value.
     */
    public double getAngle();
}
