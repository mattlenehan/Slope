# Slope 📈 (a Ramp app)

MVVM * Coroutines * Modularization * Retrofit * OkHttp * Moshi * ViewBinding * Hilt

LiveData * StateFlow * Coil * Navigation * Lottie * Material3 * Dark Mode * Gson


https://user-images.githubusercontent.com/6844125/218643367-9c013dea-2205-4a26-a4f3-87b26aaefe52.mp4 


https://user-images.githubusercontent.com/6844125/218643552-36ce26cc-a958-4fb8-915e-0b09b0ffc659.mp4


Delevopment Notes:
- 🧱 Modularization
  - 📱 app
    - MVVM (Fragment - ViewModel - Repository - Webservice)
    - Jetpack Navigation to handle the Fragment transition
    - Fragments observe the LiveDatas on the ViewModel
    - ViewModel collects from the StateFlows on the Repository
      - sort, search, and apiResponse are each Flows that combine into
        one list of ViewItems for the recyclerview adapter to take in
    - Dependency Injected the Repository into the ViewModel and the Webservice into the Repository
    - To show the field/attributes on the details screen, I converted the Transaction item into a Map<String, String> and looped through to create a reusable view for each field. I excluded redundant information that was already shown in the header and fields with empty values.
  - 📡 networking
    - Retrofit client that uses OkHttp for the HTTP client
    - Added a LocalDateAdapter and LocalDateTimeAdapter to automatically deserialize the Date and DateTime Strings
  - 🕺 models 
    - Moshi for JSON deserialization
- 🎨 Design
  - Dark & Light mode support 🌗
  - Custom Font (avenirnext) 🖋
  - Matrial3 SearchBar/SearchView 🔎
  - Transition animation in the Fragment navigation 🪄
  - Lottie empty state animation 💆
  - Coil for image loading with a roundedCorners extension function 🟡
  - AsyncListDiffer is powering the adapter items so the auto item animations in the recyclerview are used on only the items that have changed 🤹‍♀️
  - The sorting options support: Newest, Oldest, Merchant Name, and Dollar Amount. I felt these were the 4 most important pieces to see sorted. The Date section headers only appear for Newest & Oldest sort options. ⬇️⬆️
  - Search will query against Merchant Name and Category. I felt it was important to query Category so that the user can build unique views like "Show me my SaaS expenses sorted by Dollar Amount". 💰
