package com.example.flipr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListClass implements Serializable {

    String ListName;
    Date DateOfCreation;
    Boolean Status;
    List<CardsClass> Cards;

    public String getListName() {
        return ListName;
    }

    public void setListName(String ListName) {
        this.ListName = ListName;
    }

    public Date getDateOfCreation() {
        return DateOfCreation;
    }

    public void setDateOfCreation(Date DateOfCreation) {
        this.DateOfCreation = DateOfCreation;
    }

    public Boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean Status) {
        this.Status = Status;
    }

    public List<CardsClass> getCards() {
        return Cards;
    }

    public void setCards(List<CardsClass> Cards) {
        this.Cards = Cards;
    }
}
