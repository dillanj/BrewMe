package johnson.dillan.brewme;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class BreweryLab {

    private static BreweryLab sBreweryLab;

    private List<BreweryItem> mBreweries;

    public static BreweryLab get(  ){
        if ( sBreweryLab == null ){
            sBreweryLab = new BreweryLab( );
        }
        return sBreweryLab;
    }

    private BreweryLab( ){
        mBreweries = new ArrayList<>();
    }

    public void addBrewery( BreweryItem b ){
        mBreweries.add( b );
    }

    public List<BreweryItem> getBreweries(){
        return mBreweries;
    }

    public BreweryItem getBrewery( String id ){
        for ( BreweryItem brewery : mBreweries ){
            if ( brewery.getId().equals(id) ){
                return brewery;
            }
        }
        return null;
    }

    public void clearExisting(){
        mBreweries.clear();
    }


} // end of BreweryLab
