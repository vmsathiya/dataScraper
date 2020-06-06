package RunSuitesParallel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.TestNG;

public class runSuitesParallel {

	public static void main(String[] args) {
		TestNG testng = new TestNG();
		List<String> suites = new ArrayList<String>();
		suites.add("dataScraper.xml");
 		//testng.setTestSuites(Arrays.asList(new String[] {System.getProperty("user.dir") + "//testng.xml"}));
		testng.setTestSuites(suites);
		testng.setSuiteThreadPoolSize(2);
		testng.run();
	}



}
