package pl.lukasz.prostySewer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringInit {

     private final Repository repository;

    @Autowired
    public SpringInit(Repository repository) {
        this.repository = repository;
    }
}
