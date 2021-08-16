package com.airposted.bitoronbd_deliveryman.view

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.airposted.bitoronbd_deliveryman.R
import com.airposted.bitoronbd_deliveryman.databinding.FragmentOrderRequestBinding
import com.airposted.bitoronbd_deliveryman.utils.NoInternetException
import com.airposted.bitoronbd_deliveryman.utils.dismissDialog
import com.airposted.bitoronbd_deliveryman.utils.setProgressDialog
import com.airposted.bitoronbd_deliveryman.utils.snackbar
import com.airposted.bitoronbd_deliveryman.view.main.common.IOnBackPressed
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModel
import com.airposted.bitoronbd_deliveryman.view.main.home.HomeViewModelFactory
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import android.os.CountDownTimer
import android.util.Log
import android.util.MalformedJsonException
import android.view.Window
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import com.airposted.bitoronbd_deliveryman.view.main.common.CommunicatorFragmentInterface
import com.airposted.bitoronbd_deliveryman.view.main.live_parcel.MyLiveDeliveryFragment
import com.airposted.bitoronbd_deliveryman.view.main.parcel_request.ParcelRequestFragment
import com.google.gson.JsonSyntaxException


class OrderRequestFragment : Fragment(), IOnBackPressed, KodeinAware {
    lateinit var mapFragment: SupportMapFragment
    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()
    private lateinit var viewModel: HomeViewModel
    lateinit var googleMap: GoogleMap
    private lateinit var binding: FragmentOrderRequestBinding
    private lateinit var timer: CountDownTimer
    private var communicatorFragmentInterface: CommunicatorFragmentInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOrderRequestBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        communicatorFragmentInterface = context as CommunicatorFragmentInterface
        setProgressDialog(requireActivity())
        var type = ""
        when(arguments?.getInt("type")){
            1 -> type = "Envelop"
            2 -> type = "Small Box"
            3 -> type = "Large Box"
        }
        binding.quantityType.text = arguments?.getInt("quantity").toString() + " " + type + ", " +
                arguments?.getDouble("earning").toString() + "Tk"
        binding.distance.text = arguments?.getDouble("distance").toString() + " Km"

        val animationDuration = 120000 // 2500ms = 2,5s

