package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Hall {

    private int idHall;
    private String name;
    private double domeDiameter;
    private String surfaceType;
    private int capacity;
    private String address;

}