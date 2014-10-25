package com.stbam.rssnewsreader.parser;

import java.io.Serializable;

/**
 * Created by Esteban on 07-Oct-14.
 */
public class RSSItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private String _title = null;
    private String _description = null;
    private String _date = null;
    private String _image = null;
    private String _link = null;
    private String _source_page;
    private boolean _seen = false;

    public void setTitle(String title) {
        _title = title;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public void setDate(String pubDate) {
        _date = pubDate;
    }

    public void setImage(String image) {
        _image = image;
    }

    public void setLink(String link) {
        _link = link;
    }

    public void setSeen() {
        _seen = true;
    }

    public String getTitle() {
        return _title;
    }

    public String getDescription() {
        return _description;
    }

    public String getDate() {
        return _date;
    }

    public String getImage() {
        return _image;
    }

    public String getLink() {
        return _link;
    }

    public boolean isSeen() {
        return _seen;
    }

    public String get_source_page() {
        return _source_page;
    }

    public void set_source_page(String _source_page) {
        this._source_page = _source_page;
    }
}
