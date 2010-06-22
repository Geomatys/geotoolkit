package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultKml implements Kml{

    private final NetworkLinkControl networkLinkControl;
    private final AbstractFeature abstractFeature;
    private final List<SimpleType> kmlSimpleExtensions;
    private final List<AbstractObject> kmlObjectExtensions;

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
            List<AbstractObject> kmlObjectExtensions){
        this.networkLinkControl = networkLinkControl;
        this.abstractFeature = abstractFeature;
        this.kmlSimpleExtensions = (kmlSimpleExtensions == null) ? EMPTY_LIST : kmlSimpleExtensions;
        this.kmlObjectExtensions = (kmlObjectExtensions == null) ? EMPTY_LIST : kmlObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public NetworkLinkControl getNetworkLinkControl() {return this.networkLinkControl;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AbstractFeature getAbstractFeature() {return this.abstractFeature;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getKmlSimpleExtensions() {return this.kmlSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getKmlObjectExtensions() {return this.kmlObjectExtensions;}

    @Override
    public String toString(){
        String resultat = "KML DEFAULT : "+
                "AbstractFeature : "+ this.abstractFeature;
        return resultat;
    }
}
