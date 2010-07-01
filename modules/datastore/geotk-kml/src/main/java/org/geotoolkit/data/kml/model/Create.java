package org.geotoolkit.data.kml.model;

import java.util.List;

/**
 * <p>This interface maps Create element.</p>
 *
 * <pre>
 * &lt;element name="Create" type="kml:CreateType"/>
 *
 * &lt;complexType name="CreateType">
 *  &lt;sequence>
 *      &lt;element ref="kml:AbstractContainerGroup" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Create {

    /**
     *
     * @return the AbstractContainer list.
     */
    public List<AbstractContainer> getContainers();

    /**
     * 
     * @param containers
     */
    public void setContainers(List<AbstractContainer> containers);
}
