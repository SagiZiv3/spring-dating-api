package iob.logic.annotations;

import iob.boundaries.UserBoundary;
import iob.logic.UsersService;
import iob.logic.exceptions.UserPermissionException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@Aspect
@Slf4j
public class AnnotationsAspect {
    private final UsersService usersService;

    @Autowired
    public AnnotationsAspect(UsersService usersService) {
        this.usersService = usersService;
    }

    @Around("@annotation(iob.logic.annotations.RoleRestricted)")
    public Object authorizeUserRole(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Map<ParameterType, String> authorizationParameters = getParameterTypeStringMap(proceedingJoinPoint, methodSignature);

        log.debug(String.valueOf(authorizationParameters));

        // Get the user from the database, and extract its role.
        UserBoundary user = usersService.login(authorizationParameters.get(ParameterType.DOMAIN),
                authorizationParameters.get(ParameterType.EMAIL));
        UserRoleParameter passedUserRole = UserRoleParameter.valueOf(user.getRole().name().toUpperCase());

        // Get the roles from the annotation.
        UserRoleParameter[] permittedRoles = methodSignature.getMethod()
                .getAnnotation(RoleRestricted.class)
                .permittedRoles();

        // Throw exception if the user's role is not in the permitted roles list.
        if (Arrays.stream(permittedRoles).noneMatch(passedUserRole::equals)) {
            String permittedRolesString = Arrays.stream(permittedRoles)
                    .map(UserRoleParameter::name)
                    .map(WordUtils::capitalizeFully)
                    .collect(Collectors.joining(", "));
            throw new UserPermissionException(WordUtils.capitalizeFully(passedUserRole.name().toLowerCase()),
                    permittedRolesString, user.getUserId().getEmail(), user.getUserId().getDomain());
        }

        return proceedingJoinPoint.proceed();
    }

    private Map<ParameterType, String> getParameterTypeStringMap(@NonNull ProceedingJoinPoint proceedingJoinPoint, @NonNull MethodSignature methodSignature) {
        Parameter[] parameters = methodSignature.getMethod().getParameters();
        Object[] parametersValues = proceedingJoinPoint.getArgs();

        // Log the parameters with their names
        zipArrays(parameters, parametersValues)
                .map(parameter -> String.format("Parameter: %s (%s) = %s",
                        parameter.getValue1().getName(), parameter.getValue1().getType().getSimpleName(), parameter.getValue2()))
                .forEach(log::info);

        return zipArrays(parameters, parametersValues)
                .map(pair -> new Pair<>(pair.getValue1().getAnnotation(RoleParameter.class), pair.getValue2()))
                .filter(pair -> Objects.nonNull(pair.getValue1()))
                .collect(Collectors.toMap(pair -> pair.getValue1().parameterType(), pair -> pair.getValue2().toString()));
    }

    private static <A, B> Stream<Pair<A, B>> zipArrays(A[] as, B[] bs) {
        // Source: https://stackoverflow.com/a/31964093
        return IntStream.range(0, Math.min(as.length, bs.length))
                .mapToObj(i -> new Pair<>(as[i], bs[i]));
    }

    @Getter
    @RequiredArgsConstructor
    private static class Pair<TVal1, TVal2> {
        private final TVal1 value1;
        private final TVal2 value2;
    }
}