package apps.ejemplo.TimeControl;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import apps.ejemplo.TimeControl.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersTab extends Fragment implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ListView listView;

    private Spinner spinner;

    private String URL,uid;

    private Button btnEntrada, btnSortida;
    private ImageButton btnOk, btnReset;


    private int E_dia, E_mes, E_any, S_dia, S_mes, S_any;

    private ArrayAdapter<String> adapterspinner;

    private ArrayList<HashMap<String, String>> userList = new ArrayList<>();
    private int day,month,year;


    public UsersTab() {
        // Required empty public constructor
    }

    private LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users_tab, container, false);


        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        uid=user.getUid();


        URL = "http://timecontrol.ddns.net/android_app/get_data_user.php?position=0&uid=" + uid;


        btnEntrada = view.findViewById(R.id.btnEntrada);
        btnSortida = view.findViewById(R.id.btnSortida);
        btnOk = view.findViewById(R.id.btnOk);
        btnReset = view.findViewById(R.id.btnReset);

        btnEntrada.setOnClickListener(UsersTab.this);
        btnSortida.setOnClickListener(UsersTab.this);
        btnOk.setOnClickListener(UsersTab.this);
        btnReset.setOnClickListener(UsersTab.this);



        listView = view.findViewById(R.id.listView);

        spinner = view.findViewById(R.id.spinner);

        String[] opciones = {"Last Month", "Last Week", "Last Day"};

        adapterspinner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, opciones);

        spinner.setAdapter(adapterspinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                URL = "http://timecontrol.ddns.net/android_app/get_data_user.php?position=" + position + "&uid=" + uid;
                Call();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Get the selected item text from ListView
                HashMap<String, String> user;
                user=userList.get(position);
                Collection<String> values = user.values();
                ArrayList<String> listOfValues = new ArrayList<>(values);
                final String address_in=listOfValues.get(1);
                final String address_out=listOfValues.get(2);
                // Display the selected item text on TextView

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Which address do you want to check?")
                        .setIcon(R.drawable.ic_location)
                        .setCancelable(false)
                        .setPositiveButton("Address Out", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                searchLocation(address_out);
                                //dialog.cancel();
                            }
                        })
                        .setNegativeButton("Address In", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //MyActivity.this.finish();
                                searchLocation(address_in);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setCanceledOnTouchOutside(true);
                alert.show();
            }
        });


        return view;
    }

    private void searchLocation(String address) {

        LatLng latLng=getLocationFromAddress(getActivity(),address);

        Intent intent;
        intent = new Intent(getActivity(),MapsActivity.class);

        intent.putExtra("LatLng",latLng);
        startActivity(intent);

    }

    private void Call() {
        GetData(new UsersTab.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                JSON(result);
            }
        });
    }

    public void onResume() {
        super.onResume();
        Call();
    }

    private void JSON(String result) {
        userList.clear();
        ListAdapter adapter = new SimpleAdapter(getContext(), userList, R.layout.list_item_user, new String[]{"get_in", "get_out"}, new int[]{R.id.get_in, R.id.get_out});
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray users = jsonObj.getJSONArray("users");

            if (users.toString().equals("[]")) {
                //FancyToast.makeText(getContext(), "NO DATA ON THIS DATE",FancyToast.LENGTH_SHORT, FancyToast.INFO, true).show();
                listView.setAdapter(null);

            } else {

                //traversing through all the object
                for (int i = 0; i < users.length(); i++) {
                    JSONObject c = users.getJSONObject(i);
                    String get_in = c.getString("get_in");
                    String get_out = c.getString("get_out");
                    String address_in = c.getString("address_in");
                    String address_out = c.getString("address_out");

                    //getting product object from json array
                    HashMap<String, String> user = new HashMap<>();

                    user.put("get_in", get_in);
                    user.put("get_out", get_out);
                    user.put("address_in",address_in);
                    user.put("address_out",address_out);

                    userList.add(user);

                    listView.setAdapter(adapter);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public interface VolleyCallback {

        void onSuccess(String string);
    }

    private void GetData(final UsersTab.VolleyCallback callBack) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callBack.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                });

        Volley.newRequestQueue(getContext()).add(stringRequest);

    }


    @Override
    public void onClick(View v) {

        final Calendar c = Calendar.getInstance();
        year=c.get(Calendar.YEAR);
        month=c.get(Calendar.MONTH);
        day=c.get(Calendar.DAY_OF_MONTH);

        switch (v.getId()) {
            case R.id.btnEntrada:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        btnEntrada.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        E_dia = dayOfMonth;
                        E_mes = monthOfYear + 1;
                        E_any = year;
                    }
                }, year,month,day);
                datePickerDialog.show();



                break;
            case R.id.btnSortida:
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        btnSortida.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        S_dia = dayOfMonth;
                        S_mes = monthOfYear + 1;
                        S_any = year;
                    }
                }, year, month, day);
                datePickerDialog2.show();

                break;
            case R.id.btnOk:
                if (btnEntrada.getText().toString().matches("") && btnSortida.getText().toString().matches("")) {
                    FancyToast.makeText(getContext(), "Necessita insertar data",
                            FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();

                } else {
                    String data_entrada = E_any + "-" + E_mes + "-" + E_dia;
                    String data_sortida = S_any + "-" + S_mes + "-" + S_dia;
                    URL = "http://timecontrol.ddns.net/android_app/get_data_user.php?position=3&data_entrada=" + data_entrada + "&data_sortida=" + data_sortida + "&uid=" + uid;
                    Call();

                }
                break;
            case R.id.btnReset:
                spinner.setSelection(0);
                btnSortida.setText("");
                btnEntrada.setText("");
                URL = "http://timecontrol.ddns.net/android_app/get_data_user.php?position=0&uid=" + uid;
                Call();
                break;
        }
    }

}