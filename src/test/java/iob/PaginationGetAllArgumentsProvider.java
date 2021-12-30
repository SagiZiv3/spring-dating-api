package iob;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class PaginationGetAllArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        // Format: number_of_users, page, size, expected_returned_length
        return Stream.of(
                arguments(3, 1, 2, 1),
                arguments(3, 0, 2, 2),
                arguments(3, 1, 1, 1),
                arguments(5, 2, 2, 1),
                arguments(5, 1, 2, 2),
                arguments(6, 2, 2, 2)
        );
    }
}