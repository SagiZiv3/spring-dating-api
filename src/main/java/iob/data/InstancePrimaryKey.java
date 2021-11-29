package iob.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Embeddable
public class InstancePrimaryKey implements Serializable {
    //    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id = 0;
    private String domain;
}