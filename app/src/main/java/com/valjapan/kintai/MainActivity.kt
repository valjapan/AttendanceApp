package com.valjapan.kintai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val shukkinFragment: ShukkinFragment = ShukkinFragment()
    private val kakuninFragment: KakuninFragment = KakuninFragment()
    private val settingFragment: SettingFragment = SettingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, shukkinFragment).commit()
        }

        bottom_navigation.setOnNavigationItemSelectedListener {
            val transaction = supportFragmentManager.beginTransaction()
            when (it.itemId) {
                R.id.shukkin -> {
                    transaction.replace(R.id.fragment_container, shukkinFragment)
                        .addToBackStack(null).commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.kakunin -> {
                    transaction.replace(R.id.fragment_container, kakuninFragment)
                        .addToBackStack(null).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.setting -> {
                    transaction.replace(R.id.fragment_container, settingFragment)
                        .addToBackStack(null).commit()
                    return@setOnNavigationItemSelectedListener true
                }
            }

            return@setOnNavigationItemSelectedListener false

        }

    }

}
