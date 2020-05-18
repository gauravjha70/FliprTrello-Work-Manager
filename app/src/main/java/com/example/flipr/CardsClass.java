package com.example.flipr;

import java.util.Date;

public class CardsClass {

    String cardName;
    String Comment;
    Date DueDate;
    Date CreationDate;
    Boolean DueDateSet;

    public CardsClass()
    {
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String Comment) {
        this.Comment = Comment;
    }

    public Date getDueDate() {
        return DueDate;
    }

    public void setDueDate(Date DueDate) {
        this.DueDate = DueDate;
    }

    public Date getCreationDate() {
        return CreationDate;
    }

    public void setCreationDate(Date CreationDate) {
        this.CreationDate = CreationDate;
    }

    public Boolean getDueDateSet() {
        return DueDateSet;
    }

    public void setDueDateSet(Boolean DueDateSet) {
        this.DueDateSet = DueDateSet;
    }
}
