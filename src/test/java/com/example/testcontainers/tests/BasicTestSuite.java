package com.example.testcontainers.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
			FirstTest.class, SecondTest.class
})
public class BasicTestSuite {
	//unit test suite
}
