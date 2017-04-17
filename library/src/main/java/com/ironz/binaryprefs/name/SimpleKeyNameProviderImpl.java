package com.ironz.binaryprefs.name;

import com.ironz.binaryprefs.util.Constants;

public final class SimpleKeyNameProviderImpl implements KeyNameProvider {

    @Override
    public String convertStringName(String key) {
        return key + Constants.STRING_FILE_POSTFIX;
    }

    @Override
    public String convertStringSetName(String key, int index) {
        return key + "." + index + Constants.STRING_SET_FILE_POSTFIX;
    }

    @Override
    public String convertIntName(String key) {
        return key + Constants.INTEGER_FILE_POSTFIX;
    }

    @Override
    public String convertLongName(String key) {
        return key + Constants.LONG_FILE_POSTFIX;
    }

    @Override
    public String convertBooleanName(String key) {
        return key + Constants.BOOLEAN_FILE_POSTFIX;
    }

    @Override
    public String convertFloatName(String key) {
        return key + Constants.FLOAT_FILE_POSTFIX;
    }

    @Override
    public String getKeyFromFileName(String fileName) {
        return splitFileName(fileName)[0];
    }

    @Override
    public String getFileExtension(String fileName) {
        String[] strings = splitFileName(fileName);
        return strings[strings.length - 1];
    }

    private String[] splitFileName(String fileName) {
        return fileName.split("\\.");
    }
}