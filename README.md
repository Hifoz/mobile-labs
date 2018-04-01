# mobile-labs

Lab tasks for Mobile/Wearable Programming (IMT3673) at NTNU Gjøvik


### Lab1
Basic exercise in sharing info between activities using SharedPrefs and Intents

### Lab2
Very simple RSS2.0 Reader.

### Lab3
Exercise in sensors.
Using sensors to move a ball around on the screen, with audio/haptic feedback on edge collision.

### Lab4
Chat application using Firebase FireStore. (Note: Might not work in emulator due to Firebase)

**Note:** For some reason, the firebase user can still be null after calling FirebaseAuth.signInAnonymously() even when connected to internet. This will cause the app to crash when clicking the "Register" button after choosing a username if it is the first time launching the app. If you close the app once and reopen it the registering works properly. Also even though you crash, the username is saved and you are registered and can chat after restarting the app. *This was not an issue previously and only just appeared today (April 1st). I have no clue why it appeared now...*
