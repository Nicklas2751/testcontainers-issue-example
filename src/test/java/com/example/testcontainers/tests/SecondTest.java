package com.example.testcontainers.tests;

import com.example.testcontainers.junit.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class SecondTest extends TestCase {

    @Test
    public void testDoSomethingOther()
    {
        Assert.assertNotNull(seleniumEnvironment.getBrowserDriver().getPageSource());
    }
}
