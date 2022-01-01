package iob.logic.annotations;

import iob.logic.UserPermissionsHandler;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@Aspect
@Slf4j
public class AnnotationsAspect {
    private final UserPermissionsHandler permissionsHandler;

    @Autowired
    public AnnotationsAspect(UserPermissionsHandler permissionsHandler) {
        this.permissionsHandler = permissionsHandler;
    }

    @Around("@annotation(iob.logic.annotations.RoleRestricted)")
    public Object authorizeUserRole(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        log.info("Checking user's permission to invoke: {}", methodSignature.getName());
        Map<ParameterType, String> authorizationParameters = getParameterTypeStringMap(proceedingJoinPoint, methodSignature);

        log.debug("Extracted parameters {}", authorizationParameters);

        // Get the roles from the annotation.
        UserRoleParameter[] permittedRoles = methodSignature.getMethod()
                .getAnnotation(RoleRestricted.class)
                .permittedRoles();

        permissionsHandler.throwIfNotAuthorized(getIfExist(ParameterType.DOMAIN, authorizationParameters),
                getIfExist(ParameterType.EMAIL, authorizationParameters), permittedRoles);

        // Invoke the wrapped method
        return proceedingJoinPoint.proceed();
    }

    private Map<ParameterType, String> getParameterTypeStringMap(@NonNull ProceedingJoinPoint proceedingJoinPoint, @NonNull MethodSignature methodSignature) {
        log.trace("Extracting parameters from method's signature");
        Parameter[] parameters = methodSignature.getMethod().getParameters();
        Object[] parametersValues = proceedingJoinPoint.getArgs();

        // Log the parameters' name, type and value.
        zipArrays(parameters, parametersValues)
                .map(parameter -> String.format("Parameter: %s (%s) = %s",
                        parameter.getValue1().getName(), parameter.getValue1().getType().getSimpleName(), parameter.getValue2()))
                .forEach(log::debug);

        return zipArrays(parameters, parametersValues)
                // Extract the RoleParameter annotation from the properties.
                .map(pair -> new Pair<>(pair.getValue1().getAnnotation(RoleParameter.class), pair.getValue2()))
                // Filter out all the properties without the annotation.
                .filter(pair -> Objects.nonNull(pair.getValue1()))
                // Map the parameter's role-type to the parameter's value.
                .collect(Collectors.toMap(pair -> pair.getValue1().parameterType(), pair -> pair.getValue2().toString()));
    }

    private String getIfExist(ParameterType key, Map<ParameterType, String> map) {
        if (!map.containsKey(key)) {
            String parameterName = WordUtils.capitalizeFully(key.name());
            log.error("Missing  authorization parameter: {}", parameterName);
            throw new RuntimeException(String.format("Missing authorization parameter: %s",
                    parameterName));
        }
        return map.get(key);
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