#### 1.0.1:


1. Fixed lock/unlock semantics which can causes ANR


#### 1.0.0:

1. Added fetch strategies as public api. Now you can choose what kind
of initialization to perform. This allows you to define what of two
initial fetching variants to use:
    eager - fills in-mem cache immediately, sometimes it take a long time
    lazy - fills in-mem cache on demand, initial fetching is only store meta

2. Added byte array serializer as public api. Now you can store your
binary data right in storage fast and securly as possible.

3. `getAll` method now deprecated. Please, use `keys` mehtod for getting
all preferences keys because `getAll` always creates overhead for in-mem
utilization.

4. Implemented twice or more transaction running check.
Now if you trying to call `apply` or `commit` twice or more for one
instance of `PreferencesEditor` - `TransactionInvalidatedException`
will be thrown.

5. Internal api changes (global locks refactoring, structural changes,
reduced references creation for transactions).

6. Final small bug fixes.

#### 1.0.0-BETA-2:

1. Changed base64 file name algorithm to custom file-safe name encoding
with only lower cased chars and numbers table (a-z-0-9). Sorry guys if you use 
`XorKeyEncryptionImpl` implementation as file name encryption just rename your
preferences directory after updating up to 1.0.0-beta2.

2. Improved performance for cache initialization (especially for `Set<String>`
and `getAll` method).

3. Few cosmetic changes and some javadoc updates for several api.