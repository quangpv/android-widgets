package ps.billyphan.airplanescheduler

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

@SuppressLint("SetTextI18n")
class TestAdapter(view: RecyclerView) : ScrollRecyclerAdapter(view) {
    companion object {

        const val ORDER_TITLE1 = 1
        const val ORDER_LIST_1 = 2
        const val ORDER_TITLE2 = 3
        const val ORDER_LIST_2 = 4
        const val ORDER_TITLE3 = 5
        const val ORDER_FOOTER = 6

        const val TYPE_LIST_1_1 = 11
        const val TYPE_LIST_1_2 = 22
        const val TYPE_LIST_2_1 = 33
        const val TYPE_LIST_2_2 = 44
    }

    var test1Items: MutableList<String>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var test2Items: MutableList<String>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setTitle3(text: String) {
        notifyItemChanged(getOffset(ORDER_TITLE3), text)
    }

    override fun getOrders() = ORDER_TITLE1..ORDER_FOOTER

    override fun getOrderSize(order: Int): Int {
        if (order == ORDER_LIST_1) return test1Items?.size ?: 0
        if (order == ORDER_LIST_2) return test2Items?.size ?: 0
        return super.getOrderSize(order)
    }

    override fun getListViewType(positionInList: Int, orderOfList: Int): Int {
        if (orderOfList == ORDER_LIST_1) {
            if (positionInList == 0) return TYPE_LIST_1_1
            return TYPE_LIST_1_2
        }
        if (positionInList == 0) return TYPE_LIST_2_1
        return TYPE_LIST_2_2
    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LIST_1_1 -> Test11Holder(p0)
            TYPE_LIST_1_2 -> Test12Holder(p0)
            TYPE_LIST_2_1 -> Test21Holder(p0)
            TYPE_LIST_2_2 -> Test22Holder(p0)
            ORDER_TITLE1 -> Title1Holder(p0)
            ORDER_TITLE2 -> Title2Holder(p0)
            ORDER_TITLE3 -> Title3Holder(p0)
            else -> FooterHolder(p0)
        }
    }

    override fun onBindListViewHolder(p0: RecyclerView.ViewHolder, positionInList: Int) {
        if (p0 is Test1Holder) p0.bind(test1Items!![positionInList]) else
            (p0 as Test2Holder).bind(test2Items!![positionInList])
    }

    override fun onBindSingleViewHolder(p0: RecyclerView.ViewHolder, order: Int) {
        (p0 as ViewHolder).bind("Order $order")
    }

    override fun onBindSingleViewHolder(p0: RecyclerView.ViewHolder, order: Int, payloads: MutableList<Any>) {
        (p0 as ViewHolder).onChanged(payloads)
    }

    abstract class ViewHolder(view: ViewGroup, id: Int) : RecyclerView.ViewHolder(LayoutInflater.from(view.context)
            .inflate(id, view, false)) {
        val textView = itemView as TextView
        open fun bind(text: String) {
            textView.text = text
        }

        fun onChanged(payloads: MutableList<Any>) {
            bind(payloads[0] as String)
        }
    }

    abstract class Test1Holder(view: ViewGroup, id: Int) : ViewHolder(view, id)
    abstract class Test2Holder(view: ViewGroup, id: Int) : ViewHolder(view, id)

    class Test11Holder(p0: ViewGroup) : Test1Holder(p0, R.layout.item_view_item_test_11) {
        override fun bind(text: String) {
            textView.text = "ITem 1-1 $text"
        }
    }

    class Test12Holder(p0: ViewGroup) : Test1Holder(p0, R.layout.item_view_item_test_11) {
        override fun bind(text: String) {
            textView.text = "ITem 1-2 $text"
        }
    }

    class Test21Holder(p0: ViewGroup) : Test2Holder(p0, R.layout.item_view_item_test_11) {
        override fun bind(text: String) {
            textView.text = "ITem 2-1 $text"
        }
    }

    class Test22Holder(p0: ViewGroup) : Test2Holder(p0, R.layout.item_view_item_test_11) {
        override fun bind(text: String) {
            textView.text = "Item 2-2 $text"
        }
    }

    class Title1Holder(p0: ViewGroup) : ViewHolder(p0, R.layout.item_view_item_test_11) {
        override fun bind(text: String) {
            textView.text = "Title1 $text"
        }
    }

    class Title2Holder(p0: ViewGroup) : ViewHolder(p0, R.layout.item_view_item_test_11) {
        override fun bind(text: String) {
            textView.text = "Title2 $text"
        }
    }

    class Title3Holder(p0: ViewGroup) : ViewHolder(p0, R.layout.item_view_item_test_11) {
        override fun bind(text: String) {
            textView.text = "Title3 $text"
        }
    }

    class FooterHolder(p0: ViewGroup) : ViewHolder(p0, R.layout.item_view_item_test_11) {
        override fun bind(text: String) {
            textView.text = "Footer $text"
        }
    }

}
