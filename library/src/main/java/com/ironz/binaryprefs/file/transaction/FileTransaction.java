package com.ironz.binaryprefs.file.transaction;

import java.util.List;

public interface FileTransaction {
    List<TransactionElement> fetch();
    void commit(List<TransactionElement> elements);
}