package com.gropse.serviceme.pojo;

import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * Created by gropse on 10/13/2017.
 */

public class a implements Serializable {

    /**
     * id : 1
     * name : test user
     * registration_no : 87564ty56rt
     * license_no : 934859475rkfsdk
     * mobile : +91980000000001
     * email : test1@gmail.com
     * password : 25f9e794323b453885f5181f1b624d0b
     * location : e-block sector 6 noida
     * website : www.gropse.com
     * status : 0
     * created_date : 1507740304
     * modified_date : 1507740304
     * device_id : 12345e345rt543
     * security_token : d9f79d4a136756ee333419bd74076a8d
     */

    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("registration_no")
    public String registrationNo;
    @SerializedName("license_no")
    public String licenseNo;
    @SerializedName("mobile")
    public String mobile;
    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;
    @SerializedName("location")
    public String location;
    @SerializedName("website")
    public String website;
    @SerializedName("status")
    public String status;
    @SerializedName("created_date")
    public String createdDate;
    @SerializedName("modified_date")
    public String modifiedDate;
    @SerializedName("device_id")
    public String deviceId;
    @SerializedName("security_token")
    public String securityToken;

    private void asd() {
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        for (Number n : Arrays.asList(12, 123.12345, 0.23, 0.1, 2341234.212431324)) {
            Double d = n.doubleValue();
            System.out.println(df.format(d));
        }

    }

//    public <T> void showMessage(Context context, T message) {
//        try {
//            if (message instanceof Integer) {
//                Toast.makeText(context, (Integer) message, Toast.LENGTH_SHORT).show();
//            } else if (message instanceof String) {
//                Toast.makeText(context, (String) message, Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public <T> ArrayList<T> getResult(String response) {
        return new Gson().fromJson(response, new TypeToken<ArrayList<T>>(){}.getType());
    }

//    public <T> a(String response,  Type classOfT) {
//        return new Gson().fromJson(response, classOfT);
//    }

}
