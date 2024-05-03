package com.chtima.wallettracker.models;

import androidx.room.*;

import java.util.List;

public class CategoryWithTransactions {
    @Embedded
    public Category category;

    @Relation(parentColumn = "id", entityColumn = "categoryId")
    public List<Transaction> transactions;
}
