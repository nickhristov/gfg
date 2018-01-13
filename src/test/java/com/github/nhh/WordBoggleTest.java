package com.github.nhh;


import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class WordBoggleTest extends BaseClassPathInputTest {

    @Test(dataProvider = "wordboggledata")
    public void runTests(File inputFile, File outputFile) throws IOException {
        List<String> input = readFully(inputFile);
        List<String> expectedOutput = readFully(outputFile);
        WordBoggle wordBoggle = new WordBoggle();
        List<String> output = wordBoggle.solve(input);
        Assert.assertEquals(output, expectedOutput, "Output does not match for input " + input);

    }

    @DataProvider(name = "wordboggledata")
    public Object[][] dataProvider() {
        URL resource = WordBoggleTest.class.getClassLoader().getResource("wordboggle");
        assert resource != null;
        return generateTestData(resource.getFile());
    }
}