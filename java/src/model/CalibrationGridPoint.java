package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CalibrationGridPoint {

    private int idGridPoint;
    private int pointIndex;
    private CalibrationProfile profile;
    private double targetX;
    private double targetY;
    private double actualX;
    private double actualY;

}