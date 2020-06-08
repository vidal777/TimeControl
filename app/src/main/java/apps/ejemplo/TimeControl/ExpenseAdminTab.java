package apps.ejemplo.TimeControl;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import apps.ejemplo.TimeControl.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;


public class ExpenseAdminTab extends Fragment implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String uidAdmin;

    private FloatingActionButton expense;

    private ListView listViewExpense;

    private String URL,uid;

    private ArrayList<HashMap<String, String>> expensesList= new ArrayList<>();

    private Spinner spinnerExpense;

    private Button btnDateEntrance,btnDateExit;

    private TextView totalSuma;

    private ImageButton btnOk,btnReset;

    private Uri uri=null;

    private int E_dia, E_mes, E_any, S_dia, S_mes, S_any,year,month,day;

    private ArrayAdapter<String> adapterspinner;


    public ExpenseAdminTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_expense_tab, container, false);

        firebaseAuth= FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        uidAdmin=user.getUid();



        URL="http://timecontrol.ddns.net/android_app/get_expense_admin.php?position=0&uidAdmin=" + uidAdmin;
        expense=view.findViewById(R.id.expense);
        listViewExpense=view.findViewById(R.id.listViewExpense);
        spinnerExpense=view.findViewById(R.id.spinnerExpense);
        btnDateEntrance=view.findViewById(R.id.btnDateEntrance);
        btnDateExit=view.findViewById(R.id.btnDateExit);
        btnOk=view.findViewById(R.id.btnOk);
        btnReset=view.findViewById(R.id.btnReset);
        totalSuma=view.findViewById(R.id.totalSuma);

        btnDateExit.setOnClickListener(ExpenseAdminTab.this);
        btnDateEntrance.setOnClickListener(ExpenseAdminTab.this);
        btnOk.setOnClickListener(ExpenseAdminTab.this);
        btnReset.setOnClickListener(ExpenseAdminTab.this);
        expense.setOnClickListener(ExpenseAdminTab.this);
        totalSuma.setOnClickListener(ExpenseAdminTab.this);


        String[] opciones = {"Last Month", "Last Week", "Last Day"};

        adapterspinner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, opciones);

        spinnerExpense.setAdapter(adapterspinner);


        spinnerExpense.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                URL = "http://timecontrol.ddns.net/android_app/get_expense_admin.php?position=" + position + "&uidAdmin=" + uidAdmin ;
                Call_expense();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        listViewExpense.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                HashMap<String, String> user;
                user=expensesList.get(position);
                Collection<String> values = user.values();
                ArrayList<String> listOfValues = new ArrayList<>(values);

                final String concept=listOfValues.get(0);
                final String comments=listOfValues.get(1);
                final String price=listOfValues.get(2);
                String idPhoto=listOfValues.get(3);
                final String data=listOfValues.get(4);

                if (idPhoto.isEmpty()){
                    showDialog(concept,price,data,comments,null);
                }else{
                    StorageReference storageRef =
                            FirebaseStorage.getInstance().getReference();
                    storageRef.child("Expenses/"+idPhoto).getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    showDialog(concept,price,data,comments,uri);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }

            }
        });

        return view;
    }

    private void showDialog(final String concept,final String price,final String data,final String comments,Uri imageuri){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());


        final View customLayout = getLayoutInflater().inflate(R.layout.activity_details_expense, null);
        builderSingle.setView(customLayout);

        TextView txtValue=customLayout.findViewById(R.id.txtValue);
        TextView txtDate=customLayout.findViewById(R.id.txtDate);
        TextView txtConcept=customLayout.findViewById(R.id.txtConcept);
        TextView txtComments=customLayout.findViewById(R.id.txtComments);
        ImageButton btnBack3=customLayout.findViewById(R.id.btnBack3);
        ImageView imageView=customLayout.findViewById(R.id.imageView);


        txtValue.setText(price);
        txtDate.setText(data);
        txtConcept.setText(concept);
        txtComments.setText(comments);



        if(imageuri!=null){
            imageView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            Picasso.get().load(imageuri).rotate(90).into(imageView);
        }


        final AlertDialog alert = builderSingle.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();



        btnBack3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });


    }



    private void Call_expense() {
        GetExpenseData(new ExpenseAdminTab.VolleyCallbackExpense() {
            @Override
            public void onSuccessExpense(String result) {
                JSON_expense(result);
            }
        });
    }


    private interface VolleyCallbackExpense {

        void onSuccessExpense(String string);
    }

    public void onResume() {
        super.onResume();
        Call_expense();
    }

    private void GetExpenseData(final ExpenseAdminTab.VolleyCallbackExpense callBackexpense) {
        StringRequest stringRequestexpense = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callBackexpense.onSuccessExpense(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                });

        Volley.newRequestQueue(getContext()).add(stringRequestexpense);

    }



    private void JSON_expense(String result){
        Log.i("UIDADMIN",result);
        int suma = 0;
        expensesList.clear();
        ListAdapter adapter = new SimpleAdapter(getContext(), expensesList, R.layout.list_item_expense_admin, new String[]{"Name","Date", "Concept","Total"}, new int[]{R.id.Name,R.id.Date, R.id.Concept,R.id.Total});
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray users=jsonObj.getJSONArray("users");

            if (users.toString().equals("[]")){
                //FancyToast.makeText(getContext(), "NO DATA ON THIS DATE",FancyToast.LENGTH_SHORT,FancyToast.INFO,true).show();
                listViewExpense.setAdapter(null);

            }else{
                Log.i("USERS",users.toString());

                //traversing through all the object
                for (int i = 0; i < users.length(); i++) {
                    JSONObject c = users.getJSONObject(i);
                    String name=c.getString("name");
                    String date=c.getString("date");
                    String concept=c.getString("concept");
                    String price=c.getString("price");
                    String idPhoto=c.getString("idPhoto");
                    String comments=c.getString("comments");

                    //getting product object from json array
                    HashMap<String, String> user = new HashMap<>();
                    user.put("Name",name);
                    user.put("Date",date);
                    user.put("Concept",concept);
                    user.put("Total",price);
                    user.put("idPhoto",idPhoto);
                    user.put("Comments",comments);

                    expensesList.add(user);

                    listViewExpense.setAdapter(adapter);

                    suma += Integer.parseInt(price);

                }
                totalSuma.setText(suma + "");
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        year=c.get(Calendar.YEAR);
        month=c.get(Calendar.MONTH);
        day=c.get(Calendar.DAY_OF_MONTH);

        switch (v.getId()){
            case R.id.expense:
                Intent intent;
                intent = new Intent(getActivity(),AddExpense.class);
                startActivity(intent);
                break;
            case R.id.btnDateExit:
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        btnDateExit.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        S_dia = dayOfMonth;
                        S_mes = monthOfYear + 1;
                        S_any = year;
                    }
                }, year, month, day);
                datePickerDialog2.show();
                break;
            case R.id.btnDateEntrance:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        btnDateEntrance.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        E_dia = dayOfMonth;
                        E_mes = monthOfYear + 1;
                        E_any = year;
                    }
                }, year,month,day);
                datePickerDialog.show();
                break;
            case R.id.btnOk:
                if (btnDateEntrance.getText().toString().matches("") && btnDateExit.getText().toString().matches("")) {
                    FancyToast.makeText(getContext(), "Necessita insertar data",
                            FancyToast.LENGTH_SHORT, FancyToast.ERROR, true).show();

                } else {
                    String data_entrada = E_any + "-" + E_mes + "-" + E_dia;
                    String data_sortida = S_any + "-" + S_mes + "-" + S_dia;
                    URL = "http://timecontrol.ddns.net/android_app/get_expense_admin.php?position=3&data_entrada=" + data_entrada + "&data_sortida=" + data_sortida + "&uidAdmin=" + uidAdmin;
                    Call_expense();

                }
                break;
            case R.id.btnReset:
                spinnerExpense.setSelection(0);
                btnDateEntrance.setText("");
                btnDateExit.setText("");
                URL="http://timecontrol.ddns.net/android_app/get_expense_admin.php?position=0&uidAdmin=" + uidAdmin;
                Call_expense();
                break;


        }

    }




}
