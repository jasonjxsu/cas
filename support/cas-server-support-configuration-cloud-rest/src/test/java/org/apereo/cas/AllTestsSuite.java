package org.apereo.cas;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * This is {@link AllTestsSuite}.
 *
 * @author Misagh Moayyed
 * @since 6.0.0-RC3
 */
@SelectClasses({
    RestfulPropertySourceLocatorTests.class,
    RestfulCloudConfigBootstrapConfigurationTests.class
})
@Suite
public class AllTestsSuite {
}
