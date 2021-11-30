package iob.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatedByEntity implements Serializable {
    private String createdByUserDomain;
    private String createdByUserEmail;
}