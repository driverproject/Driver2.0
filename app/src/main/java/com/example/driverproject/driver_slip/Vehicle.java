package com.example.driverproject.driver_slip;
public class Vehicle {

    String vehicle_Type,vehicle_Number,date_journey,start_kms,end_kms,user_id;

    public Vehicle()
    {

    }

    public Vehicle(String user_id,String vehicle_Type, String vehicle_Number, String date_journey, String start_kms, String end_kms) {
        this.user_id=user_id;
        this.vehicle_Type = vehicle_Type;
        this.vehicle_Number = vehicle_Number;
        this.date_journey = date_journey;
        this.start_kms = start_kms;
        this.end_kms = end_kms;
    }

    public String getVehicle_Type() {
        return vehicle_Type;
    }

    public String getVehicle_Number() {
        return vehicle_Number;
    }

    public String getDate_journey() {
        return date_journey;
    }

    public String getStart_kms() {
        return start_kms;
    }

    public String getEnd_kms() {
        return end_kms;
    }

    public String getUser_id() {
        return user_id;
    }
}

