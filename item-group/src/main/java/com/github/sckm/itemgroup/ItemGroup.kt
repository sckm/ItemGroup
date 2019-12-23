package com.github.sckm.itemgroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.xwray.groupie.Item
import java.util.*

open class ItemGroup @JvmOverloads constructor(
    initItems: Collection<Item<*>> = emptyList()
) : BaseItemGroup() {
    private val children = ArrayList<Item<*>>()

    private val listUpdateCallback: ListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            notifyItemRangeChanged(position, count, payload)
        }
    }

    init {
        super.addAll(initItems)
        children.addAll(initItems)
    }

    override fun getItemCount(): Int = children.size

    override fun getItem(position: Int): Item<*> = children[position]

    override fun getPosition(item: Item<*>): Int = children.indexOfFirst { it == item }

    override fun add(item: Item<*>) {
        super.add(item)
        val position = itemCount
        children.add(item)
        notifyItemInserted(position)
    }

    override fun addAll(items: Collection<Item<*>>) {
        super.addAll(items)
        val position = itemCount
        children.addAll(items)
        notifyItemRangeInserted(position, items.size)
    }

    override fun add(position: Int, item: Item<*>) {
        super.add(position, item)
        children.add(position, item)
        notifyItemInserted(position)
    }

    override fun addAll(position: Int, items: Collection<Item<*>>) {
        if (items.isEmpty()) return
        super.addAll(position, items)
        children.addAll(position, items)
        notifyItemRangeInserted(position, items.size)
    }

    override fun remove(item: Item<*>) {
        super.remove(item)
        val index = children.indexOf(item)
        if (index < 0) return

        children.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun removeAll() {
        super.removeAll()
        val itemCount = children.size
        if (itemCount == 0) return

        children.clear()
        notifyItemRangeRemoved(0, itemCount)
    }

    override fun getAllItems(): Collection<Item<*>> {
        return children
    }

    fun isEmpty(): Boolean {
        return children.isEmpty()
    }

    fun replace(position: Int, item: Item<*>) {
        val oldItem = children[position]

        if (oldItem.isSameAs(item)) {
            if (oldItem != item) notifyItemChanged(position, oldItem.getChangePayload(item))
            return
        }

        notifyItemRemoved(position)
        notifyItemInserted(position)
    }

    /**
     * replace items at given range with the new items.
     * @param startPosition start position(inclusive)
     * @param endPosition end position(exclusive)
     */
    fun replaceItems(startPosition: Int, endPosition: Int, items: List<Item<*>>) {
        val oldItems = children.slice(startPosition until endPosition)

        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldItems.size

            override fun getNewListSize(): Int = items.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = oldItems[oldItemPosition]
                val newItem = items[newItemPosition]
                return newItem.isSameAs(oldItem)
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = oldItems[oldItemPosition]
                val newItem = items[newItemPosition]
                return newItem == oldItem
            }

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                val oldItem = oldItems[oldItemPosition]
                val newItem = items[newItemPosition]
                return oldItem.getChangePayload(newItem)
            }
        })

        (startPosition until endPosition).forEach { pos ->
            super.remove(children[startPosition])
            children.removeAt(startPosition)
        }

        children.addAll(startPosition, items)
        items.forEach { item -> super.add(item) }

        diffResult.dispatchUpdatesTo(ReplaceListUpdateCallback(this, startPosition))
    }

    fun update(groups: Collection<Item<*>>, detectMoves: Boolean = true) {
        val oldItems = children.toList()
        val newItems = ArrayList(groups)

        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldItems.size

            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = oldItems[oldItemPosition]
                val newItem = newItems[newItemPosition]
                return newItem.isSameAs(oldItem)
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = oldItems[oldItemPosition]
                val newItem = newItems[newItemPosition]
                return newItem == oldItem
            }

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                val oldItem = oldItems[oldItemPosition]
                val newItem = newItems[newItemPosition]
                return oldItem.getChangePayload(newItem)
            }
        }, detectMoves)

        super.removeAll()
        children.clear()
        children.addAll(groups)
        super.addAll(groups)
        diffResult.dispatchUpdatesTo(listUpdateCallback)
    }

    class ReplaceListUpdateCallback(
        private val baseGroup: BaseItemGroup,
        private val offset: Int
    ) : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {
            baseGroup.notifyItemRangeInserted(offset + position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            baseGroup.notifyItemRangeRemoved(offset + position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            baseGroup.notifyItemMoved(offset + fromPosition, offset + toPosition)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            baseGroup.notifyItemRangeChanged(offset + position, count, payload)
        }
    }
}
