package com.kh618.locationto;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class Point {

    private Double x ;
    private Double y ;

    public Point() {
    }

    public Point(Double x, Double y) {
        this.x = x;
        this.y = y;
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
    public Point(LatLng ll){
        SetPointFromLatLang(ll);
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
