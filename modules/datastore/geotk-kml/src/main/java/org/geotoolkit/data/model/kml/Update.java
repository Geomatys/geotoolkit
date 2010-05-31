package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 * <p>This interface maps Update element.</p>
 *
 * <br />&lt;element name="Update" type="kml:UpdateType"/>
 * <br />&lt;complexType name="UpdateType" final="#all">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:targetHref"/>
 * <br />&lt;choice maxOccurs="unbounded">
 * <br />&lt;element ref="kml:Create"/>
 * <br />&lt;element ref="kml:Delete"/>
 * <br />&lt;element ref="kml:Change"/>
 * <br />&lt;element ref="kml:UpdateOpExtensionGroup"/>
 * <br />&lt;/choice>
 * <br />&lt;element ref="kml:UpdateExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/complexType>
 * <br />&lt;element name="UpdateOpExtensionGroup" abstract="true"/>
 * <br />&lt;element name="UpdateExtensionGroup" abstract="true"/>
 *
 * @author Samuel Andrés
 */
public interface Update {

    /**
     *
     * @return
     */
    public List<Create> getCreates();

    /**
     *
     * @return
     */
    public List<Delete> getDeletes();

    /**
     *
     * @return
     */
    public List<Change> getChanges();

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
}
