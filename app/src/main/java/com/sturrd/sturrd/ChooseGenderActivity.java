package com.sturrd.sturrd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sturrd.sturrd.R;

import java.util.HashMap;
import java.util.Map;

public class ChooseGenderActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mMale, mFemale;
    private TextView txtMale, txtFemale;
    private FirebaseAuth mAuth;
    private Button mContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_gender);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        mMale = findViewById(R.id.img_male);
        mFemale = findViewById(R.id.img_female);
        mAuth = FirebaseAuth.getInstance();


        mContinue = findViewById(R.id.btn_continue);

        txtMale = findViewById(R.id.txt_male);
        txtFemale = findViewById(R.id.txt_female);


        Shader textMale = new LinearGradient(0, 0, txtMale.getWidth(), txtMale.getTextSize(),
                new int[]{
                        Color.parseColor("#FFFD297B"),
                        Color.parseColor("#FFFF655B"),

                }, null, Shader.TileMode.MIRROR);
        txtMale.getPaint().setShader(textMale);

        Shader textFemale = new LinearGradient(0, 0, txtFemale.getWidth(), txtMale.getTextSize(),
                new int[]{
                        Color.parseColor("#FFFD297B"),
                        Color.parseColor("#FFFF655B"),

                }, null, Shader.TileMode.MIRROR);
        txtFemale.getPaint().setShader(textFemale);

    }


    @Override
    public void onClick(View v) {

        mMale = findViewById(R.id.img_male);
        mFemale = findViewById(R.id.img_female);


        mMale.setOnClickListener(this);
        mFemale.setOnClickListener(this);
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        final Map userInfo = new HashMap<>();

        switch (v.getId()) {

            case R.id.img_male:
                mMale.setImageResource(R.drawable.ic_male_filled);
                mFemale.setImageResource(R.drawable.ic_female_outlined);
                userInfo.put("interest", "Female");
                userInfo.put("sex", "Male");
                currentUserDb.updateChildren(userInfo);
                break;

            case R.id.img_female:
                mFemale.setImageResource(R.drawable.ic_female_filled);
                mMale.setImageResource(R.drawable.ic_male_outlined);
                userInfo.put("interest", "Male");
                userInfo.put("sex", "Female");
                currentUserDb.updateChildren(userInfo);
                break;

            case R.id.btn_continue:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();
                break;

        }

    }
}
