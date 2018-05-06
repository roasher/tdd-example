package ru.yurkinsworkshop.tddexample.service.manualexclusion;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ManualExclusion {
    @Id
    private long id;
}
