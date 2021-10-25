package org.apereo.cas.discovery;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.BaseCasActuatorEndpoint;

import io.swagger.v3.oas.annotations.Operation;
import lombok.val;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * This is {@link CasServerDiscoveryProfileEndpoint}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@RestControllerEndpoint(id = "discoveryProfile", enableByDefault = false)
public class CasServerDiscoveryProfileEndpoint extends BaseCasActuatorEndpoint {
    private final CasServerProfileRegistrar casServerProfileRegistrar;

    public CasServerDiscoveryProfileEndpoint(final CasConfigurationProperties casProperties,
                                             final CasServerProfileRegistrar casServerProfileRegistrar) {
        super(casProperties);
        this.casServerProfileRegistrar = casServerProfileRegistrar;
    }

    /**
     * Discovery.
     *
     * @return the map
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "Produce CAS discovery profile")
    public Map<String, Object> discovery() {
        val results = new HashMap<String, Object>();
        results.put("profile", casServerProfileRegistrar.getProfile());
        return results;
    }
}
