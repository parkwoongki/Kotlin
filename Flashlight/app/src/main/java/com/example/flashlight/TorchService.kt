package com.example.flashlight

import android.app.Service
import android.content.Intent
import android.os.IBinder

class TorchService : Service() { // 서비스 클래스에서 상속 받음
    private val torch: Torch by lazy {
        // onCreate 콜백 메서드를 사용하게 되면 코드가 길어지므로 lazy로 간략화 함.
        // Torch가 처음 실행될 때 초기화됨
        Torch(this)
    }

    private var isRunning = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 외부에서 startService 메서드로 TorchService 서비스를 호출하면 onStartCommand 콜백 메서드가 호출된다
        // 보통 인텐트에 action 값을 설정하여 호출하는데 on 과 off 문자열을 액션으로 받았을때 when문을 사용하여
        // 각각 플래시를 켜고 끄는 동작을 하도록 코드를 작성함
        when (intent?.action) {
            // 앱에서 실행할 경우
            "on" -> {
                torch.flashOn()
                isRunning = true
            }
            "off" -> {
                torch.flashOff()
                isRunning = false
            }
            // 서비스를 실행할 경우
            else -> {
                isRunning = !isRunning
                if (isRunning) {
                    torch.flashOn()
                } else {
                    torch.flashOff()
                }
            }
        }

        // 서비스는 메모리 부족 등의 이유로 시스템에 의해서 강제로 종료될 수 있다.
        // onStartCommand 메서드는 다음 중 하나를 반환한다. 이 값에 따라 시스템이 강제로 종료한 후에 시스템 자원이 회복되어
        // 다시 서비스를 시작할 수 있을 때 어떻게 할지 경정함

        // START_STICKY : null 인텐트로 다시 시작한다. 명령을 실행하지는 않지만 무기한으로 실행 중이며
        // 작업을 기다리고 있는 미디어 플레이어와 비슷한 경우에 적합하다.

        // START_NOT_STICKY : 다시 시작하지 않음

        // START_REDELIVER_INTENT : 마지막 인텐트로 다시 시작한다. 능동적으로 수행 중인 파일 다운로드와 같은 서비스에 적합!
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}
