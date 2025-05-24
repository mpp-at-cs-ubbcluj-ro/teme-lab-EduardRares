package services;

import model.Flight;

public interface IClient {
    public void updateReceived(Flight flights) throws CustomException;
}
