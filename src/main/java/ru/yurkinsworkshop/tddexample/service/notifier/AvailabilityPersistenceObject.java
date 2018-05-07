package ru.yurkinsworkshop.tddexample.service.notifier;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product_availability")
@Data
public class AvailabilityPersistenceObject {

    @Id
    private long id;

    private Long productId;
    private boolean availability;

    public AvailabilityPersistenceObject(Long productId, boolean availability) {
        this.productId = productId;
        this.availability = availability;
    }
}
