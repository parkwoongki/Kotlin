package com.example.gpsmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    // 위치를 주기적으로 얻는 데 필요한 것들
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: MyLocationCallBack

    private val REQUEST_ACCESS_FINE_LOCATION = 1000
    private val polylineOptions = PolylineOptions().width(5f).color(Color.RED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 화면이 꺼지지 않게 하기
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // 세로 모드 화면 고정
        requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationInit() // 초기화 단계계
   }

    // 위치 정보를 얻기 위한 각종 초기화 단계, onCreate에서 사용함
    private fun locationInit() {
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        locationCallback = MyLocationCallBack()

        locationRequest = LocationRequest()

        // 정확도!!
        // GPS 우선
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY // 가장 정확한 위치를 요청하는 것

        // 갱신 시 필요한 초 단위!!
        // 업데이트 인터벌
        // 위치정보가 없을떄는 업데이트를 하지 않는다.
        // 상황에 따라 짧아질 수 있음, 정확하지는 않음
        // 다른 앱에서 짧은 인터벌로 위정보를 요청하면 짧아질 수 있음
        locationRequest.interval = 10000

        // 다른 앱에서 위치 갱신 시 그 정보를 가장 빠른 가격으로 입력!!
        // 정확함. 이것보다 짧은 업데이트는 하지 않음
        locationRequest.fastestInterval = 5000

        /*
        이 요청은 GPS를 사영하여 가장 정확한 위치를 요구하면서 10초마다 위치 정보를 갱신힌다.
        그 사이에 다른 앱에서 위치를 갱신했다면 5초마다 확인하면서 그 값을 활용하여 배터리를 절약한다!
         */
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap // 지도가 준비되면 구글 맵 객체 얻기

        // Add a marker in Sydney and move the camera
        // (위도와 경도를 시드니의 위치를 정하고 구글 지도 객체에 마커를 추가하고 카메라를 이동)
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney")) // 문구가 적힌 마커
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney)) // 시드니로 카메라 이동
    }

    override fun onResume() {
        super.onResume() // Activity가 활성화 될 때 실행하는..

        // 위치정보를 주기적으로 요청하는 코드는 액티비티가 화면에 보일 때만 수행하는 것이 좋다.
        // onResume() 콜백 메서드에서 위치 정보를 요청하고, onPause() 콜백 메서드에서 삭제하는 것이 일반적이다.
        // 권한 요청
        permissionCheck(cancel = { // 요청이 거부되었을 때
            showPermissionInitDialog()
        }, ok = { // 수락하였을 때
            addLocationListener()
        })
        // addLocationListener을 호출하기 전에 먼저 거부되었는지 부터 구분해내기 위해 ..
    }

    @SuppressLint("MissingPermission") // 권한 요청 에러를 표시하지 않도록 함
    private fun addLocationListener() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    // LocationCallBack을 구현한 내부 클래스는 LocationResult 객체를 반환하고 lastLocation 프로퍼티의 Location 객체를 얻어옴
    inner class MyLocationCallBack : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            val location = locationResult?.lastLocation

            // 기기의 GPS가 꺼져있거나 현재 위치를 얻을 수 없을때.. location 객체가 null일 수도 있다 null 아닐때만 작동!
            location?.run {
                // 14 level로 확대하고 현재 위치로 카메라 이동
                val latLng = LatLng(latitude, longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))

                Log.d("MapsActivity", "위도 : $latitude, 경도 : $longitude")

                // polyLine에 좌표 추가
                polylineOptions.add(latLng)

                // 선 그리기
                mMap.addPolyline(polylineOptions)
            }
        }
    }

    private fun permissionCheck(cancel: () -> Unit, ok: () -> Unit) {
        // 위치 권한이 있는지 검사
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 허용되지 않음
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 이전에 권한을 한 번 거부한 적이 있는 경우에 실행할 함수
                cancel()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
                )
            }
        } else {
            // 권한을 수락했을 때 실행할 함수
            ok()
        }
    }

    private fun showPermissionInitDialog() {
        alert("현재 위치 정보를 얻으려면 위치 권한이 필요합니다", "권한이 필요한 이유") {
            yesButton {
                // 권한 요청
                ActivityCompat.requestPermissions(
                    this@MapsActivity, // this는 DialogInterface 객체를 나타낸다. 액티비티를 명시적으로 가르키기위함
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
                )
            }
            noButton { }
        }.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty()
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // 권한 허용됨
                    addLocationListener()
                } else {
                    // 권한 거부
                    toast("권한 거부 됨")
                }
                return
            }
        }
    }

    override fun onPause() {
        super.onPause()
        removeLocationListener() // 액티비티를 내리면 위치 받아오기 중지
    }

    private fun removeLocationListener() {
        // 현재위치 요청을 삭제
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}
