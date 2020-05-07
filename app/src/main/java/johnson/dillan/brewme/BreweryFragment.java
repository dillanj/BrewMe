package johnson.dillan.brewme;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BreweryFragment extends Fragment {

    private static final String BREW_ID = "Brewery_ID";

    private View mContainer;
    private View mConstraint;

    private BreweryItem mBrewery;
    private TextView mName;
    private TextView mType;
    private TextView mTags;
    private TextView mCityState;
    private TextView mPhone;
    private TextView mStreetAddress;


    public static BreweryFragment newInstance( String id ) {
        Bundle args = new Bundle();
        args.putString( BREW_ID, id );

        BreweryFragment frag = new BreweryFragment();
        frag.setArguments( args );
        return frag;
    }

    @Override
    public void onCreate( Bundle savedInstanceState ){
        super.onCreate( savedInstanceState );
        String id = (String) getArguments().getString( BREW_ID );
        mBrewery = BreweryLab.get().getBrewery( id );
    } // end of OnCreate


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
        View v = inflater.inflate( R.layout.fragment_brewery, container, false );

        mContainer = (LinearLayout) v.findViewById( R.id.parent_container );
        mConstraint = (ConstraintLayout) v.findViewById( R.id.parent_constraint);


        mName = (TextView) v.findViewById( R.id.name );
        mName.setText( mBrewery.getName() );

        mType= (TextView) v.findViewById( R.id.type );
        mType.setText( mBrewery.getType() );

        mTags = (TextView) v.findViewById( R.id.tags);
        mTags.setText( mBrewery.getTags() );

        mCityState = (TextView) v.findViewById( R.id.cityState );
        String cityState = mBrewery.getCity() + ", " + mBrewery.getState();
        mCityState.setText( cityState );

        mPhone= (TextView) v.findViewById( R.id.phone );
        mPhone.setText( mBrewery.getPhone() );
        mPhone.setOnClickListener( new View.OnClickListener(){
            public void onClick( View arg0 ){
                Log.d( "onClick", "yes");
                Intent callIntent = new Intent( Intent.ACTION_DIAL );
                callIntent.setData(Uri.parse("tel:" + mBrewery.getPhone()));
                startActivity( callIntent );
            }
        });

        mStreetAddress = (TextView) v.findViewById( R.id.streetAddress );
        mStreetAddress.setText( mBrewery.getStreetAddress() );
//        startAnimation();
        startAnimation2();
        return v;
    } // end of onCreateView

    private void startAnimation() {
        Log.d("StartAnimation:", "called");
        mContainer.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        float nameXStart = mContainer.getLeft();
        float nameXEnd = mContainer.getMeasuredWidth() / 2;



        ObjectAnimator nameAnimator = ObjectAnimator.ofFloat( mName, "x", 0, nameXEnd ).setDuration(500);
        nameAnimator.start();
    }

    private void startAnimation2() {
        mContainer.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        float cardStart = mContainer.getBottom();
        float cardStart = mContainer.getMeasuredHeight();
        float cardEnd = mName.getMeasuredHeight();
        float nameStart = mContainer.getMeasuredHeight();
        float nameEnd = mName.getBottom();

        ObjectAnimator cardAnimator = ObjectAnimator.ofFloat( mConstraint, "y", cardStart, cardEnd ).setDuration(550);
        cardAnimator.start();
        ObjectAnimator nameAnimator = ObjectAnimator.ofFloat( mName, "y", nameStart, nameEnd ).setDuration(520);
        nameAnimator.start();
    }

} // end of BreweryFragment class
