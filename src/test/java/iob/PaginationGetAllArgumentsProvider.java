package iob;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class PaginationGetAllArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        // Format: number_of_objects_to_create, page, size, expected_returned_length
        return Stream.of(
                arguments(1, 1, 2, 0), // Create 1 object and get 2 objects from page 1, expecting to get 0 objects.
                arguments(3, 1, 2, 1), // Create 3 objects and get 2 objects from page 1, expecting to get 1 object.
                arguments(3, 0, 2, 2), // Create 3 objects and get 2 objects from page 0, expecting to get 2 objects.
                arguments(3, 1, 1, 1), // Create 3 objects and get 1 objects from page 1, expecting to get 1 object.
                arguments(5, 2, 2, 1), // Create 5 objects and get 2 objects from page 2, expecting to get 1 object.
                arguments(5, 1, 2, 2), // Create 5 objects and get 2 objects from page 1, expecting to get 2 objects.
                arguments(6, 2, 2, 2), // Create 6 objects and get 2 objects from page 2, expecting to get 2 objects.
                arguments(2, 0, 2, 2)  // Create 2 objects and get 2 objects from page 0, expecting to get 2 objects.
        );
    }
}