        binding.timer.setProgressWithAnimation(0F, animationDuration)
        timer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerText.text = (millisUntilFinished / 1000).toString() + " Seconds"
            }

            override fun onFinish() {
                requireActivity().onBackPressed()
            }
        }.start()
        mapFragment =
            childFragmentManager.findFragmentById(R.id.mapReceiverDetails) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
            googleMap.uiSettings.isZoomControlsEnabled = false
            googleMap.uiSettings.isZoomGesturesEnabled = false
            googleMap.uiSettings.isRotateGesturesEnabled = false
            googleMap.uiSettings.isScrollGesturesEnabled = false
            googleMap.uiSettings.isMapToolbarEnabled = false

            googleMap.setOnMarkerClickListener {
                false
            }
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
            }

            val location1 = LatLng(
                requireArguments().getDouble("sender_lat"),
                requireArguments().getDouble("sender_long")
            )

            val location2 = LatLng(
                requireArguments().getDouble("receiver_lat"),
                requireArguments().getDouble("receiver_long")
            )

            val bounds = LatLngBounds.Builder()
            bounds.include(location1)
            bounds.include(location2)

            lifecycleScope.launch {
                try {
                    val url = getDirectionURL(location1, location2)
                    val list = viewModel.getDirections(url)
                    val result = ArrayList<List<LatLng>>()
                    val path = ArrayList<LatLng>()
                    for (i in 0 until list.routes[0].legs[0].steps.size) {
                        path.addAll(decodePolyline(list.routes[0].legs[0].steps[i].polyline.points))
                    }
                    result.add(path)
                    val lineoption = PolylineOptions()
                    for (i in result.indices) {
                        lineoption.addAll(result[i])
                        lineoption.width(6f)
                        lineoption.color(resources.getColor(R.color.blue))
                        lineoption.geodesic(true)
                    }
                    googleMap.addPolyline(lineoption)
                    /*for (i in 0 until list.routes[0].legs[0].steps.size){
                        distance += list.routes[0].legs[0].steps[i].distance.value
                    }*/

                    val circleDrawable = resources.getDrawable(R.drawable.root_start_point)
                    val markerIcon = getMarkerIconFromDrawable(circleDrawable)
                    googleMap.addMarker(
                        MarkerOptions().position(location1)
                            .icon(markerIcon)
                            .title(requireArguments().getString("sender_address"))
                    ).showInfoWindow()

                    val circleDrawable1 = resources.getDrawable(R.drawable.ic_marker)
                    val markerIcon1 = getMarkerIconFromDrawable(circleDrawable1)
                    googleMap.addMarker(
                        MarkerOptions().position(location2)
                            .icon(markerIcon1)
                            .title(requireArguments().getString("receiver_address"))
                    ).showInfoWindow()

                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds.build(),
                            mapFragment.requireView().width,
                            mapFragment.requireView().height,
                            (mapFragment.requireView().height * 0.25f).toInt()
                        )
                    )
                    dismissDialog()
                } catch (e: ApiException) {
                    dismissDialog()
                    binding.rootLayout.snackbar(e.message!!)
                    e.printStackTrace()
                } catch (e: NoInternetException) {
                    dismissDialog()
                    binding.rootLayout.snackbar(e.message!!)
                    e.printStackTrace()
                }
            }
        }
        binding.back.setOnClickListener {
            timer.cancel()
            requireActivity().onBackPressed()
        }

        binding.accept.setOnClickListener {
            val dialogs = Dialog(requireActivity())
            dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogs.setContentView(R.layout.order_accept_dialog)
            dialogs.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogs.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,  //w
                ViewGroup.LayoutParams.MATCH_PARENT //h
            )

            val cancel = dialogs.findViewById<TextView>(R.id.cancel)
            val ok = dialogs.findViewById<TextView>(R.id.ok)
            cancel.setOnClickListener {
                dialogs.dismiss()
            }

            ok.setOnClickListener {
                dialogs.dismiss()
                setProgressDialog(requireActivity())
                lifecycleScope.launch {
                    try {
                        val response = viewModel.changeStatus(arguments?.getString("invoice")!!, 3, 0)
                        if (response.success) {
                            dismissDialog()
                            timer.cancel()
                            requireActivity().supportFragmentManager.popBackStack(
                                ParcelRequestFragment::class.java.name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            communicatorFragmentInterface!!.addContentFragment(
                                MyLiveDeliveryFragment(), true)
                        }
                        else {
                            binding.rootLayout.snackbar(response.msg)
                            dismissDialog()
                        }
                    } catch (e: JsonSyntaxException) {
                        dismissDialog()
                        binding.rootLayout.snackbar(e.message!!)
                        e.printStackTrace()
                    } catch (e: MalformedJsonException) {
                        dismissDialog()
                        binding.rootLayout.snackbar(e.message!!)
                        e.printStackTrace()
                    } catch (e: com.airposted.bitoronbd_deliveryman.utils.ApiException) {
                        dismissDialog()
                        binding.rootLayout.snackbar(e.message!!)
                        e.printStackTrace()
                    } catch (e: NoInternetException) {
                        dismissDialog()
                        binding.rootLayout.snackbar(e.message!!)
                        e.printStackTrace()
                    }

                }
            }

            dialogs.setCancelable(true)
            dialogs.show()
        }

        vibrate()
        val mediaPlayer = MediaPlayer.create(context, R.raw.notification)
        mediaPlayer.start()
    }

    private fun vibrate() {
        val vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(2000)
    }

    override fun onBackPressed(): Boolean {
        timer.cancel()
        return false
    }

    private fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor? {
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getDirectionURL(origin: LatLng, dest: LatLng): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&key=AIzaSyAJnceVASls_tIv4MiZFkzY1ZrVgu6GmW4&sensor=false&mode=driving"
    }

    private fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }

}