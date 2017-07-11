[![Build Status](https://travis-ci.org/iamironz/binaryprefs.svg?branch=master)](https://travis-ci.org/iamironz/binaryprefs)
[![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=14)
<a href="http://www.methodscount.com/?lib=com.github.iamironz%3Abinaryprefs%3A0.9.8"><img src="https://img.shields.io/badge/Methods count-556-e91e63.svg"/></a>
<a href="http://www.methodscount.com/?lib=com.github.iamironz%3Abinaryprefs%3A0.9.8"><img src="https://img.shields.io/badge/Size-67 KB-e91e63.svg"/></a>
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Binary%20Preferences-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5931)

## Binary Preferences

Rapidly fast implementation of SharedPreferences which stores each preference
in files separately, performs disk IO via NIO with memory mapped byte buffers
and works IPC (between processes).

## Advantages

* Lightweight. Zero dependency.
* Super fast (faster than most others key/value solutions).
* Small memory footprint while serialize/deserialize data.
* Zero copy in-memory cache.
* Persists only binary data. Not XML or JSON.
* Out of box data encryption support.
* Fully backward compatible with default `SharedPreferences` interface.
* Store all primitives include `double`, `char`, `byte` and `short`.
* Store complex data objects backward-compatible (see `Persistable` class documentation).
* Fully optimized IPC support (preferences change listeners and in-memory cache works between processes).
* Define custom directory for saving.
* Handle various exception events.

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


#### Dealing with `Persistable`

`Persistable` contract been added for fast and flexible saving and it's
restoring complex objects. It's pretty similar like standard java
`Externalizable` contract but without few methods which don't need for.
For usage you just need to implement this interface with methods on your
data-model.

Note about `deepClone` method: you should implement full object hierarchy
clone for fast immutable in-memory data fetching.

Sample for explanation: [TestUser.java](https://github.com/iamironz/binaryprefs/blob/master/library/src/test/java/com/ironz/binaryprefs/impl/TestUser.java#L68-L121)

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
10. ~~Reduce events (implement events transaction).~~ completed.
11. Simplify api (instance creating, exception handles).
12. Finalize serialization and persistence contract.
13. File name encrypt.
14. `Persistable` upgrade/downgrade api.
15. RxJava support.

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
