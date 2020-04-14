package com.kh618.locationto;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
/*
 * This class created to used with firebase a custom data type
 * to make easy send and received data from firebase and have a
 * method called toLatLng that's return LatLng object from our class
 */
public class Point {

    private Double x ;
    private Double y ;

    public Point() {
        this.x = 0.0;
        this.y= 0.0;
    }

    public Point(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Point(LatLng ll){
        SetPointFromLatLang(ll);
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }
    public void setY(Double y) {
        this.y = y;
    }

    Point SetPointFromLatLang(LatLng ll){
       this.setX(ll.latitude);
       this.setY(ll.longitude);
       return this;
    }

    @NonNull
    @Override
    public String toString() {

        String pointAsString;
        pointAsString = getX()+ " ---- " + getY();
        return pointAsString;
    }
    public LatLng toLatLng(){
        return new LatLng(getX(),getY());
    }
}
