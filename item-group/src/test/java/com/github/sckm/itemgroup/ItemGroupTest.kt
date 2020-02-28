/**
 * Copyright (c) 2019 lisawray
 * Released under the MIT license
 * https://opensource.org/licenses/mit-license.php
 */

package com.github.sckm.itemgroup

import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import java.util.concurrent.atomic.AtomicLong

private val ID_COUNTER = AtomicLong(0)

open class DummyItem(id: Long = ID_COUNTER.decrementAndGet()) : Item<ViewHolder>(id) {
    override fun getLayout(): Int = 0

    override fun bind(viewHolder: ViewHolder, position: Int) = Unit
}

class AlwaysUpdatingItem(id: Int) : DummyItem(id.toLong()) {
    override fun equals(other: Any?): Boolean {
        return false
    }
}

class ContentUpdatingItem(
    id: Int,
    val content: String,
    val payload: Any? = null
) : DummyItem(id.toLong()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as ContentUpdatingItem? ?: return false

        return content == that.content
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }

    override fun getChangePayload(newItem: Item<*>?): Any? {
        return payload
    }
}

@RunWith(JUnit4::class)
class ItemGroupTest {

    @Mock
    lateinit var groupAdapter: GroupAdapter<ViewHolder>
    @get:Rule
    val mockitoRule = MockitoJUnit.rule()!!

    @Test
    fun getItemCountWhenEmpty() {
        val itemGroup = ItemGroup()

        assertEquals(0, itemGroup.itemCount)
    }

    @Test
    fun getItemCountWhenNotEmpty() {
        val itemGroup = ItemGroup()
        itemGroup.add(DummyItem())
        itemGroup.add(DummyItem())

        assertEquals(2, itemGroup.itemCount)
    }

    @Test
    fun getItemCountWhenAddedInitialItems() {
        val initItems = listOf(DummyItem(), DummyItem())
        val itemGroup = ItemGroup(initItems)

        assertEquals(2, itemGroup.itemCount)
    }

    @Test
    fun getPosition() {
        val section = ItemGroup()
        val item = DummyItem()
        section.add(item)
        assertEquals(0, section.getPosition(item))
    }

    @Test
    fun getPositionWhenAddedMultipleItems() {
        val section = ItemGroup()
        val item1 = DummyItem()
        val item2 = DummyItem()
        section.add(item1)
        section.add(item2)

        assertEquals(0, section.getPosition(item1))
        assertEquals(1, section.getPosition(item2))
    }

    @Test
    fun getPositionReturnsNegativeIfItemNotPresent() {
        val section = ItemGroup()
        val item = DummyItem()
        assertEquals(-1, section.getPosition(item))
    }

