package johnson.dillan.brewme;

import android.icu.text.DecimalFormat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BreweryItem {
    private String mId;
    private String mName;
    private List<String> mTags = new ArrayList<>();
    private String mType;
    private String mStreetAddress;
    private String mCity;
    private String mState;
    private double mLongitude;
    private double mLatitude;
    private String mPhone;
    private String mWebsite;


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getTags() {
        String tagsString = "";
        for ( String tags : mTags ){
            tagsString += tags += ", ";
        }
        return tagsString;
    }

    public void addTag( String tag ) {
        mTags.add(tag);
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getStreetAddress() {
        return mStreetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        mStreetAddress = streetAddress;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {

        if ( longitude != "null" ) {
            Log.d("NULL LONG", "longitude was read as not null");
            mLongitude = Double.parseDouble(longitude);
        }
        else {
            Log.d("NULL LONG", "longitude was read as null");
            return;
        }
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        if ( latitude != "null" ) {
            Log.d("NULL LAT", "lATitude was read as not null");
            mLatitude = Double.parseDouble(latitude);
        }
        else{
            return;
        }
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(String website) {
        mWebsite = website;
    }

    public String metersToMiles( double meters ){
        DecimalFormat df = new DecimalFormat("#.##");
        double d = meters * 0.00062137119;
        double d2 = meters / 1609.344;
        return df.format( d2 );
    }

}
