package ro.mpp2025.utils.events;

import ro.mpp2025.model.Flight;

import java.io.Serializable;

public class FlightEvent implements Event {
    private FlightEventType type;
    private Flight data,oldData;

    public FlightEvent(FlightEventType type, Flight data){
        this.type = type;
        this.data = data;
    }

    public FlightEvent (FlightEventType type, Flight data, Flight oldData){
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public FlightEventType getType() {
        return type;
    }

    public Flight getData() {
        return data;
    }

    public Flight getOldData() {
        return oldData;
    }
}
