/**
 * Copyright 2010 - 2018 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.exodus.io;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;

// TODO: document
public interface DataWriter extends Closeable {

    boolean isOpen();

    Block write(byte[] b, int off, int len);

    void sync();

    void syncDirectory();

    @Override
    void close();

    void clear();

    Block openOrCreateBlock(long address, long length);

    void removeBlock(long blockAddress, @NotNull RemoveBlockType rbt);

    void truncateBlock(long blockAddress, long length);

    /**
     * Try to lock writer during specified time.
     *
     * @param timeout - if writer is already locked try to lock it during specified timeout.
     * @return true if locked successfully, false otherwise
     */
    boolean lock(long timeout);

    /**
     * Releases writer
     *
     * @return true if released successfully, false otherwise
     */
    boolean release();

    /**
     * For debug purposes, returns detailed information about current lock owner. Can be used if lock() failed.
     *
     * @return Human-readable information about lock owner.
     */
    String lockInfo();
}
