
# QR code App

**Package:** `com.kstudio.qrcode`  
**Version:** 1.0.0 (First Release, Updated June 12, 2025) ([play.google.com](https://play.google.com/store/apps/details?id=com.kstudio.qrcode))

แอปสำหรับ scan และ generate QR code ด้วย UI ที่เรียบง่าย รองรับเเละสะดวกต่อการใช้งาน

---

## 🌟 Features

- **Scan QR Codes** – สแกนได้ทันทีจากกล้อง  
- **History of Scans** – บันทึกลิงก์ที่เคยสแกนเพื่อใช้งานภายหลัง  
- **Generate QR Code** – สร้าง QR code จากข้อความหรือ URL ได้ง่าย ๆ  
- **Save & Share** – บันทึกเป็นรูปภาพและแชร์โค้ดต่อให้เพื่อนง่าย ๆ  
- **Dark Theme Support** – รองรับธีมสีเข้มบน Android 15+ (Edge to Edge)

---

## 📦 Project Structure

```
your-app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/kstudio/qrcode/
│   │   │   └── res/
│   └── AndroidManifest.xml
├── README.md
└── build.gradle
```

---

## 🚀 Quick Start

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/your-qr-code-app.git
   cd your-qr-code-app
   ```
2. **Open in Android Studio**  
   Import the project and sync dependencies.
3. **Build & Install**
   - Run the app on connected Android device or emulator

---

## ⚙️ Architecture Overview

- **UI Layer**  
  - *MainActivity*, *ScanActivity*, *GenerateActivity*
- **Business Logic**  
  - *ScannerManager*, *GeneratorManager*
- **Data Layer**  
  - *ScanHistoryRepository* with Room

---

## 🛠 Tech Stack & Dependencies

- **Kotlin**
- **ZXing หรือ ML Kit**
- **Room Database**
- **Jetpack Compose** or **XML UI**
- **Android 15 Support**

---

## 📱 Screenshots & UI

_Add screenshots in `docs/screenshots/` and use Markdown to reference them._

---

## 🧩 Example Folder Structure

```
com/kstudio/qrcode/
├── scan/
│   ├── ScanActivity.kt
│   └── ScannerManager.kt
├── generate/
│   ├── GenerateActivity.kt
│   └── GeneratorManager.kt
└── history/
    ├── ScanHistoryRepository.kt
    └── HistoryActivity.kt
```

---

## 💡 Contribution

- Pull Requests welcome
- Use Lint/Detekt/Ktlint
- Write unit/instrumented tests

---

## 📧 Support & Privacy

- **Support email:** ninekani@gmail.com  
- **Privacy Policy:** See Google Play listing

---

## 📃 Change Log

- **v1.0.0** (June 12, 2025): First Release
