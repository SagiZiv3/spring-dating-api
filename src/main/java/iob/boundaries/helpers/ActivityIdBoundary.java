package iob.boundaries.helpers;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class ActivityIdBoundary {
    private String domain, id;
}