[![Build Status](https://travis-ci.org/iamironz/binaryprefs.svg?branch=master)](https://travis-ci.org/iamironz/binaryprefs)

## Binary Preferences

Implementation of SharedPreferences which stores each preference in files separately, performs disk IO via NIO with memory mapped file and works IPC (between processes).

## Advantages

* Saving in separate files (just byte array not XML).
* All saved data encryption.
* Providing custom directory.
* Custom file adapters implementation (remote persistence, custom disk saving protocol).
* Providing custom cache mechanisms.
* Store all primitives include `byte`, `short`, `char` and `double`.
* IPC support (between processes and apk if byte encryption are similar).
* Store complex objects backward compatible (see `Persistable` class documentation).
* Working with your own lock for read/write.
* Using custom task executor (like RxJava, UI thread or Thread Pools).
* Handle exception events.
* Backward compatible with `SharedPreferences` interface.

## Usage

#### Minimal working configuration

```java
String prefName = "pref_1";
ByteEncryption byteEncryption = new AesByteEncryptionImpl("1111111111111111".getBytes(), "0000000000000000".getBytes());
DirectoryProvider directoryProvider = new AndroidDirectoryProviderImpl(context, prefName);
FileAdapter fileAdapter = new NioFileAdapter(directoryProvider, byteEncryption);
CacheProvider cacheProvider = new ConcurrentCacheProviderImpl();
EventBridge eventsBridge = new SimpleEventBridgeImpl(cacheProvider);
PersistableRegistry persistableRegistry = new PersistableRegistry();
persistableRegistry.register(TestUser.KEY, TestUser.class);
SerializerFactory serializerFactory = new SerializerFactory(persistableRegistry);
LockFactory lockFactory = new SimpleLockFactoryImpl(prefName);
TaskExecutor executor = new ScheduledBackgroundTaskExecutor();
ExceptionHandler exceptionHandler = new ExceptionHandler() {
        @Override
        public void handle(String key, Exception e) {
            //do some metric report call
        }
};
        
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

`Preferences.java` this is child of `SharedPreferences.java` from android
standard library.
That means compatibility with parent interface. Also that means you can
use this preferences implementation instead of native preferences
as before because behaviour and contract is fully respected.

## Roadmap

1. ~~Disk I/O encrypt~~ completed.
2. ~~IPC~~ completed.
3. ~~Externalizable~~ completed as `Persistable`.
4. ~~Preferences tooling (key set reading)~~ completed:
`adb shell am broadcast -a com.ironz.binaryprefs.ACTION_DUMP_PREFERENCE --es "pref_name" "your_pref_name" (optional: --es "pref_key" "your_pref_key")`.
5. ~~Custom serializers~~ completed.
6. ~~Synchronous commits~~ completed.
7. ~~Store all primitives (like byte, short, char, double)~~ completed.
8. ~~Lock free (avoid locks)~~ completed as `LockFactory`.
9. Exact background tasks for each serialization strategies.
10. RxJava support. 

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
