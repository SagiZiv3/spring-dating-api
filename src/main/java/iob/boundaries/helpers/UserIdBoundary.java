package iob.boundaries.helpers;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class UserIdBoundary {
    private String domain;
    private String email;
}