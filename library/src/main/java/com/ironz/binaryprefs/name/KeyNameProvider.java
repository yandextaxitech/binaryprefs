package com.ironz.binaryprefs.name;

public interface KeyNameProvider {
    String convertStringName(String key);

    String convertStringSetName(String key, int index);

    String convertIntName(String key);

    String convertLongName(String key);

    String convertBooleanName(String key);

    String convertFloatName(String key);

    String getKeyFromFileName(String fileName);

    String getFileExtension(String fileName);
}
