package platform.merchant.service.infrastructure.persistence.adapter.customer;

import org.springframework.stereotype.Component;
import platform.merchant.service.domain.customer.model.Customer;
import platform.merchant.service.infrastructure.persistence.jpa.customer.entity.CustomerEntity;

@Component
public class CustomerPersistenceMapper {

    public CustomerEntity toEntity(Customer customer) {
        return CustomerEntity.builder()
                .id(customer.getId())
                .userId(customer.getUserId())
                .status(customer.getStatus())
                .fullName(customer.getFullName())
                .totalTrips(customer.getTotalTrips())
                .totalSpent(customer.getTotalSpent())
                .tripPoints(customer.getTripPoints())
                .lastBookingAt(customer.getLastBookingAt())
                .lastTripAt(customer.getLastTripAt())
                .createdAt(customer.getCreatedAt())
                .createdBy(customer.getCreatedBy())
                .updatedAt(customer.getUpdatedAt())
                .updatedBy(customer.getUpdatedBy())
                .build();
    }

    public Customer toDomain(CustomerEntity customerEntity) {
        return Customer.builder()
                .id(customerEntity.getId())
                .userId(customerEntity.getUserId())
                .status(customerEntity.getStatus())
                .fullName(customerEntity.getFullName())
                .tripPoints(customerEntity.getTripPoints())
                .lastBookingAt(customerEntity.getLastBookingAt())
                .lastTripAt(customerEntity.getLastTripAt())
                .createdAt(customerEntity.getCreatedAt())
                .createdBy(customerEntity.getCreatedBy())
                .updatedAt(customerEntity.getUpdatedAt())
                .updatedBy(customerEntity.getUpdatedBy())
                .build();
    }
}
