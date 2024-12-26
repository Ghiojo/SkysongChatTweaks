package org.skysongdev.skysongchattweaks.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Condition {
    private String id;
    private String name;
    private String description;
    private String item;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        int level = 1;
        return parseDescription(level);
    }

    public String getDescription(int level) {
        return parseDescription(level);
    }

    public String getItem() {
        return item;
    }

    public Condition(String id, String name, String description, String item) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.item = item;
    }

    private String parseDescription(int level) {
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile("\\$");
        String newMessage = description;
        Matcher matcher = pattern.matcher(newMessage);

        while (matcher.find()) {
            matcher.appendReplacement(sb, "<gold>" + level + "</gold>");
        }
        matcher.appendTail(sb);
        newMessage = sb.toString();
        return newMessage;
    }

    public String getDefaultDescription() {
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile("\\$");
        String newMessage = description;
        Matcher matcher = pattern.matcher(newMessage);

        while (matcher.find()) {
            matcher.appendReplacement(sb, "<gold>[value]</gold>");
        }
        matcher.appendTail(sb);
        newMessage = sb.toString();
        return newMessage;
    }

}
