package com.github.sckm.itemgroup.benchmark

import android.widget.TextView
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.runner.AndroidJUnit4
import com.github.sckm.itemgroup.ItemGroup
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ItemGroupBenchmark {
    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun benchmarkItemGroupUpdateShuffled() {
        benchmarkRule.measureRepeated {
            val (source, shuffled) = runWithTimingDisabled { generateItems() to generateItems().shuffled() }
            val itemGroup = runWithTimingDisabled {
                ItemGroup().also { it.addAll(source) }
            }
            itemGroup.update(shuffled)
        }
    }

    @Test
    fun benchmarkItemGroupUpdateNoChanged() {
        benchmarkRule.measureRepeated {
            val (items1, items2) = runWithTimingDisabled { generateItems() to generateItems() }
            val itemGroup = runWithTimingDisabled {
                ItemGroup().also { it.addAll(items1) }
            }
            itemGroup.update(items2)
        }
    }

    @Test
    fun benchmarkSectionUpdateShuffled() {
        benchmarkRule.measureRepeated {
            val (source, shuffled) = runWithTimingDisabled { generateItems() to generateItems().shuffled() }
            val section = runWithTimingDisabled {
                Section().also { it.addAll(source) }
            }
            section.update(shuffled)
        }
    }

    @Test
    fun benchmarkSectionUpdateNoChanged() {
        benchmarkRule.measureRepeated {
            val (items1, items2) = runWithTimingDisabled { generateItems() to generateItems() }
            val section = runWithTimingDisabled {
                Section().also { it.addAll(items1) }
            }
            section.update(items2)
        }
    }

    @Test
    fun benchmarkItemGroupGetItem() {
        benchmarkRule.measureRepeated {
            val items = runWithTimingDisabled { generateItems() }
            val itemGroup = runWithTimingDisabled {
                ItemGroup().also { it.addAll(items) }
            }

            (0 until items.size).forEach {
                itemGroup.getItem(it)
            }
        }
    }


    @Test
    fun benchmarkSectionGetItem() {
        benchmarkRule.measureRepeated {
            val items = runWithTimingDisabled { generateItems() }
            val section = runWithTimingDisabled {
                Section().also { it.addAll(items) }
            }

            (0 until items.size).forEach {
                section.getItem(it)
            }
        }
    }


    private fun generateItems(): List<Item<*>> {
        return (0L..100L).map { id -> BenchmarkItem(Data(id, id.toString())) }
    }

    private data class Data(
        val id: Long,
        val text: String
    )

    private class BenchmarkItem(private val data: Data) : Item<ViewHolder>(data.id) {
        override fun getLayout(): Int = R.layout.layout_benchmark

        override fun bind(viewHolder: ViewHolder, position: Int) {
            val tv = viewHolder.root.findViewById<TextView>(R.id.text_view)
            tv.text = id.toString()
        }

        override fun equals(other: Any?): Boolean {
            if (other !is BenchmarkItem) return false
            return data == other.data
        }

        override fun hashCode(): Int {
            return data.hashCode()
        }
    }
}