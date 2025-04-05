package services;

import model.Flight;

import java.io.IOException;
import java.util.List;

public interface IObserver {
    public void update() throws CustomException;
}
