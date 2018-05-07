package ru.yurkinsworkshop.tddexample.service.notifier.lazy;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "product_availability")
@Data
public class AvailabilityPersistenceObject {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private Long productId;
    private boolean availability;

    public AvailabilityPersistenceObject(Long productId, boolean availability) {
        this.productId = productId;
        this.availability = availability;
    }
}
