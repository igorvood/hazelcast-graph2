package ru.vood.hazelcastgraph2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SomeDataJava implements Serializable {
    private String field1;
    private String field2;
    private String field3;

}
