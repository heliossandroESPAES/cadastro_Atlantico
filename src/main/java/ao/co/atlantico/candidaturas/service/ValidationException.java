package ao.co.atlantico.candidaturas.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ValidationException extends Exception {

    private final Map<String, String> errors;

    public ValidationException(Map<String, String> errors) {
        super(errors.values().stream().findFirst().orElse("Os dados informados sao invalidos."));
        this.errors = Collections.unmodifiableMap(new LinkedHashMap<>(errors));
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
