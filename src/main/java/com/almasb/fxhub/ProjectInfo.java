package com.almasb.fxhub;

import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class ProjectInfo {

    private String title;
    private String version;
    private String description;
    private List<String> authors;
    private List<String> tags;
    private String website;
    private String screenshotLink;
    private String exeZipLinkWindows;
    private String exeZipLinkLinux;
    private String exeZipLinkMac;

    public ProjectInfo(String title, String version, String description, List<String> authors, List<String> tags, String website, String screenshotLink, String exeZipLinkWindows, String exeZipLinkLinux, String exeZipLinkMac) {
        this.title = title;
        this.version = version;
        this.description = description;
        this.authors = authors;
        this.tags = tags;
        this.website = website;
        this.screenshotLink = screenshotLink;
        this.exeZipLinkWindows = exeZipLinkWindows;
        this.exeZipLinkLinux = exeZipLinkLinux;
        this.exeZipLinkMac = exeZipLinkMac;
    }

    public String getTitle() {
        return title;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getWebsite() {
        return website;
    }

    public String getScreenshotLink() {
        return screenshotLink;
    }

    public String getExeZipLinkWindows() {
        return exeZipLinkWindows;
    }

    public String getExeZipLinkLinux() {
        return exeZipLinkLinux;
    }

    public String getExeZipLinkMac() {
        return exeZipLinkMac;
    }
}
