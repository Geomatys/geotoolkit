package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultCoordinate implements Coordinate{

    private final double geodeticLongitude;
    private final double geodeticLatitude;
    private final double altitude;

    /**
     *
     * @param coordinates
     */
    public DefaultCoordinate(String coordinates){
        String[] coordinatesList = coordinates.split(",");
        this.geodeticLongitude = Double.valueOf(coordinatesList[0]);//problème lorsqu'une chaîne de chiffres débute par un espace ou autre caractère d'espacement
        this.geodeticLatitude = Double.valueOf(coordinatesList[1]);
        if(coordinatesList[2] != null){
            this.altitude = Double.valueOf(coordinatesList[2]);
        } else {
            this.altitude = Double.NaN;
        }
    }

    /**
     *
     * @param geodeticLongiude
     * @param geodeticLatitude
     * @param altitude
     */
    public DefaultCoordinate(double geodeticLongiude, double geodeticLatitude, double altitude){
        this.geodeticLongitude = geodeticLongiude;
        this.geodeticLatitude = geodeticLatitude;
        this.altitude = altitude;
    }

    /**
     *
     * @param geodeticLongiude
     * @param geodeticLatitude
     */
    public DefaultCoordinate(double geodeticLongiude, double geodeticLatitude){
        this.geodeticLongitude = geodeticLongiude;
        this.geodeticLatitude = geodeticLatitude;
        this.altitude = Double.NaN;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getGeodeticLongitude() {return this.geodeticLongitude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getGeodeticLatitude() {return this.geodeticLatitude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public double getAltitude() {return this.altitude;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getCoordinateString() {
        return String.valueOf(geodeticLongitude)+","+
                String.valueOf(geodeticLatitude)+","+
                String.valueOf(altitude);
    }

    @Override
    public String toString(){
        String resultat = "Coordinate : ";
        resultat += "\n\tGeodetic Longitude = "+geodeticLongitude+
                "\n\tGeodetic Latitude = "+geodeticLatitude+
                "\n\tAltitude = "+altitude;
        return resultat;
    }

}
