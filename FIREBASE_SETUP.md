# إعداد Firebase للتطبيق

## الخطوات التفصيلية

### 1. إنشاء مشروع Firebase

1. انتقل إلى [Firebase Console](https://console.firebase.google.com/)
2. أنشئ مشروعًا جديدًا
3. أدخل اسم المشروع: "DailyTask Monitor"
4. فعل Google Analytics (اختياري)
5. انقر "إنشاء المشروع"

### 2. إضافة تطبيق Android

1. في لوحة التحكم، انقر "إضافة تطبيق"
2. اختر Android
3. أدخل معلومات التطبيق:
   - **Package name**: `com.dailytask.monitor`
   - **App nickname**: DailyTask Monitor
   - **SHA certificate**: (اختياري) يمكنك الحصول عليه لاحقًا

### 3. تحميل ملف google-services.json

1. بعد إضافة التطبيق، حمل ملف `google-services.json`
2. انسخ الملف إلى مجلد `app/` في مشروع Android Studio

### 4. تفعيل الخدمات المطلوبة

#### أ. Firebase Authentication
1. انتقل إلى "Authentication" في القائمة الجانبية
2. انقر "Get started"
3. فعل "Email/Password" كطريقة تسجيل دخول
4. احفظ التغييرات

#### ب. Firebase Realtime Database
1. انتقل إلى "Realtime Database"
2. انقر "Create database"
3. اختر موقع الخادم الأنسب
4. ابدأ في الوضع الاختباري (أو الإنتاجي حسب الحاجة)
5. أنشئ القواعد التالية:

```json
{
  "rules": {
    "users": {
      "$userId": {
        ".read": "$userId === auth.uid",
        ".write": "$userId === auth.uid"
      }
    },
    "tasks": {
      "$userId": {
        ".read": "$userId === auth.uid",
        ".write": "$userId === auth.uid"
      }
    },
    "supervisor_links": {
      "$supervisorId": {
        ".read": "$supervisorId === auth.uid",
        ".write": "$supervisorId === auth.uid"
      }
    }
  }
}
```

#### ج. Firebase Cloud Messaging
1. انتقل إلى "Cloud Messaging"
2. انقر "Get started"
3. سيتم تفعيل الخدمة تلقائيًا

### 5. الحصول على مفاتيح API

1. انتقل إلى "Project settings"
2. انتقل إلى تبويب "Service accounts"
3. انقر "Generate new private key"
4. احفظ الملف بأمان (للاستخدام الخادمي)

### 6. تكوين FCM Server Key (اختياري)

إذا كنت تريد إرسال إشعارات من الخادم:
1. انتقل إلى "Cloud Messaging"
2. انسخ "Server key"
3. استخدمه في كود الخادم الخاص بك

### 7. تحديث ملف google-services.json

عدّل ملف `google-services.json` بمعلومات مشروعك الفعلية:

```json
{
  "project_info": {
    "project_number": "YOUR_ACTUAL_PROJECT_NUMBER",
    "project_id": "YOUR_ACTUAL_PROJECT_ID",
    "storage_bucket": "YOUR_ACTUAL_STORAGE_BUCKET"
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "YOUR_ACTUAL_MOBILE_SDK_APP_ID",
        "android_client_info": {
          "package_name": "com.dailytask.monitor"
        }
      },
      "oauth_client": [
        {
          "client_id": "YOUR_ACTUAL_OAUTH_CLIENT_ID",
          "client_type": 3
        }
      ],
      "api_key": [
        {
          "current_key": "YOUR_ACTUAL_API_KEY"
        }
      ],
      "services": {
        "appinvite_service": {
          "other_platform_oauth_client": [
            {
              "client_id": "YOUR_ACTUAL_CLIENT_ID",
              "client_type": 3
            }
          ]
        }
      }
    }
  ],
  "configuration_version": "1"
}
```

### 8. اختبار الإعداد

1. شغّل التطبيق
2. أنشئ حساب مستخدم جديد
3. تحقق من ظهور البيانات في Firebase Console

## النصائح المهمة

### أمان قاعدة البيانات
- لا تعطي صلاحيات القراءة/الكتابة للجميع
- استخدم قواعد الأمان المناسبة
- راقب استخدام قاعدة البيانات بانتظام

### الأداء
- استخدم الفهرسة للحقول التي تُستعلَم كثيرًا
- حدد من حجم البيانات المحفوظة
- استخدم القواعد لتحسين الأداء

### التكلفة
- راقب استخدامك لخدمات Firebase
- فعل حدود الإنفاق إذا لزم الأمر
- استخدم الخطة المجانية للاختبار

## حل المشكلات الشائعة

### المشكلة: فشل المصادقة
- تأكد من تفعيل Email/Password في Firebase Console
- تحقق من صحة مفاتيح API
- تأكد من اتصال الشبكة

### المشكلة: لا توجد بيانات في قاعدة البيانات
- تحقق من قواعد الأمان
- تأكد من تسجيل الدخول
- راقب أخطاء التطبيق

### المشكلة: الإشعارات لا تعمل
- تأكد من تفعيل FCM
- تحقق من تكوين الإشعارات في التطبيق
- راقب سجل FCM في Firebase Console

## الدعم

للمساعدة في إعداد Firebase:
- [Firebase Documentation](https://firebase.google.com/docs)
- [Firebase Support](https://firebase.google.com/support)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/firebase)