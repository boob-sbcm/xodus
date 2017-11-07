/**
 * Copyright 2010 - 2017 JetBrains s.r.o.
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
package jetbrains.exodus.benchmark.query

import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.PersistentEntityStoreImpl
import jetbrains.exodus.entitystore.PersistentEntityStores
import jetbrains.exodus.entitystore.PersistentStoreTransaction
import jetbrains.exodus.query.*
import jetbrains.exodus.util.Random
import org.junit.rules.TemporaryFolder

open class InMemorySortBenchmarkBase {
    lateinit var store: PersistentEntityStoreImpl
    lateinit var temporaryFolder: TemporaryFolder

    private val comparator = compareBy<Entity> { it.getProperty("int") }

    fun setup() {
        temporaryFolder = TemporaryFolder()
        temporaryFolder.create()
        store = PersistentEntityStores.newInstance(temporaryFolder.newFolder("data").absolutePath)

        val rnd = Random()
        store.computeInTransaction {
            val txn = it as PersistentStoreTransaction
            repeat(50000, {
                val value = Math.abs(rnd.nextInt())
                txn.newEntity("Issue").setProperty("int", value)
            })
        }
    }

    fun close() {
        store.close()
        temporaryFolder.delete()
    }

    open fun testMergeSort(): Long {
        return testSort({ InMemoryMergeSortIterable(it, comparator) })
    }

    open fun testMergeSortWithArrayList(): Long {
        return testSort({ InMemoryMergeSortIterableWithArrayList(it, comparator) })
    }

    open fun testTimSort(): Long {
        return testSort({ InMemoryTimSortIterable(it, comparator) })
    }

    open fun testQuickSort(): Long {
        return testSort({ InMemoryQuickSortIterable(it, comparator) })
    }

    open fun testHeapSort(): Long {
        return testSort({ InMemoryHeapSortIterable(it, comparator) })
    }

    open fun testKeapSort(): Long {
        return testSort({ InMemoryKeapSortIterable(it, comparator) })
    }

    open fun testBoundedSort(): Long {
        return testSort({ InMemoryBoundedHeapSortIterable(100, it, comparator) })
    }

    private fun testSort(sortFun: (it: Iterable<Entity>) -> SortEngine.InMemorySortIterable): Long {
        return store.computeInTransaction {
            val sorted = sortFun(it.getAll("Issue"))
            // sum of ids of least 100 entities
            sorted.take(100).map { it.id.localId }.sum()
        }
    }
}
