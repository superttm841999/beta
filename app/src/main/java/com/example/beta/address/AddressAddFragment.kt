package com.example.beta.address

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.databinding.FragmentAddressAddBinding
import com.example.beta.login.LoginViewModel
import com.example.beta.util.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.permissionx.guolindev.PermissionX
import java.io.IOException


class AddressAddFragment : Fragment() {
    private lateinit var binding: FragmentAddressAddBinding
    private val model: LoginViewModel by activityViewModels()
    private val aVM: AddressViewModel by activityViewModels()
    private val nav by lazy{ findNavController() }
    private val userDB = Firebase.firestore.collection("Users")
    private val addressDB = Firebase.firestore.collection("Address")

    //Location
    lateinit var locationManager: LocationManager
    lateinit var locationListener: LocationListener




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val userRef = userDB.document(model.user.value?.id.toString())

        binding = FragmentAddressAddBinding.inflate(inflater, container, false)
        val name = binding.nameTextInput
        val nameLayout = binding.nameTextInputLayout
        val phone = binding.phoneTextInput
        val phoneLayout = binding.phoneTextInputLayout
        val state = binding.stateTextInput
        val stateLayout = binding.stateTextInputLayout
        val district = binding.districtTextInput
        val districtLayout = binding.districtTextInputLayout
        val postalCode = binding.postalCodeTextInput
        val postalCodeLayout = binding.postalCodeTextInput
        val addressDetail = binding.detailAddressTextInput
        val addressDetailLayout = binding.detailAddressTextInputLayout

        //validation
        var validationItem = mutableMapOf(
            "name" to false,
            "phone" to true
        )

        var validationItemMsg = mutableMapOf(
            "name" to getString(R.string.blank_name),
            "phone" to getString(R.string.invalid_phone)
        )

        name.addTextChangedListener(
            afterTextChanged = {
                when{
                    isEmptyString(name.text.toString()) -> {
                        nameLayout.error = validationItemMsg["name"].toString()
                        validationItem["name"] = false
                    }
                    else -> {
                        nameLayout.error = null
                        validationItem["name"] = true
                    }
                }
            }
        )

        var formatPhoneTime = 0
        phone.addTextChangedListener (
            afterTextChanged = {
                when{
                    (!isEmptyString(phone.text.toString()) && formatPhoneTime == 0)-> {
                        if(!validateMYPhone(phone.text.toString())){
                            phoneLayout.error = validationItemMsg["phone"]
                            validationItem["phone"] = false
                            formatPhoneTime = 0
                        }
                        if(validateMYPhone(phone.text.toString())){
                            phoneLayout.error = null
                            validationItem["phone"] = true
                            formatPhoneTime+=1
                            phone.setText(formatMYPhone(phone.text.toString()))
                        }
                    }
                    (!isEmptyString(phone.text.toString()) && formatPhoneTime == 1)-> {
                        if(!validateMYPhone(phone.text.toString())){
                            phoneLayout.error = validationItemMsg["phone"]
                            formatPhoneTime = 0
                            validationItem["phone"] = false
                        }
                    }
                    else -> {
                        phoneLayout.error = null
                        validationItem["phone"] = true
                    }
                }
            }
        )

        binding.addAddressBtn.setOnClickListener {

            if(!checkValidation(validationItem)){
                if(validationItem["phone"] == false){
                    phoneLayout.error = validationItemMsg["phone"]
                }

                if(validationItem["name"] == false){
                    nameLayout.error = validationItemMsg["name"]
                }
                return@setOnClickListener
            }
            var defaultValue = when(aVM.getSize()){
                0 -> 1
                else -> 0
            }

            var newAddress = mutableMapOf<String, Any>(
                "name" to name.text.toString(),
                "phone" to phone.text.toString(),
                "state" to state.text.toString(),
                "district" to district.text.toString(),
                "postalCode" to postalCode.text.toString(),
                "detailAddress" to addressDetail.text.toString(),
                "default" to defaultValue,
                "user" to userRef
            )
            addressDB.add(newAddress)
                .addOnSuccessListener {
                    activity?.let { it2 -> "Add Address Successfully".showToast(it2) }
                    nav.navigateUp()
                }
                .addOnFailureListener {
                    activity?.let { it2 -> "Add Address Failed. Please Try Again".showToast(it2) }
                }
        }

        binding.getLocationBtn.setOnClickListener {
            PermissionX.init(this)
                .permissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                .onExplainRequestReason { scope, deniedList ->
                    scope.showRequestReasonDialog(
                        deniedList,
                        "Please give permission to get your location",
                        "OK",
                        "Cancel"
                    )
                }
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
//                        activity?.let { it -> "Permission granted".showToast(it) }
                        getLocation()
                    } else {
                        activity?.let { it -> "Less permission function will not work.".showToast(it) }
                    }
                }
        }

        return binding.root
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        binding.getLocationBtn.text = "Getting address..."
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        var hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var locationGps: Location? = null
        var locationNetwork: Location? = null

        if (hasGps || hasNetwork) {
            if (hasGps) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(p0: Location) {
                        if (p0 != null) {
                            locationGps = p0
                            getAddress(locationGps!!.latitude, locationGps!!.longitude)
                            locationManager.removeUpdates(this)
                        }
                    }
                    override fun onProviderDisabled(provider: String) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                })
                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }

            if (hasNetwork) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(p0: Location) {
                        if (p0 != null) {
                            locationNetwork = p0
                            getAddress(locationNetwork!!.latitude, locationNetwork!!.longitude)
                            locationManager.removeUpdates(this)
                        }
                    }

                    override fun onProviderDisabled(provider: String) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }

            if(locationGps!= null && locationNetwork!= null){
                if(locationGps!!.accuracy > locationNetwork!!.accuracy){
                    getAddress(locationGps!!.latitude, locationGps!!.longitude)
                }else{
                    getAddress(locationNetwork!!.latitude, locationNetwork!!.longitude)
                }
            }

            if(locationGps == null && locationNetwork == null){
                binding.getLocationBtn.text = "Get Location Failed. Please Try Again"
            }
        } else {
            binding.getLocationBtn.text = "Get Location"
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun getAddress(latitude: Double, longitude: Double) {
        Log.e("oi o0o", "nmsl")
        var addressList: List<android.location.Address> ?= null
        if(context!=null) {
            var geocoder = Geocoder(context)
            try {
                addressList = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (addressList != null) {
                for (address in addressList) {
                    binding.stateTextInput.setText(address.adminArea)
                    binding.districtTextInput.setText(address.subAdminArea)
                    binding.postalCodeTextInput.setText(address.postalCode)
                    binding.detailAddressTextInput.setText(address.getAddressLine(0))
                }
                binding.getLocationBtn.text = "Success - Get Location Again"
            }
        }
    }

}