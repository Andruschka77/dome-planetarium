package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SessionProjector {

    private int id;
    private ShowSession session;
    private CalibrationProfile profile;
    private int streamChannel;
    private double renderingLoadPercent;
    private int droppedFrames;

}