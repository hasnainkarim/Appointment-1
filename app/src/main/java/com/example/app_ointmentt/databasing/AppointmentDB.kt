package com.example.app_ointmentt.databasing

import android.content.Context
import android.preference.PreferenceManager
import com.example.app_ointmentt.models.Appointment
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AppointmentDB(val context: Context) {

    /***Create interfaces***/
    //Create appointment interfaces
    lateinit var mCreateAppointmentSuccessListener: CreateAppointmentSuccessListener
    lateinit var mCreateAppointmentFailureListener: CreateAppointmentFailureListener

    //View appointment by ID interfaces
    lateinit var mViewAppointmentByIdSuccessListener: ViewAppointmentByIdSuccessListener
    lateinit var mViewAppointmentByIdFailureListener: ViewAppointmentByIdFailureListener

    //Upcoming appointment for patients interfaces
    lateinit var mViewUpcomingAppointmentsPatientSuccessListener: ViewUpcomingAppointmentsPatientSuccessListener
    lateinit var mViewUpcomingAppointmentsPatientFailureListener: ViewUpcomingAppointmentsPatientFailureListener

    //Upcoming Appointment for doctors interfaces
    lateinit var mViewUpcomingAppointmentsDoctorSuccessListener: ViewUpcomingAppointmentsDoctorSuccessListener
    lateinit var mViewUpcomingAppointmentsDoctorFailureListener: ViewUpcomingAppointmentsDoctorFailureListener


    /***Calling API through functions***/
    fun createAppointment(slotId: String)
    {
        val sh = PreferenceManager.getDefaultSharedPreferences(context)
        val jwt = sh.getString("jwt", "NONE FOUND").toString()
        val uid = sh.getString("uid", "NONE FOUND").toString()

        if ( jwt == "NONE FOUND" || uid == "NONE FOUND" )
        {
            val message = "Please login to book your appointment."
            mCreateAppointmentFailureListener.createAppointmentFailure(message)
        }
        else
        {
            val paramsJSON = JSONObject()
            paramsJSON.put("patientId", uid)
            paramsJSON.put("slotId", slotId)

            val params = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), paramsJSON.toString())
            val headerJwt = "Bearer $jwt"
            val call = APIObject.api.createAppointment(headerJwt, params)

            call.enqueue(object: Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    mCreateAppointmentFailureListener.createAppointmentFailure(t.message)
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if ( response.isSuccessful )
                    {
                        val jsonRes = JSONObject(response.body()!!.string())
                        val appRet = Appointment().fromJSON(jsonRes.getJSONObject("appRet"))
                        mCreateAppointmentSuccessListener.createAppointmentSuccess(appRet)
                    }
                    else
                        mCreateAppointmentFailureListener.createAppointmentFailure(response.message())
                }
            })
        }
    }

    fun viewAppointmentById(appId: String)
    {
        val paramsJSON = JSONObject()
        paramsJSON.put("appId", appId)
        val params = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), paramsJSON.toString())

        val call = APIObject.api.viewAppointmentById(params)

        call.enqueue(object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                mViewAppointmentByIdFailureListener.viewAppointmentByIdFailure()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if ( response.isSuccessful )
                {
                    val jsonRes = JSONObject(response.body()!!.string())
                    val appRet = Appointment().fromJSON(jsonRes.getJSONObject("appRet"))
                    mViewAppointmentByIdSuccessListener.viewAppointmentByIdSuccess(appRet)
                }
                else
                    mViewAppointmentByIdFailureListener.viewAppointmentByIdFailure()
            }
        })
    }

    fun viewUpcomingAppointmentsPatient(patientId : String){
        val paramsJSON = JSONObject()
        paramsJSON.put("patientId", patientId)
        val params = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), paramsJSON.toString())

        val call = APIObject.api.viewUpcomingAppointmentsPatient(params)

        call.enqueue(object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                mViewUpcomingAppointmentsPatientFailureListener.viewUpcomingAppointmentsPatientFailure()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if ( response.isSuccessful )
                {
                    val jsonRes = JSONObject(response.body()!!.string())
                    val appointments = makeAppointmentArrayListFromJsonArray(jsonRes.getJSONArray("appRet"))
                    mViewUpcomingAppointmentsPatientSuccessListener.viewUpcomingAppointmentsPatientSuccess(appointments)
                }
                else
                    mViewUpcomingAppointmentsPatientFailureListener.viewUpcomingAppointmentsPatientFailure()
            }
        })
    }

    fun viewUpcomingAppointmentsDoctor(doctorId : String){
        val paramsJSON = JSONObject()
        paramsJSON.put("doctorId", doctorId)
        val params = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), paramsJSON.toString())

        val call = APIObject.api.viewUpcomingAppointmentsDoctor(params)

        call.enqueue(object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                mViewUpcomingAppointmentsDoctorFailureListener.viewUpcomingAppointmentsDoctorFailure()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if ( response.isSuccessful )
                {
                    val jsonRes = JSONObject(response.body()!!.string())
                    val appointments = makeAppointmentArrayListFromJsonArray(jsonRes.getJSONArray("appRet"))
                    mViewUpcomingAppointmentsDoctorSuccessListener.viewUpcomingAppointmentsDoctorSuccess(appointments)
                }
                else
                    mViewUpcomingAppointmentsDoctorFailureListener.viewUpcomingAppointmentsDoctorFailure()
            }
        })
    }


    /***Utilities***/
    //An utility function to return a list of appointments on the basis of jsonArray
    fun makeAppointmentArrayListFromJsonArray(jsonArray: JSONArray): ArrayList<Appointment> {
        val appointments = ArrayList<Appointment>()
        val countOfAppointment = jsonArray.length()
        for(i in 0 until countOfAppointment){
            appointments.add(Appointment().fromJSON(jsonArray.getJSONObject(i)))
        }
        return appointments
    }


    /***Interfaces***/
    interface CreateAppointmentSuccessListener
    {
        fun createAppointmentSuccess(app: Appointment)
    }

    interface CreateAppointmentFailureListener
    {
        fun createAppointmentFailure(message: String?)
    }

    interface ViewAppointmentByIdSuccessListener
    {
        fun viewAppointmentByIdSuccess(app: Appointment)
    }

    interface ViewAppointmentByIdFailureListener
    {
        fun viewAppointmentByIdFailure()
    }

    interface ViewUpcomingAppointmentsPatientSuccessListener{
        fun viewUpcomingAppointmentsPatientSuccess(appointments: ArrayList<Appointment>)
    }

    interface ViewUpcomingAppointmentsPatientFailureListener{
        fun viewUpcomingAppointmentsPatientFailure()
    }

    interface ViewUpcomingAppointmentsDoctorSuccessListener{
        fun viewUpcomingAppointmentsDoctorSuccess(appointments: ArrayList<Appointment>)
    }

    interface ViewUpcomingAppointmentsDoctorFailureListener{
        fun viewUpcomingAppointmentsDoctorFailure()
    }


    /***Interface setters***/
    fun setCreateAppointmentSuccessListener(int: CreateAppointmentSuccessListener)
    {
        this.mCreateAppointmentSuccessListener = int
    }

    fun setCreateAppointmentFailureListener(int: CreateAppointmentFailureListener)
    {
        this.mCreateAppointmentFailureListener = int
    }

    fun setViewAppointmentByIdSuccessListener(int: ViewAppointmentByIdSuccessListener)
    {
        this.mViewAppointmentByIdSuccessListener = int
    }

    fun setViewAppointmentByIdFailureListener(int: ViewAppointmentByIdFailureListener)
    {
        this.mViewAppointmentByIdFailureListener = int
    }

    fun setViewUpcomingAppointmentsPatientSuccessListener(int: ViewUpcomingAppointmentsPatientSuccessListener){
        this.mViewUpcomingAppointmentsPatientSuccessListener = int
    }

    fun setViewUpcomingAppointmentsPatientFailureListener(int: ViewUpcomingAppointmentsPatientFailureListener){
        this.mViewUpcomingAppointmentsPatientFailureListener = int
    }

    fun setViewUpcomingAppointmentsDoctorSuccessListener(int: ViewUpcomingAppointmentsDoctorSuccessListener){
        this.mViewUpcomingAppointmentsDoctorSuccessListener = int
    }

    fun setViewUpcomingAppointmentsDoctorFailureListener(int: ViewUpcomingAppointmentsDoctorFailureListener){
        this.mViewUpcomingAppointmentsDoctorFailureListener = int
    }
}