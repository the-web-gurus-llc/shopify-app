package com.john.shopifyApplication.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.john.shopifyApplication.R
import com.john.shopifyApplication.common.APIManager
import com.john.shopifyApplication.config.Constants
import com.john.shopifyApplication.main.MainActivity
import com.john.shopifyApplication.model.Product
import com.john.shopifyApplication.utils.ImageHelper
import com.john.shopifyApplication.utils.MyToastUtil
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_home_row.view.*
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AlertDialog
import android.text.Html
import android.widget.ProgressBar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.john.shopifyApplication.common.PaginationScrollListener
import com.john.shopifyApplication.main.AddProductActivity
import com.john.shopifyApplication.main.DetailActivity
import org.json.JSONException
import org.json.JSONObject


class HomeFragment : Fragment() {

    companion object {

        const val TAG = Constants.BASICTAG + "HomeFragment"
        const val roundedCon = 25.0f

        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {}
    }

    private lateinit var myView : View
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerAdapter
    private lateinit var searchBarSearchView: SearchView
    private lateinit var cardView: CardView
    private lateinit var progressBar: ProgressBar
    private var pagenationNext: String = ""

    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUserVisibleHint(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        myView = view

        linearLayoutManager = LinearLayoutManager(this.context)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = linearLayoutManager
        adapter = this.context?.let { RecyclerAdapter(it, itemClickListener, arrayListOf()) }!!

        recyclerView.adapter = adapter
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

        searchBarSearchView = view.findViewById(R.id.searchBarSearchView)
        cardView = view.findViewById(R.id.cardView)
        initLayoutAndListeners()

        recyclerView?.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                Log.d(TAG,"loadMoreItems")
                getMoreItems()
            }
        })


        return view
    }

    fun getMoreItems() {

        if(pagenationNext.isEmpty()) return

        isLoading = true
        val listItems = ArrayList<Product>()

        val successCallback: (List<Product>) -> Unit = { list ->

            activity?.runOnUiThread {
                hideProgressBar()
                listItems.addAll(list)
                Log.d(TAG, "Products count -> " + listItems.size)

                isLoading = false
                adapter.addData(listItems)
            }
        }

        val headerCallback: (String) -> Unit = {text ->
            prepareNextPagination(text)
        }
        showProgressBar()
        APIManager.share.getProductsWithPagination(pagenationNext, successCallback, headerCallback, errorCallback)
    }

    fun prepareNextPagination(text: String) {

        if(!text.contains("next")) {
            this.pagenationNext = ""
            return
        }

        val subArr = text.split("rel=\"previous\",")
        if(subArr.size == 1) {
            this.pagenationNext = getSubString(subArr[0])
        }
        else if(subArr.size == 2) {
            this.pagenationNext = getSubString(subArr[1])
        } else {
            this.pagenationNext = ""
        }
    }

    fun getSubString(text: String): String{
        if(!text.contains("<")) return ""
        if(!text.contains(">")) return ""
        return text.substring(text.indexOf('<') + 1, text.indexOf('>'))
    }

    fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }
    fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    fun initLayoutAndListeners() {

        val editText: EditText = searchBarSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)

        searchBarSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(value: String?): Boolean {
                LoadWithSearchRes(value,false)
                searchBarSearchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(value: String?): Boolean {
                if(value != null && value.isEmpty())
                    loadFirst(false)
                return true
            }

        })

        cardView.setOnClickListener {
            searchBarSearchView.isIconified = false
        }

        editText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                searchBarSearchView.isIconified = false
            }
        }

        myView.fabAdd.setOnClickListener {
            val intent = Intent(context, AddProductActivity::class.java)
            startActivity(intent)
        }
    }

    fun LoadWithIds() {

        this.adapter.clear()
        this.adapter.notifyDataSetChanged()
        myView.emptyTV.visibility = View.VISIBLE
        if(context == null) return

        val url = "${Constants.PRODUCTURL}.json"
        var shared: SharedPreferences = context!!.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val currentUser = shared.getString(Constants.PREF_CURRENT_USER,"")

        if(currentUser == null || currentUser.isEmpty()) {
            return
        }

        (context as MainActivity).showWaitDialog()
        val request = StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
            override fun onResponse(s: String) {
                if (s == "null") {
                    (context as MainActivity).closeWaitDialog()
                } else {
                    try {
                        val obj = JSONObject(s)
                        if (obj.has(currentUser)) {

                            val oldProducts = obj.getString(currentUser)
                            LoadWithIds_continue(oldProducts)

                        } else {
                            (context as MainActivity).closeWaitDialog()
                        }
                    } catch (e: JSONException) {
                        (context as MainActivity).closeWaitDialog()
                        e.printStackTrace()
                    }
                }
            }

        }, object : Response.ErrorListener {
            override fun onErrorResponse(volleyError: VolleyError) {
                (context as MainActivity).closeWaitDialog()
                println("" + volleyError)
            }
        })

        val rQueue = Volley.newRequestQueue(context)
        rQueue.add(request)
    }

    fun LoadWithIds_continue(productIDS: String) {
        val listItems = ArrayList<Product>()
        val successCallback: (List<Product>) -> Unit = { list ->

            (context as MainActivity).closeWaitDialog()

            activity?.runOnUiThread {
                listItems.addAll(list)
                Log.d(TAG, "Products count -> " + listItems.size)
                this.adapter.clear()
                adapter.addData(listItems)
                this.adapter.notifyDataSetChanged()
                if(listItems.size > 0) myView.emptyTV.visibility = View.GONE
            }
        }

        val headerCallback: (String) -> Unit = {text ->
            prepareNextPagination(text)
        }

        APIManager.share.getProductsWithIDS(productIDS, successCallback, headerCallback, errorCallback)
    }

    fun LoadWithSearchRes(value: String?, isFirstTime: Boolean) {

        if(value == null) return

        val listItems = ArrayList<Product>()
        val successCallback: (List<Product>) -> Unit = { list ->

            if(isFirstTime) (context as MainActivity).closeWaitDialog()

            activity?.runOnUiThread {
                listItems.addAll(list)
                Log.d(TAG, "Products count -> " + listItems.size)
                this.adapter.clear()
                adapter.addData(listItems)
                this.adapter.notifyDataSetChanged()
            }
        }

        val headerCallback: (String) -> Unit = {text ->
            prepareNextPagination(text)
        }

        if(isFirstTime) (context as MainActivity).showWaitDialog()
        APIManager.share.getProducts(value, successCallback, headerCallback, errorCallback)

    }

    private val itemClickListener:(Product) -> Unit = { product ->
        //MyToastUtil.showMessage(activity as MainActivity,"GoTo Detail page.")

        DetailActivity.product = product
        val intent = Intent(context,DetailActivity::class.java)
        startActivity(intent)

    }

    fun loadFirst(isFirstTime: Boolean) {
        //LoadWithSearchRes("",isFirstTime)
        LoadWithIds()
    }

    val errorCallback: (String) -> Unit = { message ->
        (context as MainActivity).closeWaitDialog()
        activity?.runOnUiThread {
            MyToastUtil.showWarning(context!!, message)
        }
    }

    override fun onResume() {
        super.onResume()
        loadFirst(true)
    }

    class RecyclerAdapter(private var context: Context, val itemClickListener: (product: Product) -> Unit, arr: ArrayList<Product?>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var productArr: ArrayList<Product?> = arr

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view: View

            view = inflater.inflate(R.layout.item_home_row, parent, false)
            return LabelViewHolder(view)
        }

        override fun getItemCount(): Int {
            return productArr.size
        }

        override fun getItemViewType(position: Int): Int {
            return 1
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is LabelViewHolder) {
                bindLabelViewHolder(holder, position)
            } else {

            }
        }

        private fun bindLabelViewHolder(holder: LabelViewHolder, position: Int) {
            holder.itemView.setOnClickListener {
                val product = productArr[position]
                if (product != null) {
                    itemClickListener(product)
                }
            }
            holder.itemView.removeTV.setOnClickListener {
                //MyToastUtil.showMessage(context as MainActivity,"Will remove it.")
                val product = productArr[position]
                if (product != null)
                    removeProduct(product, position)

            }
            val product = productArr[position] ?: return
            holder.itemView.imageView.setImageDrawable(null)
            if (!product.image.src.isEmpty()) {

                Glide.with(context!!)
                    .asBitmap()
                    .load(product.image.src)
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val sqRes = ImageHelper.getSquredBitmap(resource)
                            val round = RoundedBitmapDrawableFactory.create(context.resources,sqRes)
                            round.cornerRadius = roundedCon
                            holder.itemView.imageView.setImageDrawable(round)
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
            }

            holder.itemView.titleTV.text = product.title
            holder.itemView.priceTV.text = "$" + product.variant.price
            if(true)
                holder.itemView.soldTV.visibility = View.GONE
            else holder.itemView.soldTV.visibility = View.VISIBLE
        }

        fun removeProduct(product: Product, position: Int) {

            lateinit var dialog: AlertDialog
            val builder = AlertDialog.Builder(context)
            builder.setTitle("")
            builder.setMessage("Are you sure?")
            val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
                when(which){
                    DialogInterface.BUTTON_POSITIVE -> {
                        removeProductFinal(product.id, position)
                    }
                }
            }

            builder.setPositiveButton(Html.fromHtml("<font color='#000000'>YES</font>"),dialogClickListener)
            builder.setNegativeButton(Html.fromHtml("<font color='#000000'>No</font>"),dialogClickListener)
            dialog = builder.create()

            dialog.show()
        }

        fun removeProductFinal(id: Long, position: Int) {

            val successCallback: () -> Unit = {
                if( context != null) {
                    (context!! as MainActivity).runOnUiThread {
                        MyToastUtil.showMessage(context,"Removed successfully!")
                        removeItem(position)
                    }
                }
            }

            val errorCallback: (String) -> Unit = { message ->

                if( context != null) {
                    (context!! as MainActivity).runOnUiThread {
                        MyToastUtil.showWarning(context!!, message)
                    }
                }
            }

            APIManager.share.deleteProduct(id, successCallback, errorCallback)

        }

        fun addData(listItems: ArrayList<Product>) {
            var size = this.productArr.size
            this.productArr.addAll(listItems)
            var sizeNew = this.productArr.size
            notifyItemRangeChanged(size, sizeNew)
        }

        fun removeItem(position: Int) {
            this.productArr.removeAt(position)
            notifyDataSetChanged()
        }

        fun clear() {
            this.productArr.clear()
        }

        class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            @SuppressLint("SetTextI18n")
            fun bind() {
                itemView.titleTV.text = "Hello World"
                itemView.setOnClickListener {  }
            }
        }
    }
}