package com.valjapan.kintai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentTransaction
import com.valjapan.kintai.fragment.AddWorkLogFragment
import com.valjapan.kintai.fragment.CheckWorkFragment
import com.valjapan.kintai.fragment.SettingFragment
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val addWorkLogFragment: AddWorkLogFragment =
        AddWorkLogFragment()
    private val checkWorkFragment: CheckWorkFragment =
        CheckWorkFragment()
    private val settingFragment: SettingFragment =
        SettingFragment()
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realm = Realm.getDefaultInstance()

        checkPermission()

        if (savedInstanceState == null) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, addWorkLogFragment).commit()
        }

        bottom_navigation.setOnNavigationItemSelectedListener {
            val transaction = supportFragmentManager.beginTransaction()
            when (it.itemId) {
                R.id.addWorkLog -> {
                    transaction.replace(R.id.fragment_container, addWorkLogFragment)
                        .disallowAddToBackStack().commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.checkWork -> {
                    transaction.replace(R.id.fragment_container, checkWorkFragment)
                        .disallowAddToBackStack().commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.setting -> {
                    transaction.replace(R.id.fragment_container, settingFragment)
                        .disallowAddToBackStack().commit()
                    return@setOnNavigationItemSelectedListener true
                }
            }

            return@setOnNavigationItemSelectedListener false

        }

    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // パーミッションの許可を取得する
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1000
            )
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}
