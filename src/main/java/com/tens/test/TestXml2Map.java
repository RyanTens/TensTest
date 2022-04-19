package com.tens.test;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

public class TestXml2Map {

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        File file = new File("D:\\code\\education\\education-sparkstreaming\\src\\main\\resources\\core-site.xml");
        Map<String, String> configMap = new TestXml2Map().getMap(map, file);
        configMap.forEach((k,v) -> System.out.println(k + "::::::" + v));

    }

    public Map<String, String> getMap(Map map, File file) {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(file);
            Element configurations = document.getRootElement();
            Iterator confIt = configurations.elementIterator();
            while (confIt.hasNext()) {
                String key = "", value = "";
                Element propElement = (Element) confIt.next();
                Iterator propIt = propElement.elementIterator();
                while (propIt.hasNext()) {
                    Element child = (Element) propIt.next();
                    String name = child.getName();
                    if (name.equals("name")) {
                        key = child.getStringValue();
                        System.out.print("name=" + key);
                    } else if (name.equals("value")) {
                        value = child.getStringValue();
                        System.out.println("\tvalue=" + value);
                    }
                }
                map.put(key, value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}