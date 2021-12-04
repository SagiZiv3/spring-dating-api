package iob.boundaries.helpers;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class InstanceIdBoundary {
    private String domain, id;
}