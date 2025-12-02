package com.dailytask.monitor.ui.qr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dailytask.monitor.ui.theme.DailyTaskMonitorTheme
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QRCodeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?)?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyTaskMonitorTheme {
                QRCodeScreen()
            }
        }
    }
}

@Composable
fun QRCodeScreen(
    viewModel: QRCodeViewModel = hiltViewModel()
) {
    val userId by viewModel.userId.collectAsState()
    val qrBitmap = remember(userId) {
        if (userId.isNotEmpty()) {
            generateQRCode(userId)
        } else {
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "رمز QR الخاص بك",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (qrBitmap != null) {
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.size(300.dp)
            )
        } else {
            CircularProgressIndicator()
        }

        Text(
            text = "اطلب من المراقب مسح هذا الرمز لربط حسابك",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 32.dp)
        )

        Button(
            onClick = { viewModel.generateNewUserId() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("إنشاء رمز جديد")
        }
    }
}

private fun generateQRCode(content: String): android.graphics.Bitmap? {
    return try {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.RGB_565)
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        
        bitmap
    } catch (e: Exception) {
        null
    }
}