package com.example.flashlight

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class TorchAppWidget : AppWidgetProvider() { // 앱 위젯용 파일은 AppWidgetProvider 이라는 일종의 브로드캐스트 리시버 클래스를 상속받는다.
    // onUpdate메서드는 위젯이 업데이트 되어야할때 호출된다.
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) { // 위젯이 여러개 배치되었다면 모든 위젯을 업데이트합니다.
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    // 위젯 처음 생성시 호출된다.
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    // 여러 개일 경우 마지막 위젯이 제거될 때 호출된다.
    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        // 위젯을 업데이트할 때 수행되는 코드이다.
        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val widgetText = context.getString(R.string.appwidget_text)

            // Construct the RemoteViews object
            // 위젯은 액티비티에서 레이아웃을 다루는 것과는 조금 다르다.
            // 위젯에서 배치하는 뷰는 따로 있다.
            // 그것들은 RemoteViews 객체용으로 준비된 텍스트값을 변경하는 메서드이다.
            val views = RemoteViews(context.packageName, R.layout.torch_app_widget)
            views.setTextViewText(
                R.id.appwidget_text,
                widgetText
            ) // setTextViewText 메서드는 RemoteViews 객체용으로 준비된 텍스트값을 변경하는 메서드이다.

            /* 작성 파트 */
            // 실행할 인텐트 작성
            val intent = Intent(context, TorchService::class.java) // 서비스 인텐트 임 그게 아랫 줄 세번째에 들어감
            val pendingIntent = PendingIntent.getService(context, 0, intent, 0)
            // 위젯을 클릭하면 위에서 정의한 Intent 실행
            // 클릭이 발생할 뷰와 pendingIntent 객체가 필요함
            // PendingIntent는 실행할 인텐트 정보를 가지고 있다가 수행해준다, 어떤 인텐트를 사용할지에 따라 다른 메서드를 사용함
            // PendingIntent.getAcitivity() : 액티비티 실행
            // PendingIntent.getService() : 서비스 실행
            // PendingIntent.getBroadcast() : 브로드캐스트 실행
            views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views) // 위젯 업데이트
        }
    }
}

