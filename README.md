# DailyTask Monitor - تطبيق إدارة مهام يومية مع نظام مراقب

## نظرة عامة

DailyTask Monitor هو تطبيق Android متقدم لإدارة المهام اليومية مع نظام مراقبة صارم لا يمكن إيقافه إلا بعد تأكيد المهمة من قبل المستخدم والمراقب.

## المميزات الرئيسية

### 1. نظام الحسابات
- حسابان: مستخدم ومراقب
- ربط الحسابات عبر QR Code
- مصادقة Firebase

### 2. نظام الإنذار الصارم
- إنذار لا يتوقف إلا بتأكيد المستخدم والمراقب
- يعمل عبر Foreground Service
- لا يتأثر بقفل الشاشة أو وضع توفير الطاقة

### 3. إدارة المهام
- إنشاء مهام مع أوقات محددة
- مستويات إلحاح مختلفة
- تتبع حالة المهام

### 4. مراقبة المهام
- إشعارات فورية للمراقب
- نظام تأكيد/رفض المهام
- سجل كامل للمهام المنجزة

## البنية التقنية

### تقنيات المستخدمة
- **Kotlin** - لغة البرمجة الرئيسية
- **Jetpack Compose** - واجهة المستخدم
- **Firebase** - قاعدة البيانات والمصادقة
- **Hilt** - حقن التبعيات
- **AlarmManager** - جدولة المهام
- **FCM** - الإشعارات

### الأذونات المطلوبة
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## إعداد المشروع

### 1. متطلبات النظام
- Android Studio Arctic Fox أو أحدث
- Android SDK 21-34
- Java 8 أو أحدث

### 2. إعداد Firebase
1. أنشئ مشروع في Firebase Console
2. أضف تطبيق Android جديد
3. حمل ملف `google-services.json`
4. ضع الملف في مجلد `app/`
5. فعل المصادقة وRealtime Database وCloud Messaging

### 3. تكوين المشروع
1. عدّل `local.properties` وأضف مسار Android SDK:
```
sdk.dir=/path/to/your/android/sdk
```

2. عدّل `google-services.json` بمعلومات مشروع Firebase الخاص بك

### 4. بناء التطبيق
```bash
# تنظيف المشروع
./gradlew clean

# بناء التطبيق
./gradlew build

# بناء APK
./gradlew assembleDebug
# أو
./gradlew assembleRelease
```

## استخدام التطبيق

### للمستخدم:
1. أنشئ حساب مستخدم
2. حدد وقت بداية اليوم
3. أضف مهامك اليومية
4. عند وصول وقت المهمة، سيتم تفعيل الإنذار
5. اضغط "تم التنفيذ" بعد إكمال المهمة
6. انتظر تأكيد المراقب

### للمراقب:
1. أنشئ حساب مراقب
2. امسح QR Code الخاص بالمستخدم
3. ستصلك إشعارات عند إكمال المهام
4. أكد أو ارفض تنفيذ المهام

## هيكل قاعدة البيانات

```json
{
  "users": {
    "userId": {
      "userId": "string",
      "email": "string",
      "userType": "USER|SUPERVISOR",
      "startTime": "06:00",
      "linkedSupervisorId": "string",
      "createdAt": 1234567890
    }
  },
  "tasks": {
    "userId": {
      "taskId": {
        "taskId": "string",
        "userId": "string",
        "title": "string",
        "description": "string",
        "time": "09:00",
        "urgencyLevel": "LOW|MEDIUM|HIGH|CRITICAL",
        "status": "PENDING|IN_PROGRESS|COMPLETED|CONFIRMED",
        "createdAt": 1234567890,
        "completedAt": 1234567890,
        "confirmedBySupervisor": false,
        "messageToSupervisor": "string"
      }
    }
  },
  "supervisor_links": {
    "supervisorId": {
      "userId": {
        "supervisorId": "string",
        "userId": "string",
        "linkedAt": 1234567890
      }
    }
  }
}
```

## حل المشكلات

### المشكلة: التطبيق لا يعمل على Android 12+
الحل: تأكد من إضافة الأذونات المطلوبة في AndroidManifest.xml

### المشكلة: الإنذار لا يعمل عند قفل الشاشة
الحل: تأكد من تفعيل الأذونات التالية:
- FOREGROUND_SERVICE
- WAKE_LOCK
- SYSTEM_ALERT_WINDOW

### المشكلة: QR Code لا يعمل
الحل: تأكد من إذن CAMERA في AndroidManifest.xml

## المساهمة

للمساهمة في المشروع:
1. انسخ المستودع
2. أنشئ فرعًا جديدًا
3. اجعل تغييراتك
4. أرسل طلب سحب (Pull Request)

## الرخصة

هذا المشروع مرخص تحت رخصة MIT.

## الاتصال

لأية استفسارات أو دعم فني، يرجى التواصل عبر:
- البريد الإلكتروني: support@dailytaskmonitor.com
- المشكلات على GitHub: [رابط المشكلات]

---

**ملاحظة**: هذا التطبيق مصمم لأغراض إدارة المهام والإنتاجية. استخدمه بمسؤولية.