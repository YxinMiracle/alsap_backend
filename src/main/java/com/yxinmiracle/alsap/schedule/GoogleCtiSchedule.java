package com.yxinmiracle.alsap.schedule;

/*
 * @author  YxinMiracle
 * @date  2024-11-15 9:57
 * @Gitee: https://gitee.com/yxinmiracle
 */


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class GoogleCtiSchedule {


    public static void main(String[] args) throws Exception {
        // FeedBurner的RSS源URL
        String rssUrl = "https://feeds.feedburner.com/threatintelligence/pvexyqv7v0v";
        URL url = new URL(rssUrl);

        SAXBuilder saxBuilder = new SAXBuilder();
        InputStream inputStream = url.openStream();
        Document document = saxBuilder.build(inputStream);

        Element rootElement = document.getRootElement();
        List<Element> items = rootElement.getChildren("channel").get(0).getChildren("item");

        for (Element item : items) {
            String title = item.getChildText("title");
            String link = item.getChildText("link");
            String description = item.getChildText("description");

            System.out.println("Title: " + title);
            System.out.println("Link: " + link);
            System.out.println("Description: " + description);
            System.out.println();
        }
    }

}
