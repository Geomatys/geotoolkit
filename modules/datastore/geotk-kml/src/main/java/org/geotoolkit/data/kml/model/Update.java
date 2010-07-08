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
     * @deprecated
     */
    @Deprecated
    public List<AbstractFeature> getReplaces();

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
     * @param creates
     */
    public void setCreates(List<Create> creates);

    /**
     *
     * @param deletes
     */
    public void setDeletes(List<Delete> deletes);

    /**
     *
     * @param changes
     */
    public void setChanges(List<Change> changes);

    /**
     * 
     * @param replaces
     * @deprecated
     */
    @Deprecated
    public void setReplaces(List<AbstractFeature> replaces);

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
