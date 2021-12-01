package iob.data.primarykeys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstancePrimaryKey implements Serializable {
    private long id = 0;
    private String domain;
}