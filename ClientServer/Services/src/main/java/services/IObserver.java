package services;

import model.Flight;

import java.io.IOException;
import java.util.List;

public interface IObserver {
    public void update(List<Flight> flights) throws CustomException;

}
