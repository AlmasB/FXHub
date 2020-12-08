package com.almasb.fxhub;

import com.almasb.fxgl.core.util.Platform;

import java.util.List;

import static com.almasb.fxgl.core.util.Platform.*;

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
    private String exePathWindows;
    private String exePathLinux;
    private String exePathMac;

    // Platform specific stuff
    private String exeLink;
    private String exePath;

    public ProjectInfo(
            Platform platform,
            String title,
            String version,
            String description,
            List<String> authors,
            List<String> tags,
            String website,
            String screenshotLink,
            String exeZipLinkWindows,
            String exeZipLinkLinux,
            String exeZipLinkMac,
            String exePathWindows,
            String exePathLinux,
            String exePathMac) {

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
        this.exePathWindows = exePathWindows;
        this.exePathLinux = exePathLinux;
        this.exePathMac = exePathMac;

        if (platform == WINDOWS) {
            exeLink = exeZipLinkWindows;
            exePath = exePathWindows;
        } else if (platform == MAC) {
            exeLink = exeZipLinkMac;
            exePath = exePathMac;
        } else if (platform == LINUX) {
            exeLink = exeZipLinkLinux;
            exePath = exePathLinux;
        } else {
            // TODO:
            System.out.println("Unknown platform: " + platform);
        }
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

    public String getExeLink() {
        return exeLink;
    }

    public String getExePathWindows() {
        return exePathWindows;
    }

    public String getExePathLinux() {
        return exePathLinux;
    }

    public String getExePathMac() {
        return exePathMac;
    }

    /**
     * @return absolute or relative path to executable on end-user PC (after installation or unzipping took place)
     */
    public String getExePath() {
        return exePath;
    }

    /**
     * Note: returns empty String if platform is not one of WINDOWS|MAC|LINUX.
     *
     * @return link to the executable for a given platform
     */
    public String getExeLink(Platform platform) {
        if (platform == WINDOWS)
            return getExeZipLinkWindows();

        if (platform == MAC)
            return getExeZipLinkMac();

        if (platform == LINUX)
            return getExeZipLinkLinux();

        return "";
    }
}
