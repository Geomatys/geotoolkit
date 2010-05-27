package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class KmlDefault implements Kml{

    private NetworkLinkControl networkLinkControl;
    private AbstractFeature abstractFeature;
    private List<SimpleType> kmlSimpleExtensions;
    private List<AbstractObject> kmlObjectExtensions;

    /**
     *
     * @param networkLinkControl
     * @param abstractFeature
     * @param kmlSimpleExtensions
     * @param kmlObjectExtensions
     */
    public KmlDefault(NetworkLinkControl networkLinkControl,
            AbstractFeature abstractFeature,
            List<SimpleType> kmlSimpleExtensions,
            List<AbstractObject> kmlObjectExtensions){
        this.networkLinkControl = networkLinkControl;
        this.abstractFeature = abstractFeature;
        this.kmlSimpleExtensions = kmlSimpleExtensions;
        this.kmlObjectExtensions = kmlObjectExtensions;
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
