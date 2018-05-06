package ru.yurkinsworkshop.tddexample.service.manualexclusion;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor
public class ManualExclusion {

    @Id
    private long id;

}
