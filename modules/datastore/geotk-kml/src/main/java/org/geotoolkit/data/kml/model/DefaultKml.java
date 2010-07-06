package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultKml implements Kml {

    private final Extensions extensions = new Extensions();
    private String version = URI_KML_2_2;
    private NetworkLinkControl networkLinkControl;
    private AbstractFeature abstractFeature;

    /**
     *
     */
    public DefaultKml() {
    }

    /**
     * 
     * @param networkLinkControl
     * @param abstractFeature
     * @param kmlSimpleExtensions
     * @param kmlObjectExtensions
     */
    public DefaultKml(NetworkLinkControl networkLinkControl,
            AbstractFeature abstractFeature,
            List<SimpleType> kmlSimpleExtensions,
            List<AbstractObject> kmlObjectExtensions) {
        this.networkLinkControl = networkLinkControl;
        this.abstractFeature = abstractFeature;
        if (kmlSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.KML).addAll(kmlSimpleExtensions);
        }
        if (kmlObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.KML).addAll(kmlObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getVersion() {
        return this.version;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public NetworkLinkControl getNetworkLinkControl() {
        return this.networkLinkControl;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AbstractFeature getAbstractFeature() {
        return this.abstractFeature;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setVersion(String version) throws KmlException {
        if (URI_KML_2_1.equals(version) || URI_KML_2_2.equals(version)) {
            this.version = version;
        } else {
            throw new KmlException("Bad Kml version Uri. This reader supports 2.1 and 2.2 versions.");
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setNetworkLinkControl(NetworkLinkControl networkLinkCOntrol) {
        this.networkLinkControl = networkLinkCOntrol;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractFeature(AbstractFeature feature) {
        this.abstractFeature = feature;
    }

    @Override
    public String toString() {
        String resultat = "KML DEFAULT : "
                + "AbstractFeature : " + this.abstractFeature;
        return resultat;
    }

    @Override
    public Extensions extensions() {
        return this.extensions;
    }
}
