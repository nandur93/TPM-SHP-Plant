package com.ndu.tpmshpplant.sqlite.database.model;

/*https://www.androidhive.info/2011/11/android-sqlite-database-tutorial/*/
public class InfoTpm {
    public static final String TABLE_NAME = "InfoTpm";

    public static final String COLUMN_CONTENT_ID = "txtContentId";
    public static final String COLUMN_ICON_LINK = "txtIconLink";
    public static final String COLUMN_TITLE = "txtTitle";
    public static final String COLUMN_DESCRIPTION = "txtDescription";
    public static final String COLUMN_ARTICLE_LINK = "txtArticleLink";
    public static final String COLUMN_AUTHOR_NAME = "txtAuthor";
    public static final String COLUMN_READ_STATUS = "intReadStatus";
    public static final String COLUMN_PUBLISH_DATE = "dtmPublishDate";

    private String txtContentId;
    private String txtIconLink;
    private String txtTitle;
    private String txtDescription;
    private String txtArticleLink;
    private String txtAuthor;
    private String dtmPublishDate;
    private int intReadStatus;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                    + COLUMN_CONTENT_ID + " TEXT PRIMARY KEY,"
                    + COLUMN_ICON_LINK + " TEXT,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_DESCRIPTION + " TEXT,"
                    + COLUMN_ARTICLE_LINK + " TEXT,"
                    + COLUMN_AUTHOR_NAME + " TEXT,"
                    + COLUMN_READ_STATUS + " INT,"
                    + COLUMN_PUBLISH_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public InfoTpm() {
    }

    public InfoTpm(
            String txt_content_id,
            String txt_icon_link,
            String txt_title,
            String txt_description,
            String txt_article_link,
            String txt_author,
            int int_read_status,
            String dtm_publish_date) {
        this.txtContentId = txt_content_id;
        this.txtIconLink = txt_icon_link;
        this.txtTitle = txt_title;
        this.txtDescription = txt_description;
        this.txtArticleLink = txt_article_link;
        this.txtAuthor = txt_author;
        this.intReadStatus = int_read_status;
        this.dtmPublishDate = dtm_publish_date;
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColumnContentId() {
        return COLUMN_CONTENT_ID;
    }

    public static String getColumnIconLink() {
        return COLUMN_ICON_LINK;
    }

    public static String getColumnTitle() {
        return COLUMN_TITLE;
    }

    public static String getColumnDescription() {
        return COLUMN_DESCRIPTION;
    }

    public static String getColumnArticleLink() {
        return COLUMN_ARTICLE_LINK;
    }

    public static String getColumnAuthorName() {
        return COLUMN_AUTHOR_NAME;
    }

    public static String getColumnPublishDate() {
        return COLUMN_PUBLISH_DATE;
    }

    public static String getColumnReadStatus() {
        return COLUMN_READ_STATUS;
    }

    public String getTxtContentId() {
        return txtContentId;
    }

    public void setTxtContentId(String txtContentId) {
        this.txtContentId = txtContentId;
    }

    public String getTxtIconLink() {
        return txtIconLink;
    }

    public void setTxtIconLink(String txtIconLink) {
        this.txtIconLink = txtIconLink;
    }

    public String getTxtTitle() {
        return txtTitle;
    }

    public void setTxtTitle(String txtTitle) {
        this.txtTitle = txtTitle;
    }

    public String getTxtDescription() {
        return txtDescription;
    }

    public void setTxtDescription(String txtDescription) {
        this.txtDescription = txtDescription;
    }

    public String getTxtArticleLink() {
        return txtArticleLink;
    }

    public void setTxtArticleLink(String txtArticleLink) {
        this.txtArticleLink = txtArticleLink;
    }

    public String getTxtAuthor() {
        return txtAuthor;
    }

    public void setTxtAuthor(String txtAuthor) {
        this.txtAuthor = txtAuthor;
    }

    public String getDtmPublishDate() {
        return dtmPublishDate;
    }

    public void setDtmPublishDate(String dtmPublishDate) {
        this.dtmPublishDate = dtmPublishDate;
    }

    public int getIntReadStatus() {
        return intReadStatus;
    }

    public void setIntReadStatus(int intReadStatus) {
        this.intReadStatus = intReadStatus;
    }

    public static String getCreateTable() {
        return CREATE_TABLE;
    }
}
