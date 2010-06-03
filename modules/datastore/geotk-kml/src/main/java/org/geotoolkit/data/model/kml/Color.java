package org.geotoolkit.data.model.kml;

import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps the colorType type</p>
 *
 * <pre>
 * &lt;simpleType name="colorType">
 *  &lt;annotation>
 *      &lt;documentation>&lt;![CDATA[
 *       aabbggrr
 *       ffffffff: opaque white
 *       ff000000: opaque]]>
 *      &lt;/documentation>
 *  &lt;/annotation>
 *  &lt;restriction base="hexBinary">
 *      &lt;length value="4"/>
 *  &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Color extends SimpleType {

    /**
     *
     * @return the color hxadecimal notation.
     */
    public String getColor();
}
