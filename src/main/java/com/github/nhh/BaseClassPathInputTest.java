package com.github.nhh;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class BaseClassPathInputTest {

    private static final Object[][] EMPTY = new Object[0][2];

    protected List<String> readFully(File inputFile) throws IOException {
        List<String> result = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)))) {
            String input = br.readLine();
            while (input != null) {
                result.add(input);
                input = br.readLine();
            }
        }
        return result;
    }


    public Object[][] generateTestData(String path) {
        File file = new File(path);
        String[] filenames = file.list();
        if (filenames == null || filenames.length < 1) {
            return EMPTY;
        }
        Map<String, FileTuple> map = new HashMap<>(filenames.length);

        for (String filename : filenames) {
            String slug = parseSlug(filename);
            if (slug != null) {
                FileTuple tuple = map.getOrDefault(slug, new FileTuple());
                if (isInput(filename)) {
                    tuple.input = new File(path + "/" + filename);
                } else {
                    tuple.output = new File(path + "/" + filename);
                }
                tuple.slug = slug;
                map.put(slug, tuple);
            }
        }
        // filter crap which has no corresponding input and output

        List<FileTuple> filtered = map.values()
                .stream()
                .filter(tuple->tuple.input != null && tuple.output != null)
                .collect(Collectors.toList());

        Object[][] result = new Object[filtered.size()][2];
        int idx = 0;
        for (FileTuple tuple: filtered) {
            result[idx][0] = tuple.input;
            result[idx][1] = tuple.output;
            idx++;
        }

        return result;
    }

    private boolean isInput(String filename) {
        return filename.endsWith(".input");
    }

    private String parseSlug(String filename) {
        if (filename.endsWith(".input") || filename.endsWith(".output")) {
            return filename.replace(".input","").replace(".output","");
        }
        return null;
    }

    public static class FileTuple {
        File input;
        File output;
        String slug;
    }
}
