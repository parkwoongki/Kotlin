package com.example.flashlight

import android.annotation.TargetApi
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class Torch(context: Context) {
    private var cameraId: String? = null
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    // 플래시를 키기 위해서는 CameraManager 객체가 필요하고 이를 얻기 위해서는 위의 context가 필요하다.
    // getSystemService 메서드는 안드로이드 시스템에서 제공하는 각종 서비스를 관리하는 매니저 클래스를 생성한다.
    // 인자로는 Context 클래스에 정의된 서비스를 정의한 상수를 지정한다.
    // 여기서는 CAMERA_SERVICE를 지정했다.
    // 이 메서드는 Object형을 반환하기 때문에 as 연산자를 사용하여 CameraSerivce형으로 반환을 한다.

    init {
        cameraId = getCameraId() // 바로 카메라 ID를 얻는다. 카메라 아이디는 기기에 내장된 카메라마다 고유한 ID가 부여된다.
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun flashOn() {
        cameraManager.setTorchMode(cameraId, true)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun flashOff() {
        cameraManager.setTorchMode(cameraId, false)
    }

    private fun getCameraId(): String? { // 메서드는 카메라의 ID를 얻는 메서드이다. 카메라가 없다면 null이므로 String? 연산자 씀
        val cameraIds = cameraManager.cameraIdList // 카메라 매니저는 기기가 가지고 있는 모든 카메라 정보 목록을 제공한다.

        for (id in cameraIds) { // 이 목록을 순회하면서,
            val info = cameraManager.getCameraCharacteristics(id) // 각 ID 뱔로 세부 정보를 가지는 객체를 얻는다.
            val flashAvailable = info.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) // 플래시 기능 여부 얻기
            val lensFacing = info.get(CameraCharacteristics.LENS_FACING) // 카메라 렌즈 방향 얻기

            if (flashAvailable != null
                && flashAvailable
                && lensFacing != null
                && lensFacing == CameraCharacteristics.LENS_FACING_BACK
            ) {
                return id // 뒷면의 카메라를 발견했다면 id를 반환하고,
            }
        }
        return null // 카메라가 없다면 null을 반환한다.
    }
}