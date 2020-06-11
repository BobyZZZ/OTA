package com.ist.httplib.bean.customer;

public class AppVersion {
    public String fullMd5;
    public String fullSize;
    public String fullPath;
    public int isForce;
    public String buildDate;

    public String getFullMd5() {
        return fullMd5;
    }

    public void setFullMd5(String fullMd5) {
        this.fullMd5 = fullMd5;
    }

    public String getFullSize() {
        return fullSize;
    }

    public void setFullSize(String fullSize) {
        this.fullSize = fullSize;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public int getIsForce() {
        return isForce;
    }

    public void setIsForce(int isForce) {
        this.isForce = isForce;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    @Override
    public String toString() {
        return "AppVersion{" +
                "fullMd5='" + fullMd5 + '\'' +
                ", fullSize='" + fullSize + '\'' +
                ", fullPath='" + fullPath + '\'' +
                ", isForce=" + isForce +
                ", buildDate='" + buildDate + '\'' +
                '}';
    }
}
