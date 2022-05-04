package com.tens.util;

import java.io.File;
import java.util.ArrayList;

public class SplitUtil {
    public ArrayList<Section> spitFile(String fileName, int splitNum) {

        File inputFile = new File(fileName);
        ArrayList<Section> sections = new ArrayList<>();
        sections.add(new Section(0));
        return sections;
    }
}
