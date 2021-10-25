package org.apereo.cas;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test suite that runs all test in a batch.
 *
 * @author Misagh Moayyed
 * @since 6.0.0
 */
@SelectClasses({
    CasMetricsConfigurationTests.class,
    SystemMonitorHealthIndicatorTests.class
})
@Suite
public class CasMetricsTestsSuite {
}
