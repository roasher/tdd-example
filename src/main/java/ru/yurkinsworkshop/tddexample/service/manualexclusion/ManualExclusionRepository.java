package ru.yurkinsworkshop.tddexample.service.manualexclusion;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ManualExclusionRepository extends MongoRepository<ManualExclusion, Long> {
}
