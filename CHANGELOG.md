#### 1.0.0-BETA-2:

1. Changed base64 file name algorithm to custom file-safe name encoding
with only lower cased and number chars table (a-z-0-9). Sorry guys who
are uses XorKeyEncryptionImpl as file name encryption just rename preferences
directory in your project after update to 1.0.0-beta2.

2. Improved performance for cache initialization (especially for `Set<String>`
and `getAll` method).

3. Few cosmetic changes and some javadoc updating for several api.