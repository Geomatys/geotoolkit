package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class CoordinateDefault implements Coordinate{

    private double geodeticLongitude;
    private double geodeticLatitude;
    private double altitude;

    public CoordinateDefault(String coordinates){
        String[] coordinatesList = coordinates.split(",");
        this.geodeticLongitude = Double.valueOf(coordinatesList[0]);//problème lorsqu'une chaîne de chiffres débute par un espace ou autre caractère d'espacement
        this.geodeticLatitude = Double.valueOf(coordinatesList[1]);
        if(coordinatesList[2] != null){
            this.altitude = Double.valueOf(coordinatesList[2]);
        }
    }

    public CoordinateDefault(double geodeticLongiude, double geodeticLatitude, double altitude){
        this.geodeticLongitude = geodeticLongiude;
        this.geodeticLatitude = geodeticLatitude;
        this.altitude = altitude;
    }

    public CoordinateDefault(double geodeticLongiude, double geodeticLatitude){
        this.geodeticLongitude = geodeticLongiude;
        this.geodeticLatitude = geodeticLatitude;
    }

    @Override
    public double getGeodeticLongitude() {return this.geodeticLongitude;}

    @Override
    public double getGeodeticLatitude() {return this.geodeticLatitude;}

    @Override
    public double getAltitude() {return this.altitude;}

    @Override
    public String getCoordinateString() {
        return String.valueOf(geodeticLongitude)+","+
                String.valueOf(geodeticLatitude)+","+
                String.valueOf(altitude);
    }

    public String toString(){
        String resultat = "Coordinate : ";
        resultat += "\n\tGeodetic Longitude = "+geodeticLongitude+
                "\n\tGeodetic Latitude = "+geodeticLatitude+
                "\n\tAltitude = "+altitude;
        return resultat;
    }

}
