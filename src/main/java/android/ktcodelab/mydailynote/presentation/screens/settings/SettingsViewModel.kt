package android.ktcodelab.mydailynote.presentation.screens.settings

/*
class SettingsViewModel : ViewModel() {

    private val currentUser = Firebase.auth.currentUser!!.uid

    private val docRef = Firebase.firestore
        .collection("Pin_Lock")
        .document(currentUser)

    var isPinExists = mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            getPinLockStatus()
        }
    }
    fun savePinToFirebase(pin: List<Int>) {

        viewModelScope.launch(Dispatchers.IO) {

            val data = hashMapOf("pin" to pin)

            docRef.set(data).await()
        }

    }

    private suspend fun getPinLockStatus(){

            docRef.get()
                .addOnSuccessListener {

                if (it.exists()){

                    val getPin = it.get("pin")

                    if (!PinManager.pinExists()){

                        PinManager.savePin(getPin as List<Int>)

                        isPinExists.value = true

                    }else {
                        isPinExists.value = true
                    }

                }else {
                    isPinExists.value = true
                }
            }
            .await()
    }

    fun clearPinToFirebase(){

        viewModelScope.launch {
            docRef.delete().await()
        }
    }

}*/
