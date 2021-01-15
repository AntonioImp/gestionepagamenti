package dsbd2020.ecommerce.gestionepagamenti.service;

import dsbd2020.ecommerce.gestionepagamenti.data.OrdersRepository;
import dsbd2020.ecommerce.gestionepagamenti.entity.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    OrdersRepository repository;

    @Autowired
    OrderService(OrdersRepository repository) {
        this.repository = repository;
    }

    public Orders addOrders(Orders orders) {
        return repository.save(orders);
    }

    public Iterable<Orders> getOrdersBetweenTimestamp(Long startUnixTimestamp, Long endUnixTimestamp) {
        return repository.findByUnixTimestampBetween(startUnixTimestamp, endUnixTimestamp).orElse(null);
    }
}
