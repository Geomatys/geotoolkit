package org.geotoolkit.data.kml.model;

import java.net.URI;
import java.util.List;

/**
 * <p>This interface maps Update element.</p>
 *
 * <pre>
 * &lt;element name="Update" type="kml:UpdateType"/>
 *
 * &lt;complexType name="UpdateType" final="#all">
 *  &lt;sequence>
 *      &lt;element ref="kml:targetHref"/>
 *      &lt;choice maxOccurs="unbounded">
 *          &lt;element ref="kml:Create"/>
 *          &lt;element ref="kml:Delete"/>
 *          &lt;element ref="kml:Change"/>
 *          &lt;element ref="kml:UpdateOpExtensionGroup"/>
 *      &lt;/choice>
 *      &lt;element ref="kml:UpdateExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 * &lt;/complexType>
 *
 * &lt;element name="UpdateOpExtensionGroup" abstract="true"/>
 * &lt;element name="UpdateExtensionGroup" abstract="true"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Update {

    /**
     *
     * @return
     */
    public URI getTargetHref();

    /**
     *
     * @return
     */
    public List<Object> getUpdates();

    /**
     *
     * @return
     */
    public List<Object> getUpdateOpExtensions();

    /**
     * 
     * @return
     */
    public List<Object> getUpdateExtensions();

    /**
     *
     * @param targetHref
     */
    public void setTargetHref(URI targetHref);

    /**
     * 
     * @param updates
     */
    public void setUpdates(List<Object> updates);

    /**
     *
     * @param updateOpEXtensions
     */
    public void setUpdateOpExtensions(List<Object> updateOpEXtensions);

    /**
     * 
     * @param updateExtensions
     */
    public void setUpdateExtensions(List<Object> updateExtensions);
}
