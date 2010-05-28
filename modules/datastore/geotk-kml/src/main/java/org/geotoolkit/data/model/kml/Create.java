package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 * <p>This interface maps Create element.</p>
 *
 * <br />&lt;element name="Create" type="kml:CreateType"/>
 * <br />&lt;complexType name="CreateType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:AbstractContainerGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/complexType>
 *
 * @author Samuel Andrés
 */
public interface Create {

    /**
     *
     * @return the AbstractContainer list.
     */
    public List<AbstractContainer> getContainers();
}
