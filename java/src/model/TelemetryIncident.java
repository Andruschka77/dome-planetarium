package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TelemetryIncident {

    private int idIncident;
    private String timestamp;
    private String incidentType;
    private String severity;
    private String description;
    private int droppedFrames;
    private Projector projector;
    private ShowSession session;

}