package com.lichenaut.util;

import java.io.Serializable;
import java.util.List;

public record RestrictionData(List<String> roleIds, String reason) implements Serializable {
}
