package ro.iss2025;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.iss2025.repository.ComputerRepairRequestRepository;
import ro.iss2025.repository.ComputerRepairedFormRepository;
import ro.iss2025.repository.jdbc.ComputerRepairRequestJdbcRepository;
import ro.iss2025.repository.jdbc.ComputerRepairedFormJdbcRepository;
import ro.iss2025.services.ComputerRepairServices;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class RepairShopConfig {
    @Bean
    Properties getProps() {
        Properties props = new Properties();
        try{
            System.out.println("Searching bd.config in directory " + (new File(".").getAbsolutePath()));
            props.load(new FileReader(new File("bd.config")));
        } catch (IOException e) {
            System.err.println("Configuration file bd.config not found" + e);
        }
        return props;
    }

    @Bean
    ComputerRepairRequestRepository requestsRepo(){
        return new ComputerRepairRequestJdbcRepository(getProps());
    }

    @Bean
    ComputerRepairedFormRepository formsRepo(){
        return new ComputerRepairedFormJdbcRepository(getProps());
    }

    @Bean
    ComputerRepairServices services(){
        return new ComputerRepairServices(requestsRepo(), formsRepo());
    }

}
