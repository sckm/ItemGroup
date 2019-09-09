package com.github.sckm.itemgroup

import androidx.annotation.CallSuper
import com.xwray.groupie.Group
import com.xwray.groupie.GroupDataObserver
import com.xwray.groupie.Item
import java.util.*

abstract class BaseItemGroup : Group, GroupDataObserver {

    private val observable = GroupDataObservable()

    override fun registerGroupDataObserver(groupDataObserver: GroupDataObserver) {
        observable.registerObserver(groupDataObserver)
    }

    override fun unregisterGroupDataObserver(groupDataObserver: GroupDataObserver) {
        observable.unregisterObserver(groupDataObserver)
    }

    @CallSuper
    open fun add(item: Item<*>) {
        item.registerGroupDataObserver(this)
    }

    @CallSuper
    open fun add(position: Int, item: Item<*>) {
        item.registerGroupDataObserver(this)
    }

    @CallSuper
    open fun addAll(items: Collection<Item<*>>) {
        items.forEach { group -> group.registerGroupDataObserver(this) }
    }

    @CallSuper
    open fun addAll(position: Int, items: Collection<Item<*>>) {
        items.forEach { group -> group.registerGroupDataObserver(this) }
    }

    @CallSuper
    open fun remove(item: Item<*>) {
        item.unregisterGroupDataObserver(this)
    }

    @CallSuper
    open fun removeAll() {
        getAllItems().forEach { item -> item.unregisterGroupDataObserver(this) }
    }

    protected abstract fun getAllItems(): Collection<Item<*>>

    override fun onChanged(group: Group) {
        val item = group as Item<*>
        observable.onItemRangeChanged(this, getPosition(item), item.itemCount)
    }

    override fun onItemInserted(group: Group, position: Int) {
        val item = group as Item<*>
        observable.onItemInserted(this, getPosition(item) + position)
    }

    override fun onItemChanged(group: Group, position: Int) {
        val item = group as Item<*>
        observable.onItemChanged(this, getPosition(item) + position)
    }

    override fun onItemChanged(group: Group, position: Int, payload: Any?) {
        val item = group as Item<*>
        observable.onItemChanged(this, getPosition(item) + position, payload)
    }

    override fun onItemRemoved(group: Group, position: Int) {
        val item = group as Item<*>
        observable.onItemRemoved(this, getPosition(item) + position)
    }

    override fun onItemRangeChanged(group: Group, positionStart: Int, itemCount: Int) {
        val item = group as Item<*>
        observable.onItemRangeChanged(this, getPosition(item) + positionStart, itemCount)
    }

    override fun onItemRangeChanged(
        group: Group,
        positionStart: Int,
        itemCount: Int,
        payload: Any?
    ) {
        val item = group as Item<*>
        observable.onItemRangeChanged(this, getPosition(item) + positionStart, itemCount, payload)
    }

    override fun onItemRangeInserted(group: Group, positionStart: Int, itemCount: Int) {
        val item = group as Item<*>
        observable.onItemRangeInserted(this, getPosition(item) + positionStart, itemCount)
    }

    override fun onItemRangeRemoved(group: Group, positionStart: Int, itemCount: Int) {
        val item = group as Item<*>
        observable.onItemRangeRemoved(this, getPosition(item) + positionStart, itemCount)
    }

    override fun onItemMoved(group: Group, fromPosition: Int, toPosition: Int) {
        val item = group as Item<*>
        val itemPosition = getPosition(item)
        observable.onItemMoved(this, itemPosition + fromPosition, itemPosition + toPosition)
    }

    @CallSuper
    fun notifyItemInserted(position: Int) {
        observable.onItemInserted(this, position)
    }

    @CallSuper
    fun notifyItemRangeInserted(positionStart: Int, itemCount: Int) {
        observable.onItemRangeInserted(this, positionStart, itemCount)
    }

    @CallSuper
    fun notifyItemRemoved(position: Int) {
        observable.onItemRemoved(this, position)
    }

    @CallSuper
    fun notifyItemRangeRemoved(positionStart: Int, itemCount: Int) {
        observable.onItemRangeRemoved(this, positionStart, itemCount)
    }

    @CallSuper
    fun notifyItemMoved(fromPosition: Int, toPosition: Int) {
        observable.onItemMoved(this, fromPosition, toPosition)
    }

    @CallSuper
    fun notifyChanged() {
        observable.onChanged(this)
    }

    @CallSuper
    fun notifyItemChanged(position: Int) {
        observable.onItemChanged(this, position)
    }

    @CallSuper
    fun notifyItemChanged(position: Int, payload: Any?) {
        observable.onItemChanged(this, position, payload)
    }

    @CallSuper
    fun notifyItemRangeChanged(positionStart: Int, itemCount: Int) {
        observable.onItemRangeChanged(this, positionStart, itemCount)
    }

    @CallSuper
    fun notifyItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        observable.onItemRangeChanged(this, positionStart, itemCount, payload)
    }

    /**
     * Iterate in reverse order in case any observer decides to remove themself from the list
     * in their callback
     */
    private class GroupDataObservable {
        val observers: MutableList<GroupDataObserver> = ArrayList()

        fun onItemRangeChanged(group: Group, positionStart: Int, itemCount: Int) {
            observers.reversed().map { observer ->
                observer.onItemRangeChanged(group, positionStart, itemCount)
            }
        }

        fun onItemRangeChanged(group: Group, positionStart: Int, itemCount: Int, payload: Any?) {
            observers.reversed().map { observer ->
                observer.onItemRangeChanged(group, positionStart, itemCount, payload)
            }
        }

        fun onItemInserted(group: Group, position: Int) {
            observers.reversed().map { observer -> observer.onItemInserted(group, position) }
        }

        fun onItemChanged(group: Group, position: Int) {
            observers.reversed().map { observer -> observer.onItemChanged(group, position) }
        }

        fun onItemChanged(group: Group, position: Int, payload: Any?) {
            observers.reversed()
                .map { observer -> observer.onItemChanged(group, position, payload) }
        }

        fun onItemRemoved(group: Group, position: Int) {
            observers.reversed().map { observer -> observer.onItemRemoved(group, position) }
        }

        fun onItemRangeInserted(group: Group, positionStart: Int, itemCount: Int) {
            observers.reversed().map { observer ->
                observer.onItemRangeInserted(group, positionStart, itemCount)
            }
        }

        fun onItemRangeRemoved(group: Group, positionStart: Int, itemCount: Int) {
            observers.reversed().map { observer ->
                observer.onItemRangeRemoved(group, positionStart, itemCount)
            }
        }

        fun onItemMoved(group: Group, fromPosition: Int, toPosition: Int) {
            observers.reversed()
                .map { observer -> observer.onItemMoved(group, fromPosition, toPosition) }
        }

        fun onChanged(group: Group) {
            observers.reversed().map { observer -> observer.onChanged(group) }
        }

        fun registerObserver(observer: GroupDataObserver) {
            synchronized(observers) {
                if (observers.contains(observer)) {
                    throw IllegalStateException("Observer $observer is already registered.")
                }
                observers.add(observer)
            }
        }

        fun unregisterObserver(observer: GroupDataObserver) {
            synchronized(observers) {
                val index = observers.indexOf(observer)
                observers.removeAt(index)
            }
        }
    }
}
