package com.example.mygallery

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_photo.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

/* 클래스 밖에서 const 사용해서 상수 정의하면 컴파일 시간에 결정되는 상수가되고
 * 이 파일 내에서 어디든지 사용할 수 있다.
  * 컴파일 시간 상수 초기화는 String 또는 프리미트형 (Int 같은 기본형) 으로만 초기화가 가능하다. */
private const val ARG_URI = "uri"

class PhotoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var uri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 프래그먼트가 생성되면 onCreate() 메서드가 호출되고 ARG_URI
        arguments?.let {
            uri = it.getString(ARG_URI)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        /* onCreateView 메서드에서는 프래그먼트에 표시될 뷰를 생성한다.
        * 액티비티가 아닌 곳에서 레이아웃 리소스를 가지고 오려면 LayoutInflater 객체의 inflate() 메서드를 사용 */
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Glide를 사용해야지 메모리 관리도 쉽고 부드럽게 로딩이 가능하다!
        Glide.with(this).load(uri).into(imageView)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PhotoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        /* newInstatnce 메서드를 통해 프래그먼트를 생성할 수 있고, 인자로 uri값을 전달한다.
         * 이 값은 bundle 객체에 ARG_URI 키로 저장되고 arguments 프로퍼티에 저장된다. */
        fun newInstance(uri : String) =
            PhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URI, uri)
                }
            }
    }
}
