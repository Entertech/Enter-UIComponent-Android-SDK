package cn.entertech.entercomponentsdk

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_report_default.*

class ReportDefaultFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_default, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var attentionDatas = ArrayList<Double>()
        for(i in 0 until 500){
            attentionDatas.add(java.util.Random().nextDouble()*100)
        }
        report_attention.setData(System.currentTimeMillis()/1000,attentionDatas)
    }
}