    @Test
    fun getItem() {
        val section = ItemGroup()
        val item1 = DummyItem()
        val item2 = DummyItem()
        section.add(item1)
        section.add(item2)
        assertEquals(item2, section.getItem(1))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun getItemAtNonZeroPositionWhenEmptyThrowIndexOutOfBoundsException() {
        val section = ItemGroup()
        section.getItem(1)
    }

    @Test
    fun addWhenEmptyNotifiesAdapterAtIndexZero() {
        val section = ItemGroup()
        section.registerGroupDataObserver(groupAdapter)

        section.add(DummyItem())
        verify(groupAdapter).onItemInserted(section, 0)
    }

    @Test
    fun addTwiceWhenEmptyNotifiesAdapterAtIndexZeroAndOne() {
        val section = ItemGroup()
        section.registerGroupDataObserver(groupAdapter)

        section.add(DummyItem())
        section.add(DummyItem())
        val inOrder = inOrder(groupAdapter)

        inOrder.verify(groupAdapter).onItemInserted(section, 0)
        inOrder.verify(groupAdapter).onItemInserted(section, 1)
    }

    @Test
    fun addAtPositionWhenEmptyNotifiesAdapterAtIndexZero() {
        val section = ItemGroup()
        section.registerGroupDataObserver(groupAdapter)

        section.add(0, DummyItem())
        verify(groupAdapter).onItemInserted(section, 0)
    }

    @Test
    fun addAtPositionWhenNotEmptyNotifiesAdapterAtIndexZero() {
        val section = ItemGroup()
        section.add(0, DummyItem())
        section.registerGroupDataObserver(groupAdapter)

        section.add(0, DummyItem())
        verify(groupAdapter).onItemInserted(section, 0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun addAtNonZeroPositionWhenEmptyThrowIndexOutOfBoundsException() {
        val section = ItemGroup()
        section.registerGroupDataObserver(groupAdapter)
        section.add(1, DummyItem())
    }

    @Test
    fun addAllWhenEmptyNotifiesAdapterAtIndexZero() {
        val section = ItemGroup()
        section.registerGroupDataObserver(groupAdapter)

        section.addAll(0, listOf(DummyItem(), DummyItem()))
        verify(groupAdapter).onItemRangeInserted(section, 0, 2)
    }

    @Test
    fun addAllWhenNotEmptyNotifiesAdapterAtCorrectIndex() {
        val section = ItemGroup()
        section.addAll(listOf(DummyItem(), DummyItem()))
        section.registerGroupDataObserver(groupAdapter)

        section.addAll(listOf(DummyItem(), DummyItem()))
        verify(groupAdapter).onItemRangeInserted(section, 2, 2)
    }

    @Test
    fun addAllAtPositionWhenEmptyNotifiesAdapterAtIndexZero() {
        val section = ItemGroup()
        section.registerGroupDataObserver(groupAdapter)

        section.addAll(0, listOf(DummyItem(), DummyItem()))
        verify(groupAdapter).onItemRangeInserted(section, 0, 2)
    }

    @Test
    fun addAllAtPositionWhenNonEmptyNotifiesAdapterAtCorrectIndex() {
        val section = ItemGroup(listOf(DummyItem(), DummyItem()))
        section.registerGroupDataObserver(groupAdapter)

        section.addAll(2, listOf(DummyItem(), DummyItem(), DummyItem()))
        verify(groupAdapter).onItemRangeInserted(section, 2, 3)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun addAllAtNonZeroPositionWhenEmptyThrowIndexOutOfBoundsException() {
        val section = ItemGroup()
        section.registerGroupDataObserver(groupAdapter)
        section.addAll(1, listOf(DummyItem(), DummyItem()))
    }

    @Test
    fun addAllWorksWithSets() {
        val testSection = ItemGroup()

        val itemSet = mutableSetOf<Item<ViewHolder>>()
        itemSet.add(DummyItem())
        itemSet.add(DummyItem())

        testSection.addAll(itemSet)
        assertEquals(2, testSection.itemCount)
    }

    @Test
    fun removeWhenContainsNotifiesAdapterAtIndexZero() {
        val section = ItemGroup()
        val item = DummyItem()
        section.add(item)
        section.registerGroupDataObserver(groupAdapter)

        section.remove(item)
        verify(groupAdapter).onItemRemoved(section, 0)
        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun removeMiddleItemWhenContainsNotifiesAdapterAtCorrectIndex() {
        val section = ItemGroup()
        val item = DummyItem()
        section.addAll(
            listOf(
                DummyItem(),
                item,
                DummyItem()
            )
        )
        section.registerGroupDataObserver(groupAdapter)

        section.remove(item)
        verify(groupAdapter).onItemRemoved(section, 1)
        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun removeWhenEmptyNotNotifiesAdapter() {
        val section = ItemGroup()
        section.registerGroupDataObserver(groupAdapter)

        section.remove(DummyItem())
        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun removeWhenNotContainsNotNotifiesAdapter() {
        val section = ItemGroup()
        section.add(DummyItem())
        section.registerGroupDataObserver(groupAdapter)

        section.remove(DummyItem())
        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun removeAllWhenEmptyNotNotifiesAdapter() {
        val section = ItemGroup()
        section.registerGroupDataObserver(groupAdapter)

        section.removeAll()
        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun replaceWithSameItemButContentsIsDifferentReplaceItemInstance() {
        val section = ItemGroup()
        val oldItem = ContentUpdatingItem(0, "before item")
        section.add(oldItem)
        section.registerGroupDataObserver(groupAdapter)

        val newItem = ContentUpdatingItem(0, "after item")
        section.replace(0, newItem)

        assertThat(section.itemCount).isEqualTo(1)
        assertThat(section.getItem(0) === oldItem).isFalse()
        assertThat(section.getItem(0) === newItem).isTrue()
    }

    @Test
    fun replaceWithSameItemButContentsIsDifferentNotifiesAdapterChanged() {
        val section = ItemGroup()
        section.add(ContentUpdatingItem(0, "before item"))
        section.registerGroupDataObserver(groupAdapter)

        section.replace(0, ContentUpdatingItem(0, "after item"))
        verify(groupAdapter).onItemChanged(section, 0, null)
        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun replaceWithSameItemButContentsIsDifferentNotifiesAdapterChangedWithPayload() {
        val section = ItemGroup()
        section.add(ContentUpdatingItem(0, "before item", "old"))
        section.registerGroupDataObserver(groupAdapter)

        section.replace(0, ContentUpdatingItem(0, "after item", "new"))
        verify(groupAdapter).onItemChanged(section, 0, "old")
        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun replaceWithSameContentsReplaceItemInstance() {
        val section = ItemGroup()
        val oldItem = ContentUpdatingItem(0, "item")
        section.add(oldItem)
        section.registerGroupDataObserver(groupAdapter)

        val newItem = ContentUpdatingItem(0, "item")
        section.replace(0, newItem)

        assertThat(section.itemCount).isEqualTo(1)
        assertThat(section.getItem(0) === oldItem).isFalse()
        assertThat(section.getItem(0) === newItem).isTrue()
    }

    @Test
    fun replaceWithSameContentsNotNotifiesAdapter() {
        val section = ItemGroup()
        section.add(ContentUpdatingItem(0, "item"))
        section.registerGroupDataObserver(groupAdapter)

        section.replace(0, ContentUpdatingItem(0, "item"))
        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun replaceWithDifferentItemNotifiesAdapterRemoveAndInsert() {
        val section = ItemGroup()
        section.add(ContentUpdatingItem(0, "item"))
        section.registerGroupDataObserver(groupAdapter)

        section.replace(0, ContentUpdatingItem(1, "item"))

        val inOrder = inOrder(groupAdapter)
        inOrder.verify(groupAdapter).onItemRemoved(section, 0)
        inOrder.verify(groupAdapter).onItemInserted(section, 0)
        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun replaceItemsChangesRange() {
        val replaceItems = listOf<Item<*>>(
            AlwaysUpdatingItem(1),
            AlwaysUpdatingItem(2),
            AlwaysUpdatingItem(3)
        )
        val group = ItemGroup()
        group.add(AlwaysUpdatingItem(0))
        group.addAll(replaceItems)
        group.registerGroupDataObserver(groupAdapter)

        group.replaceItems(1, 1 + replaceItems.size, replaceItems)

        verify(groupAdapter).onItemRangeChanged(group, 1, 3, null)
        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun replaceItemsWithTheSameItemAndSameContentsReplaceInstances() {
        val oldChildren = listOf<Item<*>>(
            ContentUpdatingItem(1, "contents"),
            ContentUpdatingItem(2, "contents")
        )
        val group = ItemGroup()
        group.update(oldChildren)

        val newChildren = listOf<Item<*>>(
            ContentUpdatingItem(1, "contents"),
            ContentUpdatingItem(2, "contents")
        )
        group.replaceItems(0, newChildren.size, newChildren)

        assertThat(group.itemCount).isEqualTo(2)
        assertThat(group.getItem(0) === newChildren[0]).isTrue()
        assertThat(group.getItem(1) === newChildren[1]).isTrue()
    }

    @Test
    fun replaceItemsWithTheSameItemAndSameContentsDoesNotNotifyChange() {
        val children = listOf<Item<*>>(
            ContentUpdatingItem(1, "contents"),
            ContentUpdatingItem(2, "contents")
        )

        val group = ItemGroup()
        group.update(children)
        group.registerGroupDataObserver(groupAdapter)

        group.replaceItems(0, children.size, children)

        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun replaceItemsWithTheSameItemButDifferentContentsNotifiesChange() {
        val oldItems = listOf(
            ContentUpdatingItem(1, "contents"),
            ContentUpdatingItem(2, "contents"),
            ContentUpdatingItem(3, "contents")
        )

        val group = ItemGroup()
        group.update(oldItems)
        group.registerGroupDataObserver(groupAdapter)

        val newItems = listOf(
            ContentUpdatingItem(2, "new contents"),
            ContentUpdatingItem(3, "new contents")
        )
        group.replaceItems(1, 1 + newItems.size, newItems)

        verify(groupAdapter).onItemRangeChanged(group, 1, 2, null)
    }

    @Test
    fun replaceItemsWithTheSameItemButDifferentContentsNotifiesChangeWithPayload() {
        val oldItems = listOf(
            ContentUpdatingItem(1, "contents1", "old1"),
            ContentUpdatingItem(2, "contents2", "old2"),
            ContentUpdatingItem(3, "contents3", "old3")
        )

        val group = ItemGroup()
        group.update(oldItems)
        group.registerGroupDataObserver(groupAdapter)

        val newItems = listOf(
            ContentUpdatingItem(2, "new contents2", "new2"),
            ContentUpdatingItem(3, "new contents3", "new3")
        )
        group.replaceItems(1, 1 + newItems.size, newItems)

        verify(groupAdapter).onItemRangeChanged(group, 1, 1, "old2")
        verify(groupAdapter).onItemRangeChanged(group, 2, 1, "old3")
        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun replaceItemsWithADifferentItemReplaceItemInstances() {
        val oldItems = listOf(
            ContentUpdatingItem(1, "contents"),
            ContentUpdatingItem(2, "contents"),
            ContentUpdatingItem(3, "contents")
        )

        val group = ItemGroup()
        group.update(oldItems)

        val newItems = listOf(
            ContentUpdatingItem(4, "contents"),
            ContentUpdatingItem(5, "contents")
        )
        group.replaceItems(1, 1 + newItems.size, newItems)

        assertThat(group.itemCount).isEqualTo(3)
        assertThat(group.getItem(1) === newItems[0]).isTrue()
        assertThat(group.getItem(2) === newItems[1]).isTrue()
    }

    @Test
    fun replaceItemsWithADifferentItemNotifiesRemoveAndAdd() {
        val oldItems = listOf(
            ContentUpdatingItem(1, "contents"),
            ContentUpdatingItem(2, "contents"),
            ContentUpdatingItem(3, "contents")
        )

        val group = ItemGroup()
        group.update(oldItems)
        group.registerGroupDataObserver(groupAdapter)

        val newItems = listOf(
            ContentUpdatingItem(4, "contents"),
            ContentUpdatingItem(5, "contents")
        )
        group.replaceItems(1, 1 + newItems.size, newItems)

        verify(groupAdapter).onItemRangeRemoved(group, 1, 2)
        verify(groupAdapter).onItemRangeInserted(group, 1, 2)
    }

    @Test
    fun replaceItemsWithLargeList() {
        val oldItems = listOf(
            ContentUpdatingItem(1, "contents"),
            ContentUpdatingItem(2, "contents"),
            ContentUpdatingItem(3, "contents")
        )

        val group = ItemGroup()
        group.update(oldItems)
        group.registerGroupDataObserver(groupAdapter)

        val newItems = listOf(
            ContentUpdatingItem(4, "contents"),
            ContentUpdatingItem(5, "contents"),
            ContentUpdatingItem(6, "contents"),
            ContentUpdatingItem(7, "contents"),
            ContentUpdatingItem(8, "contents")
        )
        group.replaceItems(1, 3, newItems)

        verify(groupAdapter).onItemRangeRemoved(group, 1, 2)
        verify(groupAdapter).onItemRangeInserted(group, 1, 5)
        assertThat(group.itemCount).isEqualTo(6)
    }

    @Test
    fun replaceItemsWithSmallList() {
        val oldItems = listOf(
            ContentUpdatingItem(1, "contents"),
            ContentUpdatingItem(2, "contents"),
            ContentUpdatingItem(3, "contents")
        )

        val group = ItemGroup()
        group.update(oldItems)
        group.registerGroupDataObserver(groupAdapter)

        val newItems = listOf(
            ContentUpdatingItem(4, "contents")
        )
        group.replaceItems(1, 3, newItems)

        verify(groupAdapter).onItemRangeRemoved(group, 1, 2)
        verify(groupAdapter).onItemRangeInserted(group, 1, 1)
        assertThat(group.itemCount).isEqualTo(2)
    }

    @Test
    fun replaceItemsWithSameItems() {
        val item1 = DummyItem(1)
        val item2 = DummyItem(2)
        val item3 = DummyItem(3)

        val oldItems = listOf(item1, item2, item3)

        val group = ItemGroup()
        group.update(oldItems)
        group.registerGroupDataObserver(groupAdapter)

        val newItems = listOf(item2, item3)
        group.replaceItems(1, 3, newItems)

        verifyNoMoreInteractions(groupAdapter)
        assertThat(group.itemCount).isEqualTo(3)
    }

    @Test
    fun updateGroupChangesRange() {
        val children = mutableListOf<Item<*>>()
        children.add(AlwaysUpdatingItem(1))
        children.add(AlwaysUpdatingItem(2))

        val group = ItemGroup()
        group.registerGroupDataObserver(groupAdapter)

        group.update(children)
        verify(groupAdapter).onItemRangeInserted(group, 0, 2)
        verifyNoMoreInteractions(groupAdapter)

        group.update(children)
        verify(groupAdapter).onItemRangeChanged(group, 0, 2, null)
        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun updateGroupChangesRangeWithPayload() {
        val oldChildren = mutableListOf<Item<*>>(
            ContentUpdatingItem(1, "old content1", "old1"),
            ContentUpdatingItem(2, "old content2", "old2")
        )

        val newChildren = mutableListOf<Item<*>>(
            ContentUpdatingItem(1, "new content1", "new1"),
            ContentUpdatingItem(2, "new content2", "new2")
        )

        val group = ItemGroup()
        group.registerGroupDataObserver(groupAdapter)

        group.update(oldChildren)
        verify(groupAdapter).onItemRangeInserted(group, 0, 2)
        verifyNoMoreInteractions(groupAdapter)

        group.update(newChildren)
        verify(groupAdapter).onItemRangeChanged(group, 0, 1, "old1")
        verify(groupAdapter).onItemRangeChanged(group, 1, 1, "old2")
        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun updateWithTheSameItemAndSameContentsDoesNotNotifyChange() {
        val item = ContentUpdatingItem(1, "contents")
        val children = listOf<Item<*>>(item)

        val group = ItemGroup()
        group.update(children)
        group.registerGroupDataObserver(groupAdapter)

        group.update(children)

        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun updateWithTheSameItemButDifferentContentsNotifiesChange() {
        val oldItem = ContentUpdatingItem(1, "contents")

        val group = ItemGroup()
        group.update(listOf(oldItem))
        group.registerGroupDataObserver(groupAdapter)

        val newItem = ContentUpdatingItem(1, "new contents")
        group.update(listOf(newItem))

        verify(groupAdapter).onItemRangeChanged(group, 0, 1, null)
    }

    @Test
    fun updateWithTheSameItemButDifferentContentsNotifiesChangeWithPayload() {
        val oldItem = ContentUpdatingItem(1, "contents", "old")

        val group = ItemGroup()
        group.update(listOf(oldItem))
        group.registerGroupDataObserver(groupAdapter)

        val newItem = ContentUpdatingItem(1, "new contents", "new")
        group.update(listOf(newItem))

        verify(groupAdapter).onItemRangeChanged(group, 0, 1, "old")
        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun updateWithADifferentItemNotifiesRemoveAndAdd() {
        val oldItem = ContentUpdatingItem(1, "contents")

        val group = ItemGroup()
        group.update(listOf(oldItem))
        group.registerGroupDataObserver(groupAdapter)

        val newItem = ContentUpdatingItem(2, "contents")
        group.update(listOf(newItem))

        verify(groupAdapter).onItemRangeRemoved(group, 0, 1)
        verify(groupAdapter).onItemRangeInserted(group, 0, 1)
    }

    @Test
    fun updateGroupToEmptyNotifiesRemoveAndInsertPlaceholder() {
        val children = listOf<Item<*>>(
            AlwaysUpdatingItem(1),
            AlwaysUpdatingItem(2)
        )

        val group = ItemGroup()
        group.update(children)
        group.registerGroupDataObserver(groupAdapter)

        group.update(emptyList())

        verify(groupAdapter).onItemRangeRemoved(group, 0, 2)
        verifyNoMoreInteractions(groupAdapter)
    }

    @Test
    fun notifyChangeInAnItemCausesParentToNotifyChange() {
        val item = DummyItem()
        val children = listOf<Item<*>>(item)

        val group = ItemGroup()
        group.update(children)
        group.registerGroupDataObserver(groupAdapter)

        item.notifyChanged()

        verify(groupAdapter).onItemChanged(group, 0)
    }
}
