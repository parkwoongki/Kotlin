package com.example.flashlight

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val torch = Torch(this)

        flashSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // torch.flashOn()
                startService(intentFor<TorchService>().setAction("on")) // 인텐트에 on 액션을 성정하여 서비스를 시작!
            } else {
                // torch.flashOff()
                startService(intentFor<TorchService>().setAction("off")) // 인텐트에 off 액션을 성정하여 서비스를 시작!
            }
        }
    }
}
