package iob.boundaries.helpers;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class InvokedByBoundary {
    private UserIdBoundary userId;
}