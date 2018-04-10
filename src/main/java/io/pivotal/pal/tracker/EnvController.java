package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvController {

    private Map<String, String> environmentVars;

    public EnvController(
            @Value("${PORT:NOT SET}") String port,
            @Value("${MEMORY_LIMIT:NOT SET}") String memoryLimit,
            @Value("${CF_INSTANCE_INDEX:NOT SET}") String cfInstanceIndex,
            @Value("${CF_INSTANCE_ADDR:NOT SET}") String cfInstanceAddress) {
        environmentVars = new HashMap<>();
        environmentVars.put("PORT", port);
        environmentVars.put("MEMORY_LIMIT", memoryLimit);
        environmentVars.put("CF_INSTANCE_INDEX", cfInstanceIndex);
        environmentVars.put("CF_INSTANCE_ADDR", cfInstanceAddress);
    }

    @GetMapping("/env")
    public Map<String, String> getEnv() {
        return environmentVars;
    }
}
