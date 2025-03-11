package ro.mpp2025;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class MainBD {
    public static void main(String[] args) {

        Properties props=new Properties();
        try {
            props.load(new FileReader("Lab3/bd.config"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }
        CarRepository carRepo=new CarsDBRepository(props);
        Car newCar = new Car("Tesla","Model S", 2019);
        carRepo.add(newCar);
        newCar.setYear(2020);
        carRepo.update(newCar.getId(), newCar);
        System.out.println("Toate masinile din db");
        for(Car car:carRepo.findAll())
            System.out.println(car);
       String manufacturer="Tesla";
        System.out.println("Masinile produse de "+manufacturer);
        for(Car car:carRepo.findByManufacturer(manufacturer))
            System.out.println(car);

    }
}
