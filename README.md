# Food Recognition from images using Calorie Mama AI API

## Running the example

You need an access token to Calorie Mama API. Obtain one by creating a free developer account [https://developers.caloriemama.ai](https://developers.caloriemama.ai)

Clone the project from GitHub

```
git clone https://github.com/igorrendulic/food_recognition_with_calorie_mama.git
```

Now import project to Android Studio and create a file under resources: `res/values/secrets.xml`
```xml
<resources>
    <string name="caloriemama_token">YOUR TOKEN HERE</string>
</resources>
```

You can run the app. 

If you'd like to use Android Emulator and a WebCam read the next section.

As you probably know this is not the safest way to keep secrets in Android app. You can read more [https://rammic.github.io/2015/07/28/hiding-secrets-in-android-apps/](here).

## Setup Android Studio to use a WebCam

1. Tools -> Android -> AVD Manager
2. Create AVD if it doesn't exist or edit existing
3. Show advanced settings
4. In Camera section select from dropdown Webcam0
5. Restart Emulator if needed

