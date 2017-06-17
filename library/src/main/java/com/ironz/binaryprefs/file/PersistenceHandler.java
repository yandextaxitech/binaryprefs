package com.ironz.binaryprefs.file;

/**
 * Interface which describes success persistence event listener
 */
public interface PersistenceHandler {
    /**
     * Calls after saving has been successfully completed
     *
     * @param key target file for which persistence has been completed
     */
    void onSuccess(String key);

    /**
     * Implementation which do nothing
     */
    PersistenceHandler NO_OP = new PersistenceHandler() {
        @Override
        public void onSuccess(String key) {

        }
    };
}
