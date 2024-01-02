package garmoza.taskmanagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;

public abstract class PatchDTO {

    @JsonIgnore
    private final Set<String> patchedAttrs = new HashSet<>();

    public Set<String> getPatchedAttrs() {
        return patchedAttrs;
    }

    protected void addPatchedAttr(String attrName) {
        patchedAttrs.add(attrName);
    }

    public boolean isPatchedAttr(String attrName) {
        return patchedAttrs.contains(attrName);
    }
}
