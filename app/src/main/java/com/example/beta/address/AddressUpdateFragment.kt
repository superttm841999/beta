package com.example.beta.address

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.beta.R
import com.example.beta.databinding.FragmentAddressUpdateBinding
import com.example.beta.util.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.runBlocking
import java.io.IOException


class AddressUpdateFragment : Fragment() {
    private lateinit var binding: FragmentAddressUpdateBinding
    private val model: AddressViewModel by activityViewModels()
    private val nav by lazy{ findNavController() }
    private val id by lazy { requireArguments().getString("id") ?: "" }
    private val addressDB = Firebase.firestore.collection("Address")

    //Location
    lateinit var locationManager: LocationManager
    lateinit var locationListener: LocationListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddressUpdateBinding.inflate(inflater, container, false)
        var address = model.getAddressDetail(id)
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

        name.setText(address?.name.toString())
        phone.setText(address?.phone)
        state.setText(address?.state)
        district.setText(address?.district)
        postalCode.setText(address?.postalCode)
        addressDetail.setText(address?.detailAddress)
        if(address?.default == 1){
            binding.defaultSwitch.isChecked = true
        }

        //validation
        var validationItem = mutableMapOf(
            "name" to true,
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

        binding.deleteAddressBtn.setOnClickListener {
            if(model.getAddressDetail(id)?.default == 1){
                activity?.let{it -> "Default Address cannot delete".showToast(it)}
                return@setOnClickListener
            }
            deleteDialog()
        }

        binding.updateAddressBtn.setOnClickListener {
            if(!checkValidation(validationItem)){
                if(validationItem["phone"] == false){
                    phoneLayout.error = validationItemMsg["phone"]
                }

                if(validationItem["name"] == false){
                    nameLayout.error = validationItemMsg["name"]
                }
                return@setOnClickListener
            }

            //IF is default and click not default, nonono
            if(model.getAddressDetail(id)?.default == 1 && !binding.defaultSwitch.isChecked){
                activity?.let{it -> "Default Address cannot unselected. Please set another address as default to replace".showToast(it)}
                return@setOnClickListener

            }

            var updateAddress = mutableMapOf<String, Any>(
                "name" to name.text.toString(),
                "phone" to phone.text.toString(),
                "state" to state.text.toString(),
                "district" to district.text.toString(),
                "postalCode" to postalCode.text.toString(),
                "detailAddress" to addressDetail.text.toString()
            )

            //Just normal update
            if(model.getAddressDetail(id)?.default == 1 && binding.defaultSwitch.isChecked){
                addressDB.document(id).update(updateAddress).addOnSuccessListener {
                    activity?.let{it -> "Update Address Successfully".showToast(it)}
                    nav.navigateUp()
                }.addOnFailureListener {
                    activity?.let{it -> "Update Address Fail".showToast(it)}
                }
            }

            if(model.getAddressDetail(id)?.default == 0 && !binding.defaultSwitch.isChecked){
                addressDB.document(id).update(updateAddress).addOnSuccessListener {
                    activity?.let{it -> "Update Address Successfully".showToast(it)}
                    nav.navigateUp()
                }.addOnFailureListener {
                    activity?.let{it -> "Update Address Fail".showToast(it)}
                }
            }

            if(model.getAddressDetail(id)?.default == 0 && binding.defaultSwitch.isChecked) {

                runBlocking {
                    var addresses = model.updateDefault()
                    Firebase.firestore.runBatch { batch ->
                        if (addresses != null) {
                            for (data in addresses) {
                                if (data.id != id) {
                                    batch.update(addressDB.document(data.id), "default", 0)
                                } else {
                                    batch.update(addressDB.document(data.id), "default", 1)
                                    batch.update(addressDB.document(data.id), updateAddress)
                                }
                            }
                        }
                    }.addOnSuccessListener {
                        activity?.let{
                            "Update Address Successfully and Set As Default".showToast(it)
                        }
                        nav.navigateUp()
                    }.addOnFailureListener {
                        activity?.let{
                            "Failed to Update Address and Set As Default".showToast(it)
                        }
                    }
                }

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

    private fun deleteDialog(){
        var dialog = AlertDialog.Builder(requireActivity())
            .setTitle("Delete Address")
            .setMessage("Are you sure want to delete?")
            .setNegativeButton("NO", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                addressDB.document(id).delete()
                    .addOnSuccessListener {
                        activity?.let{it -> "Delete Address Successfully".showToast(it)}
                        nav.navigateUp()
                    }
                    .addOnFailureListener {
                        activity?.let{it -> "Delete Address Failed".showToast(it)}
                    }
                dialogInterface.dismiss()
            })
            .create()
        dialog.setCancelable(false)
        dialog.show()
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
        var addressList: List<android.location.Address> ?= null
        if(context!=null) {
            var geocoder: Geocoder = Geocoder(context)
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