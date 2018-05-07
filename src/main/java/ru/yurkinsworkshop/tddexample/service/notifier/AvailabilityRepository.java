package ru.yurkinsworkshop.tddexample.service.notifier;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AvailabilityRepository extends MongoRepository<AvailabilityPersistenceObject, Long> {

}
