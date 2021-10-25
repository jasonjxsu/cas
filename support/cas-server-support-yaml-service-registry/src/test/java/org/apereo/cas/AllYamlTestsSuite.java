package org.apereo.cas;

import org.apereo.cas.services.YamlServiceRegistryConfigurationTests;
import org.apereo.cas.services.YamlServiceRegistryTests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test suite to run all LDAP tests.
 *
 * @author Misagh Moayyed
 * @since 4.1.0
 */
@SelectClasses({
    YamlServiceRegistryConfigurationTests.class,
    YamlServiceRegistryTests.class
})
@Suite
public class AllYamlTestsSuite {
}
