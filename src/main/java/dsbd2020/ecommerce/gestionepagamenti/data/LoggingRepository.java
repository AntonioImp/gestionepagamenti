package dsbd2020.ecommerce.gestionepagamenti.data;

import dsbd2020.ecommerce.gestionepagamenti.entity.Logging;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LoggingRepository extends CrudRepository<Logging, Integer> {
    Optional<Iterable<Logging>> findByUnixTimestampBetween(Long startUnixTimestamp, Long endUnixTimestamp);
}
