package com.valjapan.kintai.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.valjapan.kintai.R
import com.valjapan.kintai.adapter.RealmViewAdapter
import com.valjapan.kintai.adapter.WorkData
import io.realm.Realm

class CheckWorkFragment : Fragment() {
    private var realm: Realm? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AddWorkLogFragment", "Fragmentの初回起動")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kakunin, container, false)
        realm = Realm.getDefaultInstance()

        val realmResults = realm?.where(WorkData::class.java)?.findAll()
        recyclerView = view.findViewById(R.id.check_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        Log.d("Debug", realmResults.toString())
        recyclerView.adapter =
            RealmViewAdapter(view.context, realmResults, false)


        // Inflate the layout for this fragment
        Log.d("AddWorkLogFragment", "AddWorkFragmentをCreateViewしました")

        return view
    }

    fun initData() {
        val list = mutableListOf<WorkData>()
        val realmResults = realm?.where(WorkData::class.java)?.findAll()

    }

}
