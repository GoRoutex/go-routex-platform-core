package platform.booking.service.domain.customer.port;



import platform.booking.service.domain.customer.model.Customer;

import java.util.Optional;

public interface CustomerRepositoryPort {

    Optional<Customer> findByUserId(String userId);
    Customer save(Customer customer);
}
