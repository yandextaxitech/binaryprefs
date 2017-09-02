#### 1.0.0-BETA-2:

1. Changed base64 file name algorithm to custom file-safe name encoding
with only lower cased chars and numbers table (a-z-0-9). Sorry guys if you use 
`XorKeyEncryptionImpl` implementation as file name encryption just rename your
preferences directory after updating up to 1.0.0-beta2.

2. Improved performance for cache initialization (especially for `Set<String>`
and `getAll` method).

3. Few cosmetic changes and some javadoc updates for several api.