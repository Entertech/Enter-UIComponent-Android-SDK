package cn.entertech.componentdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var defaultFragment = RealtimeDefaultFragment()
        var customFragment = RealtimeCustomFragment()
        var reportDefaultFragment = ReportDefaultFragment()
        var reportCustomFragment = ReportCustomFragment()
        var ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.fl_container,defaultFragment).add(R.id.fl_container,reportDefaultFragment).show(defaultFragment).hide(reportDefaultFragment).commit()

        ll_default.setOnClickListener {
            tv_base.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
            tv_custom.setTextColor(ContextCompat.getColor(this,R.color.colorPrimaryDark))
            iv_base.setImageResource(R.mipmap.ic_base_select)
            iv_custom.setImageResource(R.mipmap.ic_custom_unselect)
            var ft = supportFragmentManager.beginTransaction()
            ft.show(defaultFragment).hide(reportDefaultFragment).commit()
        }
        ll_custom.setOnClickListener {
            tv_custom.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
            tv_base.setTextColor(ContextCompat.getColor(this,R.color.colorPrimaryDark))
            iv_base.setImageResource(R.mipmap.ic_base_unselect)
            iv_custom.setImageResource(R.mipmap.ic_custom_select)
            var ft = supportFragmentManager.beginTransaction()
            ft.show(reportDefaultFragment).hide(defaultFragment).commit()
        }
    }
}
