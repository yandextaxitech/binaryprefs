[![Build Status](https://travis-ci.org/iamironz/binaryprefs.svg?branch=master)](https://travis-ci.org/iamironz/binaryprefs),

[![Methods Count](https://img.shields.io/badge/Methods and size-503 | 60 KB-e91e63.svg)](http://www.methodscount.com/?lib=com.github.iamironz%3Abinaryprefs%3A0.9.0)

## Binary Preferences

Rapidly fast implementation of SharedPreferences which stores each preference
in files separately, performs disk IO via NIO with memory mapped byte buffers
and works IPC (between processes).

## Advantages

* Lightweight. Zero dependency.
* Super fast (faster than most others key/value solutions).
* Small memory footprint while serialize/deserialize data.
* Fully backward compatible with default `SharedPreferences` interface.
* Zero copy in-memory cache.
* Persists only binary data. Not XML or JSON.
* All persisted data are encrypted. Default is AES encryption.
* Store all primitives include `double`, `char`, `byte` and `short`.
* Store complex data objects backward-compatible (see `Persistable` class documentation).
* Fully optimized IPC support (preferences change listeners and memory cache works between processes).
* Define custom directory for saving.
* Pluggable file adapters implementation (remote store, input/output stream). Default is NIO.
* Providing custom cache mechanisms. Default is `ConcurrentHashMap<String, Object>`.
* Working with your own locks for read/write mechanism.
* Using custom task executor (like RxJava, UI thread or Thread Pools).
* Handle various exception events.
* One or all values logcat dump for faster debugging.

## Usage

#### Minimal working configuration

```java
String prefName = "user_preferences";
ByteEncryption byteEncryption = new AesByteEncryptionImpl("16 bytes secret key".getBytes(), "16 bytes initial vector".getBytes());
DirectoryProvider directoryProvider = new AndroidDirectoryProviderImpl(context, prefName);
ExceptionHandler exceptionHandler = ExceptionHandler.IGNORE;
FileAdapter fileAdapter = new NioFileAdapter(directoryProvider, byteEncryption);
CacheProvider cacheProvider = new ConcurrentCacheProviderImpl();
PersistableRegistry persistableRegistry = new PersistableRegistry();
persistableRegistry.register(User.KEY, User.class);
SerializerFactory serializerFactory = new SerializerFactory(persistableRegistry);
LockFactory lockFactory = new SimpleLockFactoryImpl(directoryProvider, exceptionHandler);
EventBridge eventsBridge = new SimpleEventBridgeImpl(cacheProvider);
        
Preferences preferences = new BinaryPreferences(
        fileAdapter,
        exceptionHandler,
        eventsBridge,
        cacheProvider,
        executor,
        serializerFactory,
        lockFactory
);
```

#### Override default directory

You should re-implement your own `DirectoryProvider`, which is the provider of
base storing directory.


#### Dealing with `Persistable `

`Persistable` contract been added for fast and flexible saving and it's
restoring complex objects. It's pretty similar like standard java
`Externalizable` contract but without few methods which don't need for.
For usage you just need to implement this interface with methods on your
data-model.

Note about `deepCopy` method: you should implement full object hierarchy
copying for fast immutable in-memory data fetching.

Sample for explanation: [TestUser.java](https://github.com/iamironz/binaryprefs/blob/master/library/src/test/java/com/ironz/binaryprefs/impl/TestUser.java#L65-L117)

## Roadmap

1. ~~Disk I/O encrypt.~~ completed.
2. ~~IPC~~ completed.
3. ~~Externalizable.~~ completed as `Persistable`.
4. ~~Preferences tooling (key set reading).~~ completed:
`adb shell am broadcast -a com.ironz.binaryprefs.ACTION_DUMP_PREFERENCE --es "pref_name" "your_pref_name" (optional: --es "pref_key" "your_pref_key")`.
5. ~~Custom serializers.~~ completed.
6. ~~Synchronous commits.~~ completed.
7. ~~Store all primitives (like byte, short, char, double).~~ completed.
8. ~~Lock free (avoid locks).~~ completed as `LockFactory`.
9. ~~Exact background tasks for each serialization strategies.~~ completed.
10. File name encrypt.
11. Reduce events (implement events transaction).
12. `Persistable` upgrade/downgrade api.
13. Simplify api (instance creating, exception handles).
14. RxJava support.

## License
```
Copyright 2017 Alexander Efremenkov

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
