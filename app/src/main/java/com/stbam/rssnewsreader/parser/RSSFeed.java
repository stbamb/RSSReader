package com.stbam.rssnewsreader.parser;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/**
 * Created by Esteban on 07-Oct-14.
 */
public class RSSFeed implements Serializable {

    private static final long serialVersionUID = 1L;
    private int _itemCount = 0;
    private List _itemList;

    RSSFeed() {
        _itemList = new Vector(0);
    }

    void addItem(RSSItem item) {
        _itemList.add(item);
        _itemCount++;
    }

    public void emptyFeed()
    {
        _itemList.clear();
        _itemCount = 0;
    }

    public RSSItem getItem(int location) {
        return (RSSItem) _itemList.get(location);
    }

    public int getItemCount() {
        return _itemCount;
    }
}
