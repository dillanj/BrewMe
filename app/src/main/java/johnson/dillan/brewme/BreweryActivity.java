package johnson.dillan.brewme;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class BreweryActivity extends SingleFragmentActivity {

    private static final String BREWERY_ID = "Brewery_Item";

    @Override
    protected Fragment createFragment() {
        String id = (String) getIntent().getStringExtra( BREWERY_ID );
        return BreweryFragment.newInstance( id );
    }

    public static Intent newIntent( Context packageContext, String id ){
        Intent intent = new Intent( packageContext, BreweryActivity.class );
        intent.putExtra( BREWERY_ID, id );
        return intent;
    }
}
