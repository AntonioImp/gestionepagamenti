package dsbd2020.ecommerce.gestionepagamenti.service;

import dsbd2020.ecommerce.gestionepagamenti.data.LoggingRepository;
import dsbd2020.ecommerce.gestionepagamenti.entity.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoggingService {

    @Autowired
    LoggingRepository repository;

    public Logging addLogging(Logging logging) {
        return repository.save(logging);
    }

    public Iterable<Logging> getLoggingBetweenTimestamp(Long startUnixTimestamp, Long endUnixTimestamp) {
        return repository.findByUnixTimestampBetween(startUnixTimestamp, endUnixTimestamp).orElse(null);
    }
}
