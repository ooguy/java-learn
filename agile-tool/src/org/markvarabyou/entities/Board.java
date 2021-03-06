package org.markvarabyou.entities;

import java.util.Date;

/**
 * Board entity, set of BoardColumns.
 * User: Mark Varabyou
 * Date: 10/25/13
 * Time: 4:03 PM
 */
public class Board {
    private int id;
    private String name;
    private int createdByUserId;
    private Date creationDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(int createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Board(){}

    public Board(String name, int createdByUserId, Date creationDate){
        this.name = name;
        this.createdByUserId = createdByUserId;
        this.creationDate = creationDate;
    }
}
