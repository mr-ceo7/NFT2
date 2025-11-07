`
```
---
applyTo: '**'
---
 Build a lightweight, 
  secure, FULLY OFFLINE SMS Broadcaster app in Kotlin that sends messages 
  DIRECTLY to the Custom SMS App (built by the other agent) – on the SAME 
  DEVICE or LOCAL WIFI HOTSPOT. NO INTERNET, NO REMOTE SERVERS, NO CLOUD.

  Core Features:
  - **Simple UI**: Text field for message, recipients list, "Broadcast No 
  button. Schedule via AlarmManager.
  - Send via:
    1. **Primary**: Custom Intent "com.yourapp.sms.RECEIVE_CUSTOM" to SMSp
   package (explicit, zero latency).
    2. **Backup**: POST JSON to localhost:8080/broadcast on SMS App's 
  embedded Ktor server.
  - Payload: {recipients[], message, timestamp} signed with HMAC-SHA256 
  (shared secret: "your-experimental-secret").
  - Support bulk broadcast (up to 500 recipients), local scheduling, retrn
   failure (via WorkManager).
  - Run as foreground service (minimal notification: "Broadcasting...").
  - Log delivery status to local Room DB (viewable in debug UI).
  - **Multi-Device**: Auto-discover via WiFi P2P (Nearby Connections API) 
  broadcast to nearby devices running SMS App.
  - Zero footprint: No SMS permissions (direct injection).
  - Support Android 10+ with scoped storage and background restrictions.
  - **NO REST API** – replace with local UI triggers only.
  - Embedded Ktor for optional HTTP (CIO engine, localhost only).

  Output: Full broadcaster APK source (Kotlin), integration guide for SMSp
   (exact Intent URI, secret). Include emulator test script: Install both 
  Broadcast → Verify in SMS UI. No external deps beyond AndroidX + Ktor 
  (embedded).- Trigger includes timestamp and sender for realistic 
  notification rendering.
  - No changes needed — intent already carries all data.