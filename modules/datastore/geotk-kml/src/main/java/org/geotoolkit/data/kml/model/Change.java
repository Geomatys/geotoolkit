package org.geotoolkit.data.kml.model;

import java.util.List;

/**
 * <p>This interface maps Change element.</p>
 *
 * <pre>
 * &lt;element name="Change" type="kml:ChangeType"/>
 *
 * &lt;complexType name="ChangeType">
 *  &lt;sequence>
 *      &lt;element ref="kml:AbstractObjectGroup" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Change {

    /**
     *
     * @return the lis of AbstractObjects
     */
    public List<AbstractObject> getObjects();
}
