package services;

import java.io.IOException;

public interface IObserver {
    public void update() throws CustomException;
}
