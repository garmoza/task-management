package garmoza.taskmanagement.dto;

import garmoza.taskmanagement.exception.ForbiddenException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PatchDtoValidator {

    private final Validator validator;

    public void validate(PatchDTO dto, Set<String> allowedAttrs) {
        // names of the received fields that cannot be changed
        List<String> forbiddenAttrs = dto.getPatchedAttrs().stream()
                .filter(attr -> !allowedAttrs.contains(attr))
                .toList();
        if (!forbiddenAttrs.isEmpty()) {
            throw new ForbiddenException("change attributes not allowed: " + String.join(", ", forbiddenAttrs));
        }

        Set<ConstraintViolation<PatchDTO>> violations = validator.validate(dto);
        // violations for allowed and patched attributes only
        var allowedAttrViolations = violations.stream()
                .filter(v -> {
                    PathImpl path = (PathImpl) v.getPropertyPath();
                    String attrName = path.getLeafNode().getName();
                    return allowedAttrs.contains(attrName) && dto.isPatchedAttr(attrName);
                })
                .collect(Collectors.toSet());
        if (!allowedAttrViolations.isEmpty()) {
            throw new ConstraintViolationException(allowedAttrViolations);
        }
    }
}
