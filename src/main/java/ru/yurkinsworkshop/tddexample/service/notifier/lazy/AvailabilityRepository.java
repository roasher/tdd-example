package ru.yurkinsworkshop.tddexample.service.notifier.lazy;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AvailabilityRepository extends MongoRepository<AvailabilityPersistenceObject, ObjectId> {

    AvailabilityPersistenceObject findByProductId(Long productId);

}
