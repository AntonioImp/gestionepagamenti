package dsbd2020.ecommerce.gestionepagamenti.data;


import dsbd2020.ecommerce.gestionepagamenti.entity.Orders;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrdersRepository extends CrudRepository<Orders, Integer> {
    Optional<Iterable<Orders>> findByUnixTimestampBetween(Long startUnixTimestamp, Long endUnixTimestamp);
}
