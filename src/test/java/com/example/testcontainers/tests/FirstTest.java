package com.example.testcontainers.tests;

import com.example.testcontainers.junit.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class FirstTest extends TestCase {

    @Test
    public void testDoSomething()
    {
        Assert.assertNotNull(seleniumEnvironment.getBrowserDriver().getTitle());
    }
}
