package ru.vood.hazelcastgraph2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SomeDataJava implements Serializable {
    private transient static final long serialVersionUID = 1L;

    private String field1;
    private String field2;
    private String field3;

}
