package com.example.harranharitauygulamasi;

import android.location.Location;

import java.util.ArrayList;

public class Legs {
    Distance distance = new Distance();
    Duration duration = new Duration();
    String end_address = "";
    String start_address = "";
    Location end_location;
    Location start_location;
    ArrayList<Steps> steps = new ArrayList<>();
}
